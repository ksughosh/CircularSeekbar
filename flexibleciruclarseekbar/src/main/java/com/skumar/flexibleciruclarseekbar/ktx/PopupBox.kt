package com.skumar.flexibleciruclarseekbar.ktx

import android.content.Context
import android.support.annotation.ColorRes
import android.support.annotation.LayoutRes
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import com.skumar.flexibleciruclarseekbar.R
import kotlinx.android.synthetic.main.popup_value.view.*

/**
 * Created by s.kumar on 04.06.18.
 * Copyright © 2017 LOOP. All rights reserved.
 */
class PopupBox : LinearLayout {
    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        inflate(R.layout.popup_value, true)
    }

    var text: String? = null
        set(value) {
            field = value
            bubble_box?.text = value
        }

    fun setTextWithColor(text: String, @ColorRes color: Int) {
        this.text = text
        context?.apply {
            bubble_box?.setTextColor(getColorFromResource(color))
        }
    }
}