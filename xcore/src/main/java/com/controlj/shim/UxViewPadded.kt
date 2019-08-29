package com.controlj.shim

/**
 * Copyright (C) Control-J Pty. Ltd. ACN 103594190
 * All rights reserved
 *
 * User: clyde
 * Date: 2019-08-29
 * Time: 15:38
 *
 * An interface to be implemented by paddable things
 */
interface UxPadded {
    var paddingLeft: Double
    var paddingRight: Double
    var paddingTop: Double
    var paddingBottom: Double
}