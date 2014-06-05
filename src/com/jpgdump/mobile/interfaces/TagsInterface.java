package com.jpgdump.mobile.interfaces;

import java.util.ArrayList;

import com.jpgdump.mobile.objects.Tag;

public interface TagsInterface
{
    public ArrayList<Tag> getPictureTags(String picId);
    public int tagPicture(String picId, String sessionKey, String sessionId, String tags);
}
