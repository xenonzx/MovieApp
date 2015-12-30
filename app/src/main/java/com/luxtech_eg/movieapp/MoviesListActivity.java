package com.luxtech_eg.movieapp;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


public class MoviesListActivity extends AppCompatActivity{
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
