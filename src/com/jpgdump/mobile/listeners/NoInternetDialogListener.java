package com.jpgdump.mobile.listeners;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;

public class NoInternetDialogListener implements OnClickListener
{
    Activity activity;
    
    public NoInternetDialogListener(Activity activity)
    {
        this.activity = activity;
    }

    @Override
    public void onClick(DialogInterface dialog, int id)
    {
        Intent intent = activity.getIntent();
        intent.putExtra("reset", true);
        activity.recreate();
    }

}
