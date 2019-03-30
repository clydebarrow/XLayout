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

    fun <T : CxBase> create(ofClass: KClass<T>): T

    fun uxColor(red: Double, green: Double, blue: Double, alpha: Double): UxColor

    companion object {
        /**
         * Create an object given the class
         */

        /**
         * Create instances of things
         */

        fun uxEdgeInsets(top: Double, left: Double, bottom: Double, right: Double): UxEdgeInsets {
            val e = instance.create(UxEdgeInsets::class)
            e.top = top
            e.left = left
            e.bottom = bottom
            e.right = right
            return e
        }

        fun cxPoint(x: Double = 0.0, y: Double = 0.0): CxPoint {
            val p = instance.create(CxPoint::class)
            p.x = x
            p.y = y
            return p
        }

        fun cxSize(width: Double = 0.0, height: Double = 0.0): CxSize {
            val s = instance.create(CxSize::class)
            s.width = width
            s.height = height
            return s
        }

        fun cxRect(origin: CxPoint, size: CxSize): CxRect {
            val r = instance.create(CxRect::class)
            r.origin = origin
            r.size = size
            return r
        }

        fun cxRect(x: Double = 0.0, y: Double = 0.0, width: Double = 0.0, height: Double = 0.0): CxRect {
            return cxRect(cxPoint(x, y), cxSize(width, height))
        }

        fun uxView(frame: CxRect = cxRect()): UxView {
            val u = instance.create(UxView::class)
            u.frame = frame
            return u
        }

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
    }
}