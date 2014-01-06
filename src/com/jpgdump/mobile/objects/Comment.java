package com.jpgdump.mobile.objects;

public class Comment
{
    private String kind;
    private String id;
    private String postId;
    private String comment;
    private String created;
    private String ordinal;
    private String upvotes;
    private String downvotes;
    private String score;
    
    public Comment(String kind, String id, String postId, String comment,
            String created, String ordinal, String upvotes, String downvotes,
            String score)
    {
        super();
        this.kind = kind;
        this.id = id;
        this.postId = postId;
        this.comment = comment;
        this.created = created;
        this.ordinal = ordinal;
        this.upvotes = upvotes;
        this.downvotes = downvotes;
        this.score = score;
    }
    
    public String getKind()
    {
        return kind;
    }
    public String getId()
    {
        return id;
    }
    public String getPostId()
    {
        return postId;
    }
    public String getComment()
    {
        return comment;
    }
    public String getCreated()
    {
        return created;
    }
    public String getOrdinal()
    {
        return ordinal;
    }
    public String getUpvotes()
    {
        return upvotes;
    }
    public String getDownvotes()
    {
        return downvotes;
    }
    public String getScore()
    {
        return score;
    }
}
