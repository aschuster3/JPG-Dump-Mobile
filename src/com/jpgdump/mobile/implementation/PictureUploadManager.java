package com.jpgdump.mobile.implementation;

import java.io.File;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.webkit.MimeTypeMap;

import com.jpgdump.mobile.interfaces.TagsInterface;
import com.jpgdump.mobile.interfaces.UploadInterface;
import com.jpgdump.mobile.util.ContextFormattingLogger;

public class PictureUploadManager implements UploadInterface
{
    private final ContextFormattingLogger log = ContextFormattingLogger.getLogger(this);
    
    private final String UPLOAD_URL = "http://jpgdump.com/api/v1/uploads";
    private final String POST_URL = "http://jpgdump.com/api/v1/posts";
    
    @Override
    public int uploadPicture(String[] postInfo)//filePath, String title, String sessionKey, String sessionId)
    {
        String  filePath = postInfo[0],
                title = postInfo[1],
                sessionKey = postInfo[2],
                sessionId = postInfo[3];
        
        
        log.v("filePath: %s\nsessionKey: %s\nsessionId: %s", filePath,
                sessionKey, sessionId);
        
        int responseCode = 0;
        String url = "";
        String[] sfn = filePath.split("\\.");
        String imgType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(sfn[sfn.length - 1]);
        
        
        HttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

        
        // Upload the image
        HttpPost httppost = new HttpPost(UPLOAD_URL);
        httppost.addHeader("X-Jpgdump-Session-Key", sessionKey);
        httppost.addHeader("X-Jpgdump-Session-Id", sessionId);
        httppost.addHeader("Accept-Language", "en-US,en;q=0.5");
        httppost.addHeader("User-Agent","Mozilla/5.0 ( compatible ) ");
        httppost.addHeader("Accept", "application/json, text/javascript, */*; q=0.01");
          
        File file = new File(filePath);

        MultipartEntityBuilder mpEntity = MultipartEntityBuilder.create();
        mpEntity.addTextBody("Content-Dispostion", "form-data");
        mpEntity.addTextBody("name", "file");
        mpEntity.addBinaryBody("filename", file, ContentType.create(imgType), file.getName());
        HttpEntity mpe = mpEntity.build();
        

        httppost.setEntity(mpe);
        log.v("Executing request: %s", httppost.getRequestLine());
        HttpResponse response;
        try
        {
            response = httpclient.execute(httppost);
            HttpEntity resEntity = response.getEntity();
            
            responseCode = 1;

            // Log to confirm that previous HTTP Post succeeded
            log.v(response.getStatusLine().toString());
            if (resEntity != null) 
            {
              JSONObject rawResponse = new JSONObject(EntityUtils.toString(resEntity));
              url = rawResponse.getString("url");
              
              // Post the image
              httppost = new HttpPost(POST_URL);
              httppost.addHeader("X-Jpgdump-Session-Key", sessionKey);
              httppost.addHeader("X-Jpgdump-Session-Id", sessionId);
              httppost.addHeader("Accept-Language", "en-US,en;q=0.5");
              httppost.addHeader("User-Agent","Mozilla/5.0 ( compatible ) ");
              httppost.addHeader("Accept", "application/json, text/javascript, */*; q=0.01");
              
              mpEntity = MultipartEntityBuilder.create();
              mpEntity.addTextBody("title", title);
              mpEntity.addTextBody("url", url);
              
              httppost.setEntity(mpEntity.build());
              
              log.v("Executing request: %s", httppost.getRequestLine());
              response = httpclient.execute(httppost);
              
              resEntity = response.getEntity();
              
              if(resEntity != null)
              {
                  log.v(response.getStatusLine().toString());
                  responseCode = response.getStatusLine().getStatusCode();
                  
                  rawResponse = new JSONObject(EntityUtils.toString(resEntity));
                  
                  TagsInterface tagger = new TagsManager();
                  
                  for(int i = 4; i < postInfo.length; i++)
                  {
                      // Tag the image
                      responseCode = tagger.tagPicture(rawResponse.getString("id"), 
                              sessionKey, sessionId, postInfo[i]);
                  }
                  
                  resEntity.consumeContent();
              }
            }
        } 
        catch (Exception e)
        {
            log.e(e, "Exception Body: %s", e.getMessage());
        }
        

        httpclient.getConnectionManager().shutdown();
      
        return responseCode;
    }
    
}
