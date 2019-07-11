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
import com.controlj.utility.findViewByName
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Copyright (C) Control-J Pty. Ltd. ACN 103594190
 * All rights reserved
 *
 * User: clyde
 * Date: 2019-03-29
 * Time: 21:24
 */
class DialogViewTest {

    @Before
    fun setup() {
        Utility.setup()
    }

    @Test
    fun dialogViewTest() {
        val verticalLayout = VerticalGroup.verticalGroup {
            layout.width = 360.0
            layout.widthMode = Layout.Mode.Absolute
            spacing = 4.0
            name = "Outer"
        }
        // add a title view
        val titleView = MockUxView(50.0, 20.0,
                Layout.layout {
                    widthMode = Layout.Mode.WrapContent
                    gravity = Gravity.Center
                }, "TitleView")
        verticalLayout.add(titleView)
        verticalLayout.add(HorizontalGroup.horizontalGroup {
            name = "horiz1"
            layout.widthMode = Layout.Mode.WrapContent
            layout.margin = 5.0
            spacing = 4.0
            add(MockUxView(32.0, 32.0,
                    Layout.layout {
                        widthMode = Layout.Mode.Absolute
                        width = 32.0
                        heightMode = Layout.Mode.WrapContent

                    }, "Image1"))
            add(MockUxView(40.0, 30.0,
                    Layout.layout {
                        widthMode = Layout.Mode.Weighted
                        weight = 1.0

                    }, "Command1"))
        })
        verticalLayout.add(HorizontalGroup.horizontalGroup {
            name = "horiz2"
            layout.widthMode = Layout.Mode.WrapContent
            layout.gravity = Gravity.MiddleLeft
            layout.margin = 5.0
            spacing = 4.0
            add(MockUxView(32.0, 32.0,
                    Layout.layout {
                        widthMode = Layout.Mode.Absolute
                        width = 32.0
                        heightMode = Layout.Mode.WrapContent
                    }, "Image2"))
            add(MockUxView(32.0, 32.0,
                    Layout.layout {
                        widthMode = Layout.Mode.WrapContent
                        heightMode = Layout.Mode.WrapContent
                    }, "Command2"))
        })
        verticalLayout.frame = MockCxRect(0.0, 0.0, 360.0, 1024.0)
        verticalLayout.onMeasure(verticalLayout.frame.width, verticalLayout.frame.height)
        verticalLayout.layoutSubviews()
        assertTrue(verticalLayout.visible)
        assertTrue(titleView.visible)
        assertEquals(360.0, verticalLayout.measuredSize.width, 0.0)
        assertEquals(50.0, titleView.measuredSize.width, 0.0)
        assertEquals(155.0, titleView.frame.minX, 0.0)
        val horiz2 = verticalLayout.findViewByName("horiz2")!!
        assertTrue(horiz2.visible)
        assertEquals(68.0, horiz2.measuredSize.width, 0.0)
        assertEquals(32.0, horiz2.measuredSize.height, 0.0)
        assertEquals(112.0, verticalLayout.measuredSize.height, 0.0)
    }
}