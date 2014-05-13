package com.jpgdump.mobile.async;

import com.jpgdump.mobile.R;
import com.jpgdump.mobile.implementation.VoteManager;
import com.jpgdump.mobile.interfaces.VotingInterface;
import com.jpgdump.mobile.interfaces.VotingInterface.PostType;
import com.jpgdump.mobile.interfaces.VotingInterface.VoteType;
import com.jpgdump.mobile.objects.Comment;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;

public final class SubmitVote extends AsyncTask<PostType, Void, Integer>
{
    private final Context activity;
    private final String sessionId, sessionKey, id;
    private final VoteType voteType;
    private final TextView goatCountView;
    private final Comment comment;
    
    private final static int COLOR_RED = 0xFFFF0013;
    private final static int COLOR_GREEN = 0xFF00FF00;
    private final static int COLOR_GRAY = 0xFF808080;
    
    public SubmitVote(Context activity, String sessionId, 
            String sessionKey, String id, VoteType voteType,
            TextView goatCountView)
    {
        this.activity = activity;
        this.sessionId = sessionId;
        this.sessionKey = sessionKey;
        this.id = id;
        this.voteType = voteType;
        this.goatCountView = goatCountView;
        this.comment = null;
    }
    
    public SubmitVote(Context activity, String sessionId, 
            String sessionKey, Comment comment, VoteType voteType,
            TextView goatCountView)
    {
        this.activity = activity;
        this.sessionId = sessionId;
        this.sessionKey = sessionKey;
        this.id = comment.getId();
        this.voteType = voteType;
        this.goatCountView = goatCountView;
        this.comment = comment;
    }
    
    @Override
    protected Integer doInBackground(PostType... params)
    {
        /*
         * params[0]: contains post type (currently post or comment)
         */
        
        VotingInterface voter = new VoteManager();
        
        int responseCode;
        switch(params[0])
        {
        case POST:
            responseCode = voter.distributeGoat(sessionId, sessionKey, id, voteType);
            break;
            
        case COMMENT:
            responseCode = voter.distributeCommentGoat(sessionId, sessionKey, id, voteType);
            break;
            
        default:
            responseCode = 0;
        }
        return responseCode;
    }
    
    @Override
    protected void onPostExecute(Integer responseCode)
    {
        Resources res = activity.getResources();
        
        switch (responseCode)
        {
            case 200:
                Toast.makeText(activity, res.getString(R.string.code200vote), Toast.LENGTH_SHORT).show();
                updateView();
                checkAndUpdateComment();
                break;
            case 401:
                Toast.makeText(activity, res.getString(R.string.code401vote), Toast.LENGTH_SHORT).show();
                break;
            case 403:
                Toast.makeText(activity, res.getString(R.string.code403vote), Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(activity, res.getString(R.string.codeWTF), Toast.LENGTH_SHORT).show();
                break;
        }
    }
    
    private void updateView()
    {
        String num = (String) goatCountView.getText();
        
        int newGoats = Integer.parseInt(num);
        newGoats += voteType.getValue();
        ((Activity) activity).getIntent().putExtra("goatVal", voteType.getValue());
        goatCountView.setText("" + newGoats);
        
        if(newGoats < 0)
            { goatCountView.setTextColor(COLOR_RED);}
        else if(newGoats > 0) 
            { goatCountView.setTextColor(COLOR_GREEN);}
        else 
            { goatCountView.setTextColor(COLOR_GRAY);}
    }
    
    private void checkAndUpdateComment()
    {
        if(comment != null)
        {
            if(voteType.getValue() == 1)
            {
                comment.addUpvote();
            }
            else
            {
                comment.addDownvote();
            }
        }
    }

}
