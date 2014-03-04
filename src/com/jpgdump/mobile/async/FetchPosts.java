package com.jpgdump.mobile.async;

import java.util.List;

import android.os.AsyncTask;
import android.widget.AbsListView.OnScrollListener;
import android.widget.GridView;

import com.jpgdump.mobile.BuildConfig;
import com.jpgdump.mobile.HomeActivity;
import com.jpgdump.mobile.R;
import com.jpgdump.mobile.adapters.GridDisplayAdapter;
import com.jpgdump.mobile.fragments.RetainFragment;
import com.jpgdump.mobile.implementation.PostManager;
import com.jpgdump.mobile.interfaces.PostsInterface;
import com.jpgdump.mobile.listeners.PageBottomListener;
import com.jpgdump.mobile.objects.Post;
import com.jpgdump.mobile.util.ContextLogger;

public final class FetchPosts extends AsyncTask<String, Void, List<Post>>
{
    private final ContextLogger log = ContextLogger.getLogger(this);
    
    public static boolean pageBottomListenerFlag;
    
    HomeActivity activity;
    RetainFragment retainFragment;
    
    public FetchPosts(HomeActivity activity, RetainFragment retainFragment)
    {
        this.activity = activity;
        this.retainFragment = retainFragment;
    }
    
    @Override
    protected void onPreExecute()
    {
        pageBottomListenerFlag = false;
    }
    
    @Override
    protected List<Post> doInBackground(String... postParams)
    {
        /*
         * postParams[0]: holds the number of posts being queried
         * postParams[1]: holds the current number of posts
         * postParams[2]: holds a filter (if it exists)
         */
        
        PostsInterface posts = new PostManager();
        
        return posts.retrievePosts(postParams[0], postParams[1], "-id", postParams[2]);
    }
    
    @Override
    protected void onPostExecute(List<Post> posts)
    {
        if(BuildConfig.DEBUG)
        {
            log.v(posts.toString());
            log.v("Done");
        }
        GridView grid = (GridView) activity.findViewById(R.id.picture_viewer_activity_home);
        GridDisplayAdapter adapter = (GridDisplayAdapter) grid.getAdapter();
        if(adapter == null)
        {
            adapter = new GridDisplayAdapter(activity, posts);
            OnScrollListener scrollListener = new PageBottomListener(activity, retainFragment);
            
            grid.setAdapter(adapter);
            grid.setOnScrollListener(scrollListener);
        }
        else
        {
            adapter.addItems(posts);
            adapter.notifyDataSetChanged();
        }

        retainFragment.retainedAdapter = adapter;
        
        /*
         * Add posts as a variable to the retainFragment
         * If less than 4 images are left to be loaded, a new batch can be signaled
         */
        
        int postSize = posts.size();
        for(int x = 0; x < postSize; x++)
        {
            new LoadPicture(activity, adapter).execute(posts.get(x));
            
            pageBottomListenerFlag = (postSize - x) < 4;
        }
    }
    
    public static boolean allowDownload()
    {
        return pageBottomListenerFlag;
    }
}
