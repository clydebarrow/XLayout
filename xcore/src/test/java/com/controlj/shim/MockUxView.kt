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

package com.controlj.shim

import com.controlj.layout.Layout
import com.controlj.layout.ViewGroup

open class MockUxView(width: Double = 0.0, height: Double = 0.0, override var layout: Layout = Layout(), override var name: String = "MockUxView") : UxView {
    override var row: Int = 0
    override var column: Int = 0
    override var parent: ViewGroup? = null
    override var measuredSize: CxSize = MockCxSize()
    override var gone: Boolean = false

    var superView: MockUxView? = null
    val subViews = mutableListOf<UxView>()
    override fun removeFromSuperview() {
        val subs = superView?.subViews ?: error("Remove from superview when not attached")
        if (!subs.contains(this)) error("Superview doesn't own us!")
        subs.remove(this)
        superView = null
    }

    override fun onMeasure(availableWidth:Double, availableHeight: Double) {
        measuredSize.width = intrinsicSize.width
        measuredSize.height = intrinsicSize.height
    }

    override var visible: Boolean = true

    override var intrinsicSize: CxSize = MockCxSize(width, height)
    override var autoresizingMask: Long = 0
    override var backgroundColor: UxColor = MockUxColor(0.0, 0.0, 0.0, 0.0)
    override val layer: CxLayer = MockCxLayer()
    override var frame: CxRect = MockCxRect()
}
