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

/**
 * This interface is implemented by View, and provides a way for a layout to inform its views when
 * their visibility changes
 */
interface ViewStateListener {
    /**
     * CAlled when the view transitions from not visible to visible
     */
    fun onShown()

    /**
     * CAlled when the view becomes invisible or gone
     */
    fun onHidden()

    /**
     * called when the view is attached to a host.
     * @param host the new UILayouthost to which this view is attached
     */
    fun onAttach(host: UxHost)

    /**
     * Called when the view is detached from its host
     */
    fun onDetach()

}
