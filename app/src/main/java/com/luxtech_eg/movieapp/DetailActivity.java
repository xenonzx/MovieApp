package com.luxtech_eg.movieapp;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by ahmed on 26/12/15.
 */
public class DetailActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity_layout);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.detail_container, new DetailFragment())
                    .commit();
        }
    }
}
