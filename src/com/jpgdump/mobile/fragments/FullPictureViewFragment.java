package com.jpgdump.mobile.fragments;

import com.jpgdump.mobile.R;
import com.jpgdump.mobile.interfaces.VotingInterface.VoteType;
import com.jpgdump.mobile.listeners.GoatPressListener;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

public class FullPictureViewFragment extends Fragment
{
    private WebView fullImage;
    private TextView title, goatCount;
    private Button upGoatButton, downGoatButton;
    
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        
        //TODO: move picture view here
        
        Activity activity = getActivity();
        View view = null;
        
        
        if(activity != null)
        {
            view = inflater.inflate(R.layout.fragment_picture_view, container);
            
            Intent intent = getActivity().getIntent();

            String picUrl = intent.getStringExtra("url");
            String postId = intent.getStringExtra("postId");
            String upvotes = intent.getStringExtra("upvotes") == null ? "0"
                    : intent.getStringExtra("upvotes");
            String downvotes = intent.getStringExtra("downvotes") == null ? "0"
                    : intent.getStringExtra("downvotes");
            String titleStr = intent.getStringExtra("title");

            int goatTotal = Integer.parseInt(upvotes) - Integer.parseInt(downvotes);

            // Assign the total upvote/downvote count and give a corresponding color
            goatCount = (TextView) view.findViewById(R.id.goat_count);
            setGoatAmount(goatTotal);

            // Get the buttons and put necessary listeners
            upGoatButton = (Button) view.findViewById(R.id.peak_button);
            downGoatButton = (Button) view.findViewById(R.id.weak_button);

            upGoatButton.setOnClickListener(
                new GoatPressListener(activity, postId, VoteType.UP, goatCount));
            downGoatButton.setOnClickListener(
                new GoatPressListener(activity, postId, VoteType.DOWN, goatCount));

            // Put the title of the post at the top (if it exists)
            title = (TextView) view.findViewById(R.id.picture_title);
            if (titleStr.equals(""))
            {
                title.setVisibility(View.GONE);
            }
            else
            {
                title.setText(titleStr);
            }

            fullImage = (WebView) view.findViewById(R.id.full_size_picture);
            fullImage.getSettings().setBuiltInZoomControls(true);
            fullImage.getSettings().setUseWideViewPort(true);
            fullImage.getSettings().setLoadWithOverviewMode(true);

            // This html formatting will center the image within the webview
            String HTML_FORMAT = "<html><head><style>img {position:absolute; "
                    + "top:0; bottom:0; left:0; right:0; margin:auto;}</style><body bgcolor=\"black\"><img src = "
                    + "\"%s\" /></body></html>";
            final String html = String.format(HTML_FORMAT, picUrl);
            fullImage.loadDataWithBaseURL("", html, "text/html", "UTF-8", "");
        }
        else
        {
            //TODO: Handle a non-existant parent Activity
        }
        
        return view;
    }
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.picture, menu);
    }
    
    //TODO: Handle creating options menu
    
    private void setGoatAmount(int goatTotal)
    {
        goatCount.setText("" + goatTotal);

        if (goatTotal < 0)
        {
            goatCount.setTextColor(0xFFFF0013);
        }
        else if (goatTotal > 0)
        {
            goatCount.setTextColor(0xFF00FF00);
        }
        else
        {
            goatCount.setTextColor(0xFF808080);
        }
    }

}
