package com.controlj.layout

import org.junit.Assert.*
import org.junit.Test

/**
 * Copyright (C) Control-J Pty. Ltd. ACN 103594190
 * All rights reserved
 *
 *
 * User: clyde
 * Date: 2019-04-01
 * Time: 13:54
 */
class GravityTest {
    @Test
    fun gravityTest() {
        val bottomLeft = Gravity.None.with(Gravity.Vertical.Bottom).with(Gravity.Horizontal.Left)
        assertEquals(Gravity.BottomLeft, bottomLeft)
    }
}