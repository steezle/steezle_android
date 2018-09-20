package com.steezle.e_com.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.steezle.e_com.R;
import com.steezle.e_com.utils.TouchImageView;

import java.util.ArrayList;


public class SliderZoomImageAdapter extends android.support.v4.view.PagerAdapter {

    private LayoutInflater layoutInflater;
    private Activity activity;
    private ArrayList<String> image_arraylist;

    public SliderZoomImageAdapter(Activity activity, ArrayList<String> image_arraylist) {
        this.activity = activity;
        this.image_arraylist = image_arraylist;
    }

    @Override
    public Object instantiateItem(ViewGroup container,final int position) {

        layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater.inflate(R.layout.layout_slider_zoom_image, container, false);

        TouchImageView iv_slider = (TouchImageView) view.findViewById(R.id.iv_slider);

        Glide.with(activity)
                .load(image_arraylist.get(position))
                .thumbnail(0.1f)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.img_placeholder)
                        .error(R.drawable.img_placeholder))
                .into(iv_slider);

        container.addView(view);

        return view;
    }

    @Override
    public int getCount() {
        return image_arraylist.size();
    }


    @Override
    public boolean isViewFromObject(View view, Object obj) {
        return view == obj;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
    }

}