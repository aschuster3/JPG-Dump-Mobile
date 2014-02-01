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
        if(BuildConfig.DEBUG)
        {
            Log.i(TAG, "Number of non-completed threads: "
                    + LoadPicture.getNumThreads());
        }
        
        boolean loadMore = 4 +
        firstVisible + visibleCount >= totalCount;

        if (loadMore)
        {
            isScrollCompleted();
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) { }

    private void isScrollCompleted()
    {   
        if (LoadPicture.allowDownload() && FetchPosts.allowDownload())
        {
            Integer[] postParams = { 6, adapter.getCount() };
            new FetchPosts(activity).execute(postParams);
        }
    }

}
