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
 *
 * A FrameGroup layers its children into its own frame. The layout parameters for each child will
 * determine where in the FrameGroup the child appears. The position of each child is independent of
 * all other children
 */
open class FrameGroup(layout: Layout = Layout(), name: String = "") : ViewGroup(layout, name) {

    /**
     * The measured size of a frame group is the smallest box that will enclose all its subviews.
     * Layout parameters will be applied by the container to that.
     */
    override fun onMeasure(availableWidth: Double, availableHeight: Double) {
        logMsg(this, "FrameGroup.onMeasure($availableWidth. $availableHeight")
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
        measuredSize.width = visibleViews.maxWidth(0.0).coerceAtLeast(0.0)
        measuredSize.height = visibleViews.maxHeight(0.0).coerceAtLeast(0.0)
    }

    override fun layoutSubviews() {
        // position views by layoutGravity
        childViews.forEach { v ->
            if (v.gone) {
                v.frame = CxFactory.cxRect()
            } else {
                val boxWidth = frame.width - v.layout.margins.totalWidth()
                val boxHeight = frame.height - v.layout.margins.totalHeight()
                v.frame = CxFactory.cxRect(
                        frame.minX + v.layout.margins.left,
                        frame.minY + v.layout.margins.top,
                        when (v.layout.widthMode) {
                            Layout.Mode.Absolute -> v.layout.width
                            Layout.Mode.MatchParent -> boxWidth
                            else -> v.measuredSize.width
                        },
                        when (v.layout.heightMode) {
                            Layout.Mode.Absolute -> v.layout.height
                            Layout.Mode.MatchParent -> boxHeight
                            else -> v.measuredSize.height
                        }
                ).applyGravity(boxWidth, boxHeight, v.layout.gravity)
            }
            v.layoutSubviews()
        }
    }

    companion object {
        fun frameLayout(config: FrameGroup.() -> Unit): FrameGroup {
            return FrameGroup().apply(config)
        }
    }
}

