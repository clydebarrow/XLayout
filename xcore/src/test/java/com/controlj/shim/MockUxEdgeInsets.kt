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

class MockUxEdgeInsets(
        override var top: Double = 0.0,
        override var left: Double = 0.0,
        override var bottom: Double = 0.0,
        override var right: Double = 0.0
) : UxEdgeInsets {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UxEdgeInsets) return false

        if (top != other.top) return false
        if (left != other.left) return false
        if (bottom != other.bottom) return false
        if (right != other.right) return false

        return true
    }

    override fun hashCode(): Int {
        var result = top.hashCode()
        result = 31 * result + left.hashCode()
        result = 31 * result + bottom.hashCode()
        result = 31 * result + right.hashCode()
        return result
    }

    override fun toString(): String {
        return "UxEdgeInsets(top=$top, left=$left, bottom=$bottom, right=$right)"
    }

}
