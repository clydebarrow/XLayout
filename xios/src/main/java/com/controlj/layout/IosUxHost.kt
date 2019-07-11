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
import com.controlj.shim.IosCxRect
import com.controlj.shim.IosCxSize
import com.controlj.shim.IosUxView
import com.controlj.shim.UxView
import com.controlj.shim.asUxEdgeInsets
import org.robovm.apple.coregraphics.CGSize
import org.robovm.apple.foundation.NSOperatingSystemVersion
import org.robovm.apple.foundation.NSProcessInfo
import org.robovm.apple.uikit.UIView

/**
 * IosUxHost is the native UIView that hosts that Ux layout
 * It inserts a FrameGroup under the single child, thus respecting the child's margins and gravity.
 */

open class IosUxHost(vararg views: View) : UIView(), UxHost {
    final override val frameGroup: FrameGroup = FrameGroup()

    init {
        views.forEach { frameGroup.add(it) }
    }

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

    override fun getSizeThatFits(size: CGSize): CGSize {
        logMsg(frameGroup, "SizeThatFits($size)")
        frameGroup.onMeasure(size.width, size.height)
        logMsg(frameGroup, "FrameGroup measured size = ${frameGroup.measuredSize}")
        return (frameGroup.measuredSize as IosCxSize).cgSize
    }

    override fun layoutSubviews() {
        logMsg(frameGroup, "in layoutSubviews, frame =$frame")
        if(frame.isEmpty)
            return
        val subViewPosition = (
                when {
                    isIos11 -> bounds.applyInsets(safeAreaInsets.asUxEdgeInsets())
                    else ->
                        bounds
                }).applyInsets(frameGroup.layout.margins)
        frameGroup.frame = subViewPosition
        frameGroup.onMeasure(subViewPosition.width, subViewPosition.height)
        logMsg(frameGroup, "frame=$frame, bounds=$bounds, subViewPosition=${subViewPosition}")
        frameGroup.layoutSubviews()
    }

    override fun willMoveToSuperview(superView: UIView?) {
        super.willMoveToSuperview(superView)
        if (superView == null)
            frameGroup.onDetach()
        else {
            frameGroup.onAttach(this)
        }
    }

    /**
     * Should be called by the enclosing UIViewController when the view becomes visible, e.g. from viewWillAppear
     */
    fun onShown() {
        frameGroup.onShown()
    }

    /**
     * Should be called when the view becomes hidden, e.g. from viewDidDisappear
     */
    fun onHidden() {
        frameGroup.onHidden()
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


