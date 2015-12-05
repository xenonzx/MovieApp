package com.luxtech_eg.movieapp;


import android.app.Activity;
import android.os.Bundle;


public class MoviesListActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies_list);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new MoviesListFragment())
                    .commit();
        }
    }
}
