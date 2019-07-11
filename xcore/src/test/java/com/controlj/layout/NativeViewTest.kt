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

import com.controlj.layout.FrameGroup.Companion.frameLayout
import com.controlj.layout.VerticalGroup.Companion.verticalGroup
import com.controlj.shim.MockCxRect
import com.controlj.shim.MockHost
import com.controlj.shim.MockUxView
import com.controlj.utility.Utility
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/**
 * Copyright (C) Control-J Pty. Ltd. ACN 103594190
 * All rights reserved
 *
 *
 * User: clyde
 * Date: 2019-03-30
 * Time: 17:06
 */
@RunWith(JUnit4::class)
class NativeViewTest {

    @Before
    fun setup() {
        Utility.setup()
    }

    @Test
    fun getView() {
        val frameLayout = frameLayout {
            layout.widthMode = Layout.Mode.MatchParent
            layout.heightMode = Layout.Mode.MatchParent

        }
        val centerTop = MockUxView(40.0, 40.0, Layout(gravity = Gravity.CenterTop), "CenterTop")
        val centerBottom = MockUxView(40.0, 40.0, Layout(gravity = Gravity.CenterBottom), "CenterBottom")
        frameLayout.add(centerBottom)
        frameLayout.add(centerTop)

        val verticalLayout = verticalGroup {
            layout.widthMode = Layout.Mode.MatchParent
            layout.heightMode = Layout.Mode.MatchParent
        }
        val topHalf = MockUxView(layout = Layout(
                widthMode = Layout.Mode.MatchParent,
                heightMode = Layout.Mode.Weighted,
                weight = 1.0
        ), name = "TopHalf")
        val bottomHalf = MockUxView(layout = Layout(
                widthMode = Layout.Mode.MatchParent,
                heightMode = Layout.Mode.Weighted,
                weight = 1.0
        ), name = "BottomHalf")
        val goneView = MockUxView(layout = Layout(
                widthMode = Layout.Mode.MatchParent,
                heightMode = Layout.Mode.Weighted,
                weight = 1.0
        ), name = "Gone")
        goneView.gone = true
        verticalLayout.add(topHalf)
        verticalLayout.add(bottomHalf)
        frameLayout.add(verticalLayout)
        val host = MockHost(frameLayout, MockCxRect(0.0, 0.0, 1024.0, 768.0))
        host.attach()
        assertEquals(4, host.subViews.count())
        host.layoutSubviews()
        assertEquals(1024.0, frameLayout.frame.width, 0.0)
        assertEquals(40.0, centerTop.frame.width, 0.0)
        assertEquals(40.0, centerTop.frame.height, 0.0)
        assertEquals((1024.0 - 40.0) / 2, centerTop.frame.minX, 0.0)
        assertEquals((1024.0 - 40.0) / 2, centerBottom.frame.minX, 0.0)
        assertEquals(768.0, centerBottom.frame.maxY, 0.0)
        assertEquals(0.0, centerTop.frame.minY, 0.0)
        assertTrue(goneView.bounds.size.isZero())
    }

    @Test
    fun setView() {
    }
}
