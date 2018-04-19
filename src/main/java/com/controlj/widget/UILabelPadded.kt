package com.controlj.widget

import org.robovm.apple.coregraphics.CGRect
import org.robovm.apple.uikit.UIEdgeInsets
import org.robovm.apple.uikit.UILabel

/**
 * Created by clyde on 17/4/18.
 */
class UILabelPadded(padding: UIEdgeInsets = UIEdgeInsets.Zero()) : UILabel() {

    var padding =  padding
    set(value) {
        field = value
        invalidateIntrinsicContentSize()
    }

    override fun getTextRect(bounds: CGRect, lineLimit: Long): CGRect {
        val textRect = super.getTextRect(bounds, lineLimit)
        return textRect.inset(UIEdgeInsets(-padding.top, -padding.left, -padding.bottom, -padding.right))
    }

    override fun drawText(rect: CGRect) {
        super.drawText(rect.inset(padding))
    }
}
