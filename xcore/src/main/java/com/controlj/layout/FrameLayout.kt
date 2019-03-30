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

import com.controlj.layout.Gravity.Companion.None
import com.controlj.layout.Layout.Companion.MAX_DIMENSION
import com.controlj.shim.CxFactory
import com.controlj.shim.CxRect

/**
 * Created by clyde on 8/4/18.
 *
 * A FrameLayout layers its children into its own frame. The layoutparameters for each child will
 * determine where in the FrameLayout the child appears. The position of each child is independent of
 * all other children
 */
class FrameLayout(layout: Layout = Layout(widthMode = Layout.Mode.MatchParent, heightMode = Layout.Mode.MatchParent)) : ViewGroup(layout) {
    var gravity = Gravity.TopLeft

    override fun onMeasure(parentWidth: Double, parentHeight: Double) {
        val unresolved = mutableListOf<View>()
        val width = layout.tryResolveWidth(parentWidth)
        val height = layout.tryResolveHeight(parentHeight)

        // measure all subviews where both dimensions can be resolved

        var haveResolvedSize = false
        var maxWidth = 0.0
        var maxHeight = 0.0
        childViews.forEach { subView ->
            var subViewWidth = MAX_DIMENSION
            var subViewHeight = MAX_DIMENSION
            if (subView.layout.widthMode == Layout.Mode.MatchParent) {
                if (width == MAX_DIMENSION) {
                    unresolved.add(subView)
                    return@forEach
                } else
                    subViewWidth = width - subView.layout.margins.totalWidth()
            }
            if (subView.layout.heightMode == Layout.Mode.MatchParent) {
                if (height == MAX_DIMENSION) {
                    unresolved.add(subView)
                    return@forEach
                } else
                    subViewHeight = height - subView.layout.margins.totalHeight()
            }
            subView.measure(subViewWidth, subViewHeight)
            if (!haveResolvedSize) {
                maxWidth = subView.measuredSize.width + subView.layout.margins.totalWidth()
                maxHeight = subView.measuredSize.height + subView.layout.margins.totalHeight()
                haveResolvedSize = true
            } else {
                maxWidth = Math.max(maxWidth, subView.measuredSize.width + subView.layout.margins.totalWidth())
                maxHeight = Math.max(maxHeight, subView.measuredSize.height + subView.layout.margins.totalHeight())
            }
        }

        // now resolve the unresolved subviews

        childViews.forEach { subView ->
            val subViewWidth = if (subView.layout.widthMode == Layout.Mode.MatchParent && haveResolvedSize)
                maxWidth - subView.layout.margins.totalWidth() else MAX_DIMENSION
            val subViewHeight = if (subView.layout.heightMode == Layout.Mode.MatchParent && haveResolvedSize)
                maxHeight - subView.layout.margins.totalHeight() else MAX_DIMENSION
            subView.measure(subViewWidth, subViewHeight)
        }
        val sizeMeasured = CxFactory.cxSize(childViews.maxWidth(), childViews.maxHeight())
        measuredSize = layout.resolveSize(CxFactory.cxSize(width, height), sizeMeasured)
    }

    override fun onLayout(newPosition: CxRect, parentHidden: Boolean) {
        super.onLayout(newPosition, parentHidden)
        if (!parentHidden && visible) {
            // position views by layoutGravity
            childViews.forEach { subView ->
                if (subView.gone) {
                    subView.performLayout(CxFactory.cxRect(), false)
                } else {
                    // use sub view layoutGravity, or our own
                    val g = if (subView.layout.gravity == None) gravity else subView.layout.gravity
                    val size = subView.measuredSize
                    val subViewPosition = newPosition.applyInsets(subView.layout.margins).applyGravity(size, g)
                    subView.performLayout(subViewPosition, false)
                }
            }
        }
    }
    companion object {
        fun frameLayout(config: FrameLayout.() -> Unit): FrameLayout {
            return FrameLayout().apply(config)
        }
    }
}

