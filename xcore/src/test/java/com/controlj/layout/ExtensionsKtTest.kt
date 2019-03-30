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

import com.controlj.layout.HorizontalLayout.Companion.horizontalLayout
import com.controlj.layout.Layout.Companion.MAX_DIMENSION
import com.controlj.layout.Layout.Companion.absolute
import com.controlj.layout.Layout.Companion.layout
import com.controlj.shim.CxFactory
import com.controlj.shim.MockCxRect
import com.controlj.shim.MockCxSize
import com.controlj.shim.MockUxEdgeInsets
import com.controlj.shim.MockUxView
import com.controlj.utility.Utility
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

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

    @Before
    fun setup() {
        Utility.setup()
    }

    @Test
    fun totalWidth() {
        val uein = CxFactory.uxEdgeInsets(1.0, 20.0, 300.0, 4000.0)
        assertEquals(4020.0, uein.totalWidth(), 0.0)
    }

    @Test
    fun totalHeight() {
        val uein = CxFactory.uxEdgeInsets(1.0, 20.0, 300.0, 4000.0)
        assertEquals(301.0, uein.totalHeight(), 0.0)
    }

    @Test
    fun applyInsets() {
        val rect = CxFactory.cxRect(0.0, 5.0, 100.0, 1000.0)
        assertEquals(0.0, rect.origin.x, 0.0)
        val new = rect.applyInsets(CxFactory.uxEdgeInsets(1.0, 2.0, 3.0, 4.0))
        assertEquals(996.0, new.height, 0.0)
        assertEquals(94.0, new.width, 0.0)
        assertEquals(2.0, new.origin.x, 0.0)
        assertEquals(6.0, new.origin.y, 0.0)
    }

    @Test
    fun maxWidth() {
        val uiview = CxFactory.uxView(CxFactory.cxRect(0.0, 0.0, 20.0, 30.0))
        val nv = NativeView(uiview, Layout(width = 20.0))
        nv.measure(Layout.MAX_DIMENSION, Layout.MAX_DIMENSION)
        val ov = NativeView(CxFactory.uxView(), Layout(width = 15.0))
        nv.measure(Layout.MAX_DIMENSION, Layout.MAX_DIMENSION)
        assertEquals(20.0, listOf(nv, ov).maxWidth(), 0.0)
    }

    @Test
    fun maxHeight() {

        val horz = horizontalLayout {
            layout = layout {
                widthMode = Layout.Mode.MatchParent
                heightMode = Layout.Mode.Absolute
                height = 100.0
                margin = 10.0
            }
            addSubView(MockUxView(), absolute(10.0, 20.0))
            addSubView(MockUxView(), absolute(10.0, 40.0))
            addSubView(MockUxView(), absolute(10.0, 60.0))
        }
        horz.onMeasure(400.0, MAX_DIMENSION)
        assertEquals(400.0, horz.measuredSize.width, 0.0)
        assertEquals(100.0, horz.measuredSize.height, 0.0)
        assertEquals(60.0, horz.childViews.maxHeight(), 0.0)
    }

    @Test
    fun asSizeString() {
        var size = MockCxSize(110.4, -57.1)
        var str = size.asSizeString()
        assertEquals("{110, -57}", str)
        size = MockCxSize(Layout.MAX_DIMENSION, Layout.MAX_DIMENSION)
        str = size.asSizeString()
        assertEquals("{MAX, MAX}", str)
    }

    @Test
    fun applyPadding() {
        val startSize = MockCxSize(100.0, 10.0)
        var newSize = startSize.applyPadding(5.0)
        assertEquals(110.0, newSize.width, 0.0)
        assertEquals(20.0, newSize.height, 0.0)
        newSize = startSize.applyPadding(MockUxEdgeInsets(5.0, 6.0, 7.0, 8.0))
        assertEquals(114.0, newSize.width, 0.0)
        assertEquals(22.0, newSize.height, 0.0)

    }

    @Test
    fun applyGravity() {
        val startRect = MockCxRect(0.0, 0.0, 200.0, 20.0)
        val innerSize = MockCxSize(100.0, 10.0)
        var newRect = startRect.applyGravity(innerSize, Gravity.Center)
        assertEquals(50.0, newRect.minX, 0.0)
        assertEquals(5.0, newRect.minY, 0.0)
        assertEquals(100.0, newRect.width, 0.0)
        assertEquals(10.0, newRect.height, 0.0)

        newRect = startRect.applyGravity(innerSize, Gravity.TopLeft)
        assertEquals(0.0, newRect.minX, 0.0)
        assertEquals(0.0, newRect.minY, 0.0)
        assertEquals(100.0, newRect.width, 0.0)
        assertEquals(10.0, newRect.height, 0.0)

        newRect = startRect.applyGravity(innerSize, Gravity.CenterTop)
        assertEquals(50.0, newRect.minX, 0.0)
        assertEquals(0.0, newRect.minY, 0.0)
        assertEquals(100.0, newRect.width, 0.0)
        assertEquals(10.0, newRect.height, 0.0)

        newRect = startRect.applyGravity(innerSize, Gravity.TopRight)
        assertEquals(100.0, newRect.minX, 0.0)
        assertEquals(0.0, newRect.minY, 0.0)
        assertEquals(100.0, newRect.width, 0.0)
        assertEquals(10.0, newRect.height, 0.0)

        newRect = startRect.applyGravity(innerSize, Gravity.MiddleRight)
        assertEquals(100.0, newRect.minX, 0.0)
        assertEquals(5.0, newRect.minY, 0.0)
        assertEquals(100.0, newRect.width, 0.0)
        assertEquals(10.0, newRect.height, 0.0)

        newRect = startRect.applyGravity(innerSize, Gravity.BottomRight)
        assertEquals(100.0, newRect.minX, 0.0)
        assertEquals(10.0, newRect.minY, 0.0)
        assertEquals(100.0, newRect.width, 0.0)
        assertEquals(10.0, newRect.height, 0.0)

        newRect = startRect.applyGravity(innerSize, Gravity.CenterBottom)
        assertEquals(50.0, newRect.minX, 0.0)
        assertEquals(10.0, newRect.minY, 0.0)
        assertEquals(100.0, newRect.width, 0.0)
        assertEquals(10.0, newRect.height, 0.0)

        newRect = startRect.applyGravity(innerSize, Gravity.BottomLeft)
        assertEquals(0.0, newRect.minX, 0.0)
        assertEquals(10.0, newRect.minY, 0.0)
        assertEquals(100.0, newRect.width, 0.0)
        assertEquals(10.0, newRect.height, 0.0)

        newRect = startRect.applyGravity(innerSize, Gravity.MiddleLeft)
        assertEquals(0.0, newRect.minX, 0.0)
        assertEquals(5.0, newRect.minY, 0.0)
        assertEquals(100.0, newRect.width, 0.0)
        assertEquals(10.0, newRect.height, 0.0)

    }
}