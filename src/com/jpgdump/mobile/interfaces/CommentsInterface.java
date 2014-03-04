package com.jpgdump.mobile.interfaces;

import java.util.List;

import com.jpgdump.mobile.objects.Comment;

public interface CommentsInterface
{
    public List<Comment> retrieveComments(String maxResult, String startIndex, 
            String sort, String filter);
    
    public void postComment(String sessionId, String sessionKey,
            String postId, String inputComment);
}
