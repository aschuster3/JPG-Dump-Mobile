package com.jpgdump.mobile.async;

import com.jpgdump.mobile.R;
import com.jpgdump.mobile.implementation.VoteManager;
import com.jpgdump.mobile.interfaces.VotingInterface;

import android.app.Activity;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;

public class SubmitVote extends AsyncTask<Void, Void, Integer>
{
    Activity activity;
    String sessionId, sessionKey, postId;
    boolean isUp;
    TextView goatCount;
    
    public SubmitVote(Activity activity, String sessionId, 
            String sessionKey, String postId, boolean isUp,
            TextView goatCount)
    {
        this.activity = activity;
        this.sessionId = sessionId;
        this.sessionKey = sessionKey;
        this.postId = postId;
        this.isUp = isUp;
        this.goatCount = goatCount;
    }
    
    @Override
    protected Integer doInBackground(Void... params)
    {
        VotingInterface voter = new VoteManager();
        
        return voter.distributeGoat(sessionId, sessionKey, postId, isUp);
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
                if(isUp)
                    { newGoats++;}
                else
                    { newGoats--;}
                
                goatCount.setText("" + newGoats);
                break;
            case 401:
                Toast.makeText(activity, res.getString(R.string.code401), Toast.LENGTH_SHORT).show();
                break;
            case 403:
                Toast.makeText(activity, res.getString(R.string.code403), Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(activity, res.getString(R.string.codeWTF) + " code: " + responseCode, Toast.LENGTH_SHORT).show();
                break;
        }
    }

}
