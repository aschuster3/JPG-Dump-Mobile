package com.jpgdump.mobile.adapters;

import java.util.List;

import com.jpgdump.mobile.BuildConfig;
import com.jpgdump.mobile.R;
import com.jpgdump.mobile.interfaces.VotingInterface.PostType;
import com.jpgdump.mobile.interfaces.VotingInterface.VoteType;
import com.jpgdump.mobile.listeners.GoatPressListener;
import com.jpgdump.mobile.objects.Comment;
import com.jpgdump.mobile.util.ContextLogger;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

public class CommentListAdapter extends BaseAdapter
{
    private final ContextLogger log = ContextLogger.getLogger(this);
    
    Context context;
    List<Comment> comments;
    int layout;
    
    public CommentListAdapter(Context context, List<Comment> comments)
    {
        this.context = context;
        this.comments = comments;
        this.layout = R.layout.comment_container;
    }

    @Override
    public int getCount()
    {
        return comments.size();
    }

    @Override
    public Object getItem(int position)
    {
        return comments.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return Long.parseLong(comments.get(position).getId());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View view = convertView;
        Holder holder;
        if(view == null)
        {
            LayoutInflater inflater = 
                    (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(layout, parent, false);
            
            holder = new Holder();
            holder.commentText = (TextView) view.findViewById(R.id.comment_body);
            holder.commentVotes = (TextView) view.findViewById(R.id.comment_vote_total);
            holder.commentId = (TextView) view.findViewById(R.id.comment_id);
            holder.peakButton = (ImageButton) view.findViewById(R.id.comment_peak_button);
            holder.weakButton = (ImageButton) view.findViewById(R.id.comment_weak_button);
            
            view.setTag(holder);
        }
        else
        {
            holder = (Holder) view.getTag();
        }
        
        if(BuildConfig.DEBUG)
        {
            log.i("Comment at pos " + position + " is: " + comments.get(position).getComment());
        }
        
        final Comment comment = comments.get(position);
        
        //TODO: add an onclick to make the text view readable in a dialog
        holder.commentText.setText(comment.getComment());
        
        int voteTotal = Integer.parseInt(comment.getUpvotes()) - 
                Integer.parseInt(comment.getDownvotes());
        
        
        setGoatAmount(holder.commentVotes, voteTotal);
        holder.commentId.setText(comment.getId());
        holder.peakButton.setOnClickListener(new GoatPressListener(context, comment.getId(), VoteType.UP,
                holder.commentVotes, PostType.COMMENT));
        holder.weakButton.setOnClickListener(new GoatPressListener(context, comment.getId(), VoteType.DOWN,
                holder.commentVotes, PostType.COMMENT));
        return view;
    }
    
    static class Holder
    {
        public TextView commentText, commentVotes, commentId;
        public ImageButton peakButton, weakButton;
    }
    
    /**
     * A helper to change the color of the text view to 
     * be green for positive, red for negative, and
     * gray for zero
     * 
     * @param goatCountView
     * @param goatTotal
     */
    private void setGoatAmount(TextView goatCountView, int goatTotal)
    {
        goatCountView.setText("" + goatTotal);

        if (goatTotal < 0)
        {
            goatCountView.setTextColor(0xFFFF0013);
        }
        else if (goatTotal > 0)
        {
            goatCountView.setTextColor(0xFF00FF00);
        }
        else
        {
            goatCountView.setTextColor(0xFF808080);
        }
    }
}
