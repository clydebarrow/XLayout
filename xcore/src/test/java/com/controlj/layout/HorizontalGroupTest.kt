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

import com.controlj.shim.MockCxRect
import com.controlj.shim.MockUxView
import com.controlj.utility.Utility
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.robovm.apple.uikit.MockUxScreen

class HorizontalGroupTest {

    @Before
    fun setup() {
        Utility.setup()
    }

    @Test
    fun testSingle() {

        val layout = HorizontalGroup()
        layout.add(MockUxView(100.0, 0.0, Layout(heightMode = Layout.Mode.MatchParent)))
        val screenbounds = MockUxScreen.mainScreen.bounds
        layout.onMeasure(screenbounds.width, screenbounds.height)
        layout.frame = screenbounds
        layout.layoutSubviews()
        assertEquals(100.0, layout.measuredSize.width, 0.0)
        assertEquals(screenbounds.height, layout.childViews.first().frame.height, 0.0)
    }

    @Test
    fun testNested() {

        val screenbounds = MockUxScreen.mainScreen.bounds
        val layout = HorizontalGroup()
        layout.add(MockUxView(100.0, 0.0, Layout(heightMode = Layout.Mode.MatchParent)))
        val inner = HorizontalGroup(Layout(widthMode = Layout.Mode.Weighted, heightMode = Layout.Mode.MatchParent))

        val left = MockUxView(50.0, 50.0, Layout(widthMode = Layout.Mode.Weighted))
        val right = MockUxView(50.0, 50.0, Layout())
        inner.add(left, right)
        layout.add(inner)
        layout.onMeasure(screenbounds.width, screenbounds.height)
        layout.frame = screenbounds
        layout.layoutSubviews()
        assertEquals(inner.frame.width, screenbounds.width - 100.0, 0.0)
        assertEquals(inner.frame.width, screenbounds.width - 100.0, 0.0)
        assertEquals(right.frame.width, 50.0, 0.0)
        assertEquals(left.frame.width, screenbounds.width - 100.0 - 50.0, 0.0)
    }

    @Test
    fun testTwo() {

        val layout = HorizontalGroup()
        layout.add(MockUxView(100.0, 1.0, Layout(heightMode = Layout.Mode.MatchParent)))
        layout.addDivider()
        layout.add(MockUxView(200.0, 150.0, Layout(heightMode = Layout.Mode.WrapContent)))
        val screenbounds = MockUxScreen.mainScreen.bounds
        layout.onMeasure(screenbounds.width, screenbounds.height)
        assertEquals(301.0, layout.measuredSize.width, 0.0)
        assertEquals(150.0, layout.measuredSize.height, 0.0)
        assertEquals(150.0, layout.childViews[2].measuredSize.height, 0.0)
        assertEquals(1.0, layout.childViews[0].measuredSize.height, 0.0)
    }

    @Test
    fun weighted() {
        val layout = HorizontalGroup()
        layout.add(MockUxView(40.0, 40.0, layout = Layout(
                widthMode = Layout.Mode.Weighted,
                heightMode = Layout.Mode.MatchParent,
                gravity = Gravity.Center,
                weight = 1.0
        )))
        layout.addDivider()
        layout.add(MockUxView(20.0, 20.0, layout = Layout.layout {
            widthMode = Layout.Mode.Weighted
            heightMode = Layout.Mode.MatchParent
            gravity = Gravity.BottomRight
            margin = 1.0
            weight = 2.0
        }))
        val left = layout.childViews[0]
        val right = layout.childViews[2]
        val screenbounds = MockUxScreen.mainScreen.bounds
        layout.onMeasure(screenbounds.width, screenbounds.height)
        assertEquals(screenbounds.width, layout.measuredSize.width, 0.0)
        assertEquals(40.0, layout.measuredSize.height, 0.0)
        assertEquals(40.0, left.measuredSize.height, 0.0)
        assertEquals(20.0, right.measuredSize.height, 0.0)
        assertEquals(40.0, left.measuredSize.width, 0.0)
        assertEquals(20.0, right.measuredSize.width, 0.1)
        layout.frame = screenbounds
        layout.layoutSubviews()
        assertEquals(screenbounds.maxY - 1.0, right.frame.maxY, 0.0)
        assertEquals(screenbounds.maxX - 1, right.frame.maxX, 0.1)
        assertEquals(screenbounds.maxY - 1, right.frame.maxY, 0.1)
        assertEquals(0.0, left.frame.minX, 0.01)
        assertEquals(255.0, left.frame.maxX, 0.01)
        assertEquals(257.0, right.frame.minX, 0.01)
    }

    @Test
    fun spacing() {
        val layout = HorizontalGroup()
        layout.spacing = 2.0
        val left = MockUxView(100.0, 100.0, Layout(widthMode = Layout.Mode.Weighted, weight = 1.0))
        val right = MockUxView(100.0, 100.0, Layout(widthMode = Layout.Mode.Weighted, weight = 1.0))
        layout.add(left, right)
        layout.onMeasure(400.0, 400.0)
        layout.frame = MockCxRect(0.0, 0.0, 400.0, 400.0)
    }
}