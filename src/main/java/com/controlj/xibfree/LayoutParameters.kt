package com.controlj.xibfree

import com.controlj.xibfree.View.Companion.MATCH_PARENT
import com.controlj.xibfree.View.Companion.WRAP_CONTENT
import com.controlj.xibfree.View.Companion.logMsg
import org.robovm.apple.coregraphics.CGSize
import org.robovm.apple.uikit.UIEdgeInsets
import org.robovm.apple.uikit.UIScreen
import org.robovm.apple.uikit.UIScrollView
import kotlin.math.roundToInt

//  XibFree - http://www.toptensoftware.com/xibfree/
//
//  Copyright 2013  Copyright © 2013 Topten Software. All Rights Reserved
//
//    Licensed under the Apache License, Version 2.0 (the "License");
//    you may not use this file except in compliance with the License.
//    You may obtain a copy of the License at
//
//        http://www.apache.org/licenses/LICENSE-2.0
//
//    Unless required by applicable law or agreed to in writing, software
//    distributed under the License is distributed on an "AS IS" BASIS,
//    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//    See the License for the specific language governing permissions and
//    limitations under the License.


/// <summary>
/// LayoutParameters declare how a view should be laid out by it's parent view group.
/// </summary>

class LayoutParameters(var width: Double = View.MATCH_PARENT, var height: Double = View.MATCH_PARENT, var weight: Double = 0.0, var margins: UIEdgeInsets = UIEdgeInsets.Zero(), var gravity: Gravity = Gravity.None) {
    enum class Units {
        /// <summary>
        /// Absolute pixel dimension
        /// </summary>
        Absolute,
        /// <summary>
        /// Ratio of parent size
        /// </summary>
        ParentRatio,
        /// <summary>
        /// Ratio of content size
        /// </summary>
        ContentRatio,
        /// <summary>
        /// Ratio of adjacent dimension size
        /// </summary>
        AspectRatio,
        /// <summary>
        /// Ratio of the current screen size
        /// </summary>
        ScreenRatio,
        /// <summary>
        /// Ratio of the current UIViewHost window size
        /// </summary>
        HostRatio,
    }

    /// <summary>
    /// Initializes a new instance of the <see cref="XibFree.LayoutParameters"/> class.
    /// </summary>
    var visibility = View.Visibility.VISIBLE
    var maxWidth = MAX_DIMENSION
    var maxHeight = MAX_DIMENSION
    var minHeight = 0.0
    var minWidth = 0.0
    private var _widthUnits = Units.Absolute
    private var _heightUnits = Units.Absolute
    var widthUnits: Units
        set(value) {
            _widthUnits = value
        }
        get() {
            if (_widthUnits == Units.Absolute) {
                if (width == View.MATCH_PARENT)
                    return Units.ParentRatio
                if (width == View.WRAP_CONTENT)
                    return Units.ContentRatio
            }
            return _widthUnits
        }

    var heightUnits: Units
        set(value) {
            _heightUnits = value
        }
        get() {
            if (_heightUnits == Units.Absolute) {
                if (height == View.MATCH_PARENT)
                    return Units.ParentRatio
                if (height == View.WRAP_CONTENT)
                    return Units.ContentRatio
            }
            return _heightUnits
        }


    private val heightRatio: Double
        get() = if (_heightUnits == Units.Absolute) 1.0 else height

    private val widthRatio: Double
        get() = if (_widthUnits == Units.Absolute) 1.0 else width


    /// Gets or sets the left margin.
    /// <value>The left margin size.</value>

    var marginLeft: Double
        get() = margins.left
        set(value) {
            margins.left = value
        }
    var marginRight: Double
        get() = margins.right
        set(value) {
            margins.right = value
        }
    var marginTop: Double
        get() = margins.top
        set(value) {
            margins.top = value
        }
    var marginBotton: Double
        get() = margins.bottom
        set(value) {
            margins.bottom = value
        }

    companion object {
        val MAX_DIMENSION = Double.MAX_VALUE
        @JvmStatic

        fun dimToString(dim: Double): String {
            if (dim == MAX_DIMENSION)
                return "MAX"
            if (dim == MATCH_PARENT)
                return "match_parent"
            if (dim == WRAP_CONTENT)
                return "wrap_content"
            return dim.roundToInt().toString()
        }

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
    }

    internal fun tryResolveWidth(view: View, parentWidth: Double, parentHeight: Double): Double {
        val resolvedWidth: Double
        when (widthUnits) {
            Units.HostRatio -> resolvedWidth =  getHostSize(view).width * widthRatio
            Units.ScreenRatio -> resolvedWidth =  getScreenSize().width * widthRatio
            Units.Absolute -> resolvedWidth = width
            Units.ParentRatio -> resolvedWidth = if (parentWidth == MAX_DIMENSION) MAX_DIMENSION else parentWidth * widthRatio
            Units.AspectRatio -> resolvedWidth = tryResolveHeight(view, parentWidth, parentHeight) * widthRatio
            else -> resolvedWidth = MAX_DIMENSION
        }
        logMsg("tryResolveWidth(%s), width = %s, units = %s -> %s", dimToString(parentWidth), dimToString(width), widthUnits, dimToString(resolvedWidth))
        return resolvedWidth
    }

    internal fun tryResolveHeight(view: View, parentWidth: Double, parentHeight: Double): Double {
        val resolvedHeight: Double
        when (heightUnits) {
            Units.HostRatio -> resolvedHeight = getHostSize(view).height * heightRatio
            Units.ScreenRatio -> resolvedHeight = getScreenSize().height * heightRatio
            Units.Absolute -> resolvedHeight = height
            Units.ParentRatio -> resolvedHeight = if (parentHeight == MAX_DIMENSION) MAX_DIMENSION else parentHeight * heightRatio
            Units.AspectRatio -> resolvedHeight = if (tryResolveWidth(view, parentWidth, parentHeight) == MAX_DIMENSION) MAX_DIMENSION else tryResolveWidth(view, parentWidth, parentHeight) * heightRatio
            else ->
                resolvedHeight = MAX_DIMENSION
        }
        logMsg("tryResolveHeight(%s), height = %s, units = %s -> %s", dimToString(parentHeight), dimToString(height), heightUnits, dimToString(resolvedHeight))
        return resolvedHeight
    }

    internal fun resolveSize(size: CGSize, sizeMeasured: CGSize): CGSize {
        logMsg("ResolveSize(%s, %s)", size, sizeMeasured)
        // Resolve measured size
        if (size.width == MAX_DIMENSION)
            size.width = sizeMeasured.width
        if (size.height == MAX_DIMENSION)
            size.height = sizeMeasured.height

        // Finally, resolve aspect ratios
        if (widthUnits == Units.AspectRatio) {
            size.width = size.height * widthRatio
        }
        if (heightUnits == Units.AspectRatio) {
            size.height = size.width * heightRatio
        }
        logMsg("ResolveSize ->%s", size)
        return size
    }

    override fun toString(): String {
        return "Horz[${dimToString(width)} $widthUnits Gravity:${gravity.horizontal}] Vert[${dimToString(height)} $heightUnits Gravity:${gravity.vertical}]"
    }
}
