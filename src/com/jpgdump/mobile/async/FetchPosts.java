package com.jpgdump.mobile.async;

import java.util.List;

import android.os.AsyncTask;
import android.util.Log;
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

public final class FetchPosts extends AsyncTask<Integer, Void, List<Post>>
{
    private static final String TAG = "FetchPosts";
    
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
    protected List<Post> doInBackground(Integer... postParams)
    {
        /*
         * postParams[0]: holds the number of posts being queried
         * postParams[1]: holds the current number of posts
         * postParams[2]: holds a filter (if it exists)
         */
        
        PostsInterface posts = new PostManager();
        
        //By default, no filter, but check if sfw is enabled
        String filter = "";
        
        if(postParams[2] == 1)
        {
            filter = "[[\"safety==0\"]]";
        }
        
        return posts.retrievePosts(postParams[0], postParams[1], "-id", filter);
    }
    
    @Override
    protected void onPostExecute(List<Post> posts)
    {
        if(BuildConfig.DEBUG)
        {
            Log.v(TAG, posts.toString() + "\n");
            Log.v(TAG, "Done");
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
         * 
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
