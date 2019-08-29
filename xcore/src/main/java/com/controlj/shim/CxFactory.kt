/*
 * Copyright (c) 2019 Control-J Pty. Ltd. All rights reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * .
 *
 */

package com.controlj.shim

import kotlin.reflect.KClass

/**
 * Copyright (C) Control-J Pty. Ltd. ACN 103594190
 * All rights reserved
 *
 * User: clyde
 * Date: 2019-03-30
 * Time: 10:52
 *
 * The interface for creating Cx objects
 *
 */
interface CxFactory {

    /**
     * To be called by the singleton implementation, stores itself as the instance
     */
    fun init() {
        init(this)
    }

    /**
     * Creates an instance of the specified class
     */
    fun <T : CxBase> create(ofClass: KClass<T>): T

    /**
     * Create a [UxColor] object from the provided components
     */
    fun uxColor(red: Double, green: Double, blue: Double, alpha: Double): UxColor

    fun uxColor(red: Int, green: Int, blue: Int, alpha: Int = 0xFF): UxColor {
        return uxColor(red / 255.0, green / 255.0, blue / 255.0, alpha / 255.0)
    }

    fun uxColor(intVal: Int): UxColor {
        return CxFactory.instance.uxColor(
                (intVal shr 16) and 0xFF,
                (intVal shr 8) and 0xFF,
                intVal and 0xFF,
                if (intVal and 0xFF000000.toInt() == 0) 0xFF else (intVal shr 24) and 0xFF
        )
    }

    /**
     * Animate the changes performed in the [function] closure. The animation will run for [animationDuration]
     * seconds, and when finished the [completion] closure will be called.
     */
    fun animate(animationDuration: Double, function: () -> Unit, completion: () -> Unit = {})

    /**
     * Begin transactions on layers.
     */
    fun beginTransaction()

    /**
     * Disable animations on the changes about to happen
     */
    fun disablesActions()

    /**
     * Actions in the current transaction are completed, commit the transaction.
     */
    fun commitTransaction()

    /**
     * Get the b
     */
    companion object {
        /**
         * Create an object given the class
         */

        /**
         * Create instances of things
         */

        fun uxEdgeInsets(
                top: Double = 0.0,
                left: Double = 0.0,
                bottom: Double = 0.0,
                right: Double = 0.0
        ): UxEdgeInsets {
            val e = instance.create(UxEdgeInsets::class)
            e.top = top
            e.left = left
            e.bottom = bottom
            e.right = right
            return e
        }

        /**
         * Create an instance of a [CxPoint] from the supplied coordinates
         */
        fun cxPoint(x: Double = 0.0, y: Double = 0.0): CxPoint {
            val p = instance.create(CxPoint::class)
            p.x = x
            p.y = y
            return p
        }

        /**
         * Create an instance of a [CxSize] from the supplied dimensions
         */
        fun cxSize(width: Double = 0.0, height: Double = 0.0): CxSize {
            val s = instance.create(CxSize::class)
            s.width = width
            s.height = height
            return s
        }

        /**
         * Create an instance of a [CxRect] from the supplied origin and dimensions
         */
        fun cxRect(origin: CxPoint, size: CxSize): CxRect {
            val r = instance.create(CxRect::class)
            r.origin = origin
            r.size = size
            return r
        }

        /**
         * Create an instance of a [CxRect] from the supplied origin and dimensions
         */
        fun cxRect(x: Double = 0.0, y: Double = 0.0, width: Double = 0.0, height: Double = 0.0): CxRect {
            return cxRect(cxPoint(x, y), cxSize(width, height))
        }

        /**
         * Create an instance of a [UxView] with an optional frame
         */
        fun uxView(frame: CxRect = cxRect()): UxView {
            val u = instance.create(UxView::class)
            u.frame = frame
            return u
        }

        /**
         * Create a button
         */

        fun uxButton(text: String, image: String = "", action: (UxButton) -> Unit = {}): UxButton {
            return instance.create(UxButton::class).also {
                it.text = text
                it.image = image
                it.action = action
            }
        }

        fun beginTransaction() = instance.beginTransaction()

        fun disablesActions() = instance.disablesActions()

        fun commitTransaction() = instance.commitTransaction()

        /**
         * The singleton implementation of this interface
         */
        lateinit var instance: CxFactory
            private set

        private fun init(inst: CxFactory) {
            if (::instance.isInitialized)
                error("Duplicate creation of CxFactory")
            instance = inst
        }

        /**
         * Convenience method to create an object generically
         */
        inline fun <reified T : CxBase> create(): T {
            return instance.create(T::class)
        }

        /**
         * Animate the changes performed in the [function] closure. The animation will run for [animationDuration]
         * seconds, and when finished the [completion] closure will be called.
         */
        fun animate(animationDuration: Double, function: () -> Unit, completion: () -> Unit = {}) {
            instance.animate(animationDuration, function, completion)
        }
    }
}