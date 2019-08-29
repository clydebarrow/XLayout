package com.controlj.shim

/**
 * Copyright (C) Control-J Pty. Ltd. ACN 103594190
 * All rights reserved
 *
 * User: clyde
 * Date: 2019-08-28
 * Time: 13:58
 *
 * A generic button UI element
 */
interface UxButton: UxView {
    var text: String        // the text of the button
    var image: String       // the name of the image, if any
    var textColor: UxColor  // the text color
    var action: (UxButton) -> Unit  // the action to be run when the button is pressed
}