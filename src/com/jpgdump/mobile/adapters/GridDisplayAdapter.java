package com.jpgdump.mobile.adapters;

import java.util.ArrayList;

import com.jpgdump.mobile.HomeActivity;
import com.jpgdump.mobile.R;
import com.jpgdump.mobile.objects.Post;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GridDisplayAdapter extends BaseAdapter
{
    Context context;
    ArrayList<Post> posts;
    int layout;
    
    public GridDisplayAdapter(Context context, ArrayList<Post> posts)
    {
        this.context = context;
        this.posts = posts;
        this.layout = R.layout.grid_display_panel;
    }
    
    @Override
    public int getCount()
    {
        return posts.size();
    }

    @Override
    public Object getItem(int i)
    {
        return posts.get(i);
    }

    @Override
    public long getItemId(int i)
    {
        return posts.get(i).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View view = convertView;
        DisplayHolder holder;
        if(view == null)
        {
            LayoutInflater inflater = 
                    (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(layout, parent, false);
            
            holder = new DisplayHolder();
            holder.title = (TextView) view.findViewById(R.id.picture_title_grid_display);
            holder.thumbnail = (ImageView) view.findViewById(R.id.picture_container_grid_display);
            
            view.setTag(holder);
        }
        else
        {
            holder = (DisplayHolder) view.getTag();
        }
        
        final Post post = (Post) getItem(position);
        
        //If there is not title, hide the TextView, 
        //else put the title in and make the TextView visible
        if(post.getTitle().equals(""))
        {
            holder.title.setVisibility(View.GONE);
        }
        else
        {
            holder.title.setVisibility(View.VISIBLE);
            holder.title.setText(post.getTitle());
        }
        
        HomeActivity activity = (HomeActivity) context;
        if(activity.getBitmapFromMemCache(post.hashCode()) == null)
        {
            activity.addBitmapToMemoryCache(post.hashCode(), post.getThumbnailBitmap());
            holder.thumbnail.setImageBitmap(post.getThumbnailBitmap());
        }
        else
        {
            holder.thumbnail.setImageBitmap(activity.getBitmapFromMemCache(post.hashCode()));
        }
        
        return view;
    }
    
    static class DisplayHolder
    {
        TextView title;
        ImageView thumbnail;
    }
}
