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

import com.controlj.xibfree.LayoutParameters.Companion.MAX_DIMENSION
import org.robovm.apple.coregraphics.CGRect
import org.robovm.apple.coregraphics.CGSize

/**
 * Created by clyde on 8/4/18.
 */
open class LinearLayout(var orientation: Orientation = Orientation.Horizontal, layoutParameters: LayoutParameters = LayoutParameters(), vararg views: View) : ViewGroup(layoutParameters) {
    init {
        addSubViews(*views)
    }

    var gravity = Gravity.TopLeft
    var totalWeight = 0.0
    var spacing = 0.0

    override fun onMeasure(parentWidth: Double, parentHeight: Double) {
        if (orientation == Orientation.Vertical)
            measureVertical(parentWidth, parentHeight)
        else
            measureHorizontal(parentHeight, parentWidth)
    }

    override fun onLayout(newPosition: CGRect, parentHidden: Boolean) {
        super.onLayout(newPosition, parentHidden)
        logMsg("LinearLayout.onLayout: newPosition=%s, parentHidden=%s, visible=%s, %d subviews", newPosition, parentHidden.toString(), visible, subViews.size)
        if (!parentHidden && visible) {
            if (orientation == Orientation.Vertical)
                layoutVertical(newPosition)
            else
                layoutHorizontal(newPosition)
        }
    }

    private fun measureHorizontal(parentHeight: Double, parentWidth: Double) {
        logMsg("LinearLayout.measureHorizontal(%s, %s)", LayoutParameters.dimToString(parentWidth), LayoutParameters.dimToString(parentHeight))
        logMsg("Layoutparams = %s", layoutParameters.toString())
        val layoutHeight = layoutParameters.tryResolveHeight(this, parentWidth, parentHeight)
        var layoutWidth = layoutParameters.tryResolveWidth(this, parentWidth, parentHeight)
        logMsg("layoutWidth/Height(%g, %g)", layoutWidth, layoutHeight)
        var totalFixedSize = 0.0
        var totalWeight = 0.0
        var visibleViewCount = 0

        // calculate total fixed size
        subViews
                .filter { !it.gone }
                .forEach { v ->
                    if (v.layoutParameters.widthUnits == LayoutParameters.Units.ParentRatio)
                        totalWeight += v.layoutParameters.weight
                    else {
                        v.measure(MAX_DIMENSION, adjustLayoutHeight(layoutHeight, v))
                        totalFixedSize += v.measuredSize.width
                    }
                    totalFixedSize += v.layoutParameters.margins.totalWidth()
                    visibleViewCount++
                }

        // adjust for spacing
        if (visibleViewCount != 0)
            totalFixedSize += spacing * (visibleViewCount - 1)
        logMsg("horz totalfixed size = %g", totalFixedSize)
        // calculate total size of variable elements
        var totalVariableSize = 0.0
        if (layoutParameters.widthUnits == LayoutParameters.Units.ContentRatio || layoutWidth == MAX_DIMENSION) {
            // weird case - width is wrap_content, but some children are match_parent
            // use their natural size instead
            subViews
                    .filter { !it.gone && it.layoutParameters.widthUnits == LayoutParameters.Units.ParentRatio }
                    .forEach { v ->
                        totalVariableSize += v.measure(MAX_DIMENSION, adjustLayoutHeight(layoutHeight, v)).width
                    }
        } else {
            // prefer explicit over calculated total weight
            if (this.totalWeight != 0.0)
                totalWeight = this.totalWeight
            var room = layoutWidth - totalFixedSize
            subViews
                    .filter { !it.gone && it.layoutParameters.widthUnits == LayoutParameters.Units.ParentRatio }
                    .forEach { v ->
                        if (room < 0.0)
                            room = 0.0
                        val size = if (totalWeight == 0.0) room else room * v.layoutParameters.weight / totalWeight
                        val measured = v.measure(size, adjustLayoutHeight(layoutHeight, v)).width
                        totalVariableSize += measured
                        room -= measured
                        totalWeight -= v.layoutParameters.weight
                    }
        }

        // find max height of all children that are not match_parent
        val sizeMeasured = CGSize()
        logMsg("ready to measure height - layoutHeight == %s", LayoutParameters.dimToString(layoutHeight))
        if (layoutHeight == MAX_DIMENSION) {
            sizeMeasured.height = 0.0
            subViews
                    .filter { !it.gone && it.layoutParameters.heightUnits != LayoutParameters.Units.ParentRatio }
                    .forEach { v ->
                        sizeMeasured.height = Math.max(sizeMeasured.height, v.measuredSize.height + v.layoutParameters.margins.totalHeight())
                        logMsg("sizemeasured is %s after adding %s", sizeMeasured.asSizeString(), v.javaClass.superclass.simpleName)
                    }
            // set the height of children who want to match_parent
            subViews
                    .filter { !it.gone && it.layoutParameters.heightUnits == LayoutParameters.Units.ParentRatio }
                    .forEach { v -> v.measure(v.measuredSize.width, sizeMeasured.height) }
        }
        if (layoutWidth == MAX_DIMENSION)
            layoutWidth = totalFixedSize + totalVariableSize
        measuredSize = layoutParameters.resolveSize(CGSize(layoutWidth, layoutHeight), sizeMeasured)
        logMsg("LinearLayout.onMeasureHorizontal done: measuredSize =%s", measuredSize.asSizeString())
    }

