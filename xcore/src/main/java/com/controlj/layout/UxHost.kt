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

import com.controlj.shim.CxLayer
import com.controlj.shim.CxRect
import com.controlj.shim.UxView

/**
 * An interface between a viewgroup and the native host
 */
interface UxHost {

    /**
     * The base [FrameGroup] of this host. Add children to this.
     */

    val frameGroup: FrameGroup

    /**
     * The bounds of the host. Always zero-referenced
     */

    val bounds: CxRect

    /**
     * Add a sublayer below the given layer - i.e. the [other] layer will render on top of [subLayer]
     * If [other] is null, simply add this layer to the host
     */
    fun addSublayer(subLayer: CxLayer)

    /**
     * Add the [subView] to this host's view
     */
    fun addSubview(subView: UxView)
}
