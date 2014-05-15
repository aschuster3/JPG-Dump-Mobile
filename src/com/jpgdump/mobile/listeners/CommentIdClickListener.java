package com.jpgdump.mobile.listeners;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class CommentIdClickListener implements OnClickListener
{
    String commentId;
    EditText commentTextField;
    
    public CommentIdClickListener(String commentId, EditText commentTextField)
    {
        this.commentId = commentId;
        this.commentTextField = commentTextField;
    }
    
    @Override
    public void onClick(View v)
    {
        String appendedComment = commentTextField.getText().toString();
        
        if(!appendedComment.contains(commentId))
        {
            commentTextField.append(">" + commentId + "\n");
        }
    }

}
