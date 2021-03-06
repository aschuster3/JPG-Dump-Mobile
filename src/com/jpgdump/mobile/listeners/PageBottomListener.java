package com.jpgdump.mobile.listeners;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

import com.jpgdump.mobile.BuildConfig;
import com.jpgdump.mobile.HomeActivity;
import com.jpgdump.mobile.async.FetchPosts;
import com.jpgdump.mobile.async.LoadPicture;
import com.jpgdump.mobile.fragments.RetainFragment;
import com.jpgdump.mobile.util.ContextFormattingLogger;
import com.jpgdump.mobile.util.Tags;

public class PageBottomListener implements OnScrollListener
{
    private final ContextFormattingLogger log = ContextFormattingLogger.getLogger(this);

    private HomeActivity activity;
    private RetainFragment retainFragment;

    public PageBottomListener(HomeActivity activity, RetainFragment retainFragment)
    {
        this.activity = activity;
        this.retainFragment = retainFragment;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisible, int visibleCount,
            int totalCount)
    {
        if(BuildConfig.DEBUG)
        {
            log.i("Number of non-completed threads: %d", LoadPicture.getNumThreads());
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
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(activity);
            
            //Check to see if the filter is needed
            String sfwFilter;
            if(settings.getBoolean(Tags.SFW, false))
            {
                sfwFilter = "";
            }
            else
            {
                sfwFilter = "[[\"safety==0\"]]";
            }
            
            String[] postParams = { Tags.ADD_POSTS, "" + retainFragment.retainedAdapter.getCount(), sfwFilter };
            new FetchPosts(activity, retainFragment).execute(postParams);
        }
    }

}
