package com.jpgdump.mobile.objects;

public class Post
{
    private String kind;
    private String id;
    private String url;
    private String width;
    private String height;
    private String created;
    private int safety;
    private String mime;
    private String upvotes;
    private String downvotes;
    private String score;
    private String title;
    
    public Post(String kind, String id, String url, String width,
            String height, String created, int safety, String mime,
            String upvotes, String downvotes, String score, String title)
    {
        super();
        this.kind = kind;
        this.id = id;
        this.url = url;
        this.width = width;
        this.height = height;
        this.created = created;
        this.safety = safety;
        this.mime = mime;
        this.upvotes = upvotes;
        this.downvotes = downvotes;
        this.score = score;
        this.title = title;
    }
    
    public String getKind()
    {
        return kind;
    }
    public String getId()
    {
        return id;
    }
    public String getUrl()
    {
        return url;
    }
    public String getWidth()
    {
        return width;
    }
    public String getHeight()
    {
        return height;
    }
    public String getCreated()
    {
        return created;
    }
    public int getSafety()
    {
        return safety;
    }
    public String getMime()
    {
        return mime;
    }
    public String getUpvotes()
    {
        return upvotes;
    }
    public void addUpvote()
    {
        upvotes = "" + (Integer.parseInt(upvotes) + 1);
    }
    public String getDownvotes()
    {
        return downvotes;
    }
    public void addDownvote()
    {
        downvotes = "" + (Integer.parseInt(downvotes) + 1);
    }
    public String getScore()
    {
        return score;
    }
    public String getTitle()
    {
        return title;
    }
    public String toString()
    {
        return "Post id: " + this.id;
    }
}