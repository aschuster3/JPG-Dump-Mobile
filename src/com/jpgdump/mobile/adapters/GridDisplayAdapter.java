package com.jpgdump.mobile.adapters;

import java.util.ArrayList;

import com.jpgdump.mobile.HomeActivity;
import com.jpgdump.mobile.R;
import com.jpgdump.mobile.objects.Post;

import android.content.Context;
import android.graphics.Bitmap;
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
    
    public static final boolean DEBUG = false;
    
    public GridDisplayAdapter(Context context, ArrayList<Post> posts)
    {
        this.context = context;
        this.posts = posts;
        this.layout = R.layout.grid_display_panel;
    }
    
    public void addItems(ArrayList<Post> newPosts)
    {
        posts.addAll(newPosts);
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
            holder.title.setTextColor(0xFFCACACA);
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
        Bitmap bmp = activity.getBitmapFromMemCache(post.getId());
        if(bmp == null)
        {
            bmp = activity.getBitmapFromDiskCache(post.getId());
            if(bmp == null)
            {
                //Set the loading image and then begin loading
                
                holder.thumbnail.setImageResource(R.drawable.no_image_classifier);
            }
            else
            {
                if(DEBUG)
                {
                    holder.thumbnail.setImageResource(R.drawable.disk_image_classifier);
                }
                else
                {
                    holder.thumbnail.setImageBitmap(bmp);
                }
            }
        }
        else
        {
            if(DEBUG)
            {
                holder.thumbnail.setImageResource(R.drawable.mem_image_classifier);
            }
            else
            {
                holder.thumbnail.setImageBitmap(bmp);
            }
        }
        
        return view;
    }
    
    static class DisplayHolder
    {
        TextView title;
        ImageView thumbnail;
    }
}
