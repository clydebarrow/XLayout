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

import com.controlj.layout.FrameLayout.Companion.frameLayout
import com.controlj.layout.VerticalLayout.Companion.verticalLayout
import com.controlj.shim.MockCxRect
import com.controlj.shim.MockHost
import com.controlj.shim.MockUxView
import com.controlj.utility.Utility
import org.junit.Assert.assertEquals
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
        val host = MockHost()
        host.getUxView().frame = MockCxRect(0.0, 0.0, 1024.0, 768.0)
        val frameLayout = frameLayout {
            layout.widthMode = Layout.Mode.MatchParent
            layout.heightMode = Layout.Mode.MatchParent

            addSubView(MockUxView(40.0, 40.0), Layout ( gravity = Gravity.CenterTop ))
            addSubView(MockUxView(40.0, 40.0), Layout ( gravity = Gravity.CenterBottom ))
        }

        val verticalLayout = verticalLayout {
            layout.widthMode = Layout.Mode.MatchParent
            layout.heightMode = Layout.Mode.MatchParent
            addSubView(MockUxView(), Layout(
                    widthMode = Layout.Mode.MatchParent,
                    heightMode = Layout.Mode.Weighted,
                    weight = 1.0
            ))
            addSubView(MockUxView(), Layout(
                    widthMode = Layout.Mode.MatchParent,
                    heightMode = Layout.Mode.Weighted,
                    weight = 1.0
            ))
        }
        frameLayout.addSubView(verticalLayout)
        frameLayout.host = host
        assertEquals(1024.0, frameLayout.frame.width, 0.0)
    }

    @Test
    fun setView() {
    }
}
