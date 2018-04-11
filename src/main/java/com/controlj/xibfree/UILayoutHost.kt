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

package com.controlj.xibfree

import com.controlj.xibfree.View.Companion.logMsg
import org.robovm.apple.coregraphics.CGRect
import org.robovm.apple.coregraphics.CGSize
import org.robovm.apple.uikit.UIView
import org.robovm.apple.uikit.UIViewAutoresizing

/**
 * UILayoutHost is the native UIView that hosts that XibFree layout
 */

class UILayoutHost(val layout: ViewGroup, frame: CGRect = CGRect.Zero()) : UIView(frame), ViewGroup.IHost {
    init {
        layout.host = this
        this.autoresizingMask = UIViewAutoresizing.with(
                UIViewAutoresizing.FlexibleWidth, UIViewAutoresizing.FlexibleHeight)
    }

    fun findNativeView(view: UIView): NativeView? {
        return layout.findNativeView(view)
    }

    override fun getSizeThatFits(size: CGSize): CGSize {

        // Measure the layout
        logMsg("UILayoutHost.sizethatfits, size=%s", size)
        return layout.measure(size.width, size.height)
    }

    /// <Docs>Lays out subviews.</Docs>
    /// <summary>
    /// Called by iOS to update the layout of this view
    /// </summary>
    override fun layoutSubviews() {
        // Remeasure
        logMsg("UILayoutHost.layoutSubViews, bounds=%s", bounds)
        layout.measure(bounds.width, bounds.height)
        layout.layout(bounds, false)

        didLayoutAction?.invoke()
    }

    var didLayoutAction: (() -> Unit?)? = null

    override fun getUIView(): UIView {
        return this
    }
}
