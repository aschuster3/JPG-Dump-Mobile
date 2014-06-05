package com.jpgdump.mobile.implementation;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jpgdump.mobile.interfaces.TagsInterface;
import com.jpgdump.mobile.objects.Tag;
import com.jpgdump.mobile.util.ContextLogger;

public class TagsManager implements TagsInterface
{
    private final ContextLogger log = ContextLogger.getLogger(this);
    
    @Override
    public ArrayList<Tag> getPictureTags(String picId)
    {
        String tagsUrl = "http://jpgdump.com/api/v1/tags?startIndex=0" +
                "&maxResults=20&sort=-id&filters=[[\"postId==" + picId + "\"]]";
        
        ArrayList<Tag> tags = new ArrayList<Tag>();
        URL url = null;
        InputStream inputStream = null;
        try
        {
            //Connect to the given url and open the connection
            url = new URL(tagsUrl);
            URLConnection conn = url.openConnection();
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            
            httpConn.setRequestMethod("GET");
            httpConn.connect();

            if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK)
            {
                //If the connection is successful, read in the stream
                inputStream = httpConn.getInputStream();
                BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
                
                StringBuilder total = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null) 
                {
                    total.append(line);
                }
                
                
                //Parse the JSON and split it into the individual Posts
                JSONObject rawJson = new JSONObject(total.toString());
                JSONArray rawList = rawJson.getJSONArray("items");
                
                JSONObject postSplit;
                for(int i = 0; i < rawList.length(); i++)
                {
                    postSplit = rawList.getJSONObject(i);
                    tags.add(new Tag(postSplit.getString("kind"), postSplit.getString("id"), postSplit.getString("postId"), postSplit.getString("tag"),
                            postSplit.getString("created"), postSplit.getString("accepted")));
                }
            }
            else
            {
                throw new MalformedURLException("Teh interwebz is broken");
            }
        }
        catch (MalformedURLException e)
        {
            // Intentionally ignore error.
            log.e("The URL is misformed", e);
        }
        catch (IOException e)
        {
            // Intentionally ignore error.
            log.e("There's a problem with the BufferedReader", e);
        }
        catch (JSONException e)
        {
            // Intentionally ignore error.
            log.e("The JSON has returned something unexpected", e);
        }
        return tags;
    }

    @Override
    public int tagPicture(String picId, String sessionKey, String sessionId, String tag)
    {
        int responseCode = 2;
        final String TAG_URL = "http://jpgdump.com/api/v1/tags";
        String params;
        
        // Make sure empty tags are not submitted
        if(!tag.equals(""))
        {
            URL obj;
            params = "tag=" + tag + "&postId=" + picId;
            
            try
            {
                obj = new URL(TAG_URL);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                
                con.setRequestMethod("POST");
                con.setRequestProperty("X-Jpgdump-Session-Key", sessionKey);
                con.setRequestProperty("X-Jpgdump-Session-Id", sessionId);
                con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
                
                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(params);
                wr.flush();
                wr.close();
    
                responseCode = con.getResponseCode();
            }
            catch (Exception e)
            {
                log.e(e.getMessage(), e);
            }
        }
            
        
        return responseCode;
    }

}
