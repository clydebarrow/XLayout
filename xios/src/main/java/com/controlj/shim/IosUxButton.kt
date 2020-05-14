package com.controlj.shim

import com.controlj.layout.Layout
import org.robovm.apple.coregraphics.CGSize
import org.robovm.apple.uikit.UIButton
import org.robovm.apple.uikit.UIControlState
import org.robovm.apple.uikit.UIImage

/**
 * Copyright (C) Control-J Pty. Ltd. ACN 103594190
 * All rights reserved
 *
 * User: clyde
 * Date: 2019-08-28
 * Time: 14:03
 */
class IosUxButton(
        val uiButton: UIButton = UIButton(),
        layout: Layout = Layout(),
        name: String = uiButton::class.simpleName.toString()

) : IosUxView(uiButton, layout, name), UxButton {

    override var text: String
        get() = uiButton.getTitle(UIControlState.Normal)
        set(value) {
            uiButton.setTitle(value, UIControlState.Normal)
        }
    override var image: String = ""
        set(value) {
            field = value
            uiButton.setImage(UIImage.getImage(value), UIControlState.Normal)
        }
    override var textColor: UxColor
        get() = IosCxColor(uiButton.getTitleColor(UIControlState.Normal))
        set(value) {
            uiButton.setTitleColor((value as IosCxColor).uiColor, UIControlState.Normal)
        }
    override var action: (UxButton) -> Unit = {}

    init {
        uiButton.addOnTouchUpInsideListener { _, _ -> action(this) }
    }
}