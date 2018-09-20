package com.steezle.e_com.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.steezle.e_com.R;
import com.steezle.e_com.model.SizeItem;

/**
 * Created by Hiren Patel on 1/25/2018.
 */

public class ColorView extends LinearLayout {

    private Context context;
    private SizeItem sizeItem;
    private ColorClickListener colorClickListener;


    public ColorView(Context context, SizeItem sizeItem, ColorClickListener colorClickListener) {
        super(context);
        this.context = context;
        this.sizeItem = sizeItem;
        this.colorClickListener = colorClickListener;

        intiView();
    }

    private void intiView() {

        View view = LayoutInflater.from(context).inflate(R.layout.color_view,null);

        final TextView tvColor = (TextView) view.findViewById(R.id.tvColor);
        final ImageView iv_colorMark = (ImageView) view.findViewById(R.id.iv_colorMark);
        final ImageView iv_backGround = (ImageView) view.findViewById(R.id.iv_backGround);
        //Drawable drawable = tvSize.getBackground();

        if(sizeItem.getColor_code() != null && sizeItem.getColor_code().length() > 0 ) {
            //drawable.setColorFilter(Color.parseColor(sizeItem.getColor_code()), PorterDuff.Mode.SRC_ATOP);
            tvColor.setBackgroundColor(Color.parseColor(sizeItem.getColor_code()));
        }

        tvColor.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                colorClickListener.onColorClick(
                        sizeItem.getVariation_id(),
                        sizeItem.getPa_color(),
                        sizeItem.getQty(), tvColor, iv_colorMark, iv_backGround);
            }
        });

        this.addView(view);
    }

    public interface ColorClickListener {
        void onColorClick(String size, String naem, String qty, TextView textView, ImageView rightMark,
                          ImageView backGround);

        //void onColorClick(String size, TextView textView);
    }
}
