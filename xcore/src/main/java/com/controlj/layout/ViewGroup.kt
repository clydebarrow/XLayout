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

import com.controlj.shim.CxFactory
import com.controlj.shim.CxLayer
import com.controlj.shim.CxRect
import com.controlj.shim.CxSize
import com.controlj.shim.UxColor
import com.controlj.shim.UxView

/**
 * Created by clyde on 12/2/18.
 *
 * This class maintains a collection of subviews. Implementations of this class will layout those subviews according
 * to some algorithm.
 * @param layout The layout parameters to be applied to this group. Default is to match the parent.
 * @constructor Creates a group with the supplied list of subviews
 */
abstract class ViewGroup(override var layout: Layout = Layout(), override var name: String = "") : View {

    override var row: Int = 0
    override var column: Int = 0
    override var parent: ViewGroup? = null
    override var gone: Boolean = false
    override var visible: Boolean = true
    override val measuredSize: CxSize = CxFactory.cxSize()
    /**
     * The drawing layer for this [ViewGroup]
     * This allows backgrounds etc to be drawn for ViewGroups.
     */
    val layer: CxLayer = CxFactory.create()

    /**
     * A tag that identifies this view
     */
    var tag: Long = 0

    override var frame: CxRect = CxFactory.cxRect()
        set(value) {
            require(value.width >= 0.0 && value.height >= 0.0, { "$name: Negative frame size $value in $name" })
            field = value
            layer.frame = value
        }
    /**
     * The set of subviews of this group.  Setting this value will
     * replace the current set. A subview must not currently be a child
     * of another group.
     */

    var childViews: MutableList<View> = mutableListOf()
        set(value) {
            for (v in value)
                if (v.parent != null)
                    throw IllegalArgumentException("View is already a child of another ViewGroup")
            for (v in value)
                v.parent = this
            val newList = ArrayList<View>()
            newList.addAll(value)
            field = newList
        }

    /**
     * Insert a View at a given position.
     * @param position The desired position. If this is negative or greater than the current number of subviews, the view will be appended to the list
     * @param view The view to be added
     */
    fun insert(view: View, position: Int): View {
        if (view.parent != null)
            throw IllegalArgumentException("View is already a child of another ViewGroup (${view.parent}")
        view.parent = this
        if (position < 0 || position >= childViews.count())
            childViews.add(view)
        else
            childViews.add(position, view)
        return view
    }

    /**
     * Add a subview at the end of the current list.
     * @param view The view to be added
     */
    fun add(vararg views: View) {
        views.forEach { view ->
            insert(view, -1)
        }
    }

    override fun onAttach(host: UxHost) {
        host.addSublayer(layer)
        childViews.forEach { it.onAttach(host) }
    }

    override fun onDetach() {
        layer.removeFromSuperlayer()
        childViews.forEach { it.onDetach() }
    }

    /**
     * Remove a subview by index
     * @param index The index of the view to be removed
     */
    fun removeAt(index: Int) {
        childViews[index].onDetach()
        childViews[index].parent = null
        childViews.removeAt(index)
    }

    /**
     * remove a specific view
     * @param view The view to be removed
     */
    fun remove(view: View) {
        removeAt(childViews.indexOf(view))
    }

    /**
     * Remove all subviews
     */
    fun removeAll() {
        childViews.reversed().forEach { remove(it) }
    }

    /**
     * Called when the layout is shown
     */
    override fun onShown() {
        childViews.forEach { it.onShown() }
    }

    /**
     * Called when the layout is hidden
     */
    override fun onHidden() {
        childViews.forEach { it.onHidden() }
    }

    /**
     * Add a divider with the specified [thickness] and [color]
     */

    open fun addDivider(thickness: Double = 1.0, color: UxColor = UxColor.darkGray()): UxView {
        val view = CxFactory.uxView()
        view.layout = dividerLayout(thickness)
        view.name = "Divider"
        view.backgroundColor = color
        add(view)
        return view
    }

    /**
     * Mark the view as needing redrawing
     */

    fun invalidate() {
        layer.invalidate()
    }

    open fun dividerLayout(thickness: Double): Layout = Layout.layout { name = "Divider" }
}
