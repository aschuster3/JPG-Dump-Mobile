package com.jpgdump.mobile.implementation;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.jpgdump.mobile.BuildConfig;
import com.jpgdump.mobile.interfaces.VotingInterface;
import com.jpgdump.mobile.util.ContextLogger;

public class VoteManager implements VotingInterface
{
    private static final String POST_URL = "http://jpgdump.com/api/v1/postVotes";
    private static final String COMMENT_URL = "http://jpgdump.com/api/v1/commentVotes";
    private final ContextLogger log = ContextLogger.getLogger(this);
    
    @Override
    public int distributeGoat(String sessionId, String sessionKey, 
            String postId, VoteType voteType)
    {
        int responseCode = 1337;
        try
        {
            URL obj = new URL(POST_URL);
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
            if(BuildConfig.DEBUG)
            {
                log.i(e.getMessage(), e);
            }
        }
        return responseCode;
    }

    @Override
    public int distributeCommentGoat(String sessionId, String sessionKey, 
            String commentId, VoteType voteType)
    {
        int responseCode = 1337;
        try
        {
            URL obj = new URL(COMMENT_URL);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            
            con.setRequestMethod("POST");
            con.setRequestProperty("X-Jpgdump-Session-Key", sessionKey);
            con.setRequestProperty("X-Jpgdump-Session-Id", sessionId);
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            
            String params = "commentId=" + commentId + "&value=" + voteType.getValue();
            
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
}
