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

import com.controlj.shim.MockUxView
import com.controlj.utility.Utility
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.robovm.apple.uikit.MockUxScreen

/**
 * Copyright (C) Control-J Pty. Ltd. ACN 103594190
 * All rights reserved
 *
 * User: clyde
 * Date: 2019-03-29
 * Time: 19:46
 */
class VerticalGroupTest {

    @Before
    fun setup() {
        Utility.setup()
    }

    @Test
    fun testSingle() {

        val layout = VerticalGroup()
        layout.add(MockUxView(0.0, 100.0, Layout(widthMode = Layout.Mode.MatchParent)))
        val screenbounds = MockUxScreen.mainScreen.bounds
        layout.onMeasure(screenbounds.width, screenbounds.height)
        assertEquals(100.0, layout.measuredSize.height, 0.0)
        assertEquals(screenbounds.width, layout.measuredSize.width, 0.0)
    }

    @Test
    fun testTwo() {

        val layout = VerticalGroup()
        val screenbounds = MockUxScreen.mainScreen.bounds
        layout.add(MockUxView(10.0, 100.0, Layout(widthMode = Layout.Mode.MatchParent)))
        layout.addDivider()
        layout.add(MockUxView(150.0, 200.0, Layout(widthMode = Layout.Mode.WrapContent)))
        layout.onMeasure(screenbounds.width, screenbounds.height)
        assertEquals(301.0, layout.measuredSize.height, 0.0)
        assertEquals(150.0, layout.measuredSize.width, 0.0)
        assertEquals(10.0, layout.childViews[0].measuredSize.width, 0.0)
        assertEquals(150.0, layout.childViews[2].measuredSize.width, 0.0)
    }

    @Test
    fun weighted() {
        val layout = VerticalGroup()
        val top = MockUxView(100.0, 100.0, layout = Layout(widthMode = Layout.Mode.MatchParent, heightMode = Layout.Mode.Weighted, weight = 1.0))
        top.layout.margin = 2.0
        layout.add(top)
        layout.addDivider()
        val bottom = (MockUxView(50.0, 50.0, layout = Layout(widthMode = Layout.Mode.MatchParent, heightMode = Layout.Mode.Weighted, weight = 3.0)))
        bottom.layout.margin = 5.0
        layout.add(bottom)
        val screenbounds = MockUxScreen.mainScreen.bounds
        layout.onMeasure(screenbounds.width, screenbounds.height)
        assertEquals(screenbounds.height, layout.measuredSize.height, 0.0)
        assertEquals(104.0, layout.measuredSize.width, 0.0)
        assertEquals(100.0, layout.childViews[0].measuredSize.width, 0.0)
        assertEquals(50.0, layout.childViews[2].measuredSize.width, 0.0)
        assertEquals(100.0, layout.childViews[0].measuredSize.height, 0.1)
        assertEquals(100.0, layout.childViews[0].measuredSize.height, 0.1)

        layout.frame = screenbounds
        layout.layoutSubviews()
        assertEquals(2.0, top.frame.minX, 0.0)
        assertEquals(766.0, top.frame.maxX, 0.0)
        assertEquals(5.0, bottom.frame.minX, 0.0)
        assertEquals(763.0, bottom.frame.maxX, 0.0)

        assertEquals(2.0, top.frame.minY, 0.0)
        assertEquals(254.25, top.frame.maxY, 0.0)
        assertEquals(1019.0, bottom.frame.maxY, 0.0)
    }

}