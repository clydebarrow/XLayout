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

import com.controlj.shim.CxPoint
import com.controlj.shim.CxRect
import com.controlj.shim.CxSize

/**
 * Abstract base class for any item in the layout view hierarchy
 */
interface View : ViewStateListener {
    companion object {
        const val INVALID_WIDTH = -12345.0
        // replace this with a logger destination
    }

    /**
     * Enumerates the events a view might listen for
     */
    enum class Event {
        TAP,
        PRESS,      // i.e. long tap
        DOUBLE_TAP,
        TOUCH,
        FLING,
        ZOOM
    }

    /**
     * The set of events this view would like passed to it.
     */

    val events: Set<Event>

    /**
     * the layout for this view.
     */
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

    override fun onShown() = Unit

    override fun onHidden() = Unit

    /**
     * Post a tap on this view. Returns true if the view handled the tap, otherwise it will
     * be passed up to its parent
     *
     * @param position
     * @return true if tap has been consumed
     */
    fun onTap(position: CxPoint): Boolean = false

    /**
     * Post a double tap on this view. Returns true if the view handled the tap, otherwise it will
     * be passed up to its parent. Note that onTap will already have been called before this one
     *
     * @param position
     * @return true if tap has been consumed
     */
    fun onDoubleTap(position: CxPoint): Boolean = false

    /**
     * Deliver a long press event to the view. This will be called twice - once
     * when the gesture is first recognised, once when it ends
     *
     * @param position The position of the press
     * @param ended True if the gesture has ended
     */
    fun onPress(position: CxPoint, ended: Boolean): Boolean = false

    /**
     * Deliver a touch event to the view. This is intended to allow data to be highlighted
     * e.g. to show what is about to be dragged etc.
     *
     * @param position The position of the touch
     * @param down True if the finger is down
     */
    fun onTouch(position: CxPoint, down: Boolean) = Unit

    /**
     * Deliver a fling event to the view.
     *
     * @param position The starting position of the fling gesture
     * @param velocityX The x velocity in pixels/second
     * @param velocityY The y velocity in pixels/second
     * @return true if the gesture was consumed
     */
    fun onFling(position: CxPoint, velocityX: Double, velocityY: Double): Boolean = false

    /**
     * Deliver a zoom (pinch) gesture. Values delivered are cumulative
     *
     * @param position The center position of the gesture
     * @param deltaX The x scale of the zoom since the last call to this function
     * @param deltaY The y scale of the zoom since the last call to this function
     */
    fun onZoom(position: CxPoint, deltaX: Double, deltaY: Double): Boolean = false

    /**
     * Layout the subviews.
     */

    fun layoutSubviews() = Unit

    fun debugString(): String {
        return "$name: frame=$frame, measured=$measuredSize, layout=$layout"
    }

    // pretty-print indent for debug purposes.
    val indent: Int
        get() {
            var i = 0
            var next = parent
            while (next != null) {
                i++
                next = next.parent
            }
            return i
        }

}
