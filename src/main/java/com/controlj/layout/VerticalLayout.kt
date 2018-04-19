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
open class VerticalLayout(layout: Layout = Layout(), vararg views: View) : ViewGroup(layout) {
    init {
        addSubViews(*views)
    }

    var gravity = Gravity.TopLeft
    var totalWeight = 0.0
    var spacing = 0.0

    override fun onMeasure(parentWidth: Double, parentHeight: Double) {
        logMsg("$name: VerticalLayout.onMeasure(%s, %s)", Layout.dimToString(parentWidth), Layout.dimToString(parentHeight))
        logMsg("$name: Layoutparams = %s", layout.toString())
        val layoutWidth = layout.tryResolveWidth(parentWidth)
        var layoutHeight = layout.tryResolveHeight(parentHeight)
        //logMsg("$name: layoutWidth/Height(%g, %g)", layoutWidth, layoutHeight)
        var totalFixedSize = 0.0
        var totalWeight = 0.0
        var visibleViewCount = 0

        // calculate total fixed size
        childViews
                .filter { !it.gone }
                .forEach { v ->
                    if (v.layout.heightMode == Layout.Mode.Weighted)
                        totalWeight += v.layout.weight
                    else {
                        v.measure(adjustLayoutWidth(layoutWidth, v), MAX_DIMENSION)
                        totalFixedSize += v.measuredSize.height
                    }
                    totalFixedSize += v.layout.margins.totalHeight()
                    visibleViewCount++
                }

        // adjust for spacing
        if (visibleViewCount != 0)
            totalFixedSize += spacing * (visibleViewCount - 1)

        // calculate total size of variable elements
        var totalVariableSize = 0.0
        if (layout.heightMode == Layout.Mode.WrapContent || layoutHeight == MAX_DIMENSION) {
            // weird case - height is wrap_content, but some children are match_parent
            // use their natural size instead
            childViews
                    .filter { !it.gone && it.layout.heightMode == Layout.Mode.Weighted }
                    .forEach { v ->
                        totalVariableSize += v.measure(adjustLayoutWidth(layoutWidth, v), MAX_DIMENSION).height
                    }
        } else {
            // prefer explicit over calculated total weight
            if (this.totalWeight != 0.0)
                totalWeight = this.totalWeight
            var room = layoutHeight - totalFixedSize
            childViews
                    .filter { !it.gone && it.layout.heightMode == Layout.Mode.Weighted }
                    .forEach { v ->
                        if (room < 0.0)
                            room = 0.0
                        val size = if (totalWeight == 0.0) room else room * v.layout.weight / totalWeight
                        val measured = v.measure(adjustLayoutWidth(layoutWidth, v), size).height
                        totalVariableSize += measured
                        room -= measured
                        totalWeight -= v.layout.weight
                    }
        }

        // find max width of all children that are not match_parent
        val sizeMeasured = CGSize()
        if (layoutWidth == MAX_DIMENSION) {
            sizeMeasured.width = 0.0
            childViews
                    .filter { !it.gone && it.layout.widthMode != Layout.Mode.Weighted }
                    .forEach { v ->
                        sizeMeasured.width = Math.max(sizeMeasured.width, v.measuredSize.width + v.layout.margins.totalWidth())
                    }
            // set the width of children who want to match_parent
            childViews
                    .filter { !it.gone && it.layout.widthMode == Layout.Mode.Weighted }
                    .forEach { v -> v.measure(sizeMeasured.width, v.measuredSize.height) }
        }
        if (layoutHeight == MAX_DIMENSION)
            layoutHeight = totalFixedSize + totalVariableSize
        measuredSize = layout.resolveSize(CGSize(layoutWidth, layoutHeight), sizeMeasured)
        logMsg("$name: VerticalLayout.onMeasureVertical done: measuredSize =%s", measuredSize.asSizeString())
    }

    override fun onLayout(newPosition: CGRect, parentHidden: Boolean) {
        super.onLayout(newPosition, parentHidden)
        logMsg("$name: VerticalLayout.onLayout: newPosition=%s, parentHidden=%s, visible=%s, %d subviews", newPosition, parentHidden.toString(), visible, childViews.size)
        if (parentHidden || !visible)
            return
        var y: Double
        when (layout.gravity.vertical) {
            Gravity.Vertical.Bottom -> y = newPosition.maxY - getTotalMeasuredHeight()
            Gravity.Vertical.Center -> y = (newPosition.minY + newPosition.maxY) / 2 - getTotalMeasuredHeight() / 2
            else -> y = newPosition.minY
        }
        childViews.forEachIndexed {idx,  v ->
            // Hide hidden views
            if (v.gone) {
                v.layout(CGRect.Null(), false)
            } else {
                y += if (idx == 0) v.layout.margins.top else spacing
                val size = v.measuredSize
                // Work out horizontal layoutGravity for this control
                val x: Double
                when (gravity.horizontal) {
                    Gravity.Horizontal.Right -> x = newPosition.maxX - v.layout.margins.right - size.width
                    Gravity.Horizontal.Center -> x = (newPosition.minX + newPosition.maxX) / 2 - (size.width + v.layout.margins.totalWidth()) / 2
                    else -> x = newPosition.minX + v.layout.margins.left
                }
                v.layout(CGRect(x, y, size.width, size.height), false)
                y += size.height + v.layout.margins.bottom
            }
        }
    }

    private fun getTotalSpacing(): Double {
        if (spacing == 0.0)
            return 0.0
        val visibleViews = childViews.filter { x -> !x.gone }.count()
        if (visibleViews > 1)
            return (visibleViews - 1) * spacing
        return 0.0
    }

    private fun getTotalMeasuredHeight(): Double {
        var totHeight = getTotalSpacing()
        childViews.filter { x -> !x.gone }.forEach { x -> totHeight += x.measuredSize.height + x.layout.margins.totalHeight() }
        return totHeight
    }

    private fun adjustLayoutWidth(width: Double, v: View): Double {
        if (width == MAX_DIMENSION)
            return width
        return width - v.layout.margins.totalWidth()
    }

}
