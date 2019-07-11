package com.controlj.layout

import com.controlj.layout.HorizontalGroup.Companion.horizontalGroup
import com.controlj.layout.Layout.Companion.MAX_DIMENSION
import com.controlj.layout.Layout.Companion.absolute
import com.controlj.layout.VerticalGroup.Companion.verticalGroup
import com.controlj.logging.CJLogView
import com.controlj.shim.MockCxPoint
import com.controlj.shim.MockCxRect
import com.controlj.shim.MockUxView
import com.controlj.utility.Utility
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Copyright (C) Control-J Pty. Ltd. ACN 103594190
 * All rights reserved
 *
 *
 * User: clyde
 * Date: 2019-04-01
 * Time: 12:47
 */
class FrameGroupTest {

    @Before
    fun setup() {
        Utility.setup()
        CJLogView.logger = {println(it)}
    }

    @Test
    fun onMeasure() {
        val frameGroup = FrameGroup()
        frameGroup.add(MockUxView(layout = Layout(widthMode = Layout.Mode.MatchParent, height = 150.0)))
        frameGroup.add(MockUxView(layout = Layout(width = 200.0, heightMode = Layout.Mode.MatchParent)))
        val goneView = MockUxView(400.0, 400.0)
        goneView.gone = true
        frameGroup.add(goneView)
        frameGroup.onMeasure(200.0, 150.0)
        assertEquals(150.0, frameGroup.measuredSize.height, 0.0)
        assertEquals(200.0, frameGroup.measuredSize.width, 0.0)
        frameGroup.frame = MockCxRect(MockCxPoint(), frameGroup.measuredSize)
        assertTrue(goneView.frame.size.isZero())
    }

    @Test
    fun nesting() {
        val frameGroup = FrameGroup(absolute(200.0, 200.0))
        frameGroup.name = "frameGroup"
        val vertical = verticalGroup {
            layout.widthMode = Layout.Mode.WrapContent
            layout.heightMode = Layout.Mode.WrapContent
            layout.gravity = Gravity.Center
            name = "verticalGroup"
        }
        vertical.add(MockUxView(100.0, 50.0, name = "view1"))
        vertical.add(MockUxView(100.0, 50.0, name = "view2"))
        val horizontal = horizontalGroup {
            layout.widthMode = Layout.Mode.MatchParent
            name = "horizontalGroup"
            add(MockUxView(40.0, 10.0, name = "weighted1", layout = Layout(widthMode = Layout.Mode.Weighted, weight = 1.0)))
            add(MockUxView(40.0, 10.0, name = "weighted2", layout = Layout(widthMode = Layout.Mode.Weighted, weight = 1.0)))
        }
        vertical.add(horizontal)
        frameGroup.add(vertical)
        frameGroup.onMeasure(1000.0, 1000.0)
        println(frameGroup.measuredSize)
    }

}
