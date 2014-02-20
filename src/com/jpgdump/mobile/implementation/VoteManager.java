package com.jpgdump.mobile.implementation;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;

import com.jpgdump.mobile.BuildConfig;
import com.jpgdump.mobile.interfaces.VotingInterface;

public class VoteManager implements VotingInterface
{
    private static final String URL = "http://jpgdump.com/api/v1/postVotes";
    
    @Override
    public int distributeGoat(String sessionId, String sessionKey, 
            String postId, VoteType voteType)
    {
        int responseCode = 1337;
        try
        {
            URL obj = new URL(URL);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            
            con.setRequestMethod("POST");
            con.setRequestProperty("X-Jpgdump-Session-Key", sessionKey);
            con.setRequestProperty("X-Jpgdump-Session-Id", sessionId);
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            
            String params = "postId=" + postId + "&value=" + voteType.getValue();

            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(params);
            wr.flush();
            wr.close();

            responseCode = con.getResponseCode();
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
            
            if(BuildConfig.DEBUG)
            {
                Log.i("VoteManager", e.getMessage());
            }
        }
        return responseCode;
    }

}
