package com.jpgdump.mobile;

import com.jpgdump.mobile.interfaces.VotingInterface.VoteType;
import com.jpgdump.mobile.listeners.GoatPressListener;
import com.jpgdump.mobile.util.Tags;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FullPictureViewActivity extends FragmentActivity
{
    private WebView fullImage;
    private TextView title, goatCount;
    private Button upGoatButton, downGoatButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_picture_view);

        Intent intent = getIntent();

        String picUrl = intent.getStringExtra("url");
        String postId = intent.getStringExtra("postId");
        String upvotes = intent.getStringExtra("upvotes") == null ? "0"
                : intent.getStringExtra("upvotes");
        String downvotes = intent.getStringExtra("downvotes") == null ? "0"
                : intent.getStringExtra("downvotes");
        String titleStr = intent.getStringExtra("title");

        int goatTotal = Integer.parseInt(upvotes) - Integer.parseInt(downvotes);

        // Assign the total upvote/downvote count and give a corresponding color
        goatCount = (TextView) findViewById(R.id.goat_count);
        setGoatAmount(goatTotal);

        // Get the buttons and put necessary listeners
        upGoatButton = (Button) findViewById(R.id.weak_button);
        downGoatButton = (Button) findViewById(R.id.peak_button);

        upGoatButton.setOnClickListener(
            new GoatPressListener(this, postId, VoteType.UP, goatCount));
        downGoatButton.setOnClickListener(
            new GoatPressListener(this, postId, VoteType.DOWN, goatCount));

        // Put the title of the post at the top (if it exists)
        title = (TextView) findViewById(R.id.picture_title);
        if (titleStr.equals(""))
        {
            title.setVisibility(View.GONE);
        }
        else
        {
            title.setText(titleStr);
        }

        fullImage = (WebView) findViewById(R.id.full_size_picture);
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

    @Override
    public void onBackPressed()
    {
        int position = getIntent().getIntExtra("position", -1);
        int goatVal = getIntent().getIntExtra("goatVal", 0);

        Intent data = new Intent();
        data.putExtra("position", position);
        data.putExtra("goatVal", goatVal);
        setResult(Tags.RESULT_OK, data);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.picture, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle item selection
        switch (item.getItemId())
        {
        case R.id.picture_info:
            String width = getIntent().getStringExtra("width");
            String height = getIntent().getStringExtra("height");
            String score = getIntent().getStringExtra("score");
            String created = (String) DateFormat
                    .format("dd-MM-yyyy", Long.parseLong(getIntent()
                            .getStringExtra("created")) * 1000);

            LayoutInflater inflater = this.getLayoutInflater();
            LinearLayout popup = (LinearLayout) inflater.inflate(
                    R.layout.pic_info_popup, null);

            TextView widthTV = new TextView(this);
            widthTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            TextView heightTV = new TextView(this);
            heightTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            TextView scoreTV = new TextView(this);
            scoreTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            TextView dateTV = new TextView(this);
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

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(popup).setCancelable(true);

            AlertDialog dialog = builder.create();
            dialog.show();
            dialog.getWindow().setLayout((int) pxFromDp(250), (int) pxFromDp(300));

            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

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

    private float pxFromDp(float dp)
    {
        return dp
                * this.getResources().getDisplayMetrics().density;
    }

}
