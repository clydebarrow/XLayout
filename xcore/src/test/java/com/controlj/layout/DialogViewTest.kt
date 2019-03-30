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

import com.controlj.layout.NativeView.Companion.nativeView
import com.controlj.shim.MockCxFactory
import com.controlj.shim.MockCxTransaction
import com.controlj.shim.MockUxView
import com.controlj.utility.Utility
import org.junit.Before
import org.junit.BeforeClass
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
        val verticalLayout = VerticalLayout.verticalLayout {
            layout.width = 360.0
            name = "Outer"
        }
        // add a title view
        val titleView = MockUxView(0.0, 20.0)
        verticalLayout.addSubView(nativeView(titleView) {
            layout.widthMode = Layout.Mode.MatchParent
            layout.gravity = Gravity.Center
            name = "TitleView"
        })
        verticalLayout.addSubView(VerticalLayout.verticalLayout {
            layout.widthMode = Layout.Mode.MatchParent
            addSubView(HorizontalLayout.horizontalLayout {
                name = "horiz1"
                layout.widthMode = Layout.Mode.MatchParent
                addSubView(nativeView(MockUxView(32.0, 32.0)) {
                    layout.widthMode = Layout.Mode.Aspect
                    layout.heightMode = Layout.Mode.MatchParent
                    name = "Image1"
                })
                addSubView(nativeView(MockUxView(40.0, 30.0)) {
                    layout.widthMode = Layout.Mode.Weighted
                    layout.weight = 1.0
                    name = "Command1"
                })
            })
            addSubView(HorizontalLayout.horizontalLayout {
                name = "horiz2"
                layout.widthMode = Layout.Mode.MatchParent
                addSubView(nativeView(MockUxView(32.0, 32.0)) {
                    layout.widthMode = Layout.Mode.Aspect
                    layout.heightMode = Layout.Mode.MatchParent
                    name = "Image2"
                })
                addSubView(nativeView(MockUxView(40.0, 30.0)) {
                    layout.widthMode = Layout.Mode.Weighted
                    layout.weight = 1.0
                    name = "Command2"
                })
            })
        })
        verticalLayout.onMeasure(768.0, 1024.0)
        println("frame now ${verticalLayout.frame}")
    }
}