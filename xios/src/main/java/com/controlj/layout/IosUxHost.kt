/*
 * Copyright (c) 2019 Control-J Pty. Ltd. All rights reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * .
 *
 */

package com.controlj.layout

import com.controlj.logging.CJLogView.logMsg
import com.controlj.shim.CxLayer
import com.controlj.shim.CxRect
import com.controlj.shim.IosCxLayer
import com.controlj.shim.IosCxPoint
import com.controlj.shim.IosCxRect
import com.controlj.shim.IosCxSize
import com.controlj.shim.IosUxView
import com.controlj.shim.UxView
import com.controlj.shim.asUxEdgeInsets
import org.robovm.apple.coregraphics.CGPoint
import org.robovm.apple.coregraphics.CGSize
import org.robovm.apple.foundation.NSOperatingSystemVersion
import org.robovm.apple.foundation.NSProcessInfo
import org.robovm.apple.uikit.UIGestureRecognizerState
import org.robovm.apple.uikit.UILayoutFittingSize
import org.robovm.apple.uikit.UILayoutPriority
import org.robovm.apple.uikit.UILongPressGestureRecognizer
import org.robovm.apple.uikit.UIPinchGestureRecognizer
import org.robovm.apple.uikit.UITapGestureRecognizer
import org.robovm.apple.uikit.UIView
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * IosUxHost is the native UIView that hosts that Ux layout
 * It inserts a FrameGroup under the single child, thus respecting the child's margins and gravity.
 */

open class IosUxHost(vararg views: View) : UIView(), UxHost {
    final override val frameGroup: FrameGroup = FrameGroup().apply { views.forEach { add(it) } }

    /**
     * An enclosing [FrameGroup] to allow the child view's margins and gravity to be respected.
     */

    override val bounds: CxRect
        get() = IosCxRect(getBounds())

    override fun addSublayer(subLayer: CxLayer) {
        subLayer as IosCxLayer
        layer.addSublayer(subLayer.caLayer)
    }

    override fun addSubview(subView: UxView) {
        subView as IosUxView
        addSubview(subView.uiview)
    }

    override fun getSystemLayoutSizeFittingSize(size: CGSize, p1: Float, p2: Float): CGSize {
        frameGroup.onMeasure(size.width, size.height)
        return (frameGroup.measuredSize as IosCxSize).cgSize
    }

    override fun getSystemLayoutSizeFittingSize(size: UILayoutFittingSize): CGSize {
        return getSystemLayoutSizeFittingSize(
                size.value(),
                UILayoutPriority.DefaultLow.toFloat(),
                UILayoutPriority.DefaultLow.toFloat()
        )
    }

    override fun layoutSubviews() {
        logMsg(frameGroup, "in layoutSubviews, frame =$frame")
        if (frame.isEmpty)
            return
        updateRecognizers()
        val subViewPosition = (
                when {
                    isIos11 -> bounds.applyInsets(safeAreaInsets.asUxEdgeInsets())
                    else ->
                        bounds
                }).applyInsets(frameGroup.layout.margins)
        frameGroup.frame = subViewPosition
        frameGroup.onMeasure(subViewPosition.width, subViewPosition.height)
        frameGroup.layoutSubviews()
        frameGroup.onShown()
    }

    private val tapRecognizer by lazy {
        UITapGestureRecognizer {
            frameGroup.onTap(IosCxPoint(it.getLocationInView(this)))
        }
    }

    private val pressRecognizer by lazy {
        UILongPressGestureRecognizer {
            when(it.state) {
                UIGestureRecognizerState.Began -> frameGroup.onPress(IosCxPoint(it.getLocationInView(this)), false)
                UIGestureRecognizerState.Ended -> frameGroup.onPress(IosCxPoint(it.getLocationInView(this)), true)
                else -> Unit
            }
        }
    }

    private val doubleTapRecognizer by lazy {
        UITapGestureRecognizer {
            frameGroup.onDoubleTap(IosCxPoint(it.getLocationInView(this)))
        }.apply { numberOfTapsRequired = 2 }
    }

    private val zoomRecognizer by lazy {
        UIPinchGestureRecognizer { recognizer ->
            recognizer as UIPinchGestureRecognizer
            if (recognizer.numberOfTouches == 2L) when (recognizer.state) {
                UIGestureRecognizerState.Began,
                UIGestureRecognizerState.Changed -> {
                    val scale = recognizer.scale - 1
                    recognizer.scale = 1.0
                    val point0 = recognizer.getLocationOfTouch(0, this)
                    val point1 = recognizer.getLocationOfTouch(1, this)
                    val dy = abs(point1.y - point0.y)
                    val dx = abs(point1.x - point0.x)
                    val hippo = sqrt(dy * dy + dx * dx)
                    val center = IosCxPoint(CGPoint((point1.x + point0.x) / 2, (point1.y + point0.y) / 2))
                    frameGroup.onZoom(center, 1 + scale * dx / hippo, 1 + scale * dy / hippo)
                }
                else -> Unit
            }
        }
    }

    private fun updateRecognizers() {
        gestureRecognizers?.forEach { removeGestureRecognizer(it) }
        frameGroup.events.forEach {
            when (it) {
                View.Event.TAP -> addGestureRecognizer(tapRecognizer)
                View.Event.PRESS -> addGestureRecognizer(pressRecognizer)
                View.Event.DOUBLE_TAP -> addGestureRecognizer(doubleTapRecognizer)
                View.Event.TOUCH -> TODO()
                View.Event.FLING -> TODO()
                View.Event.ZOOM -> addGestureRecognizer(zoomRecognizer)
            }
        }
    }

    override fun willMoveToSuperview(superView: UIView?) {
        super.willMoveToSuperview(superView)
        if (superView == null) {
            frameGroup.onDetach()
            frameGroup.onHidden()
            gestureRecognizers?.forEach { removeGestureRecognizer(it) }

        } else {
            frameGroup.onAttach(this)
        }
    }

    companion object {
        val iOS11 = NSOperatingSystemVersion(11, 0, 0)
        val iOS10 = NSOperatingSystemVersion(10, 0, 0)

        fun isOSVersionOrLater(version: NSOperatingSystemVersion): Boolean {
            return NSProcessInfo.getSharedProcessInfo().isOperatingSystemAtLeastVersion(version)
        }

        val isIos11 = isOSVersionOrLater(iOS11)
        val isIos10 = isOSVersionOrLater(iOS10)
    }
}


