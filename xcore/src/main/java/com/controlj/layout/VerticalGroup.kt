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
open class VerticalGroup(layout: Layout = Layout()) : ViewGroup(layout) {

    var totalWeight = 0.0
    var spacing = 0.0

    private var totalFixedSize: Double = 0.0

    /**
     * The width is the maximum width of any child, or the available width if _all_ of those are match_parent.
     * The height is the sum of the child heights, if there are any
     * flexible elements the resulting height will be the parent height.
     */
    override fun onMeasure(availableWidth: Double, availableHeight: Double) {
        logMsg(this, "HorizontalLayout.measure(%s, %s)", availableWidth.asDim(), availableHeight.asDim())
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
        // find the maximum width
        measuredSize.width = visibleViews.maxWidth(0.0).coerceAtLeast(0.0)
        //if (measuredSize.width == 0.0) measuredSize.width = availableWidth

        // calculate the fixed height
        totalFixedSize = visibleViews.map { subView ->
            when (subView.layout.heightMode) {
                Layout.Mode.Absolute -> subView.layout.height + subView.layout.margins.totalHeight()
                Layout.Mode.WrapContent -> subView.measuredSize.height + subView.layout.margins.totalHeight()
                Layout.Mode.Weighted, Layout.Mode.MatchParent -> subView.layout.margins.totalHeight()
            }
        }.sum() + spacing * (visibleViews.size - 1).coerceAtLeast(0)
        totalWeight = visibleViews.filter {
            it.layout.heightMode == Layout.Mode.Weighted || it.layout.heightMode == Layout.Mode.MatchParent
        }
                .sumByDouble { it.layout.weight }

        measuredSize.height = if (totalWeight != 0.0) availableHeight else totalFixedSize
        logMsg(this, "VerticalGroup.onMeasureHorizontal done: measuredSize =%s", measuredSize.asSizeString())
    }

    override fun layoutSubviews() {
        logMsg(this, "VerticalGroup.layoutSubviews: newPosition=$frame, visible=$visible, ${childViews.size} subviews")
        var y = frame.origin.y
        val weightedSpace = (frame.height - totalFixedSize).coerceAtLeast(0.0)
        childViews.forEach { v ->
            // Hide hidden views
            if (v.gone) {
                v.frame = CxFactory.cxRect()
            } else {
                // position origin at top left, apply gravity later
                val boxWidth = (frame.width - v.layout.margins.totalWidth()).coerceAtLeast(0.0)
                val boxHeight = when (v.layout.heightMode) {
                    Layout.Mode.Absolute -> v.layout.height
                    Layout.Mode.WrapContent -> v.measuredSize.height
                    Layout.Mode.Weighted, Layout.Mode.MatchParent -> weightedSpace * v.layout.weight / totalWeight
                }
                logMsg(v, "boxWidth/height = $boxWidth/$boxHeight, measuredSize=${v.measuredSize}")
                v.frame = CxFactory.cxRect(
                        frame.minX + v.layout.margins.left,
                        y + v.layout.margins.top,
                        when (v.layout.widthMode) {
                            Layout.Mode.Absolute -> v.layout.width
                            Layout.Mode.Weighted,
                            Layout.Mode.MatchParent -> boxWidth
                            else -> v.measuredSize.width
                        },
                        when (v.layout.heightMode) {
                            Layout.Mode.Absolute -> v.layout.height
                            Layout.Mode.Weighted,
                            Layout.Mode.MatchParent -> boxHeight
                            else -> v.measuredSize.height
                        }
                ).applyGravity(boxWidth, boxHeight, v.layout.gravity)
                y += boxHeight + spacing + v.layout.margins.totalHeight()
            }
            v.layoutSubviews()
        }
    }

    override fun dividerLayout(thickness: Double): Layout {
        return Companion.dividerLayout(thickness)
    }

    companion object {
        fun dividerLayout(thickness: Double = 1.0): Layout {
            return Layout.layout {
                height = thickness
                heightMode = Layout.Mode.Absolute
                widthMode = Layout.Mode.MatchParent
            }
        }

        fun verticalGroup(config: VerticalGroup.() -> Unit): VerticalGroup {
            return VerticalGroup().apply(config)
        }
    }
}
