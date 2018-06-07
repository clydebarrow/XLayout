/*
 * Copyright (c) 2018. Control-J Pty. Ltd. All rights reserved
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
 */

package com.controlj.widget

import com.controlj.layout.View.Companion.logMsg
import org.robovm.apple.coregraphics.CGPoint
import org.robovm.apple.coregraphics.CGRect
import org.robovm.apple.uikit.UIBezierPath
import org.robovm.apple.uikit.UIColor
import org.robovm.apple.uikit.UIControl
import org.robovm.apple.uikit.UIEvent

/**
 * Created by clyde on 19/4/18.
 */
class Checkbox : UIControl() {
    var borderWidth = 2.0
    var contentRatio = 0.5
    var borderColor = tintColor
    var checkColor = tintColor
    var touchRadius = 5.0
    var checked: Boolean = false
    set(value) {
        field = value
        setNeedsDisplay()
    }
    private var hidden = false

    override fun setHidden(p0: Boolean) {
        hidden = p0
        super.setHidden(p0)
        logMsg("hidden = $hidden")
    }

    init {
        backgroundColor = UIColor.clear()
        setUserInteractionEnabled(true)     // don't use property syntax
    }

    override fun draw(rect: CGRect) {
        logMsg("ishidden = $hidden: ")
        if(!hidden) {
            drawOutline(rect);
            if (checked)
                drawCheck(rect)
        }
    }

    private fun drawOutline(rect: CGRect) {
        val path = UIBezierPath.newRect(rect)
        path.lineWidth = borderWidth
        borderColor.setStroke()
        path.stroke()
        backgroundColor.setFill()
        path.fill()
    }

    private fun drawCheck(rect: CGRect) {
        val path = UIBezierPath()
        val insH = rect.width * contentRatio / 2
        val insV = rect.height * contentRatio / 2
        val newrect = rect.inset(insH, insV)
        path.move(CGPoint(newrect.minX + .045 * newrect.width, newrect.minY + 0.64 * newrect.height))
        path.addLine(CGPoint(newrect.minX + .35 * newrect.width, newrect.minY + 0.95 * newrect.height))
        path.addLine(CGPoint(newrect.minX + .95 * newrect.width, newrect.minY + 0.05 * newrect.height))
        checkColor.setStroke()
        path.stroke()
    }

    override fun isPointInside(point: CGPoint, event: UIEvent): Boolean {
        val touchFrame = bounds.inset(-touchRadius, -touchRadius)
        return touchFrame.contains(point)
    }
}
