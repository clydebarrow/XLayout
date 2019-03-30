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

import com.controlj.layout.asCGRect
import org.robovm.apple.coreanimation.CALayer

/**
 * Copyright (C) Control-J Pty. Ltd. ACN 103594190
 * All rights reserved
 *
 * User: clyde
 * Date: 2019-03-30
 * Time: 13:46
 */
class iosCxLayer(val caLayer: CALayer) : CxLayer {
    override fun removeFromSuperlayer() {
        caLayer.removeFromSuperlayer()
    }

    override fun insertSublayerBelow(value: CxLayer, nextLayer: CxLayer) {
        caLayer.insertSublayerBelow((value as iosCxLayer).caLayer, (nextLayer as iosCxLayer).caLayer)
    }

    override fun addSublayer(value: CxLayer?) {
        if (value is iosCxLayer)
            caLayer.addSublayer(value.caLayer)
    }

    override var isHidden: Boolean
        get() = caLayer.isHidden
        set(value) {
            caLayer.isHidden = value
        }
    override var frame: CxRect
        get() = iosCxRect(caLayer.frame)
        set(value) {
            caLayer.frame = value.asCGRect()
        }
}