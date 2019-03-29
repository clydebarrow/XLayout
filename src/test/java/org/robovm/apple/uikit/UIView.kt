package org.robovm.apple.uikit

import org.robovm.apple.coregraphics.CGPoint
import org.robovm.apple.coregraphics.CGRect
import org.robovm.apple.coregraphics.CGSize

/**
 * Copyright (C) Control-J Pty. Ltd. ACN 103594190
 * All rights reserved
 *
 * User: clyde
 * Date: 2019-03-29
 * Time: 18:01
 */
open class UIView(var frame: CGRect = CGRect()) {

    fun getSizeThatFits(size: CGSize): CGSize {
        val width = if (intrinsicContentSize.width == 0.0) size.width else intrinsicContentSize.width
        val height = if (intrinsicContentSize.height == 0.0) size.height else intrinsicContentSize.height
        return CGSize(width, height)
    }

    open var backgroundColor = UIColor.clear()

    open val bounds: CGRect
        get() = CGRect(CGPoint(0.0, 0.0), frame.size)
    open var intrinsicContentSize = CGSize(0.0, 0.0)

    companion object {
        fun sized(width: Double = 0.0, height: Double = 0.0): UIView {
            val view = UIView()
            view.intrinsicContentSize.width = width
            view.intrinsicContentSize.height = height
            return view
        }
    }
}

