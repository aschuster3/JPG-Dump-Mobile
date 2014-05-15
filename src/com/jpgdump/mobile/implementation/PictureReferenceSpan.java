package com.jpgdump.mobile.implementation;

import com.jpgdump.mobile.R;
import com.jpgdump.mobile.interfaces.PostsInterface;
import com.jpgdump.mobile.util.ContextLogger;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.webkit.WebView;

public class PictureReferenceSpan extends ClickableSpan
{
    private final ContextLogger log = ContextLogger.getLogger(this);
    private String postId;
    private Activity activity;
    
    public PictureReferenceSpan(String postId, Activity activity)
    {
        this.postId = postId;
        this.activity = activity;
    }
    
    @Override
    public void onClick(View widget)
    {
        log.i("You're pressing " + postId);
        
        new FetchPostReferenced().execute();
    }
    
    @Override
    public void updateDrawState(TextPaint ds)
    {
        super.updateDrawState(ds);
        ds.setTextScaleX(1.2f);
        ds.setARGB(255, 255, 255, 0);
    }
    
    private class FetchPostReferenced extends AsyncTask<Void, Void, String>
    {

        @Override
        protected String doInBackground(Void... params)
        {
            PostsInterface postManager = new PostManager();
            
            return postManager.getPictureURL(postId);
        }
        
        @Override
        protected void onPostExecute(String pictureUrl)
        {
            if(pictureUrl != null)
            {
                WebView webView = new WebView(activity);
                
                webView.getSettings().setBuiltInZoomControls(true);
                webView.getSettings().setUseWideViewPort(true);
                webView.getSettings().setLoadWithOverviewMode(true);
                
                
                String HTML_FORMAT = "<html><head><style>img {display:block; "
                        + "margin:auto}</style>" 
                        + "<body bgcolor=\"black\"><img src = "
                        + "\"%s\" /></body></html>";
                
                final String html = String.format(HTML_FORMAT, pictureUrl);
                webView.loadDataWithBaseURL("", html, "text/html", "UTF-8", "");
                
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle(R.string.picture_referenced)
                       .setView(webView)
                       .setNegativeButton(R.string.okay_button, null)
                       .create()
                       .show();
            }
            else
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle(R.string.picture_not_found)
                       .setMessage(R.string.picture_not_found_message)
                       .setNegativeButton(R.string.okay_button, null)
                       .create()
                       .show();
            }
        }
        
    }
    
}
