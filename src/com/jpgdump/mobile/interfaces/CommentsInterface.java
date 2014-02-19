package com.jpgdump.mobile.interfaces;

import java.util.ArrayList;

import com.jpgdump.mobile.objects.Comment;

public interface CommentsInterface
{
    public ArrayList<Comment> retrieveComments(int maxResult, int startIndex, String sort, 
            String filter);
}
