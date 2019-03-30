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

interface UxView : CxBase {
    fun getSizeThatFits(cxSize: CxSize): CxSize {
        return CxFactory.cxSize(
                if (intrinsicSize.width == 0.0) cxSize.width else intrinsicSize.width,
                if (intrinsicSize.height == 0.0) cxSize.height else intrinsicSize.height)
    }

    fun addSubview(view: UxView)
    fun removeFromSuperview()

    val intrinsicSize: CxSize
    var backgroundColor: UxColor
    val layer: CxLayer
    var frame: CxRect
    val bounds: CxRect
        get() = CxFactory.cxRect(CxFactory.cxPoint(), frame.size)
    var autoresizingMask: Long
    var hided: Boolean

    companion object {
        fun animate(animationDuration: Double, function: () -> Unit, completion: () -> Unit = {}) {
        }

    }

}
