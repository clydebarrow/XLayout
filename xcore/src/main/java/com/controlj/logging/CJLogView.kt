package com.controlj.logging

import com.controlj.layout.View

/**
 * Copyright (C) Control-J Pty. Ltd. ACN 103594190
 * All rights reserved
 *
 * User: clyde
 * Date: 2019-04-02
 * Time: 13:54
 */
object CJLogView {
    var logger: (message: String) -> Unit = {
        // System.err.println(message)
    }

    fun logMsg(view: View?, format: String, vararg args: Any) {
        logger("".padEnd((view?.indent ?: 0) * 4, ' ') +
                view?.name.toString() + ": " +
                String.format(format, *args))
    }
}