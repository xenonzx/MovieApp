package com.luxtech_eg.movieapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.luxtech_eg.movieapp.data.Movie;


public class MoviesListActivity extends AppCompatActivity implements MoviesListFragment.Callback {
    String TAG=MoviesListActivity.class.getSimpleName();
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
                //todo detail fragment with the first movie
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
        // TODO: retrum real first object instead of temp
        return new Movie(102899,"temp","temp","temp","temp","/fYzpM9GmpBlIC893fNjoWCwE24H.jpg");
    }

    @Override
    public void onItemSelected(Movie movie) {
        Log.v(TAG, "onItemSelected");
        if (twoPane){
            Bundle arguments = new Bundle();
            arguments.putSerializable(DetailFragment.MOVIE_OBJECT_KEY, movie);
            DetailFragment df =new DetailFragment();
            df.setArguments(arguments);
            getFragmentManager().beginTransaction()
                    .replace(R.id.detail_container,df )
                    .commit();
        }else {
            // one pane
            Intent intent = new Intent(this, DetailActivity.class)
                    .putExtra(DetailFragment.MOVIE_OBJECT_KEY,movie);
            startActivity(intent);
        }

    }
}
