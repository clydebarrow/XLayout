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

import com.controlj.logging.CJLogView.logMsg
import com.controlj.shim.CxFactory

/**
 * Created by clyde on 8/4/18.
 */
open class HorizontalGroup(
        layout: Layout = Layout(),
        name: String = ""
) : ViewGroup(layout, name) {
    var spacing = 0.0       // space to be inserted betweeen elements
    private var totalFixedSize = 0.0
    private var totalWeight = 0.0

    /**
     * The height is the maximum height of any child, or the available height if any of those are match_parent.
     * The width is the sum of the child widths, if there are any
     * flexible elements the resulting width will be the parent width.
     */
    override fun onMeasure(availableWidth: Double, availableHeight: Double) {
        logMsg(this, "HorizontalLayout.measure(%s, %s)", availableWidth.asDim(), availableHeight.asDim())
        measuredSize.height = 0.0
        totalFixedSize = 0.0

        // filter out views that are gone
        val visibleViews = childViews.filterNot { it.gone }
        // measure all children
        visibleViews.forEach {
            val childWidth = if (it.layout.widthMode == Layout.Mode.Absolute) it.layout.width else availableWidth
            val childHeight = if (it.layout.heightMode == Layout.Mode.Absolute) it.layout.height else availableHeight
            it.onMeasure(
                    (childWidth - it.layout.margins.totalWidth()).coerceAtLeast(0.0),
                    (childHeight - it.layout.margins.totalHeight()).coerceAtLeast(0.0)
            )
        }
        // find the maximum height
        measuredSize.height = visibleViews.maxHeight(0.0).coerceAtLeast(0.0)
        //if (measuredSize.height == 0.0) measuredSize.height = availableHeight

        // calculate the fixed width
        totalFixedSize = visibleViews.map { subView ->
            when (subView.layout.widthMode) {
                Layout.Mode.Absolute -> subView.layout.width + subView.layout.margins.totalWidth()
                Layout.Mode.WrapContent -> subView.measuredSize.width + subView.layout.margins.totalWidth()
                Layout.Mode.Weighted, Layout.Mode.MatchParent -> subView.layout.margins.totalWidth()
            }
        }.sum() + spacing * (visibleViews.size - 1).coerceAtLeast(0)
        measuredSize.width = totalFixedSize
        totalWeight = visibleViews.filter {
            it.layout.widthMode == Layout.Mode.Weighted || it.layout.widthMode == Layout.Mode.MatchParent
        }.sumByDouble { it.layout.weight }

        // calculate total size of variable elements
        // there are weighted elements
        if (totalWeight != 0.0)
            measuredSize.width = availableWidth
        logMsg(this, "HorizontalLayout.onMeasureHorizontal done: totalWeight=${totalWeight}, measuredSize =%s", measuredSize.asSizeString())
    }

    override fun layoutSubviews() {
        logMsg(this, "HorizontalLayout.onLayout: newPosition=$frame, visible=$visible, ${childViews.size} subviews")
        var x = frame.origin.x
        val weightedSpace = frame.width - totalFixedSize
        logMsg(this, "horizontal layout $frame, x=$x, gravity=${layout.gravity.horizontal}, size=$measuredSize")
        childViews.forEach { v ->
            // Hide hidden views
            if (v.gone) {
                v.frame = CxFactory.cxRect()
            } else {
                // position origin at top left, apply gravity later
                val boxHeight = (frame.height - v.layout.margins.totalHeight()).coerceAtLeast(0.0)
                val boxWidth = when (v.layout.widthMode) {
                    Layout.Mode.Absolute -> v.layout.width
                    Layout.Mode.WrapContent -> v.measuredSize.width
                    Layout.Mode.Weighted, Layout.Mode.MatchParent -> weightedSpace * v.layout.weight / totalWeight
                }
                logMsg(v, "boxWidth/height = $boxWidth/$boxHeight, measuredSize=${v.measuredSize}, gravity=${v.layout.gravity}")
                v.frame = CxFactory.cxRect(
                        x + v.layout.margins.left,
                        frame.minY + v.layout.margins.top,
                        when (v.layout.widthMode) {
                            Layout.Mode.Absolute -> v.layout.width
                            Layout.Mode.Weighted, Layout.Mode.MatchParent -> boxWidth
                            else -> v.measuredSize.width
                        },
                        when (v.layout.heightMode) {
                            Layout.Mode.Absolute -> v.layout.height
                            Layout.Mode.Weighted, Layout.Mode.MatchParent -> boxHeight
                            else -> v.measuredSize.height
                        }
                ).applyGravity(boxWidth, boxHeight, v.layout.gravity)
                x += boxWidth + spacing + v.layout.margins.totalWidth()
            }
            v.layoutSubviews()
        }
    }

    override fun dividerLayout(thickness: Double): Layout {
        return Layout.layout {
            width = thickness
            widthMode = Layout.Mode.Absolute
            heightMode = Layout.Mode.MatchParent
        }
    }

    companion object {
        fun horizontalGroup(config: HorizontalGroup.() -> Unit): HorizontalGroup {
            return HorizontalGroup().apply(config)
        }
    }
}
