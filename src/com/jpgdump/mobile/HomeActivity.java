package com.jpgdump.mobile;

import com.jpgdump.mobile.async.FetchPosts;
import com.jpgdump.mobile.fragments.RetainFragment;
import com.jpgdump.mobile.listeners.PageBottomListener;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;
import android.view.Menu;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.AbsListView.OnScrollListener;

public class HomeActivity extends Activity
{
    private LruCache<String, Bitmap> memoryCache;
    private GridView pictureGrid;
    private BaseAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        
        pictureGrid = (GridView)findViewById(R.id.picture_viewer_activity_home);
        
        
        //Instantiate the memory cache, used to later hold bitmaps retrieved
        //from JPG Dump
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        final int cacheSize = maxMemory / 4;

        //A handler for when the orientation changes
        RetainFragment retainFragment =
                RetainFragment.findOrCreateRetainFragment(getFragmentManager());
        
        memoryCache = retainFragment.retainedCache;
        adapter = retainFragment.retainedAdapter;
        if(memoryCache == null)
        {
            memoryCache = new LruCache<String, Bitmap>(cacheSize)
            {
                @Override
                protected int sizeOf(String key, Bitmap bitmap)
                {
                    return bitmap.getByteCount() / 1024;
                }
            };
            
            retainFragment.retainedCache = memoryCache;
        
            Integer[] postParams = { 10, 0 };
            
            if(isOnline())
            {
                new FetchPosts(this, retainFragment).execute(postParams);
            }
            else
            {
                //Handle not being connected
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.no_internet_dialog_title);
                builder.setMessage(R.string.no_internet_dialog_message);
                builder.setPositiveButton(R.string.no_internet_dialog_positive, null);
                builder.setNegativeButton(R.string.no_internet_dialog_negative, null);
            }
        }
        else
        {
            OnScrollListener scrollListener = new PageBottomListener(this, retainFragment);
            pictureGrid.setAdapter(adapter);
            pictureGrid.setOnScrollListener(scrollListener);
        }
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap)
    {
        if (getBitmapFromMemCache(key) == null)
        {
            memoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key)
    {
        return memoryCache.get(key);
    }

    public boolean isOnline()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting())
        {
            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }
    
}
