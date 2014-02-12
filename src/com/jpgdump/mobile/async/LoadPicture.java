package com.jpgdump.mobile.async;

import com.jpgdump.mobile.HomeActivity;
import com.jpgdump.mobile.objects.Post;
import com.jpgdump.mobile.util.PictureManager;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.BaseAdapter;

public class LoadPicture extends AsyncTask<Post, Void, Bitmap>
{   
    HomeActivity activity;
    BaseAdapter adapter;
    
    public static int numThreads = 0;
    
    public LoadPicture(HomeActivity activity, BaseAdapter adapter)
    {
        this.activity = activity;
        this.adapter = adapter;
        numThreads++;
    }
    
    @Override
    protected Bitmap doInBackground(Post... post)
    {
        if(isCancelled() || activity.diskLruCacheContainsKey(post[0].getId()))
        {
            return null;
        }
        else
        {
            //Fetch the post
            Bitmap bmp = PictureManager.decodeSampleBitmapFromInputStream(
                            post[0].getUrl(), 150, 150);
            
            //Add to the cache
            activity.addBitmapToMemoryCache(post[0].getId(), 
                                            bmp);
            
            return bmp;
        }
    }
    
    @Override
    protected void onCancelled()
    {
        numThreads--;
    }
    
    @Override
    protected void onPostExecute(Bitmap bmp)
    {
        numThreads--;
        adapter.notifyDataSetChanged();
    }
    
    public static int getNumThreads()
    {
        return numThreads;
    }
    
    /* A flag for PageBottomListener */
    public static boolean allowDownload()
    {
        return numThreads < 4;
    }
}
