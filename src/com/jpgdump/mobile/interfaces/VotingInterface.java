package com.jpgdump.mobile.interfaces;

public interface VotingInterface
{
    public static enum VoteType {
        UP(1),
        DOWN(-1);
        
        private final int value;
        
        private VoteType(int value) {
            this.value = value;
        }
        
        public final int getValue() {
        	return value;
        }
    }
    
    /*
     * Returns true if successful, false otherwise (such as when you've
     * already voted on it)
     */
    public int distributeGoat(String sessionId, String sessionKey,
            String postId, VoteType voteType);
}
