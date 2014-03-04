package com.jpgdump.mobile.fragments;

import com.jpgdump.mobile.R;
import com.jpgdump.mobile.async.FetchComments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CommentViewerFragment extends Fragment
{
    public static CommentViewerFragment newInstance(String postId)
    {
        CommentViewerFragment frag = new CommentViewerFragment();
        
        Bundle args = new Bundle();
        args.putString("postId", postId);
        frag.setArguments(args);
        
        return frag;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        Activity activity = getActivity();
        View view = null;
        
        if(activity != null)
        {
            view = inflater.inflate(R.layout.fragment_comment_viewer, container,
                    false);
            
            /*
             * Parameters for initial comment query
             */
            Bundle bundle = getArguments();
            String postId = bundle.getString("postId");
            
            String[] commentParams = new String[3];
            commentParams[0] = "10";
            commentParams[1] = "0";
            commentParams[2] = "[[\"postId==" + postId + "\"]]";
            
            new FetchComments(this).execute(commentParams);
        }

        return view;
    }
}
