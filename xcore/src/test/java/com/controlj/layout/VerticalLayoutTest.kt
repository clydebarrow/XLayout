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
class VerticalLayoutTest {

    @Before
    fun setup() {
        Utility.setup()
    }

    @Test
    fun testSingle() {

        val layout = VerticalLayout()
        layout.addSubView(MockUxView(0.0, 100.0), Layout(widthMode = Layout.Mode.MatchParent))
        val screenbounds = MockUxScreen.mainScreen.bounds
        layout.onMeasure(screenbounds.width, screenbounds.height)
        assertEquals(100.0, layout.measuredSize.height, 0.0)
        assertEquals(screenbounds.width, layout.measuredSize.width, 0.0)
    }

    @Test
    fun testTwo() {

        val layout = VerticalLayout()
        layout.addSubView(MockUxView(0.0, 100.0), Layout(widthMode = Layout.Mode.MatchParent))
        layout.addDivider()
        layout.addSubView(MockUxView(150.0, 200.0), Layout(widthMode = Layout.Mode.WrapContent))
        val screenbounds = MockUxScreen.mainScreen.bounds
        layout.onMeasure(screenbounds.width, screenbounds.height)
        assertEquals(301.0, layout.measuredSize.height, 0.0)
        assertEquals(screenbounds.width, layout.measuredSize.width, 0.0)
        assertEquals(screenbounds.width, layout.childViews[0].measuredSize.width, 0.0)
        assertEquals(150.0, layout.childViews[2].measuredSize.width, 0.0)
    }

    @Test
    fun weighted() {
        val layout = VerticalLayout(Layout(heightMode = Layout.Mode.MatchParent))
        layout.addSubView(MockUxView(), Layout(heightMode = Layout.Mode.Weighted, weight = 1.0, widthMode = Layout.Mode.MatchParent))
        layout.addDivider()
        layout.addSubView(MockUxView(), Layout(heightMode = Layout.Mode.Weighted, weight = 2.0, widthMode = Layout.Mode.MatchParent))
        val screenbounds = MockUxScreen.mainScreen.bounds
        layout.onMeasure(screenbounds.width, screenbounds.height)
        assertEquals(screenbounds.height, layout.measuredSize.height, 0.0)
        assertEquals(screenbounds.width, layout.measuredSize.width, 0.0)
        assertEquals(screenbounds.width, layout.childViews[0].measuredSize.width, 0.0)
        assertEquals(screenbounds.width, layout.childViews[2].measuredSize.width, 0.0)
        assertEquals((screenbounds.height - 1.0) / 3, layout.childViews[0].measuredSize.height, 0.1)
    }

    @Test
    fun aspect() {
        val layout = VerticalLayout(Layout(heightMode = Layout.Mode.MatchParent))
        layout.addSubView(MockUxView(), Layout(heightMode = Layout.Mode.Weighted, weight = 1.0, widthMode = Layout.Mode.MatchParent))
        layout.addSubView(MockUxView(), Layout(heightMode = Layout.Mode.Aspect, aspectRatio = 0.5, widthMode = Layout.Mode.MatchParent))
        val screenbounds = MockUxScreen.mainScreen.bounds
        layout.onMeasure(screenbounds.width, screenbounds.height)
        assertEquals(screenbounds.height, layout.measuredSize.height, 0.0)
        assertEquals(screenbounds.width, layout.measuredSize.width, 0.0)
        assertEquals(screenbounds.width, layout.childViews[0].measuredSize.width, 0.0)
        assertEquals(screenbounds.width, layout.childViews[1].measuredSize.width, 0.0)
        assertEquals(screenbounds.width * .5, layout.childViews[1].measuredSize.height, 0.1)

    }
}