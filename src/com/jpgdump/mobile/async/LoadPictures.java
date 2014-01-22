package com.jpgdump.mobile.async;

import java.util.ArrayList;

import com.jpgdump.mobile.R;
import com.jpgdump.mobile.adapters.GridDisplayAdapter;
import com.jpgdump.mobile.objects.Post;
import com.jpgdump.mobile.util.PictureManager;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.BaseAdapter;
import android.widget.GridView;

public class LoadPictures extends AsyncTask<Void, Void, ArrayList<Post>>
{
    ArrayList<Post> prePosts, picturedPosts;
    Activity activity;
    BaseAdapter adapter;
    
    
    public LoadPictures(Activity activity, ArrayList<Post> prePosts)
    {
        this.activity = activity;
        this.prePosts = prePosts;
    }
    
    @Override
    protected void onPreExecute()
    {
        picturedPosts = new ArrayList<Post>();
        adapter = new GridDisplayAdapter(activity, picturedPosts);
        GridView grid = (GridView) activity.findViewById(R.id.picture_viewer_activity_home);
        grid.setAdapter(adapter);
    }
    
    @Override
    protected ArrayList<Post> doInBackground(Void... arg0)
    {
        int postSize = prePosts.size();
        Post post;
        for(int j = 0; j < postSize; j++)
        {   
            //If the process is ended early, return
            if(isCancelled())
            {
                return null;
            }
            post = prePosts.get(j);
            post.setThumbnailBitmap(
                    PictureManager.decodeSampleBitmapFromInputStream(
                            post.getUrl(), 150, 150));
            picturedPosts.add(post);
            activity.runOnUiThread(new Runnable(){

                @Override
                public void run()
                {
                    adapter.notifyDataSetChanged();
                }
                
            });
        }
        return null;
    }
}
