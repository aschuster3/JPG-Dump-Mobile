package com.jpgdump.mobile.util;

import java.io.File;

import android.content.Context;

public final class Utils
{
    public static final int IO_BUFFER_SIZE_BYTES = 8 * 1024;
    private static final ContextFormattingLogger log = ContextFormattingLogger.getLogger(Utils.class);

    private Utils()
    {
    }
    
    public static File getCacheDir(Context context)
    {
        File cacheDirectory = context.getExternalCacheDir();
        if (cacheDirectory != null)
        {
            return cacheDirectory;
        }
        // Typically happens when the external directory isn't mounted.
        cacheDirectory = context.getCacheDir();
        log.i("Unable to find external cache directory, using %s", 
            cacheDirectory.getPath());
        return cacheDirectory;
    }
}