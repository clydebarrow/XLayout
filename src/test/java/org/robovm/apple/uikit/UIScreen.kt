package org.robovm.apple.uikit

import org.robovm.apple.coregraphics.CGRect

/**
 * Copyright (C) Control-J Pty. Ltd. ACN 103594190
 * All rights reserved
 *
 * User: clyde
 * Date: 2019-03-29
 * Time: 20:02
 */
class UIScreen(frame: CGRect): UIView(frame) {

    companion object {
        private val mainScreen = UIScreen(CGRect(0.0, 0.0, 768.0, 1024.0))
        fun getMainScreen(): UIScreen = UIScreen.mainScreen
    }
}