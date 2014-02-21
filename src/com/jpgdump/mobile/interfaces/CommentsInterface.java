package com.jpgdump.mobile.interfaces;

import java.util.List;

import com.jpgdump.mobile.interfaces.VotingInterface.VoteType;
import com.jpgdump.mobile.objects.Comment;

public interface CommentsInterface
{
    public List<Comment> retrieveComments(int maxResult, int startIndex, 
            String sort, String filter);
    
    public void postComment(String sessionId, String sessionKey,
            String postId, VoteType voteType);
}
