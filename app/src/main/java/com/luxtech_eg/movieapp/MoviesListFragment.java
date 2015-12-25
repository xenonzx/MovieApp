package com.luxtech_eg.movieapp;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by ahmed on 05/12/15.
 */
public class MoviesListFragment extends Fragment {
    ListView moviesLV;
    ArrayList<String> moviesAL = new ArrayList<String>();
    ArrayAdapter <String> moviesAdapter;
    final static String APIKEY= "52665819dfedc14d67605e2bb09ed729";
    public MoviesListFragment(){

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.movies_list_fragment_layout,container,false);
        moviesLV= (ListView) rootView.findViewById(R.id.listview_movies);
        moviesAdapter= new ArrayAdapter<String>(getActivity(),R.layout.list_item_movie,R.id.tv_movie_name,moviesAL);
        moviesLV.setAdapter(moviesAdapter);
        //TODO remove the following lines later start
        populateDummyData();
        moviesAdapter.notifyDataSetChanged();
        //// TODO: end
        return rootView;
    }
    void populateDummyData(){
        moviesAL.add("hha1");
        moviesAL.add("hha2");
        moviesAL.add("hha3");
        moviesAL.add("hha4");
    }
    String buildPopularMoviesUri(){
        //http://api.themoviedb.org/3/discover/movie?api_key=&sort_by=popularity.desc
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http").authority("api.themoviedb.org")
                .appendPath("3")
                .appendPath("discover")
                .appendPath("movie")
                .appendQueryParameter("api_key", APIKEY )
                .appendQueryParameter("sort_by", "popularity.desc");
        return builder.build().toString();
    }

    String buildTopRatedMoviesUri(){
        //http://api.themoviedb.org/3/discover/movie?api_key=52665819dfedc14d67605e2bb09ed729&sort_by=vote_average.desc
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http").authority("api.themoviedb.org")
                .appendPath("3")
                .appendPath("discover")
                .appendPath("movie")
                .appendQueryParameter("api_key", APIKEY )
                .appendQueryParameter("sort_by", "vote_average.desc");

        return builder.build().toString();
    }

}
