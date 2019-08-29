package com.controlj.shim

import kotlin.reflect.KClass

/**
 * Copyright (C) Control-J Pty. Ltd. ACN 103594190
 * All rights reserved
 *
 * User: clyde
 * Date: 2019-04-01
 * Time: 07:00
 */
@Suppress("UNCHECKED_CAST")
class IosCxFactory : CxFactory {
    override fun animate(animationDuration: Double, function: () -> Unit, completion: () -> Unit) {
        function()
        completion()
    }

    override fun beginTransaction() {
    }

    override fun disablesActions() {
    }

    override fun commitTransaction() {
    }

    override fun uxColor(red: Double, green: Double, blue: Double, alpha: Double): UxColor {
        return IosCxColor(red, green, blue, alpha)
    }

    init {
        init()
    }

    override fun <T : CxBase> create(ofClass: KClass<T>): T {
        return when (ofClass) {
            CxPoint::class -> IosCxPoint() as T
            CxRect::class -> IosCxRect() as T
            CxSize::class -> IosCxSize() as T
            UxView::class -> IosUxView() as T
            UxEdgeInsets::class -> IosUxEdgeInsets() as T
            CxLayer::class -> IosCxLayer() as T
            UxButton::class -> IosUxButton() as T
            else -> error("Unknown Cx class ${ofClass.simpleName}")
        }
    }
}