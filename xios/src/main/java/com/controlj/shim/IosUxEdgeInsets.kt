package com.controlj.shim

import org.robovm.apple.uikit.UIEdgeInsets

class IosUxEdgeInsets(val uiEdgeInsets: UIEdgeInsets= UIEdgeInsets()) : UxEdgeInsets {
    override var top: Double
        get() = uiEdgeInsets.top
        set(value) {uiEdgeInsets.top = value}
    override var left: Double
        get() = uiEdgeInsets.left
        set(value) {uiEdgeInsets.left = value}
    override var bottom: Double
        get() = uiEdgeInsets.bottom
        set(value) {uiEdgeInsets.bottom = value}
    override var right: Double
        get() = uiEdgeInsets.right
        set(value) {uiEdgeInsets.right = value}

    constructor(  top: Double,   left: Double,   bottom: Double,   right: Double) :this(UIEdgeInsets(top, left, bottom, right))

}
fun UIEdgeInsets.asUxEdgeInsets(): UxEdgeInsets {
    return IosCxEdgeInsets(top, left, bottom, right)
}

