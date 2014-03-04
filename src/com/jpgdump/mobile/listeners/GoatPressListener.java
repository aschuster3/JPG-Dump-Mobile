package com.jpgdump.mobile.listeners;

import com.jpgdump.mobile.async.SubmitVote;
import com.jpgdump.mobile.interfaces.VotingInterface.PostType;
import com.jpgdump.mobile.interfaces.VotingInterface.VoteType;
import com.jpgdump.mobile.util.Tags;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public final class GoatPressListener implements OnClickListener
{
    private final Context activity;
    private final String postId;
    private final VoteType voteType;
    private final TextView goatCount;
    private final PostType type;
    
    public GoatPressListener(Context activity, String postId,
            VoteType voteType, TextView goatCount, PostType type)
    {
        this.activity = activity;
        this.postId = postId;
        this.voteType = voteType;
        this.goatCount = goatCount;
        this.type = type;
    }
    
    @Override
    public void onClick(View view)
    {
        SharedPreferences prefs = ((Context)activity).getSharedPreferences(Tags.SESSION_INFO, 0);
        String sessionId = prefs.getString(Tags.SESSION_ID, "");
        String sessionKey = prefs.getString(Tags.SESSION_KEY, "");
        
        new SubmitVote(activity, sessionId, sessionKey, postId, voteType, goatCount).execute(type);
    }
}
