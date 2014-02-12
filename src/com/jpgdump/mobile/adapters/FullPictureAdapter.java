package com.jpgdump.mobile.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.widget.BaseAdapter;

public class FullPictureAdapter extends FragmentPagerAdapter
{
    private BaseAdapter adapter;
    
    public FullPictureAdapter(FragmentManager fm, BaseAdapter adapter)
    {
        super(fm);
        this.adapter = adapter;
    }

    @Override
    public Fragment getItem(int arg0)
    {
        return null;
    }

    @Override
    public int getCount()
    {
        return adapter.getCount();
    }

}
