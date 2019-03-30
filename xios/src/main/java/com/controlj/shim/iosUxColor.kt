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

import org.robovm.apple.uikit.UIColor

/**
 * Copyright (C) Control-J Pty. Ltd. ACN 103594190
 * All rights reserved
 *
 * User: clyde
 * Date: 2019-03-30
 * Time: 13:42
 */
class iosUxColor(uiColor: UIColor): UxColor {
    override val red: Double = uiColor.rgba[0]
    override val green: Double = uiColor.rgba[1]
    override val blue: Double = uiColor.rgba[2]
    override val alpha: Double = uiColor.rgba[3]
}

fun UxColor.asUIColor(): UIColor {
    return UIColor.fromRGBA(red, green, blue, alpha)
}