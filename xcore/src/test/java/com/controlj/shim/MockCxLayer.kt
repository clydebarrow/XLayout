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

class MockCxLayer : CxLayer {
    val subLayers = mutableListOf<CxLayer>()
    var superLayer: MockCxLayer? = null

    override fun removeFromSuperlayer() {
        val subs = superLayer?.subLayers ?: error("No Superlayer to remove from")
        if (!subs.contains(this))
            error("Superlayer does not own us")
        subs.remove(this)
        superLayer = null
    }

    override fun insertSublayerBelow(value: CxLayer, nextLayer: CxLayer) {
        val idx = subLayers.indexOf(nextLayer)
        if (idx == -1)
            subLayers.add(value)
        else
            subLayers.add(idx, value)
    }

    override fun addSublayer(value: CxLayer?) {
        if (value != null)
            subLayers.add(value)
    }

    override var isHidden: Boolean = false
    override var frame: CxRect = MockCxRect()

}
