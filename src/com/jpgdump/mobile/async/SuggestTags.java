package com.jpgdump.mobile.async;

import com.jpgdump.mobile.implementation.TagsManager;
import com.jpgdump.mobile.interfaces.TagsInterface;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

public class SuggestTags extends AsyncTask<String, Void, Integer>
{
    Activity activity;
    
    public SuggestTags(Activity activity)
    {
        this.activity = activity;
    }
    
    @Override
    protected Integer doInBackground(String... params)
    {
        TagsInterface tagger = new TagsManager();
        
        int responseCode = 100;
        for(int i = 3; i < params.length; i++)
        {
            responseCode = tagger.tagPicture(params[0], params[1], params[2], params[i]);
        }
        return responseCode;
    }
    
    @Override
    protected void onPostExecute(Integer responseCode)
    {
        switch(responseCode)
        {
            case 2:
                Toast.makeText(activity, "There was an error suggesting one of your tags", Toast.LENGTH_SHORT).show();
                break;
                
            case 100:
                // No tags submitted
                break;
            
            case 200:
                Toast.makeText(activity, "Tags submitted", Toast.LENGTH_SHORT).show();
                break;
                
            default:
                // Wut
                break;
        }
    }

}
