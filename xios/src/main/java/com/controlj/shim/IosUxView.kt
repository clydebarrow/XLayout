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

import com.controlj.layout.Layout
import com.controlj.layout.View
import com.controlj.layout.ViewGroup
import org.robovm.apple.coregraphics.CGSize
import org.robovm.apple.uikit.UITapGestureRecognizer
import org.robovm.apple.uikit.UIView

open class IosUxView(
        val uiview: UIView = UIView(),
        override var layout: Layout = Layout(),
        override var name: String = uiview::class.simpleName.toString()
) : UxView, UxPadded {

    override var paddingLeft: Double = 0.0
    override var paddingRight: Double = 0.0
    override var paddingTop: Double = 0.0
    override var paddingBottom: Double = 0.0

    override fun onMeasure(availableWidth: Double, availableHeight: Double) {
        val cgSize = uiview.getSizeThatFits(CGSize(availableWidth, availableHeight))
        measuredSize.width = cgSize.width + paddingLeft + paddingRight
        measuredSize.height = cgSize.height + paddingTop + paddingBottom
    }

    override fun removeFromSuperview() {
        uiview.removeFromSuperview()
    }

    override val intrinsicSize: CxSize
        get() = IosCxSize(uiview.intrinsicContentSize)

    override var backgroundColor: UxColor
        get() = IosCxColor(uiview.backgroundColor)
        set(value) {
            value as IosCxColor
            uiview.backgroundColor = value.uiColor
        }
    override val layer: CxLayer
        get() = IosCxLayer(uiview.layer)
    override var events: Set<View.Event> = setOf()
    override var frame: CxRect
        get() = IosCxRect(uiview.frame)
        set(value) {
            value as IosCxRect
            uiview.frame = value.cgRect
        }
    override var visible: Boolean
        get() = !uiview.isHidden
        set(value) {
            uiview.isHidden = !value
        }
    override var row: Int = 0
    override var column: Int = 0
    override var parent: ViewGroup? = null
    override var measuredSize: CxSize = IosCxSize()
    override var gone: Boolean = false

    override fun layoutSubviews() {
        uiview.layoutSubviews()
    }
}

/**
 * Extension function to make adding native views to a viewgroup easy.
 */
fun ViewGroup.addView(
        uiview: UIView,
        layout: Layout = Layout(),
        name: String = uiview::class.simpleName.toString()
) {
    add(IosUxView(uiview, layout, name))
}
