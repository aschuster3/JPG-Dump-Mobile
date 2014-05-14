package com.jpgdump.mobile.adapters;

import java.util.List;

import com.jpgdump.mobile.BuildConfig;
import com.jpgdump.mobile.R;
import com.jpgdump.mobile.implementation.CommentReferenceSpan;
import com.jpgdump.mobile.implementation.PictureReferenceSpan;
import com.jpgdump.mobile.interfaces.VotingInterface.PostType;
import com.jpgdump.mobile.interfaces.VotingInterface.VoteType;
import com.jpgdump.mobile.listeners.GoatPressListener;
import com.jpgdump.mobile.objects.Comment;

import android.content.Context;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

public class CommentListAdapter extends BaseAdapter
{
    //private final ContextLogger log = ContextLogger.getLogger(this);
    
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
            
            view.setTag(holder);
        }
        else
        {
            holder = (Holder) view.getTag();
        }
        
        if(BuildConfig.DEBUG)
        {
            //log.i("Comment at pos " + position + " is: " + comments.get(position).getComment());
        }
        
        final Comment comment = comments.get(position);
        
        holder.commentText.setText(parseComment(comment.getComment()));
        holder.commentText.setMovementMethod(LinkMovementMethod.getInstance());
        
        
        int voteTotal = Integer.parseInt(comment.getUpvotes()) - 
                Integer.parseInt(comment.getDownvotes());
        
        
        setGoatAmount(holder.commentVotes, voteTotal);
        holder.commentId.setText(comment.getId());
        holder.peakButton.setOnClickListener(new GoatPressListener(context, comment, VoteType.UP,
                holder.commentVotes, PostType.COMMENT));
        return view;
    }
    
    static class Holder
    {
        public TextView commentText, commentVotes, commentId;
        public ImageButton peakButton;
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
    
    /**
     * A helper that sets Spans for picture references and
     * comment references.
     * 
     * @param comment Unparsed comment
     * @return Parsed comment with proper Spans
     */
    private SpannableString parseComment(CharSequence comment)
    {
        StringBuilder picId = new StringBuilder();
        SpannableString parsedComment = new SpannableString(comment);
        
        // Find the picture references and set their spans
        int     indexOfCarrot = ((String)comment).indexOf("^"),
                endSpan = indexOfCarrot + 1;
        while(indexOfCarrot != -1)
        {
            while(endSpan < comment.length() && comment.charAt(endSpan) >= '0' && comment.charAt(endSpan) <= '9')
            {
                picId.append(comment.charAt(endSpan));
                endSpan++;
            }
            
            if(picId.toString().matches("[0-9]+"))
            {
                parsedComment.setSpan(new PictureReferenceSpan(picId.toString()), indexOfCarrot, endSpan, 0);
            }
            
            indexOfCarrot = comment.toString().indexOf("^", endSpan);
            endSpan = indexOfCarrot + 1;
            picId = new StringBuilder();
        }
        
        // Find the comment references and set their spans
        int     indexOfAngleBracket = ((String)comment).indexOf(">");
        endSpan = indexOfAngleBracket + 1;
        
        while(indexOfAngleBracket != -1)
        {
            while(endSpan < comment.length() && comment.charAt(endSpan) >= '0' && comment.charAt(endSpan) <= '9')
            {
                picId.append(comment.charAt(endSpan));
                endSpan++;
            }
            
            if(picId.toString().matches("[0-9]+"))
            {
                parsedComment.setSpan(new CommentReferenceSpan(picId.toString()), indexOfAngleBracket, endSpan, 0);
            }
            
            indexOfAngleBracket = comment.toString().indexOf(">", endSpan);
            endSpan = indexOfAngleBracket + 1;
            picId = new StringBuilder();
        }
        
        
        return parsedComment;
    }
}
