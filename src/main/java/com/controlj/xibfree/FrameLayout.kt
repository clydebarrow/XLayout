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

import com.controlj.xibfree.Gravity.Companion.None
import com.controlj.xibfree.LayoutParameters.Companion.MAX_DIMENSION
import org.robovm.apple.coregraphics.CGRect
import org.robovm.apple.coregraphics.CGSize

/**
 * Created by clyde on 8/4/18.
 */
class FrameLayout(layoutParameters: LayoutParameters = LayoutParameters(MATCH_PARENT, MATCH_PARENT)) : ViewGroup(layoutParameters) {
    var gravity = Gravity.TopLeft

    override fun onMeasure(parentWidth: Double, parentHeight: Double) {
        val unresolved = ArrayList<View>()
        val width = layoutParameters.tryResolveWidth(this, parentWidth, parentHeight)
        val height = layoutParameters.tryResolveHeight(this, parentWidth, parentHeight)

        // measure all subviews where both dimensions can be resolved

        var haveResolvedSize = false
        var maxWidth = 0.0
        var maxHeight = 0.0
        for (subView in subViews) {
            var subViewWidth = MAX_DIMENSION
            var subViewHeight = MAX_DIMENSION
            if (subView.layoutParameters.widthUnits == LayoutParameters.Units.ParentRatio) {
                if (width == MAX_DIMENSION) {
                    unresolved.add(subView)
                    continue
                } else
                    subViewWidth = width - subView.layoutParameters.margins.totalWidth()
            }
            if (subView.layoutParameters.heightUnits == LayoutParameters.Units.ParentRatio) {
                if (height == MAX_DIMENSION) {
                    unresolved.add(subView)
                    continue
                } else
                    subViewHeight = height - subView.layoutParameters.margins.totalHeight()
            }
            subView.measure(subViewWidth, subViewHeight)
            if (!haveResolvedSize) {
                maxWidth = subView.measuredSize.width + subView.layoutParameters.margins.totalWidth()
                maxHeight = subView.measuredSize.height + subView.layoutParameters.margins.totalHeight()
                haveResolvedSize = true
            } else {
                maxWidth = Math.max(maxWidth, subView.measuredSize.width + subView.layoutParameters.margins.totalWidth())
                maxHeight = Math.max(maxHeight, subView.measuredSize.height + subView.layoutParameters.margins.totalHeight())
            }
        }

        // now resolve the unresolved subviews

        for (subView in subViews) {
            val subViewWidth = if (subView.layoutParameters.widthUnits == LayoutParameters.Units.ParentRatio && haveResolvedSize)
                maxWidth - subView.layoutParameters.margins.totalWidth() else MAX_DIMENSION
            val subViewHeight = if (subView.layoutParameters.heightUnits == LayoutParameters.Units.ParentRatio && haveResolvedSize)
                maxHeight - subView.layoutParameters.margins.totalHeight() else MAX_DIMENSION
            subView.measure(subViewWidth, subViewHeight)
        }
        val sizeMeasured = CGSize(subViews.maxWidth(), subViews.maxHeight())
        measuredSize = layoutParameters.resolveSize(CGSize(width, height), sizeMeasured)
    }

    override fun onLayout(newPosition: CGRect, parentHidden: Boolean) {
        super.onLayout(newPosition, parentHidden)
        if (!parentHidden && visible) {
            // position views by gravity
            for (subView in subViews) {
                if (subView.gone) {
                    subView.layout(CGRect.Null(), false)
                    continue
                }
                // use sub view gravity, or our own
                val g = if (subView.layoutParameters.gravity == None) gravity else subView.layoutParameters.gravity
                val size = subView.measuredSize
                val subViewPosition = newPosition.applyInsets(subView.layoutParameters.margins).applyGravity(size, g)
                subView.layout(subViewPosition, false)
            }
        }
    }
}

