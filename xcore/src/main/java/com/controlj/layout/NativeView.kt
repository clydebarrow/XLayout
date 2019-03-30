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

/**
 * NativeView provides a wrapper around a native view control allowing it to partipate
 * it the XibFree's layout logic
 */

package com.controlj.layout

import com.controlj.shim.CxFactory
import com.controlj.shim.CxLayer
import com.controlj.shim.CxRect
import com.controlj.shim.CxSize
import com.controlj.shim.UxView

open class NativeView(view: UxView, layout: Layout = Layout()) : View(layout) {
    /**
     * The measurer allows customisation of the size. The default implementation does not change it.
     */
    var measurer: ((native: UxView, constraint: CxSize) -> CxSize) = { _, constraint -> constraint }

    /**
     * This is the UIView contained in this object
     */
    var view: UxView = view
        set(value) {
            if (field != value) {
                // Detach old view from host
                if (host != null)
                    onDetach()

                // Store the new view
                field = value

                // Turn off auto-resizing, we'll take care of that thanks
                field.autoresizingMask = 0

                // Attach the new view to the host
                host?.let { onAttach(it) }
            }
        }

    override var host: IHost? = null
        set(value) {
            field?.apply { onDetach() }
            field = value
            value?.apply { onAttach(value) }
        }

    /**
     * Called when a layout pass is being done. onMeasure will have been previously called
     */
    override fun onLayout(newPosition: CxRect, parentHidden: Boolean) {
        logMsg("$name: NativeView(%s) onLayout = %s", view.javaClass.simpleName, newPosition)
        view.hided = parentHidden || !visible
        if (parent != null && parent!!.animate) {
            /*
            if (view.bounds.isEmpty) {
                val frame = bounds
                frame.width = 0.0
                frame.x = 0.0
                view.frame = frame
            } */
            UxView.animate(parent!!.animationDuration,
                    {
                        view.frame = newPosition
                    })
            return
        }
        view.frame = newPosition
    }

    /**
     * Calculate the preferred size for this view
     */
    override fun onMeasure(parentWidth: Double, parentHeight: Double) {
        // Resolve width for absolute and parent ratio
        logMsg("$name: NativeView(%s).onMeasure(%s, %s), layoutParameters = %s", view.javaClass.superclass.simpleName, Layout.dimToString(parentWidth), Layout.dimToString(parentHeight), layout)
        val width = layout.tryResolveWidth(parentWidth)
        val height = layout.tryResolveHeight(parentHeight)
        logMsg("$name: NativeView(%s) after resolve: %s, %s", view.javaClass.simpleName, Layout.dimToString(width), Layout.dimToString(height))

        // Do we need to measure our content?
        var sizeMeasured = CxFactory.cxSize()
        if (width == Layout.MAX_DIMENSION || height == Layout.MAX_DIMENSION) {
            val size = view.getSizeThatFits(CxFactory.cxSize(width, height))
            logMsg("$name: getSizeThatFits returned %s", size)
            sizeMeasured = measurer(view, size)
        }

        // Set the measured size
        measuredSize = layout.resolveSize(CxFactory.cxSize(width, height), sizeMeasured)
        logMsg("NativeView(%s) measuredSize = %s", view.javaClass.superclass.simpleName, measuredSize)
    }

    /**
     * Called when this view is to be added to its parent
     */
    override fun onAttach(host: IHost) {
        // attach the view to the hosting view by adding as a subview
        //logMsg("onAttach")
        host.getUxView().addSubview(view)
    }

    /**
    Overridden to remove this native view from the parent native view
     */
    override fun onDetach() {
        // remove from the hosting view by removing it from the superview
        logMsg("Ondetach")
        view.removeFromSuperview()
    }

    /**
     * Find the layout view hosting the given UIView. Can only be this view or nothing
     */

    override fun findNativeView(uxView: UxView): NativeView? {
        return if (view == uxView) this else null
    }

    /**
     * Get the view's CALayer
     */
    override fun getDisplayLayer(): CxLayer? {
        return view.layer
    }

    override fun findFirstSublayer(): CxLayer? {
        return null
    }

    companion object {
        fun nativeView(view: UxView, layout: Layout = Layout(), config: NativeView.() -> Unit = {}): NativeView {
            return NativeView(view, layout).apply(config)
        }

        fun nativeView(view: UxView, width: Double, height: Double, config: NativeView.() -> Unit = {}): NativeView {
            return NativeView(view, Layout.absolute(width, height)).apply(config)
        }
    }
}
