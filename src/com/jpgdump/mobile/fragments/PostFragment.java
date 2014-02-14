package com.jpgdump.mobile.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jpgdump.mobile.R;
import com.jpgdump.mobile.objects.Post;

public class PostFragment extends Fragment
{
   // private Post post;
    
    public static PostFragment newInstance(Post post)
    {
        PostFragment postFrag = new PostFragment();
        
        Bundle args = new Bundle();
        args.putString("kind", post.getKind());
        args.putString("id", post.getId());
        args.putString("url", post.getUrl());
        args.putString("width", post.getWidth());
        args.putString("height", post.getHeight());
        args.putString("created", post.getCreated());
        args.putInt("safety", post.getSafety());
        args.putString("mime", post.getMime());
        args.putString("upvotes", post.getUpvotes());
        args.putString("downvotes", post.getDownvotes());
        args.putString("score", post.getScore());
        args.putString("title", post.getTitle());
        
        postFrag.setArguments(args);
        
        return postFrag;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
//        Bundle bundle = getArguments();
        
//        post = new Post(bundle.getString("kind"), bundle.getString("id"),
//                bundle.getString("url"), bundle.getString("width"), bundle.getString("height"),
//                bundle.getString("created"), bundle.getInt("safety"), bundle.getString("mime"),
//                bundle.getString("upvotes"), bundle.getString("downvotes"),
//                bundle.getString("score"), bundle.getString("title"));
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.activity_full_picture_view, container,
                false);
        return view;
    }
}

//private String kind;
//private String id;
//private String url;
//private String width;
//private String height;
//private String created;
//private int safety;
//private String mime;
//private String upvotes;
//private String downvotes;
//private String score;
//private String title;
//private Bitmap thumbnailBitmap = null;