package com.jpgdump.mobile.async;

import com.jpgdump.mobile.implementation.PictureUploadManager;
import com.jpgdump.mobile.interfaces.UploadInterface;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

public class UploadPicture extends AsyncTask<String, Void, Integer>
{
    Activity activity;
    ProgressDialog pd;
    
    public UploadPicture(Activity activity)
    {
        this.activity = activity;
        this.pd = new ProgressDialog(activity);
    }
    
    @Override
    protected void onPreExecute()
    {
        pd.setIndeterminate(true);
        pd.setMessage("Uploading");
        pd.show();
    }
    
    @Override
    protected Integer doInBackground(String... params)
    {
        UploadInterface uploader = new PictureUploadManager();
        
        return uploader.uploadPicture(params);
    }
    
    @Override
    protected void onPostExecute(Integer responseCode)
    {
        pd.dismiss();
        
        switch(responseCode)
        {
            case 0:
                // Failed to upload
                break;
                
            case 1:
                // Failed to post
                break;
                
            case 2:
                // Tags failed
                break;
                
            case 200:
                Toast.makeText(activity, "Successfully posted", Toast.LENGTH_SHORT).show();
                activity.getIntent().putExtra("reset", true);
                activity.recreate();
                break;
                
            case 400:
                // Bad request (Shouldn't happen)
                break;
                
            case 403:
                // Error in session
                break;
                
            default:
                Toast.makeText(activity, "You dun goofed: " + responseCode, Toast.LENGTH_SHORT).show();
                break;
        }
    }

}
