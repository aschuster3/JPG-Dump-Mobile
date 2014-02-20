package com.jpgdump.mobile.async;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import com.jpgdump.mobile.BuildConfig;
import com.jpgdump.mobile.objects.Session;
import com.jpgdump.mobile.util.Tags;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

public class CreateSession extends AsyncTask<Void, Void, Session>
{
    private static final String URL = "http://jpgdump.com/api/v1/sessions";
    private final Activity activity;
    
    public CreateSession(Activity activity)
    {
        this.activity = activity;
    }
    
    @Override
    protected Session doInBackground(Void... arg0)
    {
        Session sesh = null;
        URL obj = null;
        try
        {
            obj = new URL(URL);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            con.setRequestMethod("POST");
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.flush();
            wr.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null)
            {
                response.append(inputLine);
            }
            in.close();

            JSONObject rawJson = new JSONObject(response.toString());
            
            sesh = new Session(rawJson.getString("kind"), rawJson.getString("id"),
                    rawJson.getString("key"));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return sesh;
    }
    
    @Override
    protected void onPostExecute(Session sesh)
    {
        //Save the session id and session key to the phone
        SharedPreferences sharedPrefs = activity.getSharedPreferences(Tags.SESSION_INFO, 0);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        
        editor.putString(Tags.SESSION_ID, sesh.getId());
        editor.putString(Tags.SESSION_KEY, sesh.getKey());
        
        if(BuildConfig.DEBUG)
        {
            Log.i("CreateSession", "###New Session Created###\nSession ID: " + sesh.getId() + "\n"
                                 + "Session Key: " + sesh.getKey());
        }
        
        editor.commit();
    }
}
