package com.jpgdump.mobile;

import java.io.File;

import com.jpgdump.mobile.async.FetchPosts;
import com.jpgdump.mobile.fragments.RetainFragment;
import com.jpgdump.mobile.listeners.GridPressListener;
import com.jpgdump.mobile.listeners.PageBottomListener;
import com.jpgdump.mobile.util.DiskLruImageCache;
import com.jpgdump.mobile.util.Tags;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;
import android.view.Menu;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.AbsListView.OnScrollListener;

public class HomeActivity extends Activity
{
    private LruCache<String, Bitmap> memoryCache;
    private DiskLruImageCache diskLruCache;
    private final Object diskCacheLock = new Object();

    private GridView pictureGrid;
    private BaseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        pictureGrid = (GridView) findViewById(R.id.picture_viewer_activity_home);

        OnItemClickListener gridPress = new GridPressListener(this);
        pictureGrid.setOnItemClickListener(gridPress);

        // Instantiate the memory cache, used to later hold bitmaps retrieved
        // from JPG Dump
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        final int cacheSize = maxMemory / 4;

        // A handler for when the orientation changes
        RetainFragment retainFragment = RetainFragment
                .findOrCreateRetainFragment(getFragmentManager());

        memoryCache = retainFragment.retainedCache;
        diskLruCache = retainFragment.retainedDiskCache;
        adapter = retainFragment.retainedAdapter;
        if (memoryCache == null)
        {
            // Initialize disk cache on background thread
            new InitDiskCacheTask(retainFragment).execute();

            // Initialize mem cache
            memoryCache = new LruCache<String, Bitmap>(cacheSize)
            {
                @Override
                protected int sizeOf(String key, Bitmap bitmap)
                {
                    return bitmap.getByteCount() / 1024;
                }
            };

            retainFragment.retainedCache = memoryCache;

            Integer[] postParams = { Tags.START_POSTS , 0 };

            if (isOnline())
            {
                new FetchPosts(this, retainFragment).execute(postParams);
            }
            else
            {
                // Handle not being connected
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.no_internet_dialog_title);
                builder.setMessage(R.string.no_internet_dialog_message);
                builder.setPositiveButton(R.string.no_internet_dialog_positive,
                        null);
                builder.setNegativeButton(R.string.no_internet_dialog_negative,
                        null);
            }
        }
        else
        {
            OnScrollListener scrollListener = new PageBottomListener(this,
                    retainFragment);
            pictureGrid.setAdapter(adapter);
            pictureGrid.setOnScrollListener(scrollListener);
        }
    }
    
    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap)
    {
        // Add to mem cache
        if (getBitmapFromMemCache(key) == null)
        {
            memoryCache.put(key, bitmap);
        }
        
        // Also add to disk cache
        synchronized (diskCacheLock)
        {
            if (diskLruCache != null && !diskLruCache.containsKey(key))
            {
                Log.i("Home Activity", "Cache is being added to");
                diskLruCache.put(key, bitmap);
            }
        }

    }

    public Bitmap getBitmapFromMemCache(String key)
    {
        return memoryCache.get(key);
    }

    public Bitmap getBitmapFromDiskCache(String key)
    {
        synchronized (diskCacheLock)
        {
            // Wait while disk cache is started from background thread
            while (diskLruCache.isDiskCacheStarting())
            {
                try
                {
                    diskCacheLock.wait();
                }
                catch (InterruptedException e)
                {
                }
            }
            if (diskLruCache != null)
            {

                Log.i("Home Activity", "Cache is being accessed");
                return diskLruCache.getBitmap(key);
            }
        }
        return null;
    }

    public boolean diskLruCacheContainsKey(String key)
    {
        return diskLruCache.containsKey(key);
    }
    
    public boolean isOnline()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        
        return (netInfo != null && netInfo.isConnectedOrConnecting());
    }

    // Creates a unique subdirectory of the designated app cache directory.
    // Tries to use external but if not mounted, falls back on internal storage.
    public static File getDiskCacheDir(Context context, String uniqueName)
    {
        // Check if media is mounted or storage is built-in, if so, try and use
        // external cache dir otherwise use internal cache dir

        final String cachePath = Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())
                || !Environment.isExternalStorageRemovable() ? context
                .getExternalCacheDir().getPath() : context.getCacheDir()
                .getPath();

        return new File(cachePath + File.separator + uniqueName);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    class InitDiskCacheTask extends AsyncTask<Void, Void, Void>
    {
        RetainFragment retainFragment;
        
        public InitDiskCacheTask(RetainFragment retainFragment)
        {
            this.retainFragment = retainFragment;
        }
        
        @Override
        protected Void doInBackground(Void... params)
        {
            synchronized (diskCacheLock)
            {
                diskLruCache = new DiskLruImageCache(HomeActivity.this, diskCacheLock);
            }
            return null;
        }
        
        @Override
        protected void onPostExecute(Void ignore)
        {
            retainFragment.retainedDiskCache = diskLruCache;
        }
    }

}
