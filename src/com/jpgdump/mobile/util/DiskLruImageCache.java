package com.jpgdump.mobile.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.jakewharton.disklrucache.DiskLruCache;
import com.jpgdump.mobile.BuildConfig;

public class DiskLruImageCache
{

    private DiskLruCache mDiskCache;
    private boolean diskCacheStarting = true;
    private static final int DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10MB
    private static final String DISK_CACHE_SUBDIR = "thumbnails";
    
    
    private CompressFormat mCompressFormat = CompressFormat.JPEG;
    private int mCompressQuality = 80;
    private static final int APP_VERSION = 1;
    private static final int VALUE_COUNT = 1;

    public DiskLruImageCache(Context context, Object diskCacheLock)
    {
        try
        {
            final File diskCacheDir = getDiskCacheDir(context, DISK_CACHE_SUBDIR);
            mDiskCache = DiskLruCache.open(diskCacheDir, APP_VERSION, VALUE_COUNT,
                    DISK_CACHE_SIZE);
            diskCacheStarting = false; // Finished initialization
            diskCacheLock.notifyAll(); // Wake any waiting threads
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private boolean writeBitmapToFile(Bitmap bitmap, DiskLruCache.Editor editor)
            throws IOException, FileNotFoundException
    {
        OutputStream out = null;
        try
        {
            out = new BufferedOutputStream(editor.newOutputStream(0),
                    Utils.IO_BUFFER_SIZE);
            return bitmap.compress(mCompressFormat, mCompressQuality, out);
        }
        finally
        {
            if (out != null)
            {
                out.close();
            }
        }
    }

    private File getDiskCacheDir(Context context, String uniqueName)
    {

        // Check if media is mounted or storage is built-in, if so, try and use
        // external cache dir otherwise use internal cache dir
        final String cachePath = Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())
                || !Utils.isExternalStorageRemovable() ? Utils
                .getExternalCacheDir(context).getPath() : context.getCacheDir()
                .getPath();

        return new File(cachePath + File.separator + uniqueName);
    }

    public void put(String key, Bitmap data)
    {

        DiskLruCache.Editor editor = null;
        try
        {
            editor = mDiskCache.edit(key);
            if (editor == null)
            {
                return;
            }

            if (writeBitmapToFile(data, editor))
            {
                mDiskCache.flush();
                editor.commit();
                if (BuildConfig.DEBUG)
                {
                    Log.d(Tags.DISKLRU, "image put on disk cache " + key);
                }
            }
            else
            {
                editor.abort();
                if (BuildConfig.DEBUG)
                {
                    Log.d(Tags.DISKLRU,
                            "ERROR on: image put on disk cache " + key);
                }
            }
        }
        catch (IOException e)
        {
            if (BuildConfig.DEBUG)
            {
                Log.d(Tags.DISKLRU, "ERROR on: image put on disk cache "
                        + key);
            }
            try
            {
                if (editor != null)
                {
                    editor.abort();
                }
            }
            catch (IOException ignored)
            {
            }
        }

    }

    public Bitmap getBitmap(String key)
    {

        Bitmap bitmap = null;
        DiskLruCache.Snapshot snapshot = null;
        try
        {

            snapshot = mDiskCache.get(key);
            if (snapshot == null)
            {
                return null;
            }
            final InputStream in = snapshot.getInputStream(0);
            if (in != null)
            {
                final BufferedInputStream buffIn = new BufferedInputStream(in,
                        Utils.IO_BUFFER_SIZE);
                bitmap = BitmapFactory.decodeStream(buffIn);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (snapshot != null)
            {
                snapshot.close();
            }
        }

        if (BuildConfig.DEBUG)
        {
            Log.d(Tags.DISKLRU, bitmap == null ? ""
                    : "image read from disk " + key);
        }

        return bitmap;

    }

    public boolean containsKey(String key)
    {

        boolean contained = false;
        DiskLruCache.Snapshot snapshot = null;
        try
        {
            snapshot = mDiskCache.get(key);
            contained = snapshot != null;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (snapshot != null)
            {
                snapshot.close();
            }
        }

        return contained;

    }

    public void clearCache()
    {
        if (BuildConfig.DEBUG)
        {
            Log.d(Tags.DISKLRU, "disk cache CLEARED");
        }
        try
        {
            mDiskCache.delete();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public File getCacheFolder()
    {
        return mDiskCache.getDirectory();
    }

    public boolean isDiskCacheStarting()
    {
        return diskCacheStarting;
    }

}