    private fun measureVertical(parentWidth: Double, parentHeight: Double) {
        //logMsg("LinearLayout.measureVertical(%g, %g)", parentWidth, parentHeight)
        //logMsg("Layoutparams = %s", layoutParameters.toString())
        val layoutWidth = layoutParameters.tryResolveWidth(this, parentWidth, parentHeight)
        var layoutHeight = layoutParameters.tryResolveHeight(this, parentWidth, parentHeight)
        //logMsg("layoutWidth/Height(%g, %g)", layoutWidth, layoutHeight)
        var totalFixedSize = 0.0
        var totalWeight = 0.0
        var visibleViewCount = 0

        // calculate total fixed size
        subViews
                .filter { !it.gone }
                .forEach { v ->
                    if (v.layoutParameters.heightUnits == LayoutParameters.Units.ParentRatio)
                        totalWeight += v.layoutParameters.weight
                    else {
                        v.measure(adjustLayoutWidth(layoutWidth, v), MAX_DIMENSION)
                        totalFixedSize += v.measuredSize.height
                    }
                    totalFixedSize += v.layoutParameters.margins.totalHeight()
                    visibleViewCount++
                }

        // adjust for spacing
        if (visibleViewCount != 0)
            totalFixedSize += spacing * (visibleViewCount - 1)

        // calculate total size of variable elements
        var totalVariableSize = 0.0
        if (layoutParameters.heightUnits == LayoutParameters.Units.ContentRatio || layoutHeight == MAX_DIMENSION) {
            // weird case - height is wrap_content, but some children are match_parent
            // use their natural size instead
            subViews
                    .filter { !it.gone && it.layoutParameters.heightUnits == LayoutParameters.Units.ParentRatio }
                    .forEach { v ->
                        totalVariableSize += v.measure(adjustLayoutWidth(layoutWidth, v), MAX_DIMENSION).height
                    }
        } else {
            // prefer explicit over calculated total weight
            if (this.totalWeight != 0.0)
                totalWeight = this.totalWeight
            var room = layoutHeight - totalFixedSize
            subViews
                    .filter { !it.gone && it.layoutParameters.heightUnits == LayoutParameters.Units.ParentRatio }
                    .forEach { v ->
                        if (room < 0.0)
                            room = 0.0
                        val size = if (totalWeight == 0.0) room else room * v.layoutParameters.weight / totalWeight
                        val measured = v.measure(adjustLayoutWidth(layoutWidth, v), size).height
                        totalVariableSize += measured
                        room -= measured
                        totalWeight -= v.layoutParameters.weight
                    }
        }

        // find max width of all children that are not match_parent
        val sizeMeasured = CGSize()
        if (layoutWidth == MAX_DIMENSION) {
            sizeMeasured.width = 0.0
            subViews
                    .filter { !it.gone && it.layoutParameters.widthUnits != LayoutParameters.Units.ParentRatio }
                    .forEach { v ->
                        sizeMeasured.width = Math.max(sizeMeasured.width, v.measuredSize.width + v.layoutParameters.margins.totalWidth())
                    }
            // set the width of children who want to match_parent
            subViews
                    .filter { !it.gone && it.layoutParameters.widthUnits == LayoutParameters.Units.ParentRatio }
                    .forEach { v -> v.measure(sizeMeasured.width, v.measuredSize.height) }
        }
        if (layoutHeight == MAX_DIMENSION)
            layoutHeight = totalFixedSize + totalVariableSize
        measuredSize = layoutParameters.resolveSize(CGSize(layoutWidth, layoutHeight), sizeMeasured)
        logMsg("LinearLayout.onMeasureVertical done: measuredSize =%s", measuredSize.asSizeString())
    }

