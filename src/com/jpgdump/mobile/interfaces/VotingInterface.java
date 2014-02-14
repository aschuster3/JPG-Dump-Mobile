package com.jpgdump.mobile.interfaces;

public interface VotingInterface
{
    
    /*
     * Returns true if successful, false otherwise (such as when you've
     * already voted on it)
     */
    public int distributeGoat(String sessionId, String sessionKey,
            String postId, boolean upOrDown);
}
