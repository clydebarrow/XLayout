package org.robovm.apple.uikit

/**
 * Copyright (C) Control-J Pty. Ltd. ACN 103594190
 * All rights reserved
 *
 * User: clyde
 * Date: 2019-03-29
 * Time: 15:55
 */
data class UIEdgeInsets(var top: Double, var left: Double, var bottom: Double, var right: Double) {
    constructor(): this(0.0, 0.0, 0.0, 0.0)

    val isTest = true
}