package com.jpgdump.mobile;

import android.os.Bundle;
import android.view.MenuItem;
import android.app.Activity;

public class PreferencesActivity extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            //Refresh the page
            case android.R.id.home:
                onBackPressed();
                
            //Do default action
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
