package com.jpgdump.mobile.async;

import java.util.ArrayList;

import com.jpgdump.mobile.BuildConfig;
import com.jpgdump.mobile.HomeActivity;
import com.jpgdump.mobile.R;
import com.jpgdump.mobile.adapters.GridDisplayAdapter;
import com.jpgdump.mobile.fragments.RetainFragment;
import com.jpgdump.mobile.implementation.PostManager;
import com.jpgdump.mobile.interfaces.PostsInterface;
import com.jpgdump.mobile.listeners.PageBottomListener;
import com.jpgdump.mobile.objects.Post;
import com.jpgdump.mobile.util.Tags;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.AbsListView.OnScrollListener;
import android.widget.GridView;

public class FetchPosts extends AsyncTask<Integer, Void, ArrayList<Post>>
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
    protected ArrayList<Post> doInBackground(Integer... postParams)
    {
        /*
         * postParams[0]: holds the number of posts being queried
         * postParams[1]: holds the current number of posts
         */
        
        PostsInterface posts = new PostManager();
        
        //Get the temporary loading image and add it to the memory cache 
        Bitmap loadingPic = BitmapFactory.decodeResource(activity.getResources(), R.drawable.jpg_dump_temp_loading_pic);
        activity.addBitmapToMemoryCache(Tags.LOADING_BITMAP, loadingPic);
        
        return posts.retrievePosts(postParams[0], postParams[1], "-id", "");
    }
    
    @Override
    protected void onPostExecute(ArrayList<Post> posts)
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
        
        for(int x = 0; x < posts.size(); x++)
        {
            new LoadPicture(activity, adapter).execute(posts.get(x));
        }
        
        pageBottomListenerFlag = true;
    }
    
    public static boolean allowDownload()
    {
        return pageBottomListenerFlag;
    }
}
