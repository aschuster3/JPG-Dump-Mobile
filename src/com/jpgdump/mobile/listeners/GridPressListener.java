package com.jpgdump.mobile.listeners;

import com.jpgdump.mobile.FullPictureViewActivity;
import com.jpgdump.mobile.objects.Post;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class GridPressListener implements OnItemClickListener
{
    Activity activity;
    
    public GridPressListener(Activity activity)
    {
        this.activity = activity;
    }
    
    @Override
    public void onItemClick(AdapterView<?> grid, View view, int position, long id)
    {
        Intent intent = new Intent(activity, FullPictureViewActivity.class);
        Post post = (Post) grid.getAdapter().getItem(position);
        
        intent.putExtra("url", post.getUrl());
        
        activity.startActivity(intent);
    }

}
