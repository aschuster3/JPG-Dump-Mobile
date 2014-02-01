package com.jpgdump.mobile.objects;

import android.graphics.Bitmap;

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
    private Bitmap thumbnailBitmap = null;
    
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
    public String getDownvotes()
    {
        return downvotes;
    }
    public String getScore()
    {
        return score;
    }
    public String getTitle()
    {
        return title;
    }
    public Bitmap getThumbnailBitmap()
    {
        return thumbnailBitmap;
    }
    public void setThumbnailBitmap(Bitmap pictureBitmap)
    {
        this.thumbnailBitmap = pictureBitmap;
    }
    public String toString()
    {
        return "Post id: " + this.id;
    }
}