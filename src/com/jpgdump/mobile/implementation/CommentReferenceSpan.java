package com.jpgdump.mobile.implementation;

import com.jpgdump.mobile.R;
import com.jpgdump.mobile.interfaces.CommentsInterface;
import com.jpgdump.mobile.util.ContextLogger;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

public class CommentReferenceSpan extends ClickableSpan
{
    private final ContextLogger log = ContextLogger.getLogger(this);
    private String commentId;
    private Activity activity;
    
    public CommentReferenceSpan(String commentId, Activity activity)
    {
        this.commentId = commentId;
        this.activity = activity;
    }
    
    @Override
    public void onClick(View widget)
    {
        log.i("You pressed " + commentId);
        new FetchCommentReferenced().execute();
    }
    
    @Override
    public void updateDrawState(TextPaint ds)
    {
        super.updateDrawState(ds);
        ds.setTextScaleX(1.2f);
        ds.setARGB(255, 255, 144, 70);
    }
    
    /**
     * A helper that sets Spans for picture references and
     * comment references.
     * 
     * @param comment Unparsed comment
     * @return Parsed comment with proper Spans
     */
    private SpannableString parseComment(CharSequence comment)
    {
        StringBuilder picId = new StringBuilder();
        SpannableString parsedComment = new SpannableString(comment);
        
        // Find the picture references and set their spans
        int     indexOfCarrot = ((String)comment).indexOf("^"),
                endSpan = indexOfCarrot + 1;
        while(indexOfCarrot != -1)
        {
            while(endSpan < comment.length() && comment.charAt(endSpan) >= '0' && comment.charAt(endSpan) <= '9')
            {
                picId.append(comment.charAt(endSpan));
                endSpan++;
            }
            
            if(picId.toString().matches("[0-9]+"))
            {
                parsedComment.setSpan(new PictureReferenceSpan(picId.toString(), activity), indexOfCarrot, endSpan, 0);
            }
            
            indexOfCarrot = comment.toString().indexOf("^", endSpan);
            endSpan = indexOfCarrot + 1;
            picId = new StringBuilder();
        }
        
        // Find the comment references and set their spans
        int     indexOfAngleBracket = ((String)comment).indexOf(">");
        endSpan = indexOfAngleBracket + 1;
        
        while(indexOfAngleBracket != -1)
        {
            while(endSpan < comment.length() && comment.charAt(endSpan) >= '0' && comment.charAt(endSpan) <= '9')
            {
                picId.append(comment.charAt(endSpan));
                endSpan++;
            }
            
            if(picId.toString().matches("[0-9]+"))
            {
                parsedComment.setSpan(new CommentReferenceSpan(picId.toString(), activity), indexOfAngleBracket, endSpan, 0);
            }
            
            indexOfAngleBracket = comment.toString().indexOf(">", endSpan);
            endSpan = indexOfAngleBracket + 1;
            picId = new StringBuilder();
        }
        
        
        return parsedComment;
    }
    
    public static float pixelsToSp(Context context, Float px) 
    {
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return px/scaledDensity;
    }
    
    
    private class FetchCommentReferenced extends AsyncTask<Void, Void, String>
    {

        @Override
        protected String doInBackground(Void... params)
        {
            CommentsInterface commentManager = new CommentManager();
            
            return commentManager.retrieveComment(commentId);
        }
        
        @Override
        protected void onPostExecute(String comment)
        {
            if(comment != null)
            {
                TextView textView = new TextView(activity);
                
                textView.setText(parseComment(comment));
                textView.setTextSize(pixelsToSp(activity, 25f));
                textView.setMovementMethod(LinkMovementMethod.getInstance());
                
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle(R.string.comment_reference)
                       .setView(textView)
                       .setNegativeButton(R.string.okay_button, null)
                       .create()
                       .show();
            }
            else
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle(R.string.comment_not_found)
                       .setMessage(R.string.comment_not_found_message)
                       .setNegativeButton(R.string.okay_button, null)
                       .create()
                       .show();
            }
        }
        
    }
}
