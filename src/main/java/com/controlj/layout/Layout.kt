/*
 * Copyright (c) 2018. Control-J Pty. Ltd. All rights reserved
 * Copyright 2013  Copyright © 2013 Topten Software. All Rights Reserved
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

import com.controlj.layout.View.Companion.logMsg
import org.robovm.apple.coregraphics.CGSize
import org.robovm.apple.uikit.UIEdgeInsets
import kotlin.math.roundToInt

/**
 * A [Layout] describes how a view should be laid out in its parent.
 */

data class Layout(
        var width: Double = 0.0,
        var widthMode: Mode = if (width == 0.0) Mode.WrapContent else Mode.Absolute,
        var height: Double = 0.0,
        var heightMode: Mode = if (height == 0.0) Mode.WrapContent else Mode.Absolute,
        var weight: Double = 0.0,
        var aspectRatio: Double = 1.0,
        var gravity: Gravity = Gravity.None) {

    /// <summary>
    /// Initializes a new instance of the <see cref="XibFree.LayoutParameters"/> class.
    /// </summary>
    var maxWidth = MAX_DIMENSION
    var maxHeight = MAX_DIMENSION
    var minHeight = 0.0
    var minWidth = 0.0
    var leftMargin: Double = 0.0
    var rightMargin: Double = 0.0
    var topMargin: Double = 0.0
    var bottomMargin: Double = 0.0
    var margins: UIEdgeInsets
        get() = UIEdgeInsets(topMargin, leftMargin, bottomMargin, rightMargin)
        set(value) {
            leftMargin = value.left
            rightMargin = value.right
            topMargin = value.top
            bottomMargin = value.bottom
        }

    var margin: Double
        set(value) {
            leftMargin = value
            rightMargin = value
            topMargin = value
            bottomMargin = value
        }
        get() = leftMargin

    enum class Mode {
        // dimension in points
        Absolute,
        // fill the parent's width or height
        MatchParent,
        // wrap the content
        WrapContent,
        // Use weight to calculate the size
        Weighted,
        // make one dimension equal to a multiple of the other
        Aspect
    }

    /**
     * This class will build a Layout object
     */
    class Builder() {
        val layout: Layout = Layout()
        private var widthSet = false
        private var heightSet = false

        private fun fail(message: String) {
            throw IllegalArgumentException(message)
        }

        fun widthMode(mode: Mode): Builder {
            if (widthSet)
                fail("Duplicate width mode")
            widthSet = true
            layout.widthMode = mode
            return this
        }

        fun heightMode(mode: Mode): Builder {
            if (heightSet)
                fail("Duplicate height mode")
            heightSet = true
            layout.heightMode = mode
            return this
        }

        fun width(width: Double): Builder {
            if (layout.width != 0.0)
                fail("Duplicate width")
            layout.width = width
            return this
        }

        fun height(height: Double): Builder {
            if (layout.height != 0.0)
                fail("Duplicate height")
            layout.height = height
            return this
        }

        fun minHeight(minHeight: Double): Builder {
            if (layout.minHeight != 0.0)
                fail("Duplicate minHeight")
            layout.minHeight = minHeight
            return this
        }

        fun maxHeight(maxHeight: Double): Builder {
            if (layout.maxHeight != MAX_DIMENSION)
                fail("Duplicate maxHeight")
            layout.maxHeight = maxHeight
            return this
        }

        fun minWidth(minWidth: Double): Builder {
            if (layout.minWidth != 0.0)
                fail("Duplicate minWidth")
            layout.minWidth = minWidth
            return this
        }

        fun maxWidth(maxWidth: Double): Builder {
            if (layout.maxWidth != MAX_DIMENSION)
                fail("Duplicate maxWidth")
            layout.maxWidth = maxWidth
            return this
        }

        fun weight(weight: Double): Builder {
            if (layout.weight != 0.0)
                fail("Duplicate weight")
            layout.weight = weight
            return this
        }

        fun gravity(gravity: Gravity): Builder {
            if (layout.gravity != Gravity.None)
                fail("Duplicate gravity")
            layout.gravity = gravity
            return this
        }

        fun margins(margins: UIEdgeInsets): Builder {
            if (layout.margins != UIEdgeInsets.Zero())
                fail("Duplicate margins")
            layout.margins = margins
            return this
        }

        fun margin(value: Double): Builder {
            return margins(UIEdgeInsets(value, value, value, value))
        }

        fun build(): Layout {
            if (layout.widthMode == Mode.Weighted && layout.heightMode == Mode.Weighted)
                fail("Only one axis can be weighted")       // TODO allow this for other layouts maybe?
            if (!widthSet && layout.width != 0.0)
                layout.widthMode = Mode.Absolute
            if (!heightSet && layout.height != 0.0)
                layout.heightMode = Mode.Absolute
            return layout
        }
    }

    companion object {

        val MAX_DIMENSION = Float.MAX_VALUE.toDouble()

        fun layout(config: Layout.() -> Unit): Layout {
            return Layout().apply(config)
        }

        @JvmStatic
        fun dimToString(dim: Double): String {
            if (dim == MAX_DIMENSION)
                return "MAX"
            return dim.roundToInt().toString()
        }

        fun absolute(width: Double, height: Double): Layout {
            return Layout(width, Mode.Absolute, height, Mode.Absolute)
        }

        /*
        private fun getHostSize(view: View): CGSize {
            // Get the host
            val host = view.host
            if (host == null)
                return getScreenSize()

            var hostView = host.getUIView()

            // Use outer scroll view if present
            val parent = hostView.superview
            if (parent is UIScrollView)
                hostView = parent

            // Return size
            return hostView.bounds.size
        }

        fun getScreenSize(): CGSize {
            return UIScreen.getMainScreen().bounds.size
        }
        */
    }

    internal fun tryResolveWidth(parentWidth: Double): Double {
        val resolvedWidth: Double
        when (widthMode) {
            Mode.Absolute -> resolvedWidth = width
            Mode.Aspect -> resolvedWidth = height * aspectRatio
            Mode.Weighted, Mode.MatchParent -> resolvedWidth = parentWidth
            else -> resolvedWidth = MAX_DIMENSION
        }
        logMsg("tryResolveWidth(%s), width = %s, mode = %s -> %s", dimToString(parentWidth), dimToString(width), widthMode, dimToString(resolvedWidth))
        return resolvedWidth
    }

    internal fun tryResolveHeight(parentHeight: Double): Double {
        val resolvedHeight: Double
        when (heightMode) {
            Mode.Absolute -> resolvedHeight = height
            Mode.Aspect -> resolvedHeight = width * aspectRatio
            Mode.Weighted, Mode.MatchParent -> resolvedHeight = parentHeight
            else ->
                resolvedHeight = MAX_DIMENSION
        }
        logMsg("tryResolveHeight(%s), height = %s, units = %s -> %s", dimToString(parentHeight), dimToString(height), heightMode, dimToString(resolvedHeight))
        return resolvedHeight
    }

    internal fun resolveSize(size: CGSize, sizeMeasured: CGSize): CGSize {
        logMsg("ResolveSize (%s, %s), (%s, %s)", dimToString(size.width), dimToString(size.height), dimToString(sizeMeasured.width), dimToString(sizeMeasured.height))
        // Resolve measured size
        if (size.width == MAX_DIMENSION)
            size.width = sizeMeasured.width
        if (size.height == MAX_DIMENSION)
            size.height = sizeMeasured.height
        if(widthMode == Mode.Aspect)
            size.width = size.height * aspectRatio
        else if(heightMode == Mode.Aspect)
            size.height = size.width * aspectRatio
        logMsg("ResolveSize ->(%s, %s)", dimToString(size.width), dimToString(size.height))
        return size
    }

    override fun toString(): String {
        return "Horz[${dimToString(width)} $widthMode Gravity:${gravity.horizontal}] Vert[${dimToString(height)} $heightMode Gravity:${gravity.vertical}] Weight: $weight"
    }
}
