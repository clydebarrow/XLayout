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

import com.controlj.shim.MockCxFactory
import com.controlj.shim.MockCxTransaction
import com.controlj.shim.MockUxView
import com.controlj.utility.Utility
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.robovm.apple.uikit.MockUxScreen

class HorizontalLayoutTest {

    @Before
    fun setup() {
        Utility.setup()
    }

    @Test
    fun testSingle() {

        val layout = HorizontalLayout(Layout(heightMode = Layout.Mode.MatchParent))
        layout.addSubView(MockUxView(100.0, 0.0), Layout(heightMode = Layout.Mode.MatchParent))
        val screenbounds = MockUxScreen.mainScreen.bounds
        layout.onMeasure(screenbounds.width, screenbounds.height)
        assertEquals(100.0, layout.measuredSize.width, 0.0)
        assertEquals(screenbounds.height, layout.measuredSize.height, 0.0)
    }

    @Test
    fun testTwo() {

        val layout = HorizontalLayout()
        layout.addSubView(MockUxView(100.0, 0.0), Layout(heightMode = Layout.Mode.MatchParent))
        layout.addDivider()
        layout.addSubView(MockUxView(200.0, 150.0), Layout(heightMode = Layout.Mode.WrapContent))
        val screenbounds = MockUxScreen.mainScreen.bounds
        layout.onMeasure(screenbounds.width, screenbounds.height)
        assertEquals(301.0, layout.measuredSize.width, 0.0)
        assertEquals(150.0, layout.measuredSize.height, 0.0)
        assertEquals(150.0, layout.childViews[0].measuredSize.height, 0.0)
        assertEquals(150.0, layout.childViews[2].measuredSize.height, 0.0)
    }

    @Test
    fun weighted() {
        val layout = HorizontalLayout(Layout(widthMode = Layout.Mode.MatchParent, heightMode = Layout.Mode.MatchParent))
        layout.addSubView(MockUxView(), Layout(widthMode = Layout.Mode.Weighted, weight = 1.0, heightMode = Layout.Mode.MatchParent))
        layout.addDivider()
        layout.addSubView(MockUxView(), Layout(widthMode = Layout.Mode.Weighted, weight = 2.0, heightMode = Layout.Mode.MatchParent))
        val screenbounds = MockUxScreen.mainScreen.bounds
        layout.onMeasure(screenbounds.width, screenbounds.height)
        assertEquals(screenbounds.width, layout.measuredSize.width, 0.0)
        assertEquals(screenbounds.height, layout.measuredSize.height, 0.0)
        assertEquals(screenbounds.height, layout.childViews[0].measuredSize.height, 0.0)
        assertEquals(screenbounds.height, layout.childViews[2].measuredSize.height, 0.0)
        assertEquals((screenbounds.width - 1.0) / 3, layout.childViews[0].measuredSize.width, 0.1)
    }

    @Test
    fun aspect() {
        val layout = HorizontalLayout(Layout(widthMode = Layout.Mode.MatchParent, heightMode = Layout.Mode.MatchParent))
        layout.addSubView(MockUxView(), Layout(widthMode = Layout.Mode.Weighted, weight = 1.0, heightMode = Layout.Mode.MatchParent))
        layout.addSubView(MockUxView(), Layout(widthMode = Layout.Mode.Aspect, aspectRatio = 0.5, heightMode = Layout.Mode.MatchParent))
        val screenbounds = MockUxScreen.mainScreen.bounds
        layout.onMeasure(screenbounds.width, screenbounds.height)
        assertEquals(screenbounds.height, layout.measuredSize.height, 0.0)
        assertEquals(screenbounds.width, layout.measuredSize.width, 0.0)
        assertEquals(screenbounds.height, layout.childViews[0].measuredSize.height, 0.0)
        assertEquals(screenbounds.height, layout.childViews[1].measuredSize.height, 0.0)
        assertEquals(screenbounds.height * .5, layout.childViews[1].measuredSize.width, 0.1)

    }
}