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

import com.controlj.shim.CxRect
import com.controlj.shim.CxSize

/**
 * Abstract base class for any item in the layout view hierarchy
 * @param layout The layout parameters for this view. The default is to wrap content
 */
interface View : ViewStateListener {
    companion object {
        const val INVALID_WIDTH = -12345.0
        // replace this with a logger destination
    }


    val indent: Int
        get() {
            var i: Int = 0
            var next = parent
            while (next != null) {
                i++
                next = next.parent
            }
            return i
        }

    var layout: Layout
    /**
     * The frame of this layout. Assigning to this * will layout the view and its subviews.
     * It also determines the size of the background layer, if it exists
     */
    var frame: CxRect

    /**
     * The row in a table that this view is assigned to
     */
    var row: Int
    /**
     * The column in a table that this view is assigned to
     */
    var column: Int

    /**
     * A name mainly for debug use
     */
    var name: String
    /**
     * The parent of this view
     */
    var parent: ViewGroup?

    /**
     * Overridden by subviews to perform the actual measurement
     * @param availableWidth Available width of the parent view group (might be nfloat.MaxValue).
     * @param availableHeight Available height of the parent view group(might be nfloat.MaxValue)</param>
     */
    fun onMeasure(availableWidth: Double, availableHeight: Double)

    /**
     * Mark the measurement of this view as invalid
     */
    fun invalidateMeasure() {
        measuredSize.width = INVALID_WIDTH
    }

    /**
     * Where the measured size is stored after onMeasure
     */
    val measuredSize: CxSize

    /**
     * True if this view is not displayed and takes no space in the layout
     */
    var gone: Boolean

    /**
     * True if this view is currently visible in the hierarchy
     */
    var visible: Boolean

    override fun onShown() {
    }

    override fun onHidden() {
    }

    /**
     * Layout the subviews.
     */

    fun layoutSubviews() {

    }
}
