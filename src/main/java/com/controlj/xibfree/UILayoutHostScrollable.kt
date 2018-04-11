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

import org.robovm.apple.coregraphics.CGPoint
import org.robovm.apple.coregraphics.CGRect
import org.robovm.apple.uikit.UIScrollView
import org.robovm.apple.uikit.UIViewAutoresizing

/**
 * Created by clyde on 8/4/18.
 */
class UILayoutHostScrollable(val layout: ViewGroup, frame: CGRect = CGRect()) : UIScrollView(frame) {
    val layoutHost = UILayoutHost(layout)

    init {
        layoutHost.autoresizingMask = UIViewAutoresizing.None
        autoresizingMask = UIViewAutoresizing.with(UIViewAutoresizing.FlexibleHeight, UIViewAutoresizing.FlexibleWidth)
        addSubview(layoutHost)
    }

    override fun layoutSubviews() {
        layout.measure(bounds.width, Double.MAX_VALUE)
        val size = layout.measuredSize
        layoutHost.frame = CGRect(CGPoint.Zero(), size)
        contentSize = size
    }
}
