package com.jpgdump.mobile;

import java.io.File;

import com.jpgdump.mobile.async.CreateSession;
import com.jpgdump.mobile.async.FetchPosts;
import com.jpgdump.mobile.fragments.RetainFragment;
import com.jpgdump.mobile.listeners.GridPressListener;
import com.jpgdump.mobile.listeners.NoInternetDialogListener;
import com.jpgdump.mobile.listeners.PageBottomListener;
import com.jpgdump.mobile.objects.Post;
import com.jpgdump.mobile.util.DiskLruImageCache;
import com.jpgdump.mobile.util.Tags;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;
import android.view.Menu;
import android.view.MenuItem;
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
        
        if(isOnline())
        {
            boolean shouldReset = getIntent().getBooleanExtra("reset", false);
            
            pictureGrid = (GridView) findViewById(R.id.picture_viewer_activity_home);
            
            //Retrieve Session Id or, if it doesn't exist, create it
            SharedPreferences prefs = getSharedPreferences(Tags.SESSION_INFO, 0);
            String sessionId = prefs.getString(Tags.SESSION_ID, "");
            if(sessionId.equals(""))
            {
                new CreateSession(this).execute();
            }
            
            if(BuildConfig.DEBUG)
            {
                Log.i("CreateSession", "###New Session Created###\nSession ID: " + prefs.getString(Tags.SESSION_ID, "") + "\n"
                        + "Session Key: " + prefs.getString(Tags.SESSION_KEY, ""));
            }
            
            //Retrieve settings information
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
            
            boolean sfw = settings.getBoolean(Tags.SFW, true);
            
            
            OnItemClickListener gridPress = new GridPressListener(this);
            pictureGrid.setOnItemClickListener(gridPress);
    
            /* 
             * Instantiate the memory cache, used to later hold bitmaps retrieved
             * from JPG Dump
             */
            final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
            final int cacheSize = maxMemory / 4;
    
            // A handler for when the orientation changes
            RetainFragment retainFragment = RetainFragment
                    .findOrCreateRetainFragment(getFragmentManager());
    
            memoryCache = retainFragment.retainedCache;
            diskLruCache = retainFragment.retainedDiskCache;
            adapter = retainFragment.retainedAdapter;
            if (memoryCache == null || shouldReset)
            {
                getIntent().putExtra("reset", false);
                
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
                
                Integer[] postParams = new Integer[3];
                postParams[0] = Tags.START_POSTS;
                postParams[1] = 0;
                if(sfw)
                {
                    postParams[2] = 0;
                }
                else
                {
                    postParams[2] = 1;
                }
    
                new FetchPosts(this, retainFragment).execute(postParams);
            }
            else
            {
                OnScrollListener scrollListener = new PageBottomListener(this,
                        retainFragment);
                pictureGrid.setAdapter(adapter);
                pictureGrid.setOnScrollListener(scrollListener);
            }
        }
        else
        {
            // Handle not being connected
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.no_internet_dialog_title)
                   .setMessage(R.string.no_internet_dialog_message)
                   .setNegativeButton(R.string.no_internet_dialog_positive,
                    new NoInternetDialogListener(this))
                   .create()
                   .show();
        }
    }
    
    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap)
    {
        if(BuildConfig.DEBUG)
        {
            Log.i("HomeActivity", "Key value: " + key);
            if(bitmap == null)
            {
                Log.i("HomeActivity", "Bitmap is null");
            }
        }
        
        if(bitmap != null)
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
                    e.printStackTrace();
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
    
    /*
     * Checks connection to the internet
     */
    public boolean isOnline()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        
        return (netInfo != null && netInfo.isConnectedOrConnecting());
    }

    /* 
     * Creates a unique subdirectory of the designated app cache directory.
     * Tries to use external but if not mounted, falls back on internal storage.
     */
    public static File getDiskCacheDir(Context context, String uniqueName)
    {
        /*
         *  Check if media is mounted or storage is built-in, if so, try and use
         *  external cache dir otherwise use internal cache dir
         */
        final String cachePath = Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())
                || !Environment.isExternalStorageRemovable() ? context
                .getExternalCacheDir().getPath() : context.getCacheDir()
                .getPath();

        return new File(cachePath + File.separator + uniqueName);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(BuildConfig.DEBUG)
        {
            Log.i("HomeActivity", "Reached onActivityResult()");
        }
        
        if(requestCode == Tags.POST_REQUEST_CODE)
        {
            if(resultCode == Tags.RESULT_OK)
            {
                int position = data.getIntExtra("position", -1);
                int goatVal = data.getIntExtra("goatVal", 0);
                
                if(BuildConfig.DEBUG)
                {
                    Log.i("HomeActivity", "Request and result code worked\nPosition: " + position + "\n"
                            +"goatVal: " + goatVal);
                }
                
                if(position != -1)
                {
                    RetainFragment retainFragment = RetainFragment
                            .findOrCreateRetainFragment(getFragmentManager());
                    
                    /*
                     * This checks to see if memory has been cleared since the app was 
                     * first opened.  If the retainedAdapter doesn't exist, the home
                     * screen will refresh.
                     */
                    if(retainFragment.retainedAdapter != null)
                    {
                        Post post = (Post) retainFragment.retainedAdapter.getItem(position);
                        if(goatVal == 1)
                        {
                            post.addUpvote();
                        }
                        else if(goatVal == -1)
                        {
                            post.addDownvote();
                        }
                    }
                    else
                    {
                        getIntent().putExtra("reset", true);
                        this.recreate();
                    }
                }
            }
        }
        else if(requestCode == Tags.SETTINGS_REQUEST_CODE)
        {
            if(resultCode == Tags.RESULT_CHANGE_MADE)
            {
                getIntent().putExtra("reset", true);
                this.recreate();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        /*
         * refresh: Refreshes the page and retrieves new posts if
         *      they exist
         * 
         * action_settings: Will lead to the settings page for the
         *      application
         */
        switch (item.getItemId())
        {
            //Refresh the page
            case R.id.refresh:
                getIntent().putExtra("reset", true);
                this.recreate();
                return true;
                
            //Open the settings menu
            case R.id.action_settings:
                Intent intent = new Intent(this, PreferencesActivity.class);
                startActivityForResult(intent, Tags.SETTINGS_REQUEST_CODE);
                return true;
                
            //Do default action
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*
     * Initializes the DiskLruCache so that it may be used to hold images.
     * This segement is also synchronized so as to avoid accessing it before
     * it has been initialized.
     */
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
