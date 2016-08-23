package com.wzy.helptravel.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by wzy on 2016/7/8.
 */
public class MainFragmentAdapter extends FragmentPagerAdapter {

    private Context mContext;
    private List<Fragment> mFragmentList;

    public MainFragmentAdapter(Context context, List<Fragment> fragmentList, FragmentManager fm) {
        super(fm);
        mContext = context;
        mFragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }
}
