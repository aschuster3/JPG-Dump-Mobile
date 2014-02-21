package com.jpgdump.mobile;

import com.jpgdump.mobile.util.Tags;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FullPictureViewActivity extends FragmentActivity
{
    private FrameLayout frame;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_viewer);
        
        frame = (FrameLayout) findViewById(R.id.activity_post_viewer_frame);

        //TODO: Attach the fragments to this activity
        FragmentManager fragManager = getFragmentManager();
        
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

    
    //TODO: Delete this, move to FullPictureFragment
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle item selection
        switch (item.getItemId())
        {
        case R.id.picture_info:
            String width = getIntent().getStringExtra("width");
            String height = getIntent().getStringExtra("height");
            String score = getIntent().getStringExtra("score");
            String created = (String) DateFormat
                    .format("dd-MM-yyyy", Long.parseLong(getIntent()
                            .getStringExtra("created")) * 1000);

            LayoutInflater inflater = this.getLayoutInflater();
            LinearLayout popup = (LinearLayout) inflater.inflate(
                    R.layout.pic_info_popup, null);

            TextView widthTV = new TextView(this);
            widthTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            TextView heightTV = new TextView(this);
            heightTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            TextView scoreTV = new TextView(this);
            scoreTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            TextView dateTV = new TextView(this);
            dateTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

            widthTV.setText("Width:\t\t\t" + width);
            widthTV.setTextColor(0xFFFFFFFF);
            heightTV.setText("Height:\t\t\t" + height);
            heightTV.setTextColor(0xFFFFFFFF);
            scoreTV.setText("Score:\t\t\t" + score);
            scoreTV.setTextColor(0xFFFFFFFF);
            dateTV.setText("Date:\t\t\t" + created);
            dateTV.setTextColor(0xFFFFFFFF);

            popup.addView(widthTV);
            popup.addView(heightTV);
            popup.addView(scoreTV);
            popup.addView(dateTV);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(popup).setCancelable(true);

            AlertDialog dialog = builder.create();
            dialog.show();
            dialog.getWindow().setLayout((int) pxFromDp(250), (int) pxFromDp(300));

            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    private float pxFromDp(float dp)
    {
        return dp
                * this.getResources().getDisplayMetrics().density;
    }

}
