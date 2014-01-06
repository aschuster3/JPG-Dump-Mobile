package com.jpgdump.mobile.objects;

public class Tag
{
    private String kind;
    private String id;
    private String postId;
    private String tag;
    private String created;
    private String accepted;
    
    public Tag(String kind, String id, String postId, String tag,
            String created, String accepted)
    {
        super();
        this.kind = kind;
        this.id = id;
        this.postId = postId;
        this.tag = tag;
        this.created = created;
        this.accepted = accepted;
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
    public String getTag()
    {
        return tag;
    }
    public String getCreated()
    {
        return created;
    }
    public String getAccepted()
    {
        return accepted;
    }
}