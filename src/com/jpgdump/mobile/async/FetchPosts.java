package com.jpgdump.mobile.async;

import java.util.ArrayList;

import com.jpgdump.mobile.implementation.PostManager;
import com.jpgdump.mobile.interfaces.PostsInterface;
import com.jpgdump.mobile.objects.Post;

import android.os.AsyncTask;

public class FetchPosts extends AsyncTask<Integer, Void, ArrayList<Post>>
{

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

}
