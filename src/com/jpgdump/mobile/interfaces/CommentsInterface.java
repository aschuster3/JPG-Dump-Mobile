package com.jpgdump.mobile.interfaces;

import java.util.List;

import com.jpgdump.mobile.objects.Comment;

public interface CommentsInterface
{
    public List<Comment> retrieveComments(String maxResult, String startIndex, 
            String sort, String filter);
    
    public String retrieveComment(String commentId);
    
    public int postComment(String sessionId, String sessionKey,
            String postId, String inputComment);
}
