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

import com.controlj.layout.FrameGroup
import com.controlj.layout.UxHost
import com.controlj.layout.ViewGroup

/**
 * Copyright (C) Control-J Pty. Ltd. ACN 103594190
 * All rights reserved
 *
 * User: clyde
 * Date: 2019-03-30
 * Time: 17:09
 */
class MockHost(val viewGroup: ViewGroup, override val bounds: CxRect) : UxHost {

    override val frameGroup = FrameGroup()
    val subViews = mutableListOf<UxView>()
    val layers = mutableListOf<CxLayer>()

    init {
        frameGroup.frame = bounds
        frameGroup.add(viewGroup)
    }

    fun attach() {
        frameGroup.onAttach(this)
    }

    override fun addSublayer(subLayer: CxLayer) {
        layers.add(subLayer)
    }

    override fun addSubview(subView: UxView) {
        subViews.add(subView)
        layers.add(subView.layer)
    }

    fun layoutSubviews() {
        frameGroup.onMeasure(bounds.width, bounds.height)
        frameGroup.frame = bounds
        frameGroup.layoutSubviews()
    }
}