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

import org.robovm.apple.coreanimation.CALayer
import org.robovm.apple.coregraphics.CGRect
import org.robovm.apple.coregraphics.CGSize
import org.robovm.apple.uikit.UIView

/**
 * Abstract base class for any item in the layout view hierarchy
 * @param layoutParameters The layout parameters for this view. The default is to wrap content
 */
abstract class View(open var layoutParameters: LayoutParameters = LayoutParameters()): ViewStateListener {
    companion object {
        @JvmStatic
        fun logMsg(format: String, vararg args: Any) {
            System.err.println(String.format(format, *args))
        }
    }

    enum class Visibility {
        VISIBLE,
        INVISIBLE,
        GONE
    }

    /**
     * The row in a table that this view is assigned to
     */
    var row: Int = 0
    /**
     * The column in a table that this view is assigned to
     */
    var column: Int = 0

    /**
     * The parent of this view
     */
    var parent: ViewGroup? = null
        set(value) {
            if (field != value) {
                // Detach old host
                if (host != null)
                    onDetach()

                // Store new parent
                field = value

                // Attach to new host
                if (host != null)
                    onAttach(host!!)
            }
        }

    /**
     * Used to find the UIView hosting this View
     */
    internal open var host: ViewGroup.IHost?
        get() = parent!!.host
        set(value) {
            throw IllegalArgumentException("Can't set host on plain view")
        }


    /**
     * Internal notification that this view has been attached to a hosting view
     */
    override fun onAttach(host: ViewGroup.IHost) {
    }

    /**
     * Internal notification that this view has been detached from a hosting view
     */
    override fun onDetach() {
    }

    /**
     * Layout the subviews in this view using dimensions calculated during the last measure cycle
     * @param newPosition The new bounding rectangle of the view
     * @param parentHidden True if the parent is currently hidden
     */
    fun layout(newPosition: CGRect, parentHidden: Boolean) {
        onLayout(newPosition, parentHidden)
    }

    /**
     * Overridden by subclasses to perform the actual layout process
     * @param newPosition The new bounding rectangle of the view
     * @param parentHidden True if the parent is currently hidden
     */
    internal abstract fun onLayout(newPosition: CGRect, parentHidden: Boolean)

    /**
     * Measure the subviews of this view
     * @param parentWidth Available width of the parent view group (might be nfloat.MaxValue).
     * @param parentHeight Available height of the parent view group(might be nfloat.MaxValue)</param>
     */
    fun measure(parentWidth: Double, parentHeight: Double): CGSize {
        invalidateMeasure()
        onMeasure(parentWidth, parentHeight)
        if (measuredSize.width < 0)
            throw IllegalStateException("onMeasure didn't set measurement before returning")
        return measuredSize
    }

    /**
     * Overridden by subviews to perform the actual measurement
     * @param parentWidth Available width of the parent view group (might be nfloat.MaxValue).
     * @param parentHeight Available height of the parent view group(might be nfloat.MaxValue)</param>
     */
    internal abstract fun onMeasure(parentWidth: Double, parentHeight: Double)

    /**
     * Mark the measurement of this view as invalid
     */
    fun invalidateMeasure() {
        measuredSize.width = -1.0
    }

    /**
     * Where the measured size is stored after onMeasure
     */
    internal var measuredSize: CGSize = CGSize(-1.0, -1.0)
        set(size) {
            if (layoutParameters.minWidth != 0.0 && size.width < layoutParameters.minWidth)
                size.width = layoutParameters.minWidth
            if (layoutParameters.minHeight != 0.0 && size.height < layoutParameters.minHeight)
                size.height = layoutParameters.minHeight
            if (layoutParameters.maxWidth != 0.0 && size.width > layoutParameters.maxWidth)
                size.width = layoutParameters.maxWidth
            if (layoutParameters.maxHeight != 0.0 && size.height > layoutParameters.maxHeight)
                size.height = layoutParameters.maxHeight
            field = size
        }

    internal abstract fun getDisplayLayer(): CALayer?
    internal abstract fun findFirstSublayer(): CALayer?

    /**
     * Find a child UIView with the given tag.
     * @param tag The tag to be searched for
     * @return The UIView if a matching view with tag is found, othewise null
     */
    internal abstract fun findUIView(tag: Long): UIView?

    /**
     * Find a child layout View with the given tag
     * @param tag The tag to be searched for
     * @return The child with matching tag if found, else null
     */
    internal abstract fun findLayoutView(tag: Long): View?

    /**
     * Find the wrapper view for the given UIView
     * @param  uiView A UIView of interest
     * @return a NativeView wrapping the given UIView if found, else null
     */
    abstract fun findNativeView(uiView: UIView): NativeView?

    /**
     * True if this view is not displayed and takes no space in the layout
     */
    var gone: Boolean
        get() = layoutParameters.visibility == Visibility.GONE
        set(value) {
            layoutParameters.visibility = if (value) Visibility.GONE else Visibility.VISIBLE
        }

    /**
     * True if this view is currently visible in the hierarchy
     */
    var visible: Boolean
        get() = layoutParameters.visibility == Visibility.VISIBLE
        set(value) {
            layoutParameters.visibility = if (value) Visibility.VISIBLE else Visibility.INVISIBLE
        }

    /**
     * Remove the view from its parent
     */
    fun removeFromSuperview() {
        parent?.removeSubView(this)
    }

    var animate: Boolean = false
    var animationDuration: Double = 0.0

    override fun onShown() {
    }

    override fun onHidden() {
    }
}
