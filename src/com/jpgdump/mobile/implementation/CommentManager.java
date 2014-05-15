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
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jpgdump.mobile.BuildConfig;
import com.jpgdump.mobile.interfaces.CommentsInterface;
import com.jpgdump.mobile.objects.Comment;
import com.jpgdump.mobile.util.ContextLogger;

public class CommentManager implements CommentsInterface
{
    private final ContextLogger log = ContextLogger.getLogger(this);
    
    private final static String POST_COMMENT_URL = "http://jpgdump.com/api/v1/comments";
    
    @Override
    public List<Comment> retrieveComments(String maxResults, String startIndex,
            String sortBy, String filters)
    {
        String commentUrl = "http://jpgdump.com/api/v1/comments?startIndex=" + startIndex + 
                "&maxResults=" + maxResults + "&sort=" + sortBy;
        
        if(!filters.equals(""))
        {
            commentUrl += "&filters=" + filters;
        }
        
        if(BuildConfig.DEBUG)
        {
            log.i("Comment Url: " + commentUrl);
        }
        
        List<Comment> comments = new ArrayList<Comment>();
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
    
    @Override
    public int postComment(String sessionId, String sessionKey,
            String postId, String inputComment)
    {
        int responseCode = 1337;
        try
        {
            URL obj = new URL(POST_COMMENT_URL);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            
            con.setRequestMethod("POST");
            con.setRequestProperty("X-Jpgdump-Session-Key", sessionKey);
            con.setRequestProperty("X-Jpgdump-Session-Id", sessionId);
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            
            String params = "postId=" + postId + "&comment=" + inputComment;
            
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(params);
            wr.flush();
            wr.close();

            responseCode = con.getResponseCode();
            
        }
        catch (Exception e)
        {
            if(BuildConfig.DEBUG)
            {
                log.i(e.getMessage(), e);
            }
        }
        return responseCode;
    }

    @Override
    public String retrieveComment(String commentId)
    {
        String commentUrl = "http://jpgdump.com/api/v1/comments/" + commentId;
        
        String comment = null;
        
        if(BuildConfig.DEBUG)
        {
            log.i("Comment Url: " + commentUrl);
        }
        
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
                
                JSONObject rawJson = new JSONObject(total.toString());
                
                comment = rawJson.getString("comment");
            }
            else
            {
                throw new MalformedURLException("Teh interwebz is broken with response code " + httpConn.getResponseCode());
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
        return comment;
    }
}
