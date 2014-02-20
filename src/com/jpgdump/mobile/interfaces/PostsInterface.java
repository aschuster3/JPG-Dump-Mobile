package com.jpgdump.mobile.interfaces;

import java.util.ArrayList;

import com.jpgdump.mobile.objects.Post;

public interface PostsInterface
{
    /**
     * @param maxResults Maximum number of posts to retrieve
     * @param startIndex Retrieve starting from this index
     * @param sortBy Orders the posts based on a sort criteria.
     * An example would be "-id", which does decending order by
     * the posts id number
     * @param filters Removes posts if they match a certain 
     * criteria.  An example would be "[[\"id>1000\"]]" to remove
     * posts with id numbers greater than 1000
     * @return Returns the fully processed posts, ready to be
     * acted upon.
     */
    public ArrayList<Post> retrievePosts(int maxResults, int startIndex, 
            String sortBy, String filters);
}
