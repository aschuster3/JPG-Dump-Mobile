package com.jpgdump.mobile;

import com.jpgdump.mobile.fragments.FullPictureViewFragment;
import com.jpgdump.mobile.util.Tags;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

public class FullPictureViewActivity extends FragmentActivity
{
    public static final int FRAME_ID = R.id.activity_post_viewer_frame;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_viewer);

        //TODO: Attach the fragments to this activity
        FragmentManager fragManager = getFragmentManager();
        
        FullPictureViewFragment fragment = FullPictureViewFragment.newInstance();
        
        fragment.setHasOptionsMenu(true);
        
        fragManager.beginTransaction()
                   .add(FRAME_ID, fragment, 
                           Tags.FULL_PICTURE_VIEW_FRAGMENT)
                   .commit();
        
        Log.i("", "Attached fragment");
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
}
