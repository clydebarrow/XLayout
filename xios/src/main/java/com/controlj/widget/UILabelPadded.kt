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

package com.controlj.widget

import org.robovm.apple.coregraphics.CGRect
import org.robovm.apple.uikit.UIEdgeInsets
import org.robovm.apple.uikit.UILabel

/**
 * Created by clyde on 17/4/18.
 */
class UILabelPadded(padding: UIEdgeInsets = UIEdgeInsets.Zero()) : UILabel() {

    var padding =  padding
    set(value) {
        field = value
        invalidateIntrinsicContentSize()
    }

    override fun getTextRect(bounds: CGRect, lineLimit: Long): CGRect {
        val textRect = super.getTextRect(bounds, lineLimit)
        return textRect.inset(UIEdgeInsets(-padding.top, -padding.left, -padding.bottom, -padding.right))
    }

    override fun drawText(rect: CGRect) {
        super.drawText(rect.inset(padding))
    }
}
