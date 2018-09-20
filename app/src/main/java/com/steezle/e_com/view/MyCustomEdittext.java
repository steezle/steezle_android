package com.steezle.e_com.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by Bhavan on 4/22/2017.
 */
public class MyCustomEdittext extends EditText {
    public MyCustomEdittext(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.setTypeface( Typeface.createFromAsset(context.getAssets(), "fonts/PROXIMA NOVA REGULAR.ttf"));
    }

    public MyCustomEdittext(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface( Typeface.createFromAsset(context.getAssets(), "fonts/PROXIMA NOVA REGULAR.ttf"));
    }

    public MyCustomEdittext(Context context) {
        super(context);
        this.setTypeface( Typeface.createFromAsset(context.getAssets(), "fonts/PROXIMA NOVA REGULAR.ttf"));
    }
}
