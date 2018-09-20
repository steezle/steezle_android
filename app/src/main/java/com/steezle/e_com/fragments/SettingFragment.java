package com.steezle.e_com.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.steezle.e_com.R;
import com.steezle.e_com.adapter.SettingPagerAdapter;
import com.steezle.e_com.model.User;
import com.steezle.e_com.networking.AppSharedPreferences;

import java.util.ArrayList;
import java.util.List;

import info.hoang8f.android.segmented.SegmentedGroup;

import static com.facebook.FacebookSdk.getApplicationContext;


public class SettingFragment extends Fragment implements RadioGroup.OnCheckedChangeListener {
    public static SettingFragment newInstance() {
        SettingFragment fragment = new SettingFragment();
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    SegmentedGroup segmented5;
    private User user;
    private List<SettingPagerAdapter> mBaseFragments;
    private int position = 0;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.act_settings, container, false);
        segmented5 = (SegmentedGroup) v.findViewById(R.id.segmented2);
        segmented5.setOnCheckedChangeListener(this);
        initData();

        user = AppSharedPreferences.getSharePreference(getApplicationContext()).getUser();

        SettingPagerAdapter currentFragment = getFragment();
        replaceFragment(currentFragment);

        return v;
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