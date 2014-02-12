package com.jpgdump.mobile.fragments;

import com.jpgdump.mobile.util.DiskLruImageCache;
import com.jpgdump.mobile.util.Tags;

import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.LruCache;
import android.widget.BaseAdapter;


/*
 * The purpose of this Fragment is to persist data
 * across orientation changes.  It does not directly
 * display views.
 */
public class RetainFragment extends Fragment
{    
    public LruCache<String, Bitmap> retainedCache;
    public DiskLruImageCache retainedDiskCache;
    public BaseAdapter retainedAdapter;

    public RetainFragment() {}

    public static RetainFragment findOrCreateRetainFragment(FragmentManager fm)
    {
        RetainFragment fragment = (RetainFragment) fm.findFragmentByTag(Tags.RETAIN_FRAG);
        if (fragment == null)
        {
            fragment = new RetainFragment();
            fm.beginTransaction().add(fragment, Tags.RETAIN_FRAG).commit();
        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        //Makes sure the fragment persists on orientation change
        setRetainInstance(true);
    }

}
