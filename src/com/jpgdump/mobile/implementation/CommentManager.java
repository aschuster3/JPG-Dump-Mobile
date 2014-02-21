package com.jpgdump.mobile.implementation;

import java.io.BufferedReader;
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

import com.jpgdump.mobile.interfaces.CommentsInterface;
import com.jpgdump.mobile.objects.Comment;
import com.jpgdump.mobile.util.ContextLogger;

public class CommentManager implements CommentsInterface
{
    private final ContextLogger log = ContextLogger.getLogger(this);
    
    @Override
    public ArrayList<Comment> retrieveComments(int maxResults, int startIndex,
            String sortBy, String filters)
    {
        String commentUrl = "http://jpgdump.com/api/v1/comments?startIndex=" + startIndex + 
                "&maxResults=" + maxResults + "&sort=" + sortBy;
        
        if(!filters.equals(""))
        {
            commentUrl += "&filters=" + filters;
        }
        
        ArrayList<Comment> comments = new ArrayList<Comment>();
        URL url = null;
        InputStream inputStream = null;
        try
        {
            //Connect to the given url and open the connection
            url = new URL(commentUrl);
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
                
                JSONObject commentSplit;
                for(int i = 0; i < rawList.length(); i++)
                {
                    commentSplit = rawList.getJSONObject(i);
                    comments.add(new Comment(commentSplit.getString("kind"), commentSplit.getString("id"),
                            commentSplit.getString("postId"), commentSplit.getString("comment"), 
                            commentSplit.getString("created"), commentSplit.getString("ordinal"), 
                            commentSplit.getString("upvotes"), commentSplit.getString("downvotes"),
                            commentSplit.getString("score")));
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
        
        return comments;
    }
}
