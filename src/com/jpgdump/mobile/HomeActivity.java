package com.jpgdump.mobile;

import com.jpgdump.mobile.async.FetchPosts;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.util.LruCache;
import android.view.Menu;

public class HomeActivity extends Activity
{
    private LruCache<Integer, Bitmap> memoryCache;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        final int cacheSize = maxMemory / 4;

        memoryCache = new LruCache<Integer, Bitmap>(cacheSize)
        {
            @Override
            protected int sizeOf(Integer key, Bitmap bitmap)
            {
                return bitmap.getByteCount() / 1024;
            }
        };

        Integer[] postParams = {25, 0};

        new FetchPosts(this).execute(postParams);
    }

    public void addBitmapToMemoryCache(Integer key, Bitmap bitmap)
    {
        if (getBitmapFromMemCache(key) == null)
        {
            memoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(Integer key)
    {
        return memoryCache.get(key);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

}
