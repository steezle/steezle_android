package com.steezle.e_com.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.steezle.e_com.R;
import com.steezle.e_com.utils.ExtendedViewPager;

import java.util.ArrayList;


public class SliderAdapter extends android.support.v4.view.PagerAdapter {

    private LayoutInflater layoutInflater;
    private Activity activity;
    private ArrayList<String> image_arraylist;
    private TextView[] dots;
    LinearLayout ll_dots;

    public SliderAdapter(Activity activity, ArrayList<String> image_arraylist) {
        this.activity = activity;
        this.image_arraylist = image_arraylist;
    }


    @Override
    public Object instantiateItem(ViewGroup container,final int position) {

        layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater.inflate(R.layout.layout_slider, container, false);

        ImageView iv_slider = (ImageView) view.findViewById(R.id.iv_slider);

        Glide.with(activity)
                .load(image_arraylist.get(position))
                .thumbnail(0.1f)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.img_placeholder)
                        .error(R.drawable.img_placeholder))
                .into(iv_slider);

        container.addView(view);

        iv_slider.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog=new Dialog( activity, R.style.AppTheme_NoActionBar );
                dialog.setContentView( R.layout.product_detail_dialog );
                ll_dots=(LinearLayout)dialog.findViewById( R.id.ll_dots );
                ImageView img_close=(ImageView)dialog.findViewById( R.id.img_close );
                ExtendedViewPager vp_slider=(ExtendedViewPager)dialog.findViewById( R.id.vp_slider );
                SliderZoomImageAdapter imageAdapter = new SliderZoomImageAdapter(activity, image_arraylist);


                //SliderAdapter sliderPagerAdapter = new SliderAdapter(activity, image_arraylist);
                vp_slider.setAdapter(imageAdapter);
                vp_slider.setCurrentItem( position );
                imageAdapter.notifyDataSetChanged();
                //sliderPagerAdapter.notifyDataSetChanged();
                addBottomDots(position);

                vp_slider.setOnPageChangeListener( new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        addBottomDots( position );
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                } );
                img_close.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                } );
                dialog.show();

            }
        } );
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
    private void addBottomDots(int currentPage) {

        dots = new TextView[image_arraylist.size()];

        ll_dots.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(activity);
            dots[i].setCompoundDrawablePadding(7);
            dots[i].setTextSize(35);
            dots[i].setCompoundDrawablesWithIntrinsicBounds(R.drawable.roundblack, 0, 0, 0);
            ll_dots.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setCompoundDrawablesWithIntrinsicBounds(R.drawable.round, 0, 0, 0);
    }

}