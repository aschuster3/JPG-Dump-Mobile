package com.jpgdump.mobile.implementation;

import com.jpgdump.mobile.util.ContextLogger;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

public class CommentReferenceSpan extends ClickableSpan
{
    private final ContextLogger log = ContextLogger.getLogger(this);
    private String commentId;
    
    public CommentReferenceSpan(String commentId)
    {
        this.commentId = commentId;
    }
    
    @Override
    public void onClick(View widget)
    {
        log.i("You pressed " + commentId);
    }
    
    @Override
    public void updateDrawState(TextPaint ds)
    {
        super.updateDrawState(ds);
        ds.setARGB(255, 0, 255, 255);
    }

}
