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
        
        return uploader.uploadPicture(params[0], params[1], params[2]);
    }
    
    @Override
    protected void onPostExecute(Integer responseCode)
    {
        pd.dismiss();
        
        switch(responseCode)
        {
            case 200:
                Toast.makeText(activity, "Successfully posted", Toast.LENGTH_SHORT).show();
                
            default:
                Toast.makeText(activity, "You dun goofed: " + responseCode, Toast.LENGTH_SHORT).show();
                
        }
    }

}
