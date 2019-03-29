package org.robovm.apple.uikit

/**
 * Copyright (C) Control-J Pty. Ltd. ACN 103594190
 * All rights reserved
 *
 * User: clyde
 * Date: 2019-03-29
 * Time: 20:25
 */
class UIColor(val red: Double, val green: Double, val blue: Double, val alpha: Double) {
    companion object {
        @JvmStatic
        fun darkGray() = UIColor(0.3333, 0.3333, 0.3333, 1.0)
        @JvmStatic
        fun clear() = UIColor(0.0, 0.0, 0.0, 0.0)
    }
}