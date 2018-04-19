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

import org.robovm.apple.coreanimation.CALayer
import org.robovm.apple.coreanimation.CATransaction
import org.robovm.apple.coregraphics.CGRect
import org.robovm.apple.uikit.UIView

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
    var layer: CALayer? = null
        set(value) {
            field?.removeFromSuperlayer()
            field = value
            if (value != null) {
                val ourHost = host
                if (ourHost != null) {
                    val hostView = ourHost.getUIView()
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
    override var host: IHost? = null
        get() {
            if (field == null)
                return parent?.host
            return field
        }
        set(value) {
            if (field != null)
                onDetach()
            field = value
            if (field != null)
                onAttach(field!!)
        }

    /**
     * The set of subviews of this group.  Setting this value will
     * replace the current set. A subview must not currently be a child
     * of another group.
     */

    var childViews: MutableList<View> = ArrayList<View>()
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
    fun insertSubView(position: Int, view: UIView, layout: Layout) {
        insertSubView(position, NativeView(view, layout))
    }

    /**
     * Add a native UIView at the end of the subviews
     * @param view The view to be added
     * @param layout The layout parameters to be used for the subview
     */
    fun addSubView(view: UIView, layout: Layout) {
        insertSubView(-1, view, layout)
    }

    fun addSubView(view: UIView, width: Double, height: Double) {
        addSubView(view, Layout(Layout.Mode.Absolute, Layout.Mode.Absolute, width, height))
    }

    override fun onAttach(host: IHost) {
        if (layer != null)
            host.getUIView().layer.addSublayer(layer)
        for (v in childViews)
            v.onAttach(host)
    }

    override fun onDetach() {
        layer?.removeFromSuperlayer()
        for (v in childViews)
            v.onDetach()
    }

    override fun findFirstSublayer(): CALayer? {
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

    override fun findUIView(tag: Long): UIView? {
        for (v in childViews) {
            val result = v.findUIView(tag)
            if (result != null)
                return result
        }
        return null
    }

    override fun findLayoutView(tag: Long): View? {
        if (tag == this.tag)
            return this
        for (subView in childViews) {
            val result = subView.findLayoutView(tag)
            if (result != null)
                return result
        }
        return null
    }

    override fun findNativeView(uiView: UIView): NativeView? {
        for (subview in childViews) {
            val result = subview.findNativeView(uiView)
            if (result != null)
                return result
        }
        return null
    }

    override fun getDisplayLayer(): CALayer? {
        return layer
    }

    override fun onLayout(newPosition: CGRect, parentHidden: Boolean) {
        val newHidden = parentHidden || !visible
        if (layer != null) {
            if (newHidden != layer!!.isHidden) {
                // changing visibiliy - disable any animations
                CATransaction.begin()
                CATransaction.disablesActions()
                layer!!.isHidden = newHidden
                layer!!.frame = newPosition
                CATransaction.commit()
            } else if (!layer!!.isHidden)
                layer!!.frame = newPosition
        }
        if (newHidden)
            for (subView in childViews)
                subView.layout(CGRect.Null(), false)

    }

    /**
     * Remove a subview by index
     * @param index The index of the view to be removed
     */
    fun removeSubView(index: Int) {
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
    fun removeSubView(view: UIView) {
        for (v in childViews) {
            if (v is NativeView && v.view == view) {
                removeSubView(childViews.indexOf(v))
                break
            }
        }
    }

    fun removeAllSubviews() {
        val cnt = childViews.size
        for (i in cnt - 1..0)
            removeSubView(i)
    }

    interface IHost {
        fun getUIView(): UIView
    }

    override fun onShown() {
        childViews.forEach { it.onShown() }
    }

    override fun onHidden() {
        childViews.forEach { it.onHidden() }
    }
}
