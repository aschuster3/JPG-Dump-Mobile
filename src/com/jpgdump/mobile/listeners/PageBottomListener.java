package com.jpgdump.mobile.listeners;

import com.jpgdump.mobile.BuildConfig;
import com.jpgdump.mobile.HomeActivity;
import com.jpgdump.mobile.async.FetchPosts;
import com.jpgdump.mobile.async.LoadPicture;

import android.util.Log;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;

public class PageBottomListener implements OnScrollListener
{
    private static final String TAG = "PageBottomListener";
    
    private int currentVisibleItemCount = 0;
    private int currentScrollState = 0;

    private HomeActivity activity;
    private BaseAdapter adapter;

    public PageBottomListener(HomeActivity activity, BaseAdapter adapter)
    {
        this.activity = activity;
        this.adapter = adapter;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisible, int visibleCount,
            int totalCount)
    {
        boolean loadMore = /* maybe add a padding */
        firstVisible + visibleCount >= totalCount;

        if (loadMore)
        {
            isScrollCompleted();
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState)
    {
       //Not implemented
    }

    private void isScrollCompleted()
    {
        if(BuildConfig.DEBUG)
        {
            Log.i(TAG, "Number of non-completed threads: "
                    + LoadPicture.getNumThreads());
        }
        
        if (LoadPicture.getNumThreads() < 4)
        {
            Integer[] postParams = { 6, adapter.getCount() };
            new FetchPosts(activity).execute(postParams);
        }
    }

}
