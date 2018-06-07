/*
 * Copyright (c) 2018. Control-J Pty. Ltd. All rights reserved
 * Copyright (c) 2013 Topten Software. All rights reserved
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
 */

package com.controlj.layout

import com.controlj.layout.View.Companion.logMsg
import org.robovm.apple.coregraphics.CGRect
import org.robovm.apple.coregraphics.CGSize
import org.robovm.apple.foundation.NSOperatingSystemVersion
import org.robovm.apple.foundation.NSProcessInfo
import org.robovm.apple.uikit.UIView

/**
 * UILayoutHost is the native UIView that hosts that XibFree layout
 * It acts as a FrameLayout with one child, i.e. the child will fill the frame of this view
 */

open class UILayoutHost @JvmOverloads constructor(val viewGroup: ViewGroup, frame: CGRect = CGRect.Zero()) : UIView(frame), ViewGroup.IHost {
    init {
        viewGroup.host = this
        setTranslatesAutoresizingMaskIntoConstraints(false)
    }

    fun findNativeView(view: UIView): NativeView? {
        return viewGroup.findNativeView(view)
    }

    override fun getSizeThatFits(size: CGSize): CGSize {

        // Measure the layout
        return viewGroup.measure(size.width - viewGroup.layout.margins.totalWidth(), size.height - viewGroup.layout.margins.totalHeight())
    }

    /// <Docs>Lays out subviews.</Docs>
    /// <summary>
    /// Called by iOS to update the layout of this view
    /// </summary>
    override fun layoutSubviews() {
        val subViewPosition = (
                if (isIos11)
                    bounds.inset(safeAreaInsets)
                else
                    bounds
                ).applyInsets(viewGroup.layout.margins)
        viewGroup.onMeasure(subViewPosition.width, subViewPosition.height)
        val size = viewGroup.measuredSize
        logMsg("${viewGroup.name}: frame=$frame, bounds=$bounds, subViewPosition=${subViewPosition.applyGravity(size, viewGroup.layout.gravity)}")
        viewGroup.layout(subViewPosition.applyGravity(size, viewGroup.layout.gravity), false)
        didLayoutAction?.invoke()
    }

    override fun willMoveToSuperview(superView: UIView?) {
        super.willMoveToSuperview(superView)
        if (superView != null)
            frame = superView.bounds
    }

    var didLayoutAction: (() -> Unit?)? = null

    override fun getUIView(): UIView {
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
