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
 * this class mimics a UIEdgeInsets
 * Note that the order of the constructor arguments is the same strange order as UIedgeInsets
 *
 */
interface UxEdgeInsets : CxBase {
    var top: Double
    var left: Double
    var bottom: Double
    var right: Double

    /**
     * Are all the insets zero?
     */
    val isZero
        get() = top == 0.0 && bottom == 0.0 && left == 0.0 && right == 0.0

    /**
     * Get the sum of the top and bottom insets
     */
    fun totalHeight(): Double {
        return top + bottom
    }

    /**
     * Get the sum of the left and right insets
     */
    fun totalWidth(): Double {
        return left + right
    }

}
