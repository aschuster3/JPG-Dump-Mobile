package com.jpgdump.mobile.implementation;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import com.jpgdump.mobile.interfaces.UploadInterface;
import com.jpgdump.mobile.util.ContextFormattingLogger;

public class PictureUploadManager implements UploadInterface
{
    private final ContextFormattingLogger log = ContextFormattingLogger.getLogger(this);

    @Override
    public int uploadPicture(String filePath, String sessionKey, String sessionId)
    {
        String urlToConnect = "http://jpgdump.com/api/v1/uploads";
        File fileToUpload = new File(filePath);
        String boundary = Long.toHexString(System.currentTimeMillis()); // Just generate some unique random value.

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost postRequest = new HttpPost(urlToConnect);


        HttpResponse response = null;
        try
        {
            response = httpClient.execute(postRequest);
        } 
        catch (ClientProtocolException e)
        {
            e.printStackTrace();
        } 
        catch (IOException e)
        {
            e.printStackTrace();
        } 

        
        if(response == null)
        {
            return 1337;
        }
        return response.getStatusLine().getStatusCode();
    }

}
