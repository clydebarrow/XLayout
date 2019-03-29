package com.controlj.layout

import org.junit.Assert.assertEquals
import org.junit.Test
import org.robovm.apple.uikit.UIScreen
import org.robovm.apple.uikit.UIView

/**
 * Copyright (C) Control-J Pty. Ltd. ACN 103594190
 * All rights reserved
 *
 * User: clyde
 * Date: 2019-03-29
 * Time: 19:46
 */
class HorizontalLayoutTest {

    @Test
    fun testSingle() {

        val layout = HorizontalLayout()
        layout.addSubView(UIView.sized(100.0, 0.0), Layout(heightMode = Layout.Mode.MatchParent))
        val screenbounds = UIScreen.getMainScreen().bounds
        layout.onMeasure(screenbounds.width, screenbounds.height)
        assertEquals(100.0, layout.measuredSize.width, 0.0)
        assertEquals(screenbounds.height, layout.measuredSize.height, 0.0)
    }

    @Test
    fun testTwo() {

        val layout = HorizontalLayout()
        layout.addSubView(UIView.sized(100.0, 0.0), Layout(heightMode = Layout.Mode.MatchParent))
        layout.addDivider()
        layout.addSubView(UIView.sized(200.0, 150.0), Layout(heightMode = Layout.Mode.WrapContent))
        val screenbounds = UIScreen.getMainScreen().bounds
        layout.onMeasure(screenbounds.width, screenbounds.height)
        assertEquals(301.0, layout.measuredSize.width, 0.0)
        assertEquals(screenbounds.height, layout.measuredSize.height, 0.0)
        assertEquals(screenbounds.height, layout.childViews[0].measuredSize.height, 0.0)
        assertEquals(150.0, layout.childViews[2].measuredSize.height, 0.0)
    }

    @Test
    fun weighted() {
        val layout = HorizontalLayout(Layout(widthMode = Layout.Mode.MatchParent))
        layout.addSubView(UIView(), Layout(widthMode = Layout.Mode.Weighted, weight = 1.0, heightMode = Layout.Mode.MatchParent))
        layout.addDivider()
        layout.addSubView(UIView(), Layout(widthMode = Layout.Mode.Weighted, weight = 2.0, heightMode = Layout.Mode.MatchParent))
        val screenbounds = UIScreen.getMainScreen().bounds
        layout.onMeasure(screenbounds.width, screenbounds.height)
        assertEquals(screenbounds.width, layout.measuredSize.width, 0.0)
        assertEquals(screenbounds.height, layout.measuredSize.height, 0.0)
        assertEquals(screenbounds.height, layout.childViews[0].measuredSize.height, 0.0)
        assertEquals(screenbounds.height, layout.childViews[2].measuredSize.height, 0.0)
        assertEquals((screenbounds.width - 1.0) / 3, layout.childViews[0].measuredSize.width, 0.1)
    }

    @Test
    fun aspect() {
        val layout = HorizontalLayout(Layout(widthMode = Layout.Mode.MatchParent))
        layout.addSubView(UIView(), Layout(widthMode = Layout.Mode.Weighted, weight = 1.0, heightMode = Layout.Mode.MatchParent))
        layout.addSubView(UIView(), Layout(widthMode = Layout.Mode.Aspect, aspectRatio = 0.5, heightMode = Layout.Mode.MatchParent))
        val screenbounds = UIScreen.getMainScreen().bounds
        layout.onMeasure(screenbounds.width, screenbounds.height)
        assertEquals(screenbounds.height, layout.measuredSize.height, 0.0)
        assertEquals(screenbounds.width, layout.measuredSize.width, 0.0)
        assertEquals(screenbounds.height, layout.childViews[0].measuredSize.height, 0.0)
        assertEquals(screenbounds.height, layout.childViews[1].measuredSize.height, 0.0)
        assertEquals(screenbounds.height * .5, layout.childViews[1].measuredSize.width, 0.1)

    }
}