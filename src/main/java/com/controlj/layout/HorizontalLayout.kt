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

import com.controlj.layout.Layout.Companion.MAX_DIMENSION
import org.robovm.apple.coregraphics.CGRect
import org.robovm.apple.coregraphics.CGSize

/**
 * Created by clyde on 8/4/18.
 */
open class HorizontalLayout(layout: Layout = Layout(), vararg views: View) : ViewGroup(layout) {
    init {
        addSubViews(*views)
    }

    var totalWeight = 0.0
    var spacing = 0.0


    override fun onMeasure(parentWidth: Double, parentHeight: Double) {
        logMsg("$name: HorizontalLayout.measure(%s, %s)", Layout.dimToString(parentWidth), Layout.dimToString(parentHeight))
        logMsg("$name: Layoutparams = %s", layout.toString())
        val layoutHeight = layout.tryResolveHeight(parentHeight)
        var layoutWidth = layout.tryResolveWidth(parentWidth)
        logMsg("$name: layoutWidth/Height is now (%s, %s)", Layout.dimToString(layoutWidth), Layout.dimToString(layoutHeight))
        var totalFixedSize = 0.0
        var totalWeight = 0.0
        var visibleViewCount = 0

        // calculate total fixed size
        childViews
                .filter { !it.gone }
                .forEach { v ->
                    if (v.layout.widthMode == Layout.Mode.Weighted)
                        totalWeight += v.layout.weight
                    else {
                        v.measure(MAX_DIMENSION, adjustLayoutHeight(layoutHeight, v))
                        totalFixedSize += v.measuredSize.width
                    }
                    totalFixedSize += v.layout.margins.totalWidth()
                    visibleViewCount++
                }

        // adjust for spacing
        if (visibleViewCount != 0)
            totalFixedSize += spacing * (visibleViewCount - 1)
        logMsg("$name: horz totalfixed size = %g, totalWeight=$totalWeight", totalFixedSize)
        // calculate total size of variable elements
        var totalVariableSize = 0.0
        if (layout.widthMode == Layout.Mode.WrapContent || layoutWidth == MAX_DIMENSION) {
            // weird case - width is wrap_content, but some children are match_parent
            // use their natural size instead
            childViews
                    .filter { !it.gone && it.layout.widthMode == Layout.Mode.Weighted }
                    .forEach { v ->
                        logMsg("$name: weird case for ${v.name}")
                        totalVariableSize += v.measure(MAX_DIMENSION, adjustLayoutHeight(layoutHeight, v)).width
                    }
        } else {
            // prefer explicit over calculated total weight
            if (this.totalWeight != 0.0)
                totalWeight = this.totalWeight
            var room = layoutWidth - totalFixedSize
            childViews.forEach {
                logMsg("   $name: childview ${it.name} layout ${it.layout} filter=%s", (!it.gone && it.layout.widthMode == Layout.Mode.Weighted).toString())
            }
            childViews
                    .filter { !it.gone && it.layout.widthMode == Layout.Mode.Weighted }
                    .forEach { v ->
                        if (room < 0.0)
                            room = 0.0
                        logMsg("$name: weighting ${v.name} totalWeight=$totalWeight, room=$room, v.weight=${v.layout.weight}")
                        val size = if (totalWeight == 0.0) room else room * v.layout.weight / totalWeight
                        val measured = v.measure(size, adjustLayoutHeight(layoutHeight, v)).width
                        totalVariableSize += measured
                        room -= measured
                        totalWeight -= v.layout.weight
                    }
        }

        // find max height of all children that are not match_parent
        val sizeMeasured = CGSize()
        logMsg("$name: ready to measure height - layoutHeight == %s", Layout.dimToString(layoutHeight))
        if (layoutHeight == MAX_DIMENSION) {
            sizeMeasured.height = 0.0
            childViews
                    .filter { !it.gone && it.layout.heightMode != Layout.Mode.Weighted }
                    .forEach { v ->
                        sizeMeasured.height = Math.max(sizeMeasured.height, v.measuredSize.height + v.layout.margins.totalHeight())
                        logMsg("$name: sizemeasured is %s after adding %s", sizeMeasured.asSizeString(), v.javaClass.superclass.simpleName)
                    }
            // set the height of children who want to match_parent
            childViews
                    .filter { !it.gone && it.layout.heightMode == Layout.Mode.Weighted }
                    .forEach { v -> v.measure(v.measuredSize.width, sizeMeasured.height) }
        }
        if (layoutWidth == MAX_DIMENSION)
            layoutWidth = totalFixedSize + totalVariableSize
        measuredSize = layout.resolveSize(CGSize(layoutWidth, layoutHeight), sizeMeasured)
        logMsg("$name: HorizontalLayout.onMeasureHorizontal done: measuredSize =%s", measuredSize.asSizeString())
    }


    private fun getTotalSpacing(): Double {
        if (spacing == 0.0)
            return 0.0

        val visibleViews = childViews.filter { x -> !x.gone }.count()
        if (visibleViews > 1)
            return (visibleViews - 1) * spacing
        return 0.0
    }

    private fun getTotalMeasuredWidth(): Double {
        var totWidth = getTotalSpacing()
        childViews.filter { x -> !x.gone }.forEach { x -> totWidth += x.measuredSize.width + x.layout.margins.totalWidth() }
        return totWidth
    }

    // Do subview layout when in horizontal orientation
    override fun onLayout(newPosition: CGRect, parentHidden: Boolean) {
        super.onLayout(newPosition, parentHidden)
        logMsg("$name: HorizontalLayout.onLayout: newPosition=%s, parentHidden=%s, visible=%s, %d subviews", newPosition, parentHidden.toString(), visible, childViews.size)
        if (parentHidden || !visible)
            return
        var x: Double
        /*
        // gravity will be applied by the parent view
        when (layout.gravity.horizontal) {
            Gravity.Horizontal.Right -> x = newPosition.maxX - getTotalMeasuredWidth()
            Gravity.Horizontal.Center -> x = (newPosition.minX + newPosition.maxX) / 2 - getTotalMeasuredWidth() / 2
            else -> x = newPosition.minX
        } */
        x = newPosition.minX
        logMsg("$name: horizontal layout %s, x=$x, gravity=${layout.gravity.horizontal}, totalmeasured=${getTotalMeasuredWidth()}", newPosition)
        childViews.forEachIndexed { idx, v ->
            // Hide hidden views
            if (v.gone) {
                v.layout(CGRect.Null(), false)
            } else {
                x += if (idx == 0) v.layout.margins.left else spacing
                val size = v.measuredSize
                // Work out vertical gravity for this control
                val g = v.layout.gravity.vertical
                val y: Double
                when (g) {
                    Gravity.Vertical.Bottom -> y = newPosition.maxY - v.layout.margins.bottom - size.height
                    Gravity.Vertical.Center -> y = (newPosition.minY + newPosition.maxY) / 2 - (size.height + v.layout.margins.totalHeight()) / 2.0
                    else -> y = newPosition.minY + v.layout.margins.top
                }
                logMsg("$name: layout ${v.name} size = $size, y = $y")
                v.layout(CGRect(x, y, size.width, size.height), false)
                x += size.width + v.layout.margins.right
            }
        }
    }

    private fun adjustLayoutHeight(height: Double, v: View): Double {
        if (height == MAX_DIMENSION)
            return height
        return height - v.layout.margins.totalHeight()
    }


    companion object {
        fun horizontalLayout(config: HorizontalLayout.() -> Unit): HorizontalLayout {
            return HorizontalLayout().apply(config)
        }
    }
}
