package com.jpgdump.mobile.fragments;

import com.jpgdump.mobile.R;
import com.jpgdump.mobile.async.FetchComments;
import com.jpgdump.mobile.listeners.SubmitCommentListener;
import com.jpgdump.mobile.util.ContextFormattingLogger;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class CommentViewerFragment extends Fragment
{
    private final ContextFormattingLogger log = ContextFormattingLogger.getLogger(this);
    
    private EditText commentTextField;
    private Button commentSubmitButton;
    
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
            
            commentTextField = (EditText) view.findViewById(R.id.comment_text_field);
            commentSubmitButton = (Button) view.findViewById(R.id.comment_submit_button);
            
            commentSubmitButton.setOnClickListener(new SubmitCommentListener(commentTextField, postId, activity));
            
            String[] commentParams = new String[3];
            commentParams[0] = "10";
            commentParams[1] = "0";
            commentParams[2] = "[[\"postId==" + postId + "\"]]";
            
            new FetchComments(this).execute(commentParams);
        }

        return view;
    }
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.comments, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Activity activity = getActivity();
        
        log.i("Item id: " + item.getItemId() + "\nAndroid Home: " + android.R.id.home);
        
        // Handle item selection
        switch (item.getItemId())
        {
            case android.R.id.home:
                activity.onBackPressed();
                
            default:
                return super.onOptionsItemSelected(item);
            
        }
    }
}
