package com.jpgdump.mobile.listeners;

import java.util.ArrayList;

import com.jpgdump.mobile.R;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class TagAddClickListener implements OnClickListener
{
    Activity activity;
    EditText tagText;
    LinearLayout currentTags;
    ArrayList<String> tags;
    
    public TagAddClickListener(Activity activity, EditText tagText, 
            LinearLayout currentTags, ArrayList<String> tags)
    {
        this.activity = activity;
        this.tagText = tagText;
        this.currentTags = currentTags;
        this.tags = tags;
    }

    @Override
    public void onClick(View v)
    {
        if(tags.size() > 4)
        {
            Toast.makeText(activity, 
                    activity.getResources().getString(R.string.enough_tags), 
                    Toast.LENGTH_SHORT).show();
        }
        else
        {
            TextView newTag = new TextView(activity);
            LinearLayout.LayoutParams params = 
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
            params.topMargin = 10;
            params.bottomMargin = 10;
            
            final String theNewTag = tagText.getText().toString();
            
            tagText.setText("");
            
            newTag.setText(theNewTag);
            tags.add(theNewTag);
            
            newTag.setLayoutParams(params);
            newTag.setClickable(true);
            newTag.setGravity(Gravity.CENTER_HORIZONTAL);
            newTag.setTextSize(12f * activity.getResources().getDisplayMetrics().scaledDensity);
            newTag.setOnClickListener(new View.OnClickListener()
            {

                @Override
                public void onClick(View v)
                {
                    tags.remove(theNewTag);
                    currentTags.removeView(v);
                }
                
            });
            
            currentTags.addView(newTag);
        }
    }

}
