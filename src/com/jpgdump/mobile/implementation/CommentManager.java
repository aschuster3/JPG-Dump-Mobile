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
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.jpgdump.mobile.BuildConfig;
import com.jpgdump.mobile.interfaces.CommentsInterface;
import com.jpgdump.mobile.interfaces.VotingInterface.VoteType;
import com.jpgdump.mobile.objects.Comment;

public class CommentManager implements CommentsInterface
{
    private static final String TAG = "CommentManager";
    
    
    @Override
    public List<Comment> retrieveComments(int maxResults, int startIndex,
            String sortBy, String filters)
    {
        String commentUrl = "http://jpgdump.com/api/v1/comments?startIndex=" + startIndex + 
                "&maxResults=" + maxResults + "&sort=" + sortBy;
        
        if(!filters.equals(""))
        {
            commentUrl += "&filters=" + filters;
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
            if(BuildConfig.DEBUG)
            {
                Log.e(TAG, "The URL is misformed");
            }
            e.printStackTrace();
        }
        catch (IOException e)
        {
            if(BuildConfig.DEBUG)
            {
                Log.e(TAG, "There's a problem with the BufferedReader");
            }
            e.printStackTrace();
        }
        catch (JSONException e)
        {
            if(BuildConfig.DEBUG)
            {
                Log.e(TAG, "The JSON has returned something unexpected");
            }
            e.printStackTrace();
        }
        
        return comments;
    }
    
    @Override
    public void postComment(String sessionId, String sessionKey,
            String postId, VoteType voteType)
    {
        // TODO: write to post comments
    }

}
