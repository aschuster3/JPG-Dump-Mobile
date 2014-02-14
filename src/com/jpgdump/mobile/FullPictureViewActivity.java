package com.jpgdump.mobile;

import com.jpgdump.mobile.listeners.GoatPressListener;
import com.jpgdump.mobile.util.Tags;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
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
        String upvotes = intent.getStringExtra("upvotes")  == null ? "0" : intent.getStringExtra("upvotes");
        String downvotes = intent.getStringExtra("downvotes") == null ? "0" : intent.getStringExtra("downvotes");
        String titleStr = intent.getStringExtra("title");
        
        int goatTotal = Integer.parseInt(upvotes) - Integer.parseInt(downvotes);

        //Assign the total upvote/downvote count and give a corresponding color
        goatCount = (TextView) findViewById(R.id.goat_count);
        setGoatAmount(goatTotal);
        
        //Get the buttons and put necessary listeners
        upGoatButton = (Button) findViewById(R.id.weak_button);
        downGoatButton = (Button) findViewById(R.id.peak_button);
        
        upGoatButton.setOnClickListener(new GoatPressListener(this, postId, false, goatCount));
        downGoatButton.setOnClickListener(new GoatPressListener(this, postId, true, goatCount));
        
        
        
        //Put the title of the post at the top (if it exists)
        title = (TextView) findViewById(R.id.picture_title);
        if(titleStr.equals("")) 
            { title.setVisibility(View.GONE);}
        else 
            { title.setText(titleStr);}
        
        fullImage = (WebView) findViewById(R.id.full_size_picture);
        fullImage.getSettings().setBuiltInZoomControls(true);
        fullImage.getSettings().setUseWideViewPort(true);
        fullImage.getSettings().setLoadWithOverviewMode(true);
        
        
        //This html formatting will center the image within the webview
        String HTML_FORMAT = "<html><head><style>img {position:absolute; " +
                "top:0; bottom:0; left:0; right:0; margin:auto;}</style><body bgcolor=\"black\"><img src = "+
                "\"%s\" /></body></html>";
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
    
    private void setGoatAmount(int goatTotal)
    {
        goatCount.setText("" + goatTotal);
        
        if(goatTotal < 0) 
            { goatCount.setTextColor(0xFFFF0013);}
        else if(goatTotal > 0) 
            { goatCount.setTextColor(0xFF00FF00);}
        else 
            { goatCount.setTextColor(0xFF808080);}
    }
}
