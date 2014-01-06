package com.jpgdump.mobile.objects;

public class Session
{
    private String kind;
    private String id;
    private String key;
    
    public Session(String kind, String id, String key)
    {
        super();
        this.kind = kind;
        this.id = id;
        this.key = key;
    }
    
    public String getKind()
    {
        return kind;
    }
    public String getId()
    {
        return id;
    }
    public String getKey()
    {
        return key;
    }
}