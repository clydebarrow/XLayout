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

/**
 * NativeView provides a wrapper around a native view control allowing it to partipate
 * it the XibFree's layout logic
 */

package com.controlj.xibfree

import org.robovm.apple.coreanimation.CALayer
import org.robovm.apple.coregraphics.CGRect
import org.robovm.apple.coregraphics.CGSize
import org.robovm.apple.uikit.UIView
import org.robovm.apple.uikit.UIViewAutoresizing

open class NativeView(view: UIView, layoutParameters: LayoutParameters = LayoutParameters()) : View(layoutParameters) {
    override var layoutParameters: LayoutParameters = layoutParameters
        get() {
            if (view is UILayoutHost)
                return (view as UILayoutHost).layout.layoutParameters
            return super.layoutParameters
        }

    /**
     * The measurer allows customisation of the size. The default implementation does not change it.
     */
    var measurer: ((native: UIView, constraint: CGSize) -> CGSize) = { _, constraint -> constraint }

    /**
     * This is the UIView contained in this object
     */
    var view: UIView = view
        set(value) {
            if (field != value) {
                // Detach old view from host
                host?.apply { onDetach() }

                // Store the new view
                field = value

                // Turn off auto-resizing, we'll take care of that thanks
                field.autoresizingMask = UIViewAutoresizing.None

                // Attach the new view to the host
                host?.apply { onAttach(host!!) }
            }
        }

    override var host: ViewGroup.IHost? = null
        set(value) {
            field?.apply { onDetach() }
            field = value
            value?.apply { onAttach(value) }
        }

    /**
     * Called when a layout pass is being done. onMeasure will have been previously called
     */
    override fun onLayout(newPosition: CGRect, parentHidden: Boolean) {
        logMsg("NativeView(%s) onLayout = %s", view.javaClass.superclass.simpleName, newPosition)
        view.isHidden = parentHidden || !visible
        if (parent != null && parent!!.animate) {
            /*
            if (view.bounds.isEmpty) {
                val frame = bounds
                frame.width = 0.0
                frame.x = 0.0
                view.frame = frame
            } */
            UIView.animate(parent!!.animationDuration,
                    {
                        view.frame = newPosition
                        view.bounds = newPosition
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
        logMsg("NativeView(%s).onMeasure(%g, %g), layoutParameters = %s", view.javaClass.superclass.simpleName, parentWidth, parentHeight, layoutParameters)
        val width = layoutParameters.tryResolveWidth(this, parentWidth, parentHeight)
        val height = layoutParameters.tryResolveHeight(this, parentWidth, parentHeight)
        logMsg("NativeView(%s) after resolve: %g, %g", view.javaClass.superclass.simpleName, width, height)

        // Do we need to measure our content?
        var sizeMeasured = CGSize.Zero()
        if (width == LayoutParameters.MAX_DIMENSION || height == LayoutParameters.MAX_DIMENSION) {
            val size = view.getSizeThatFits(CGSize(width, height))
            logMsg("getSizeThatFits returned %s", size)
            sizeMeasured = measurer(view, size)
        }

        // Set the measured size
        measuredSize = layoutParameters.resolveSize(CGSize(width, height), sizeMeasured)
        logMsg("NativeView(%s) onmeasure = %s", view.javaClass.superclass.simpleName, measuredSize)
    }

    /**
     * Called when this view is to be added to its parent
     */
    override fun onAttach(host: ViewGroup.IHost) {
        // attach the view to the hosting view by adding as a subview
        logMsg("onAttach")
        host.getUIView().addSubview(view)
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
     * Find a native view in the hierarchy with the given tag
     */
    override fun findUIView(tag: Long): UIView? {
        return view.getViewWithTag(tag)
    }

    /**
     * Find a layout view with the given tag in the hierarchy. Can only be this view or nothing.
     */
    override fun findLayoutView(tag: Long): View? {
        if (view.tag == tag)
            return this
        return null
    }

    /**
     * Find the layout view hosting the given UIView. Can only be this view or nothing
     */

    override fun findNativeView(uiView: UIView): NativeView? {
        return if (view == uiView) this else null
    }

    /**
     * Get the view's CALayer
     */
    override fun getDisplayLayer(): CALayer? {
        return view.layer
    }

    override fun findFirstSublayer(): CALayer? {
        return null
    }
}
