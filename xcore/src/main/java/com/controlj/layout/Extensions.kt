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

fun List<View>.maxHeight(dflt: Double): Double {
    // find the maximum height
    return this.map { subView ->
        when (subView.layout.heightMode) {
            Layout.Mode.Absolute -> subView.layout.height + subView.layout.margins.totalHeight()
            Layout.Mode.MatchParent,
            Layout.Mode.WrapContent -> subView.measuredSize.height + subView.layout.margins.totalHeight()
            Layout.Mode.Weighted -> error("Weighted height inside horizontalGroup")
        }
    }.maxOrNull() ?: dflt
}

fun List<View>.maxWidth(dflt: Double): Double {
    // find the maximum width
    return this.map { subView ->
        when (subView.layout.widthMode) {
            Layout.Mode.Absolute -> subView.layout.width + subView.layout.margins.totalWidth()
            Layout.Mode.MatchParent,
            Layout.Mode.WrapContent -> subView.measuredSize.width + subView.layout.margins.totalWidth()
            Layout.Mode.Weighted -> error("Weighted width inside VerticalGroup")
        }
    }.maxOrNull() ?: dflt
}

fun CxSize.asSizeString(): String {
    return "{${width.asDim()}, ${height.asDim()}}"
}

fun CxSize.applyPadding(padding: UxEdgeInsets): CxSize {
    return CxFactory.cxSize(width + padding.totalWidth(), height + padding.totalHeight())
}

fun CxSize.applyPadding(padding: Double): CxSize {
    return CxFactory.cxSize(width + padding * 2, height + padding * 2)
}

/**
 * Given a CxRect and a new size, apply the specified gravity to relocate the origin
 *
 */
fun CxRect.applyGravity(width: Double, height: Double, g: Gravity): CxRect {
    origin.x += when (g.horizontal) {
        Gravity.Horizontal.Right -> width - size.width
        Gravity.Horizontal.Center -> (width - size.width) / 2.0
        else -> 0.0
    }
    origin.y += when (g.vertical) {
        Gravity.Vertical.Bottom -> height - size.height
        Gravity.Vertical.Center -> (height - size.height) / 2.0
        else -> 0.0
    }
    return this
}


fun CxRect.applyGravity(newSize: CxSize, g: Gravity): CxRect {
    return applyGravity(newSize.width, newSize.height, g)
}
