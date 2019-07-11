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

@file:Suppress("UNCHECKED_CAST")

package com.controlj.shim

import kotlin.reflect.KClass

/**
 * Copyright (C) Control-J Pty. Ltd. ACN 103594190
 * All rights reserved
 *
 * User: clyde
 * Date: 2019-03-30
 * Time: 10:51
 */
class MockCxFactory : CxFactory {
    override fun animate(animationDuration: Double, function: () -> Unit, completion: () -> Unit) {
        function()
        completion()
    }

    override fun beginTransaction() {
    }

    override fun disablesActions() {
    }

    override fun commitTransaction() {
    }

    override fun uxColor(red: Double, green: Double, blue: Double, alpha: Double): UxColor {
        return MockUxColor(red, green, blue, alpha)
    }

    init {
        init()
    }

    override fun <T : CxBase> create(ofClass: KClass<T>): T {
        return when (ofClass) {
            CxPoint::class -> MockCxPoint() as T
            CxRect::class -> MockCxRect() as T
            CxSize::class -> MockCxSize() as T
            UxView::class -> MockUxView() as T
            UxEdgeInsets::class -> MockUxEdgeInsets() as T
            CxLayer::class -> MockCxLayer() as T
            else -> error("Unknown Cx class ${ofClass.simpleName}")
        }
    }
}