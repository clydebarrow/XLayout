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

package org.robovm.apple.uikit

import com.controlj.shim.CxRect
import com.controlj.shim.MockCxRect
import com.controlj.shim.MockUxView

/**
 * Copyright (C) Control-J Pty. Ltd. ACN 103594190
 * All rights reserved
 *
 * User: clyde
 * Date: 2019-03-29
 * Time: 20:02
 */
class MockUxScreen(override var frame: CxRect) : MockUxView() {

    companion object {
        val mainScreen = MockUxScreen(MockCxRect(0.0, 0.0, 768.0, 1024.0))
    }
}