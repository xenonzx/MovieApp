package com.luxtech_eg.movieapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.luxtech_eg.movieapp.data.Movie;

/**
 * Created by ahmed on 26/12/15.
 */
public class DetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity_layout);
        if (savedInstanceState == null) {

            Movie m=(Movie)getIntent().getExtras().getSerializable(DetailFragment.MOVIE_OBJECT_KEY);
            Bundle arguments = new Bundle();
            arguments.putSerializable(DetailFragment.MOVIE_OBJECT_KEY, m);
            DetailFragment df =new DetailFragment();
            df.setArguments(arguments);
            getFragmentManager().beginTransaction()
                    .add(R.id.detail_container, df)
                    .commit();
        }
    }
}
