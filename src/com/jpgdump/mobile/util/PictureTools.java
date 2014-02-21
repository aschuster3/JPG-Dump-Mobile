package com.jpgdump.mobile.util;

import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class PictureTools
{
    public static int calculateInSampleSize(BitmapFactory.Options options,
            int reqWidth, int reqHeight)
    {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth)
        {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2
            // and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth)
            {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
    
    public static Bitmap decodeSampleBitmapFromInputStream(String url,
            int reqWidth, int reqHeight)
    {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        InputStream is0 = null;
        try
        {
            is0 = InternetTools.openHttpConnection(url);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is0, null, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);

        InputStream is1 = null;
        try
        {
            is1 = InternetTools.openHttpConnection(url);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap bmp = BitmapFactory.decodeStream(is1, null, options);

        try
        {
            if(is1 != null)
            {
                is1.close();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return bmp;

    }
    
    public static Bitmap decodeBitmapFromInputStream(String url)
    {
        InputStream is = null;
        try
        {
            is = InternetTools.openHttpConnection(url);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        
        Bitmap bmp = BitmapFactory.decodeStream(is);
        
        try
        {
            is.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return bmp;
    }
}
