package com.skumar.flexibleciruclarseekbar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
/**
 * The MIT License (MIT)
 * Copyright (c) <2016> <Sughosh Krishna Kumar >
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

/**
 * Popup box for a bubble out in the slider view
 * Created by Sughosh Krishna Kumar on 12/05/16.
 */
public class PopupBox extends LinearLayout {
    Context mContext;
    TextView mTextView;

    // Initialize constructors
    public PopupBox(Context context) {
        super(context);
        mContext = context;
        layoutInflation();
    }

    public PopupBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        layoutInflation();
    }

    public PopupBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        layoutInflation();
    }

    /**
     * Inflate the layout to be displayed
     */
    private void layoutInflation() {
        LayoutInflater.from(mContext).inflate(R.layout.popup_value, this, true);
        mTextView = (TextView) findViewById(R.id.bubble_box);
    }

    /**
     * Set text value for the textview
     *
     * @param text  string
     * @param color color of the string
     */
    public void setTextWithColor(String text, int color) {
        mTextView.setText(text);
        mTextView.setTextColor(color);
    }

    /**
     * Set text value
     *
     * @param text string
     */
    public void setText(String text) {
        mTextView.setText(text);
    }


}
