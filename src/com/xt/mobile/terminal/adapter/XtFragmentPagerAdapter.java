package com.xt.mobile.terminal.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gzhul on 2020/6/10.
 */
public class XtFragmentPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> mFragmentList = new ArrayList<>();

    public XtFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragmentList) {
        super(fm);
        if (fragmentList != null && fragmentList.size() > 0) mFragmentList.addAll(fragmentList);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    //Remove a page for the given position. The adapter is responsible for removing the view from its container
    //防止重新销毁视图
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //如果注释这行，那么不管怎么切换，page都不会被销毁
        super.destroyItem(container, position, object);
    }
}
