package com.jpgdump.mobile.async;

import java.util.ArrayList;

import com.jpgdump.mobile.R;
import com.jpgdump.mobile.implementation.TagsManager;
import com.jpgdump.mobile.interfaces.TagsInterface;
import com.jpgdump.mobile.objects.Tag;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class FetchTags extends AsyncTask<String, Void, ArrayList<Tag>>
{
    private Activity activity;
    
    
    public FetchTags(Activity activity)
    {
        this.activity = activity;
    }
    
    @Override
    protected ArrayList<Tag> doInBackground(String... params)
    {
        TagsInterface tagFetcher = new TagsManager();
        
        return tagFetcher.getPictureTags(params[0]);
    }
    
    @Override
    protected void onPostExecute(ArrayList<Tag> tags)
    {
        StringBuilder approvedTags = new StringBuilder(),
                          unapprovedTags = new StringBuilder();
        
        for(int i = 0; i < tags.size(); i++)
        {
            if(tags.get(i).getAccepted().equals("1"))
            {
                approvedTags.append(tags.get(i).getTag());

                if(i < tags.size() -1)
                {
                    approvedTags.append(", ");
                }
            }
            else
            {
                unapprovedTags.append(tags.get(i).getTag());
                
                if(i < tags.size() -1)
                {
                    unapprovedTags.append(", ");
                }
            }
        }
        
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        View view = inflater.inflate(R.layout.dialog_tags_display, null, false);
        
        ((TextView) view.findViewById(R.id.approved_tags)).setText(approvedTags.toString());
        ((TextView) view.findViewById(R.id.unapproved_tags)).setText(unapprovedTags.toString());
        
        builder.setView(view);
        builder.setNeutralButton(R.string.okay_button, null);
        builder.create();
        builder.show();
    }

}
