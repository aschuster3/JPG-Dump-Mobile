package com.jpgdump.mobile.async;

import com.jpgdump.mobile.R;
import com.jpgdump.mobile.implementation.VoteManager;
import com.jpgdump.mobile.interfaces.VotingInterface;
import com.jpgdump.mobile.interfaces.VotingInterface.VoteType;

import android.app.Activity;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;

public final class SubmitVote extends AsyncTask<Void, Void, Integer>
{
    private final Activity activity;
    private final String sessionId, sessionKey, postId;
    private final VoteType voteType;
    private final TextView goatCount;
    
    public SubmitVote(Activity activity, String sessionId, 
            String sessionKey, String postId, VoteType voteType,
            TextView goatCount)
    {
        this.activity = activity;
        this.sessionId = sessionId;
        this.sessionKey = sessionKey;
        this.postId = postId;
        this.voteType = voteType;
        this.goatCount = goatCount;
    }
    
    @Override
    protected Integer doInBackground(Void... params)
    {
        VotingInterface voter = new VoteManager();
        return voter.distributeGoat(sessionId, sessionKey, postId, voteType);
    }
    
    @Override
    protected void onPostExecute(Integer responseCode)
    {
        Resources res = activity.getResources();
        
        switch (responseCode)
        {
            case 200:
                Toast.makeText(activity, res.getString(R.string.code200), Toast.LENGTH_SHORT).show();
                String num = (String) goatCount.getText();
                
                int newGoats = Integer.parseInt(num);
                newGoats += voteType.getValue();
                activity.getIntent().putExtra("goatVal", voteType.getValue());
                goatCount.setText("" + newGoats);
                
                if(newGoats < 0)
                    { goatCount.setTextColor(0xFFFF0013);}
                else if(newGoats > 0) 
                    { goatCount.setTextColor(0xFF00FF00);}
                else 
                    { goatCount.setTextColor(0xFF808080);}
                break;
            case 401:
                Toast.makeText(activity, res.getString(R.string.code401), Toast.LENGTH_SHORT).show();
                break;
            case 403:
                Toast.makeText(activity, res.getString(R.string.code403), Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(activity, res.getString(R.string.codeWTF), Toast.LENGTH_SHORT).show();
                break;
        }
    }

}
