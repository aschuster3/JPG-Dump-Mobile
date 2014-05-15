package com.jpgdump.mobile.async;

import java.util.List;

import android.app.Fragment;
import android.os.AsyncTask;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.jpgdump.mobile.R;
import com.jpgdump.mobile.adapters.CommentListAdapter;
import com.jpgdump.mobile.implementation.CommentManager;
import com.jpgdump.mobile.interfaces.CommentsInterface;
import com.jpgdump.mobile.objects.Comment;

public class FetchComments extends AsyncTask<String, Void, List<Comment>>
{
    Fragment fragment;
    
    public FetchComments(Fragment fragment)
    {
        this.fragment = fragment;
    }
    
    @Override
    protected List<Comment> doInBackground(String... commentParams)
    {
        /*
         * commentParams[0]: holds the number of comments being queried
         * commentParams[1]: holds the current number of comments
         * commentParams[2]: holds a filter (if it exists)
         */
        
        CommentsInterface comments = new CommentManager();
        
        return comments.retrieveComments(commentParams[0], commentParams[1], "id", commentParams[2]);
    }
    
    @Override
    protected void onPostExecute(List<Comment> comments)
    {
        /*
         * Populate the list designated for comments
         * 
         * Check if comments are present.  If they are not, don't make the adapter
         */
        if(fragment != null && fragment.getActivity() != null)
        {
            View view = fragment.getView();
            ListView commentList = (ListView) view.findViewById(R.id.comments_list);
            EditText commentTextField = (EditText) view.findViewById(R.id.comment_text_field);
            
            if(comments.size() != 0)
            {
                BaseAdapter commentViewAdapter = new CommentListAdapter(fragment.getActivity(), commentTextField, comments);
                
                commentList.setAdapter(commentViewAdapter);
            }
        }
    }
}
