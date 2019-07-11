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

/**
 * Copyright (C) Control-J Pty. Ltd. ACN 103594190
 * All rights reserved
 *
 * User: clyde
 * Date: 2019-03-30
 * Time: 11:48
 */
interface UxColor : CxBase {
    val red: Double
    val green: Double
    val blue: Double
    val alpha: Double

    companion object {
        fun darkGray(): UxColor = CxFactory.instance.uxColor(0.333, 0.333, 0.333, 1.0)
        fun clear(): UxColor = CxFactory.instance.uxColor(0.0, 0.0, 0.0, 0.0)
        fun white(): UxColor = CxFactory.instance.uxColor(1.0, 1.0, 1.0, 1.0)
        fun fromWhiteAlpha(white: Double, alpha: Double): UxColor {
            return CxFactory.instance.uxColor(white, white, white, alpha)
        }

    }
}