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

import com.controlj.layout.Layout.Companion.MAX_DIMENSION
import org.robovm.apple.coregraphics.CGPoint
import org.robovm.apple.coregraphics.CGRect
import org.robovm.apple.coregraphics.CGSize
import org.robovm.apple.uikit.UILayoutPriority
import org.robovm.apple.uikit.UIScrollView
import org.robovm.apple.uikit.UIView
import org.robovm.apple.uikit.UIViewAutoresizing

/**
 * Created by clyde on 8/4/18.
 */
open class UxScrollViewHost(layout: Layout = Layout()) : UIScrollView() {
    private val layoutHost = IosUxHost()

    val frameGroup: FrameGroup
        get() = layoutHost.frameGroup

    init {
        layoutHost.autoresizingMask = UIViewAutoresizing(0L)
        //autoresizingMask = UIViewAutoresizing.with(UIViewAutoresizing.FlexibleHeight, UIViewAutoresizing.FlexibleWidth)
        frameGroup.layout = layout
    }

    override fun willMoveToSuperview(superView: UIView?) {
        super.willMoveToSuperview(superView)
        if (superView == null)
            layoutHost.removeFromSuperview()
        else {
            addSubview(layoutHost)
        }
    }

    override fun layoutSubviews() {
        if (bounds.isEmpty)
            return
        val size = layoutHost.getSystemLayoutSizeFittingSize(
                CGSize(bounds.width, MAX_DIMENSION),
                UILayoutPriority.DefaultLow.toFloat(),
                UILayoutPriority.DefaultLow.toFloat()
        )
        layoutHost.frame = CGRect(CGPoint.Zero(), CGSize(bounds.width, size.height.coerceAtLeast(bounds.height)))
        layoutHost.layoutSubviews()
        contentSize = layoutHost.frame.size
        setShowsHorizontalScrollIndicator(size.width > bounds.width)
        setShowsVerticalScrollIndicator(size.height > bounds.height)
        isScrollEnabled = true
        isUserInteractionEnabled = true
    }
}
