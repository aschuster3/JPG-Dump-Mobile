package com.jpgdump.mobile.async;

import com.jpgdump.mobile.HomeActivity;
import com.jpgdump.mobile.objects.Post;
import com.jpgdump.mobile.util.PictureManager;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class LoadPicture extends AsyncTask<Post, Void, Bitmap>
{   
    HomeActivity activity;
    ImageView imageView;
    BaseAdapter adapter;
    
    public static int numThreads = 0;
    
    public LoadPicture(HomeActivity activity, ImageView imageView, BaseAdapter adapter)
    {
        this.activity = activity;
        this.imageView = imageView;
        this.adapter = adapter;
        numThreads++;
    }
    
    @Override
    protected Bitmap doInBackground(Post... post)
    {
        if(isCancelled())
        {
            return null;
        }
        else
        {
            post[0].setDownloading(true);
            
            //Fetch the post
            post[0].setThumbnailBitmap(PictureManager
                    .decodeSampleBitmapFromInputStream(
                            post[0].getUrl(), 150, 150));
            
            //Add to the cache
            activity.addBitmapToMemoryCache(post[0].getId(), 
                                            post[0].getThumbnailBitmap());
            
            post[0].setDownloading(false);
            return post[0].getThumbnailBitmap();
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
        imageView.setImageBitmap(bmp);
        adapter.notifyDataSetChanged();
    }
    
    public static int getNumThreads()
    {
        return numThreads;
    }
}
