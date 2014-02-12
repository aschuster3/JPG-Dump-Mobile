package com.jpgdump.mobile;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.webkit.WebView;

public class FullPictureViewActivity extends FragmentActivity
{
    private WebView fullImage;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        //This is to test the transfer of a post
        setContentView(R.layout.post_fragment_layout);
        
        fullImage = (WebView) findViewById(R.id.full_size_picture);
        fullImage.getSettings().setBuiltInZoomControls(true);

        String url = getIntent().getStringExtra("url");
        
        //This html formatting will center the image within the webview
        String HTML_FORMAT = "<html><head><style>img {position:absolute; " +
                "top:0; bottom:0; left:0; right:0; margin:auto;}</style><body bgcolor=\"black\"><img src = "+
                "\"%s\" /></body></html>";
        final String html = String.format(HTML_FORMAT, url);
        fullImage.loadDataWithBaseURL("", html, "text/html", "UTF-8", "");
    }
}
