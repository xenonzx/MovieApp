package com.luxtech_eg.movieapp;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


public class MoviesListActivity extends AppCompatActivity{
    boolean twoPane;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies_list);
        if (findViewById(R.id.detail_container)!=null){
            twoPane=true;
        }else{
            twoPane=false;
        }

        if (savedInstanceState == null) {
            //master fragment
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new MoviesListFragment())
                    .commit();
            if (twoPane) {
                getFragmentManager().beginTransaction()
                        .add(R.id.detail_container, new MoviesListFragment())
                        .commit();
            }

        }
    }
}
