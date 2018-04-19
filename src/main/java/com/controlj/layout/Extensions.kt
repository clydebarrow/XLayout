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
import org.robovm.apple.uikit.UIEdgeInsets

/**
 * Created by clyde on 9/4/18.
 */
fun UIEdgeInsets.totalWidth(): Double {
    return left + right
}

fun UIEdgeInsets.totalHeight(): Double {
    return top + bottom
}

fun CGRect.applyInsets(insets: UIEdgeInsets): CGRect {
    return CGRect(this.origin.x + insets.left, origin.y + insets.top,
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

fun CGSize.asSizeString(): String {
    return "{${Layout.dimToString(width)}, ${Layout.dimToString(height)}}"
}

fun CGSize.applyPadding(padding: UIEdgeInsets): CGSize {
    return CGSize(width + padding.totalWidth(), height + padding.totalHeight())
}

fun CGRect.applyGravity(size: CGSize, g: Gravity): CGRect {
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
    return CGRect(left, top, size.width, size.height)
}
