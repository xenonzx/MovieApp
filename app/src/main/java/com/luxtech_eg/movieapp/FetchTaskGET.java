package com.luxtech_eg.movieapp;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by ahmed on 30/12/15.
 */
public class FetchTaskGET extends AsyncTask<String,Void,String>{
    String LogTag ="FetchTaskGET";

    public String getLogTAG() {
        return LogTag;
    }

    public void setLogTAG(String TAG) {
        this.LogTag = TAG;
    }




    @Override
    protected String doInBackground(String... url) {
        Log.v(LogTag, "doInBackground");
        if (url.length == 0) {
            // protection
            return null;
        }
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String moviesJson;
        try{
            URL apiUrl=new URL(url[0]);
            Log.v(LogTag, " url passedto AsyncTask" + url[0]);
            urlConnection = (HttpURLConnection) apiUrl.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                Log.v(LogTag,"no input stream");
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            moviesJson=buffer.toString();
            return moviesJson;


        }catch (MalformedURLException e) {
            Log.e(LogTag,"MalformedURLException");
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LogTag, "Error closing stream", e);
                }
            }
        }
        return null;
    }
}
