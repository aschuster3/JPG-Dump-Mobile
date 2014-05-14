package com.jpgdump.mobile.implementation;

import com.jpgdump.mobile.util.ContextLogger;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

public class PictureReferenceSpan extends ClickableSpan
{
    private final ContextLogger log = ContextLogger.getLogger(this);
    private String postId;
    
    public PictureReferenceSpan(String postId)
    {
        this.postId = postId;
    }
    
    @Override
    public void onClick(View widget)
    {
        log.i("You're pressing " + postId);
    }
    
    @Override
    public void updateDrawState(TextPaint ds)
    {
        super.updateDrawState(ds);
        ds.setARGB(255, 255, 255, 0);
    }

}
