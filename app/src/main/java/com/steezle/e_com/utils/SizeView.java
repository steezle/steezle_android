package com.steezle.e_com.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.steezle.e_com.R;

/**
 * Created by Hiren Patel on 1/25/2018.
 */

public class SizeView extends LinearLayout {

    private Context context;
    private String size;
    private SizeClickListener sizeClickListener;


    public SizeView(Context context, String size, SizeClickListener sizeClickListener) {
        super(context);
        this.context = context;
        this.size = size;
        this.sizeClickListener = sizeClickListener;
        intiView();
    }

    private void intiView() {

        View view = LayoutInflater.from(context).inflate(R.layout.size_view,null);
        final TextView tvSize = (TextView) view.findViewById(R.id.tvSize);
        tvSize.setText(size);
        tvSize.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                sizeClickListener.onSizeClick(tvSize.getText().toString() , tvSize);
            }
        });

        this.addView(view);
    }

    public interface SizeClickListener {
        void onSizeClick(String size, TextView textSize);
    }
}
