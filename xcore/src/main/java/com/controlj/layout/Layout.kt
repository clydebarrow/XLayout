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
import com.controlj.shim.CxFactory
import com.controlj.shim.UxEdgeInsets
import kotlin.math.roundToInt

/**
 * A [Layout] describes how a view should be laid out in its parent.
 */

class Layout(
        var width: Double = 0.0,
        var widthMode: Mode = if (width == 0.0) Mode.WrapContent else Mode.Absolute,
        var height: Double = 0.0,
        var heightMode: Mode = if (height == 0.0) Mode.WrapContent else Mode.Absolute,
        var weight: Double = 1.0,
        var gravity: Gravity = Gravity.None,
        margin: Double = 0.0) {

    /// <summary>
    /// Initializes a new instance of the <see cref="XibFree.LayoutParameters"/> class.
    /// </summary>
    var maxWidth = MAX_DIMENSION
    var maxHeight = MAX_DIMENSION
    var minHeight = 0.0
    var minWidth = 0.0
    var margins: UxEdgeInsets = CxFactory.uxEdgeInsets()
        set(value) {
            field.left = value.left
            field.right = value.right
            field.top = value.top
            field.bottom = value.bottom
        }

    var margin: Double
        set(value) {
            margins.left = value
            margins.right = value
            margins.top = value
            margins.bottom = value
        }
        get() = margins.left

    init {
        if(margin != 0.0)
            this.margin = margin
    }
    enum class Mode {
        // dimension in points
        Absolute,
        // fill the parent's width or height
        MatchParent,
        // wrap the content
        WrapContent,
        // Use weight to calculate the size
        Weighted,
    }


    companion object {

        val MAX_DIMENSION = Float.MAX_VALUE.toDouble()

        fun layout(config: Layout.() -> Unit): Layout {
            return Layout().apply(config)
        }

        fun fill(): Layout {
            return Layout(widthMode = Mode.MatchParent, heightMode = Mode.MatchParent)
        }

        fun matchWidth(): Layout = Layout(widthMode = Mode.MatchParent)
        fun matchHeight(): Layout = Layout(heightMode = Mode.MatchParent)
        fun weightedWidth(weight: Double = 1.0): Layout = Layout(widthMode = Mode.Weighted, weight = weight)
        fun weightedHeight(weight: Double = 1.0): Layout = Layout(heightMode = Mode.Weighted, weight = weight)

        fun absolute(width: Double, height: Double): Layout {
            return Layout(width, Mode.Absolute, height, Mode.Absolute)
        }
    }

    override fun toString(): String {
        return "Horz[${dimToString(widthMode, width, weight)}:${gravity.horizontal}] Vert[${dimToString(heightMode, height, weight)}:${gravity.vertical}]"
    }

    private fun dimToString(mode: Mode, dim: Double, weight: Double): String {
        return when(mode) {
            Mode.Absolute -> dim.asDim()
            Mode.MatchParent, Mode.WrapContent -> mode.name
            Mode.Weighted -> "Weighted($weight)"
        }
    }
}

fun Double.asDim(): String {
    if (this == MAX_DIMENSION)
        return "MAX"
    return roundToInt().toString()
}

