package com.jpgdump.mobile.objects;

public class Vote
{
    private String kind;
    private String postId;
    private String value;
    private String created;
    
    public Vote(String kind, String postId, String value, String created)
    {
        super();
        this.kind = kind;
        this.postId = postId;
        this.value = value;
        this.created = created;
    }
    
    public String getKind()
    {
        return kind;
    }
    public String getPostId()
    {
        return postId;
    }
    public String getValue()
    {
        return value;
    }
    public String getCreated()
    {
        return created;
    }
}