package com.jpgdump.mobile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.jpgdump.mobile.async.CreateSession;
import com.jpgdump.mobile.async.FetchPosts;
import com.jpgdump.mobile.async.UploadPicture;
import com.jpgdump.mobile.fragments.RetainFragment;
import com.jpgdump.mobile.listeners.GridPressListener;
import com.jpgdump.mobile.listeners.NoInternetDialogListener;
import com.jpgdump.mobile.listeners.PageBottomListener;
import com.jpgdump.mobile.listeners.TagAddClickListener;
import com.jpgdump.mobile.listeners.UploadPromptClickListener;
import com.jpgdump.mobile.objects.Post;
import com.jpgdump.mobile.util.ContextFormattingLogger;
import com.jpgdump.mobile.util.DiskLruImageCache;
import com.jpgdump.mobile.util.PictureTools;
import com.jpgdump.mobile.util.Tags;

public class HomeActivity extends Activity
{
    private final ContextFormattingLogger log = ContextFormattingLogger.getLogger(this);
    
    private LruCache<String, Bitmap> memoryCache;
    private DiskLruImageCache diskLruCache;
    private final Object diskCacheLock = new Object();

    private GridView pictureGrid;
    private BaseAdapter adapter;
    
    private String currentImageFilePath;

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
                log.i("Session Started: (%s:%s)", 
                    prefs.getString(Tags.SESSION_ID, ""), 
                    prefs.getString(Tags.SESSION_KEY, ""));
            }
            
            //Retrieve settings information
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
            
            boolean sfw = settings.getBoolean(Tags.SFW, false);
            
            
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
                
                String[] postParams = new String[3];
                postParams[0] = Tags.START_POSTS;
                postParams[1] = "0";
                if(sfw)
                {
                    postParams[2] = "";
                }
                else
                {
                    postParams[2] = "[[\"safety==0\"]]";
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
            log.i("Bitmap Key: %s", key);
            if(bitmap == null)
            {
                log.i("Bitmap is null");
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
                    log.i("Cache is being added to");
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
                log.i("Cache is being accessed");
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
            log.i("Reached onActivityResult()");
        }
        
        if(requestCode == Tags.POST_REQUEST_CODE &&
                resultCode == Tags.RESULT_OK)
        {
            updatePost(data);
        }
        else if(requestCode == Tags.SETTINGS_REQUEST_CODE &&
                resultCode == Tags.RESULT_CHANGE_MADE)
        {
            getIntent().putExtra("reset", true);
            this.recreate();
        }
        else if(requestCode == Tags.SELECT_PICTURE &&
                resultCode == RESULT_OK)
        {
            final Bitmap imageBitmap;
            final String filePath;
            
            // Get the filePath variable set
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(
                               selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            filePath = cursor.getString(columnIndex);
            cursor.close();

            // Build the image from the filePath
            Options ops = new Options();
            
            ops.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, ops);
            
            ops.inSampleSize = PictureTools.calculateInSampleSize(ops, 80, 60);
            ops.inJustDecodeBounds = false;
            
            imageBitmap = BitmapFactory.decodeFile(filePath, ops);
            
            uploadPrompt(imageBitmap, filePath);
            
        }
        else if(requestCode == Tags.CAMERA_REQUEST_CODE &&
                resultCode == RESULT_OK)
        {
            final Bitmap imageBitmap;
            final String filePath;
            
            // Set by createImageFile() method listed below
            filePath = currentImageFilePath;
            
            // Build image from filePath
            Options ops = new Options();
            
            ops.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, ops);
            
            ops.inSampleSize = PictureTools.calculateInSampleSize(ops, 80, 60);
            ops.inJustDecodeBounds = false;
            
            imageBitmap = BitmapFactory.decodeFile(filePath, ops);
            
            uploadPrompt(imageBitmap, filePath);
        }
    }
    
    private void updatePost(Intent data)
    {
        int position = data.getIntExtra("position", -1);
        int goatVal = data.getIntExtra("goatVal", 0);
        
        if(BuildConfig.DEBUG)
        {
            log.i("Request and result code worked! Position:%d goalVal: %d", position, goatVal);
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
    
    private void uploadPrompt(final Bitmap imageBitmap,  final String filePath)
    {
        final ArrayList<String> tags = new ArrayList<String>();
        
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        final ViewGroup dialogView = (ViewGroup) inflater.inflate(R.layout.dialog_upload_picture, null, false);
        final EditText titleText = (EditText) dialogView.findViewById(R.id.post_title);
        final ImageView thumbnailPreview = (ImageView) dialogView.findViewById(R.id.image_preview);
        thumbnailPreview.setImageBitmap(imageBitmap);
        
        final LinearLayout currentTags = (LinearLayout) dialogView.findViewById(R.id.tags_text_list);
        final EditText tagText = (EditText) dialogView.findViewById(R.id.tag_text_field);
        final Button addTagButton = (Button) dialogView.findViewById(R.id.tag_submit_button);
        addTagButton.setOnClickListener(new TagAddClickListener(this, tagText, currentTags, tags));
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        
        builder.setView(dialogView)
               .setTitle(R.string.upload_picture)
               .setNeutralButton(getResources().getString(R.string.okay_button), null);
        final AlertDialog dialog = builder.create();
        
        dialog.show();
        
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener()
               {

                    @Override
                    public void onClick(View view)
                    {
                        if(tags.size() < 1)
                        {
                            Toast.makeText(HomeActivity.this, 
                                    HomeActivity.this.getResources().getString(R.string.tag_your_stuff), 
                                    Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            SharedPreferences prefs = HomeActivity.this.getSharedPreferences(Tags.SESSION_INFO, 0);
                            
                            String[] postInfo = new String[4];
                            postInfo[0] = filePath;
                            postInfo[1] = titleText.getText().toString();
                            postInfo[2] = prefs.getString(Tags.SESSION_KEY, "");
                            postInfo[3] = prefs.getString(Tags.SESSION_ID, "");
                            
                            String[] allTags = new String[tags.size()];
                            tags.toArray(allTags);
                            
                            new UploadPicture(HomeActivity.this).execute(concat(postInfo, allTags));
                            dialog.dismiss();
                        }
                    }
                   
               });
        
        
    }
    
    private String[] concat(String[] A, String[] B) 
    {
        int aLen = A.length;
        int bLen = B.length;
        String[] C= new String[aLen+bLen];
        System.arraycopy(A, 0, C, 0, aLen);
        System.arraycopy(B, 0, C, aLen, bLen);
        return C;
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
                
            //Prompt user to take a picture or choose one from gallery    
            case R.id.upload_picture:
                showUploadPrompt();
                
                return true;
                
            //Do default action
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    private void showUploadPrompt()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        OnClickListener listener = new UploadPromptClickListener(this);
        
        builder.setMessage("Upload from your gallery or take a new picture?");
        builder.setPositiveButton(getResources().getString(R.string.take_photo), listener);
        builder.setNegativeButton(getResources().getString(R.string.existing_picture), listener);
        builder.setNeutralButton(getResources().getString(R.string.cancel), null);
        builder.create();
        builder.show();
    }
    
    @SuppressLint("SimpleDateFormat")
    public File createImageFile() throws IOException 
    {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES + "/JPG Dump/");
        
        if(!storageDir.exists())
        {
            storageDir.mkdir();
        }
        
        File image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",         /* suffix */
            storageDir      /* directory */
        );
        
        currentImageFilePath = image.getAbsolutePath();
        
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentImageFilePath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
        
        return image;
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
