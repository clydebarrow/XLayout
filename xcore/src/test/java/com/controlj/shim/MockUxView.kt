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

open class MockUxView(width: Double = 0.0, height: Double = 0.0) : UxView {

    var superView: MockUxView? = null
    val subViews = mutableListOf<UxView>()
    override fun removeFromSuperview() {
        val subs = superView?.subViews ?: error("Remove from superview when not attached")
        if(!subs.contains(this)) error("Superview doesn't own us!")
        subs.remove(this)
        superView = null
    }

    override var hided: Boolean = false
    override fun addSubview(view: UxView) {
        view as MockUxView
        if(view.superView != null)
            error("Adding already claimed view to superview")
        view.superView = this
        subViews.add(view)
    }

    override var intrinsicSize: CxSize = MockCxSize(width, height)
    override var autoresizingMask: Long = 0
    override var backgroundColor: UxColor = MockUxColor(0.0, 0.0, 0.0, 0.0)
    override val layer: CxLayer = MockCxLayer()
    override var frame: CxRect = MockCxRect()
}
