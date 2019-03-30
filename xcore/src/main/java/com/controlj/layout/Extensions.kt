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

import com.controlj.shim.CxFactory
import com.controlj.shim.CxRect
import com.controlj.shim.CxSize
import com.controlj.shim.UxEdgeInsets

/**
 * Created by clyde on 9/4/18.
 */

fun CxRect.applyInsets(insets: UxEdgeInsets): CxRect {
    return CxFactory.cxRect(this.origin.x + insets.left, origin.y + insets.top,
            width - insets.totalWidth(), height - insets.totalHeight())
}

fun List<View>.maxWidth(): Double {
    var width = 0.0
    for (v in this)
        width = Math.max(width, v.measuredSize.width + v.layout.margins.totalWidth())
    return width
}

fun List<View>.maxHeight(): Double {
    var height = 0.0
    for (v in this)
        height = Math.max(height, v.measuredSize.height + v.layout.margins.totalHeight())
    return height
}

fun CxSize.asSizeString(): String {
    return "{${Layout.dimToString(width)}, ${Layout.dimToString(height)}}"
}

fun CxSize.applyPadding(padding: UxEdgeInsets): CxSize {
    return CxFactory.cxSize(width + padding.totalWidth(), height + padding.totalHeight())
}

fun CxSize.applyPadding(padding: Double): CxSize {
    return CxFactory.cxSize(width + padding * 2, height + padding * 2)
}

/**
 * Given a CxRect and a new size, apply the specified gravity to create a new CxRect.
 *
 */
fun CxRect.applyGravity(size: CxSize, g: Gravity): CxRect {
    val left: Double
    when (g.horizontal) {
        Gravity.Horizontal.Right -> left = this.maxX - size.width
        Gravity.Horizontal.Center -> left = (minX + maxX - size.width) / 2.0
        else -> left = minX
    }
    val top: Double
    when (g.vertical) {
        Gravity.Vertical.Bottom -> top = this.maxY - size.height
        Gravity.Vertical.Center -> top = (maxY + minY - size.height) / 2.0
        else -> top = minY
    }
    return CxFactory.cxRect(left, top, size.width, size.height)
}

/**
 * Select all the text in a text field.
 */
//fun UITextField.selectAll() {
//performSelector(Selector.register("selectAll"), null, 0.0)
//}