    private fun layoutVertical(newPosition: CGRect) {
        var y: Double
        when (gravity.vertical) {
            Gravity.Vertical.Bottom -> y = newPosition.maxY - getTotalMeasuredHeight()
            Gravity.Vertical.Center -> y = (newPosition.minY + newPosition.maxY) / 2 - getTotalMeasuredHeight() / 2
            else -> y = newPosition.minY
        }
        var first = true

        subViews.forEach { v ->
            // Hide hidden views
            if (v.gone) {
                v.layout(CGRect.Null(), false)
                return@forEach
            }

            if (!first)
                y += spacing
            else
                first = false


            y += v.layoutParameters.margins.top

            val size = v.measuredSize

            // Work out horizontal gravity for this control
            val x: Double
            when (gravity.horizontal) {

                Gravity.Horizontal.Right -> x = newPosition.maxX - v.layoutParameters.margins.right - size.width

                Gravity.Horizontal.Center -> x = (newPosition.minX + newPosition.maxX) / 2 - (size.width + v.layoutParameters.margins.totalWidth()) / 2
                else -> x = newPosition.minX + v.layoutParameters.margins.left
            }

            v.layout(CGRect(x, y, size.width, size.height), false)
            y += size.height + v.layoutParameters.margins.bottom
        }
    }

    private fun getTotalSpacing(): Double {
        if (spacing == 0.0)
            return 0.0

        val visibleViews = subViews.filter { x -> !x.gone }.count()
        if (visibleViews > 1)
            return (visibleViews - 1) * spacing
        return 0.0
    }

    private fun getTotalMeasuredHeight(): Double {
        var totHeight = getTotalSpacing()
        subViews.filter { x -> !x.gone }.forEach { x -> totHeight += x.measuredSize.height + x.layoutParameters.margins.totalHeight() }
        return totHeight
    }

    private fun getTotalMeasuredWidth(): Double {
        var totWidth = getTotalSpacing()
        subViews.filter { x -> !x.gone }.forEach { x -> totWidth += x.measuredSize.width + x.layoutParameters.margins.totalWidth() }
        return totWidth
    }

    // Do subview layout when in horizontal orientation
    private fun layoutHorizontal(newPosition: CGRect) {
        var x: Double
        when (gravity.horizontal) {
            Gravity.Horizontal.Right -> x = newPosition.maxX - getTotalMeasuredWidth()

            Gravity.Horizontal.Center -> x = (newPosition.minX + newPosition.maxX) / 2 - getTotalMeasuredWidth() / 2
            else -> x = newPosition.minX

        }

        var first = true

        subViews.forEach { v ->
            // Hide hidden views
            if (v.gone) {
                v.layout(CGRect.Null(), false)
                return@forEach
            }

            if (!first)
                x += spacing
            else
                first = false

            x += v.layoutParameters.margins.left

            val size = v.measuredSize

            // Work out vertical gravity for this control
            var g = v.layoutParameters.gravity.vertical
            if (g == Gravity.Vertical.None)
                g = gravity.vertical

            val y: Double
            when (g) {

                Gravity.Vertical.Bottom -> y = newPosition.maxY - v.layoutParameters.margins.bottom - size.height

                Gravity.Vertical.Center -> y = (newPosition.minY + newPosition.maxY) / 2 - (size.height + v.layoutParameters.margins.totalHeight()) / 2.0
                else -> y = newPosition.minY + v.layoutParameters.margins.top
            }


            v.layout(CGRect(x, y, size.width, size.height), false)

            x += size.width + v.layoutParameters.margins.right
        }
    }

    private fun adjustLayoutHeight(height: Double, v: View): Double {
        if (height == MAX_DIMENSION)
            return height
        return height - v.layoutParameters.margins.totalHeight()
    }


    private fun adjustLayoutWidth(width: Double, v: View): Double {
        if (width == MAX_DIMENSION)
            return width
        return width - v.layoutParameters.margins.totalWidth()
    }

}
