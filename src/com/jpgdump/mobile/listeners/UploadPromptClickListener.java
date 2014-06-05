package com.jpgdump.mobile.listeners;

import java.io.File;
import java.io.IOException;

import com.jpgdump.mobile.HomeActivity;
import com.jpgdump.mobile.R;
import com.jpgdump.mobile.util.Tags;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

public class UploadPromptClickListener implements OnClickListener
{
    Activity activity;
    
    public UploadPromptClickListener(Activity activity)
    {
        this.activity = activity;
    }

    @Override
    public void onClick(DialogInterface dialog, int which)
    {
        Intent intent;
        
        switch(which)
        {
            case AlertDialog.BUTTON_POSITIVE:
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(intent.resolveActivity(activity.getPackageManager()) != null)
                {
                    File photoFile = null;
                    try 
                    {
                        photoFile = ((HomeActivity) activity).createImageFile();
                    } catch (IOException ex) 
                    {
                        // Error occurred while creating the File
                        Toast.makeText(activity, 
                                activity.getResources().getString(R.string.error_making_picture), 
                                Toast.LENGTH_SHORT).show();
                    }
                    
                    // Continue only if the File was successfully created
                    if (photoFile != null) 
                    {
                        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(photoFile));
                        activity.startActivityForResult(intent, Tags.CAMERA_REQUEST_CODE);
                    }
                }
                break;
                
            case AlertDialog.BUTTON_NEGATIVE:
                intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                activity.startActivityForResult(
                        Intent.createChooser(intent, "Select Picture"), Tags.SELECT_PICTURE);
                break;
                
            default:
                break;
        }

    }

}
