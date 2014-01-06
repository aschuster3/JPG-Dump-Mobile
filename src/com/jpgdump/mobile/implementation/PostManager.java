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

import android.util.Log;

import com.jpgdump.mobile.BuildConfig;
import com.jpgdump.mobile.interfaces.PostsInterface;
import com.jpgdump.mobile.objects.Post;

public class PostManager implements PostsInterface
{
    private static final String TAG = "PostManager";
    
    @Override
    public ArrayList<Post> retrievePosts(int maxResults, int startIndex,
            String sortBy, String filters)
    {
        String postUrl = "http://jpgdump.com/api/v1/posts?startIndex=" + 
                startIndex + "&maxResults=" + maxResults + "&sort=" + sortBy;
        
        if(!filters.equals(""))
        {
            postUrl +=  "&filters=" + filters;
        }
        
        ArrayList<Post> posts = new ArrayList<Post>();
        URL url = null;
        InputStream inputStream = null;
        try
        {
            //Connect to the given url and open the connection
            url = new URL(postUrl);
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
                    posts.add(new Post(postSplit.getString("kind"), postSplit.getString("id"),
                            postSplit.getString("url"), postSplit.getString("width"),
                            postSplit.getString("height"), postSplit.getString("created"), 
                            postSplit.getInt("safety"), postSplit.getString("mime"),
                            postSplit.getString("upvotes"), postSplit.getString("downvotes"),
                            postSplit.getString("score"), postSplit.getString("title")));
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
        return posts;
    }

}
