package com.steezle.e_com.view;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;


public class MyCustomTextview extends android.support.v7.widget.AppCompatTextView {

    public MyCustomTextview(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.setTypeface( Typeface.createFromAsset(context.getAssets(),"fonts/PROXIMA NOVA REGULAR.ttf"));
    }

    public MyCustomTextview(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface( Typeface.createFromAsset(context.getAssets(),"fonts/PROXIMA NOVA REGULAR.ttf"));
    }

    public MyCustomTextview(Context context) {
        super(context);
        this.setTypeface( Typeface.createFromAsset(context.getAssets(),"fonts/PROXIMA NOVA REGULAR.ttf"));
    }
}
