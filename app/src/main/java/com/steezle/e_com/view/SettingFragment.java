package com.steezle.e_com.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.steezle.e_com.R;

import java.util.ArrayList;
import java.util.List;

import com.steezle.e_com.adapter.SettingPagerAdapter;
import com.steezle.e_com.fragments.ProfileFragment;
import com.steezle.e_com.fragments.AddressFragment;
import com.steezle.e_com.fragments.PaymentFragment;

import info.hoang8f.android.segmented.SegmentedGroup;

/**
 * Created by juli on 15/12/17.
 */
public class SettingFragment extends Fragment implements RadioGroup.OnCheckedChangeListener {


    private SegmentedGroup segmented5;
    private ImageView imgsetting;
    private List<SettingPagerAdapter> mBaseFragments;
    private int position = 0;

    public static SettingFragment newInstance() {
        SettingFragment fragment = new SettingFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.act_settings, container, false);
        segmented5 = (SegmentedGroup) v.findViewById(R.id.segmented2);
        segmented5.setOnCheckedChangeListener(this);
        initData();
        imgsetting = (ImageView) v.findViewById(R.id.img_setting);
        imgsetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), MoreActivity.class);
                startActivity(i);
            }
        });
        SettingPagerAdapter currentFragment = getFragment();
        replaceFragment(currentFragment);

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void initData() {

        mBaseFragments = new ArrayList<>();
        mBaseFragments.add(new ProfileFragment());
        mBaseFragments.add(new AddressFragment());
        mBaseFragments.add(new PaymentFragment());

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

        switch (checkedId) {
            case R.id.button21:
                position = 0;
                break;
            case R.id.button23:
                position = 1;
                break;
            case R.id.button24:
                position = 2;
                break;
            default:
                position = 0;
                break;
        }


        SettingPagerAdapter currentFragment = getFragment();
        replaceFragment(currentFragment);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (getFragment() instanceof ProfileFragment) {
            getFragment().onActivityResult(requestCode, resultCode, data);
        }

    }

    private void replaceFragment(SettingPagerAdapter fragment) {

        FragmentManager fm = getActivity().getSupportFragmentManager();

        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.fl_main, fragment);
        transaction.commit();

    }

    private SettingPagerAdapter getFragment() {
        return mBaseFragments.get(position);
    }
}


