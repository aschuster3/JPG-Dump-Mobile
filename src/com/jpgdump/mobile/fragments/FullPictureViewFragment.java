package com.jpgdump.mobile.fragments;

import com.jpgdump.mobile.FullPictureViewActivity;
import com.jpgdump.mobile.R;
import com.jpgdump.mobile.interfaces.VotingInterface.PostType;
import com.jpgdump.mobile.interfaces.VotingInterface.VoteType;
import com.jpgdump.mobile.listeners.GoatPressListener;
import com.jpgdump.mobile.util.Tags;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * This fragment is purposed with displaying the picture or gif supplied from
 * the website. It handles voting and displaying picture properties.
 */
public class FullPictureViewFragment extends Fragment
{
    private WebView fullImage;
    private TextView title, goatCountView;
    private Button upGoatButton, downGoatButton;

    public static FullPictureViewFragment newInstance()
    {
        return new FullPictureViewFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {

        Activity activity = getActivity();
        View view = null;

        if (activity != null)
        {
            view = inflater.inflate(R.layout.fragment_picture_view, container,
                    false);

            Intent intent = getActivity().getIntent();

            String picUrl = intent.getStringExtra("url");
            String postId = intent.getStringExtra("postId");
            String upvotes = intent.getStringExtra("upvotes") == null ? "0"
                    : intent.getStringExtra("upvotes");
            String downvotes = intent.getStringExtra("downvotes") == null ? "0"
                    : intent.getStringExtra("downvotes");
            String titleStr = intent.getStringExtra("title");
            String height = intent.getStringExtra("height");

            int goatTotal = Integer.parseInt(upvotes)
                    - Integer.parseInt(downvotes);

            /*
             *  Assign the total upvote/downvote count and give a corresponding
             *  color
             */
            goatCountView = (TextView) view.findViewById(R.id.goat_count);
            setGoatAmount(goatTotal);

            // Get the buttons and put necessary listeners
            upGoatButton = (Button) view.findViewById(R.id.peak_button);
            downGoatButton = (Button) view.findViewById(R.id.weak_button);

            upGoatButton.setOnClickListener(new GoatPressListener(activity,
                    postId, VoteType.UP, goatCountView, PostType.POST));
            downGoatButton.setOnClickListener(new GoatPressListener(activity,
                    postId, VoteType.DOWN, goatCountView, PostType.POST));

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
            fullImage.setBackgroundColor(Color.BLACK);
            
            fullImage.getSettings().setBuiltInZoomControls(true);
            fullImage.getSettings().setUseWideViewPort(true);
            fullImage.getSettings().setLoadWithOverviewMode(true);

            // This html formatting will center the image within the webview and make the background black
            String HTML_FORMAT;
            
            if(Integer.parseInt(height) < 2000)
            {
                HTML_FORMAT = "<html><head><style>img {position:absolute; "
                        + "top:0; bottom:0; left:0; right:0; margin:auto;}</style>" 
                        + "<body bgcolor=\"black\"><img src = "
                        + "\"%s\" /></body></html>";
            }
            else
            {
                HTML_FORMAT = "<html><head><style>img {display:block; "
                        + "margin:auto}</style>" 
                        + "<body bgcolor=\"black\"><img src = "
                        + "\"%s\" /></body></html>";
            }
         
            // This html formatting will make the background black
//            String HTML_FORMAT = "<html><head>"
//                    + "<body bgcolor=\"black\"><img src = "
//                    + "\"%s\" /></body></html>";
            
            // This html formatting will center the image horizontally and make the background black
            
            final String html = String.format(HTML_FORMAT, picUrl);
            fullImage.loadDataWithBaseURL("", html, "text/html", "UTF-8", "");
        }

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.picture, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Activity activity = getActivity();
        Intent intent = activity.getIntent();

        // Handle item selection
        switch (item.getItemId())
        {
        case R.id.picture_info:
            pictureInfoPopup(activity, intent);

            return true;
        case R.id.comments:
            FragmentManager manager = getActivity().getFragmentManager();
            
            Fragment commentFrag = CommentViewerFragment.newInstance(intent.getStringExtra("postId"));
            commentFrag.setHasOptionsMenu(true);
            
            manager.beginTransaction()
                   .replace(FullPictureViewActivity.FRAME_ID, 
                           commentFrag,
                           Tags.COMMENT_VIEWER_FRAGMENT)
                   .addToBackStack(null)
                   .commit();
            return true;
            
        case android.R.id.home:
            activity.onBackPressed();
            
        default:
            return super.onOptionsItemSelected(item);
            
        }
    }

    private float pxFromDp(float dp)
    {
        return dp * this.getResources().getDisplayMetrics().density;
    }

    private void setGoatAmount(int goatTotal)
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
    
    /*
     * Brings up a popup up with the properties of a picture
     */
    private void pictureInfoPopup(Activity activity, Intent intent)
    {
        String width = intent.getStringExtra("width");
        String height = intent.getStringExtra("height");
        String score = intent.getStringExtra("score");
        String created = (String) DateFormat
                .format("dd-MM-yyyy", Long.parseLong(intent
                        .getStringExtra("created")) * 1000);

        LayoutInflater inflater = activity.getLayoutInflater();
        LinearLayout popup = (LinearLayout) inflater.inflate(
                R.layout.pic_info_popup, null);

        TextView widthTV = new TextView(activity);
        widthTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        TextView heightTV = new TextView(activity);
        heightTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        TextView scoreTV = new TextView(activity);
        scoreTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        TextView dateTV = new TextView(activity);
        dateTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

        widthTV.setText("Width:\t\t\t" + width);
        widthTV.setTextColor(0xFFFFFFFF);
        heightTV.setText("Height:\t\t\t" + height);
        heightTV.setTextColor(0xFFFFFFFF);
        scoreTV.setText("Score:\t\t\t" + score);
        scoreTV.setTextColor(0xFFFFFFFF);
        dateTV.setText("Date:\t\t\t" + created);
        dateTV.setTextColor(0xFFFFFFFF);

        popup.addView(widthTV);
        popup.addView(heightTV);
        popup.addView(scoreTV);
        popup.addView(dateTV);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(popup).setCancelable(true);

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setLayout((int) pxFromDp(250),
                (int) pxFromDp(300));
    }

}
