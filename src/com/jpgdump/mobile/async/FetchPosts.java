package com.jpgdump.mobile.async;

import java.util.ArrayList;

import com.jpgdump.mobile.BuildConfig;
import com.jpgdump.mobile.implementation.PostManager;
import com.jpgdump.mobile.interfaces.PostsInterface;
import com.jpgdump.mobile.objects.Post;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

public class FetchPosts extends AsyncTask<Integer, Void, ArrayList<Post>>
{
    private static final String TAG = "FetchPosts";
    Activity activity;
    
    public FetchPosts(Activity activity)
    {
        this.activity = activity;
    }
    
    @Override
    protected ArrayList<Post> doInBackground(Integer... postParams)
    {
        /*
         * postParams[0]: holds the number of posts being queried
         * postParams[1]: holds the current number of posts
         */
        
        PostsInterface posts = new PostManager();
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
        new LoadPictures(activity, posts).execute();
    }
}
