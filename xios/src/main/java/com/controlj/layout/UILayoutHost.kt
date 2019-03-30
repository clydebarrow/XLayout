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

import com.controlj.layout.View.Companion.logMsg
import com.controlj.shim.CxLayer
import com.controlj.shim.CxRect
import com.controlj.shim.CxSize
import com.controlj.shim.UxColor
import com.controlj.shim.UxEdgeInsets
import com.controlj.shim.UxView
import com.controlj.shim.asUIColor
import com.controlj.shim.iosCxEdgeInsets
import com.controlj.shim.iosCxLayer
import com.controlj.shim.iosCxRect
import com.controlj.shim.iosCxSize
import com.controlj.shim.iosUxColor
import com.controlj.shim.iosUxView
import org.robovm.apple.coregraphics.CGRect
import org.robovm.apple.coregraphics.CGSize
import org.robovm.apple.foundation.NSOperatingSystemVersion
import org.robovm.apple.foundation.NSProcessInfo
import org.robovm.apple.uikit.UIEdgeInsets
import org.robovm.apple.uikit.UIScreen
import org.robovm.apple.uikit.UIView
import org.robovm.apple.uikit.UIViewAutoresizing

/**
 * UILayoutHost is the native UIView that hosts that XibFree layout
 * It acts as a FrameLayout with one child, i.e. the child will fill the frame of this view
 */

open class UILayoutHost constructor(val viewGroup: ViewGroup, frame: CGRect = UIScreen.getMainScreen().bounds) : UIView(frame), IHost, UxView {
    override var hided: Boolean
        get() = isHidden
        set(value) {
            isHidden = value
        }

    override fun addSubview(view: UxView) {
        view as iosUxView
        addSubview(view.uiview)
    }

    override var intrinsicSize: CxSize = iosCxSize(CGSize())
    override var backgroundColor: UxColor
        get() = iosUxColor(getBackgroundColor())
        set(value) {
            setBackgroundColor(value.asUIColor())
        }
    override val layer: CxLayer
        get() = iosCxLayer(getLayer())
    override var frame: CxRect
        get() = getFrame().asCxRect()
        set(value) {
            setFrame(value.asCGRect())
        }
    override var autoresizingMask: Long
        get() = getAutoresizingMask().value()
        set(value) {
            setAutoresizingMask(UIViewAutoresizing(value))
        }

    init {
        viewGroup.host = this
    }

    override fun getSizeThatFits(size: CGSize): CGSize {

        // Measure the layout
        return viewGroup.measure(
                size.width - viewGroup.layout.margins.totalWidth(),
                size.height - viewGroup.layout.margins.totalHeight()
        ).asCGSize()
    }

    /// <Docs>Lays out subviews.</Docs>
    /// <summary>
    /// Called by iOS to update the layout of this view
    /// </summary>
    @Suppress("DEPRECATION")
    override fun layoutSubviews() {
        setTranslatesAutoresizingMaskIntoConstraints(false)
        val subViewPosition = (
                when {
                    isIos11 -> bounds.applyInsets(safeAreaInsets.asCxRect())
                    else ->
                        bounds
                }).applyInsets(viewGroup.layout.margins)
        viewGroup.onMeasure(subViewPosition.width, subViewPosition.height)
        val size = viewGroup.measuredSize
        logMsg("${viewGroup.name}: frame=$frame, bounds=$bounds, subViewPosition=${subViewPosition.applyGravity(size, viewGroup.layout.gravity)}")
        viewGroup.performLayout(subViewPosition.applyGravity(size, viewGroup.layout.gravity), false)
        didLayoutAction?.invoke()
    }

    override fun willMoveToSuperview(superView: UIView?) {
        super.willMoveToSuperview(superView)
        if (superView != null)
            frame = superView.bounds.asCxRect()
    }

    var didLayoutAction: (() -> Unit?)? = null

    override fun getUxView(): UxView {
        return this
    }

    /**
     * Should be called by the enclosing UIViewController when the view becomes visible, e.g. from viewWillAppear
     */
    fun onShown() {
        viewGroup.onShown()
    }

    /**
     * Should be called when the view becomes hidden, e.g. from viewDidDisappear
     */
    fun onHidden() {
        viewGroup.onHidden()
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

private fun UIEdgeInsets.asCxRect(): UxEdgeInsets {
    return iosCxEdgeInsets(top, left, bottom, right)
}

fun CGRect.asCxRect(): CxRect {
    return iosCxRect(this)
}

fun CGSize.asCxSize(): CxSize {
    return iosCxSize(this)
}
fun CxRect.asCGRect(): CGRect {
    return CGRect(origin.x, origin.y, size.width, size.height)
}

fun CxSize.asCGSize(): CGSize {
    return CGSize(width, height)
}

fun CGRect.applyInsets(insets: UIEdgeInsets): CGRect {
    return CGRect(this.origin.x + insets.left, origin.y + insets.top,
            width - insets.left - insets.right, height - insets.top - insets.bottom)
}

fun CGRect.applyInsets(insets: UxEdgeInsets): CxRect {
    return iosCxRect(CGRect(this.origin.x + insets.left, origin.y + insets.top,
            width - insets.left - insets.right, height - insets.top - insets.bottom))
}


