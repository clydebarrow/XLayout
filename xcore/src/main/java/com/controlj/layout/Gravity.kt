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

/**
 * Gravity flags are used to specify how a subview (or subviews) should be aligned
 * within a larger container
 */

package com.controlj.layout

data class Gravity(val horizontal: Horizontal = Horizontal.None, val vertical: Vertical = Vertical.None) {

    enum class Horizontal {
        None,
        Left,
        Right,
        Center
    }

    enum class Vertical {
        None,
        Top,
        Bottom,
        Center,
    }

    infix fun with(other: Vertical): Gravity {
        return Gravity(horizontal, other)
    }

    infix fun with(other: Horizontal): Gravity {
        return Gravity(other, vertical)
    }

    companion object {
        val None = Gravity(Horizontal.None, Vertical.None)
        val Center = Gravity(Horizontal.Center, Vertical.Center)
        val BottomRight = Gravity(Horizontal.Right, Vertical.Bottom)
        val TopLeft = Gravity(Horizontal.Left, Vertical.Top)
        val TopRight = Gravity(Horizontal.Right, Vertical.Top)
        val BottomLeft = Gravity(Horizontal.Left, Vertical.Bottom)
        val MiddleLeft = Gravity(Horizontal.Left, Vertical.Center)
        val MiddleRight = Gravity(Horizontal.Right, Vertical.Center)
        val CenterTop = Gravity(Horizontal.Center, Vertical.Top)
        val CenterBottom = Gravity(Horizontal.Center, Vertical.Bottom)
    }
}
