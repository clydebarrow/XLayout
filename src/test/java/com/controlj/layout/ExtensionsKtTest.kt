package com.controlj.layout

import org.junit.Assert.assertEquals
import org.junit.Test
import org.robovm.apple.coregraphics.CGRect
import org.robovm.apple.uikit.UIEdgeInsets
import org.robovm.apple.uikit.UIView

/**
 * Copyright (C) Control-J Pty. Ltd. ACN 103594190
 * All rights reserved
 *
 *
 * User: clyde
 * Date: 2019-03-29
 * Time: 15:54
 */
class ExtensionsKtTest {

    @Test
    fun totalWidth() {
        val uein = UIEdgeInsets(1.0, 20.0, 300.0, 4000.0)
        assertEquals(4020.0, uein.totalWidth(), 0.0)
    }

    @Test
    fun totalHeight() {
        val uein = UIEdgeInsets(1.0, 20.0, 300.0, 4000.0)
        assertEquals(301.0, uein.totalHeight(), 0.0)
    }

    @Test
    fun applyInsets() {
        val rect = CGRect(0.0, 5.0, 100.0, 1000.0)
        assertEquals(0.0, rect.origin.x, 0.0)
        val new = rect.applyInsets(UIEdgeInsets(1.0, 2.0, 3.0, 4.0))
        assertEquals(996.0, new.height, 0.0)
        assertEquals(94.0, new.width, 0.0)
        assertEquals(2.0, new.origin.x, 0.0)
        assertEquals(6.0, new.origin.y, 0.0)
    }

    @Test
    fun maxWidth() {
        val uiview = UIView(CGRect(0.0, 0.0, 20.0, 30.0))
        val nv = NativeView(uiview, Layout(width = 20.0))
        nv.measure(Layout.MAX_DIMENSION, Layout.MAX_DIMENSION)
        val ov = NativeView(UIView(), Layout(width = 15.0))
        nv.measure(Layout.MAX_DIMENSION, Layout.MAX_DIMENSION)
        assertEquals(20.0, listOf(nv, ov).maxWidth(), 0.0)
    }

    @Test
    fun maxHeight() {
    }

    @Test
    fun asSizeString() {
    }

    @Test
    fun applyPadding() {
    }

    @Test
    fun applyPadding1() {
    }

    @Test
    fun applyGravity() {
    }

    @Test
    fun selectAll() {
    }
}