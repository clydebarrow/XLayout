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

import com.controlj.logging.CJLogView.logMsg
import com.controlj.layout.UxHost
import com.controlj.layout.Layout
import com.controlj.layout.View
import com.controlj.layout.asDim

interface UxView : CxBase, View {

    fun removeFromSuperview()

    val intrinsicSize: CxSize
    var backgroundColor: UxColor
    val layer: CxLayer
    val bounds: CxRect
        get() = CxFactory.cxRect(CxFactory.cxPoint(), frame.size)
    var autoresizingMask: Long

    /**
     * Called when this view is to be added to its parent
     */
    override fun onAttach(host: UxHost) {
        // attach the view to the hosting view by adding as a subview
        logMsg(this, "onAttach")
        host.addSubview(this)
    }

    /**
    Overridden to remove this native view from the parent native view
     */
    override fun onDetach() {
        // remove from the hosting view by removing it from the superview
        logMsg(this, "Ondetach")
        removeFromSuperview()
    }
}
