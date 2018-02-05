package com.syezon.note_xh.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by admin on 2016/6/8.
 */
public class MyBaseFragmentAdapter extends FragmentPagerAdapter {
    private List<Fragment> views;

    public MyBaseFragmentAdapter(FragmentManager fm, List<Fragment> views) {
        super(fm);
        this.views=views;
    }

    @Override
    public Fragment getItem(int position) {
        return views.get(position);
    }

    @Override
    public int getCount() {
        return views==null? 0: views.size();
    }
}
