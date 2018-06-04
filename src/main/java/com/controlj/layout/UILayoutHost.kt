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

import org.robovm.apple.coregraphics.CGRect
import org.robovm.apple.coregraphics.CGSize
import org.robovm.apple.uikit.UIView
import org.robovm.apple.uikit.UIViewAutoresizing

/**
 * UILayoutHost is the native UIView that hosts that XibFree layout
 * It acts as a FrameLayout with one child, i.e. the child will fill the frame of this view
 */

class  UILayoutHost @JvmOverloads constructor(val viewGroup: ViewGroup, frame: CGRect = CGRect.Zero()) : UIView(frame), ViewGroup.IHost {
    init {
        viewGroup.host = this
        this.autoresizingMask = UIViewAutoresizing.with(
                UIViewAutoresizing.FlexibleWidth, UIViewAutoresizing.FlexibleHeight)
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
        viewGroup.onMeasure(bounds.width, bounds.height)
        val size = viewGroup.measuredSize
        val subViewPosition = bounds.applyInsets(viewGroup.layout.margins).applyGravity(size, viewGroup.layout.gravity)
        viewGroup.layout(subViewPosition, false)
        didLayoutAction?.invoke()
    }

    override fun willMoveToSuperview(superView: UIView?) {
        super.willMoveToSuperview(superView)
        if(superView != null)
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

}
