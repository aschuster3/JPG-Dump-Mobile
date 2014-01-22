package com.jpgdump.mobile.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class InternetTools
{
    public static InputStream openHttpConnection(String strURL)
            throws IOException
    {
        InputStream inputStream = null;
        URL url = new URL(strURL);
        URLConnection conn = url.openConnection();

        try
        {
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setRequestMethod("GET");
            httpConn.connect();

            if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK)
            {
                inputStream = httpConn.getInputStream();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return inputStream;
    }
}
