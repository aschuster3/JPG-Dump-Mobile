package com.jpgdump.mobile.async;

import com.jpgdump.mobile.R;
import com.jpgdump.mobile.implementation.CommentManager;
import com.jpgdump.mobile.interfaces.CommentsInterface;
import com.jpgdump.mobile.util.Tags;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

public class PostComment extends AsyncTask<String, Void, Integer>
{
    private String sessionId, sessionKey;
    private Activity activity;
    
    public PostComment(String sessionId, String sessionKey, Activity activity)
    {
        this.sessionId = sessionId;
        this.sessionKey = sessionKey;
        this.activity = activity;
    }

    @Override
    protected Integer doInBackground(String... params)
    {
        /*
         *  params[0]: The comment that the user wishes to post
         *  params[1]: The postId
         */
        
        CommentsInterface commentManager = new CommentManager();
        
        return commentManager.postComment(sessionId, sessionKey, params[0], params[1]);
    }
    
    @Override
    protected void onPostExecute(Integer responseCode)
    {
        Resources res = activity.getResources();
        
        switch (responseCode)
        {
            case 200:
                Toast.makeText(activity, res.getString(R.string.code200comment), Toast.LENGTH_SHORT).show();
                Fragment currentFrag = activity.getFragmentManager().findFragmentByTag(Tags.COMMENT_VIEWER_FRAGMENT);
                
                if(currentFrag != null)
                {
                    activity.getFragmentManager().beginTransaction()
                                                 .detach(currentFrag)
                                                 .attach(currentFrag)
                                                 .commit();
                    
                    //Clear the text field after the comment has been submitted
                    EditText textField = (EditText) currentFrag.getView().findViewById(R.id.comment_text_field);
                    textField.setText("");
                    
                    //Hide the keyboard after the comment has been submitted
                    InputMethodManager imm = (InputMethodManager)activity.getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(textField.getWindowToken(), 0);
                }
                break;
            case 400:
                Toast.makeText(activity, res.getString(R.string.code400comment), Toast.LENGTH_SHORT).show();
                break;
            case 401:
                Toast.makeText(activity, res.getString(R.string.code401comment), Toast.LENGTH_SHORT).show();
                break;
            case 403:
                Toast.makeText(activity, res.getString(R.string.code403comment), Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(activity, res.getString(R.string.codeWTF), Toast.LENGTH_SHORT).show();
                break;
        }
    }

}
