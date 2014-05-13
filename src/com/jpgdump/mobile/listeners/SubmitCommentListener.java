package com.jpgdump.mobile.listeners;

import com.jpgdump.mobile.async.PostComment;
import com.jpgdump.mobile.util.Tags;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class SubmitCommentListener implements OnClickListener
{
    private EditText commentTextField;
    private String postId;
    private Activity activity;
    
    public SubmitCommentListener(EditText commentTextField, String postId, Activity activity)
    {
        this.commentTextField = commentTextField;
        this.postId = postId;
        this.activity = activity;
    }
    
    @Override
    public void onClick(View v)
    {
        SharedPreferences prefs = ((Context)activity).getSharedPreferences(Tags.SESSION_INFO, 0);
        String sessionId = prefs.getString(Tags.SESSION_ID, "");
        String sessionKey = prefs.getString(Tags.SESSION_KEY, "");
        String commentText = commentTextField.getText().toString();
        
        new PostComment(sessionId, sessionKey, activity).execute(postId, commentText);
    }

}
