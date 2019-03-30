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

package com.controlj.shim

import com.controlj.layout.asCxRect
import com.controlj.layout.asCxSize
import org.robovm.apple.uikit.UIView
import org.robovm.apple.uikit.UIViewAutoresizing

class iosUxView(val uiview: UIView) : UxView {
    override fun addSubview(view: UxView) {
        view as iosUxView
        uiview.addSubview(view.uiview)
    }

    override fun removeFromSuperview() {
        uiview.removeFromSuperview()
    }

    override val intrinsicSize: CxSize
        get() = uiview.intrinsicContentSize.asCxSize()
    override var backgroundColor: UxColor
        get() = iosUxColor(uiview.backgroundColor)
        set(value) {
            uiview.backgroundColor = value.asUIColor()
        }
    override val layer: CxLayer
        get() = iosCxLayer(uiview.layer)
    override var frame: CxRect
        get() = uiview.frame.asCxRect()
        set(value) {
            value as iosCxRect
            uiview.frame = value.cgRect
        }
    override var autoresizingMask: Long
        get() = uiview.autoresizingMask.value()
        set(value) {
            uiview.autoresizingMask = UIViewAutoresizing(value)
        }
    override var hided: Boolean
        get() = uiview.isHidden
        set(value) {
            uiview.isHidden = value
        }
}
