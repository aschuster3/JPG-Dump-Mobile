package com.jpgdump.mobile.listeners;

import com.jpgdump.mobile.R;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;

public class CommentOverflowListener implements OnClickListener
{
    Context context;
    String commentId, comment;
    
    public CommentOverflowListener(Context context, String commentId, String comment)
    {
        this.context = context;
        this.commentId = commentId;
        this.comment = comment;
    }
    
    @Override
    public void onClick(View view)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(commentId)
               .setMessage(comment)
               .setNeutralButton(context.getResources().getString(R.string.okay_button), null)
               .create()
               .show();
    }

}
