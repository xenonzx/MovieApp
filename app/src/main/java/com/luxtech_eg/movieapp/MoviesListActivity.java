package com.luxtech_eg.movieapp;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.luxtech_eg.movieapp.data.Movie;


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


            if (twoPane) {
                Bundle arguments = new Bundle();
                arguments.putSerializable(DetailFragment.MOVIE_OBJECT_KEY, getFirstMoviObject());
                DetailFragment df =new DetailFragment();
                df.setArguments(arguments);
                getFragmentManager().beginTransaction()
                        .add(R.id.detail_container,df )
                        .commit();
            }
            //master fragment
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new MoviesListFragment())
                    .commit();

        }
    }
    Movie getFirstMoviObject(){
        return new Movie(102899,"temp","temp","temp","temp","/fYzpM9GmpBlIC893fNjoWCwE24H.jpg");
    }
}
