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

import com.controlj.shim.CxTransaction
import com.controlj.shim.CxFactory
import com.controlj.shim.CxLayer
import com.controlj.shim.CxRect
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
abstract class ViewGroup(layout: Layout = Layout(), vararg views: View) : View(layout) {
    init {
        addSubViews(*views)
    }

    /**
     * The drawing layer
     */
    var layer: CxLayer? = null
        set(value) {
            field?.removeFromSuperlayer()
            field = value
            if (value != null) {
                val ourHost = host
                if (ourHost != null) {
                    val hostView = ourHost.getUxView()
                    val nextLayer = findFirstSublayer()
                    if (nextLayer != null)
                        hostView.layer.insertSublayerBelow(value, nextLayer)
                    else
                        hostView.layer.addSublayer(value)

                }
            }
        }
    /**
     * A tag that identifies this view
     */
    var tag: Long = 0

    /**
     * The native host for this view hierarchy
     */
    public override var host: IHost? = null
        get() {
            if (field == null)
                return parent?.host
            return field
        }
        set(value) {
            if (field != null)
                onDetach()
            field = value
            if (value != null)
                onAttach(value)
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
    fun insertSubView(position: Int, view: View) {
        if (view.parent != null)
            throw IllegalArgumentException("View is already a child of another ViewGroup")
        view.parent = this
        if (position < 0 || position >= childViews.count())
            childViews.add(view)
        else
            childViews.add(position, view)
    }

    /**
     * Add a subview at the end of the current list.
     * @param view The view to be added
     */
    fun addSubView(view: View) {
        insertSubView(-1, view)
    }

    /**
     * Add a collection of Views
     * @param views The collection of subviews to be added
     */

    fun addSubViews(vararg views: View) {
        views.forEach {
            addSubView(it)
        }
    }

    /**
     * Insert a native UIView at a given position.
     * @param position The desired position. If this is negative or greater than the current number of subviews, the view will be appended to the list
     * @param view The view to be added
     * @param layout The layout parameters to be used for the subview
     */
    fun insertSubView(position: Int, view: UxView, layout: Layout): NativeView {
        val nativeView = NativeView(view, layout)
        insertSubView(position, nativeView)
        return nativeView
    }

    /**
     * Add a native UIView at the end of the subviews
     * @param view The view to be added
     * @param layout The layout parameters to be used for the subview
     */
    fun addSubView(view: UxView, layout: Layout = Layout()): NativeView {
        return insertSubView(-1, view, layout)
    }

    fun addSubView(view: UxView, width: Double, height: Double) {
        addSubView(view, Layout(width, Layout.Mode.Absolute, height, Layout.Mode.Absolute))
    }

    override fun onAttach(host: IHost) {
        if (layer != null)
            host.getUxView().layer.addSublayer(layer)
        for (v in childViews)
            v.onAttach(host)
    }

    override fun onDetach() {
        layer?.removeFromSuperlayer()
        for (v in childViews)
            v.onDetach()
    }

    override fun findFirstSublayer(): CxLayer? {
        for (v in childViews) {
            val l = v.getDisplayLayer()
            if (l != null)
                return l
            val r = v.findFirstSublayer()
            if (r != null)
                return r
        }
        return null
    }

    override fun findNativeView(uxView: UxView): NativeView? {
        for (subview in childViews) {
            val result = subview.findNativeView(uxView)
            if (result != null)
                return result
        }
        return null
    }

    override fun getDisplayLayer(): CxLayer? {
        return layer
    }

    override fun onLayout(newPosition: CxRect, parentHidden: Boolean) {
        val newHidden = parentHidden || !visible
        layer?.apply {
            CxTransaction.begin()
            if (newHidden != isHidden) {
                // changing visibiliy - disable any animations
                CxTransaction.disablesActions()
                isHidden = newHidden
                frame = newPosition
            } else if (!isHidden)
                frame = newPosition
            CxTransaction.commit()
        }
        if (newHidden) {
            indent += 4
            childViews.forEach { subView ->
                subView.performLayout(CxFactory.cxRect(), false)
            }
            indent -= 4
        }
    }

    /**
     * Remove a subview by index
     * @param index The index of the view to be removed
     */
    fun removeSubView(index: Int) {
        childViews[index].onDetach()
        childViews.removeAt(index)
    }

    /**
     * remove a specific view
     * @param view The view to be removed
     */
    fun removeSubView(view: View) {
        removeSubView(childViews.indexOf(view))
    }

    /**
     * Remove a view wrapping the specified UIView
     * @param view The UIView whose wrapping View should be removed
     */
    fun removeSubView(view: UxView) {
        for (v in childViews) {
            if (v is NativeView && v.view == view) {
                removeSubView(childViews.indexOf(v))
                break
            }
        }
    }

    /**
     * Remove all subviews
     */
    fun removeAllSubviews() {
        val cnt = childViews.size
        for (i in cnt - 1..0)
            removeSubView(i)
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

    open fun addDivider(thickness: Double = 1.0, color: UxColor = UxColor.darkGray()): NativeView {
        val view = CxFactory.uxView()
        view.backgroundColor = color
        return addSubView(view, dividerLayout(thickness))
    }

    open fun dividerLayout(thickness: Double): Layout = Layout.layout { name = "Divider" }
}
