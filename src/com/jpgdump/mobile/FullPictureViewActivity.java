package com.jpgdump.mobile;

import com.jpgdump.mobile.fragments.FullPictureViewFragment;
import com.jpgdump.mobile.util.ContextLogger;
import com.jpgdump.mobile.util.Tags;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class FullPictureViewActivity extends FragmentActivity
{
    private final ContextLogger log = ContextLogger.getLogger(this);
    
    public static final int FRAME_ID = R.id.activity_post_viewer_frame;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_viewer);
        
        getActionBar().setDisplayHomeAsUpEnabled(true);
        
        if(savedInstanceState == null)
        {

            FragmentManager fragManager = getFragmentManager();
            
            FullPictureViewFragment pictureFrag = FullPictureViewFragment.newInstance();
            pictureFrag.setHasOptionsMenu(true);
            pictureFrag.setRetainInstance(true);
             
            fragManager.beginTransaction()
                       .add(FRAME_ID, pictureFrag, 
                               Tags.FULL_PICTURE_VIEW_FRAGMENT)
                       .commit();
        }
    }

    @Override
    public void onBackPressed()
    {
        FragmentManager manager = getFragmentManager();
        
        if(manager.findFragmentByTag(Tags.COMMENT_VIEWER_FRAGMENT) == null)
        {
            if(BuildConfig.DEBUG)
            {
                log.i("Fragment wasn't found, finish");
            }
            
            int position = getIntent().getIntExtra("position", -1);
            int goatVal = getIntent().getIntExtra("goatVal", 0);
    
            Intent data = new Intent();
            data.putExtra("position", position);
            data.putExtra("goatVal", goatVal);
            setResult(Tags.RESULT_OK, data);
            finish();
        }
        else
        {
            if(BuildConfig.DEBUG)
            {
                log.i("Fragment was found, pop it");
            }
            manager.popBackStack();
        }
    }
}
