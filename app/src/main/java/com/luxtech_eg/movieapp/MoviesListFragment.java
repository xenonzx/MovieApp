package com.luxtech_eg.movieapp;

import android.app.Fragment;
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
        ArrayAdapter <String> moviesAdapter= new ArrayAdapter<String>(getActivity(),R.layout.list_item_movie,R.id.tv_movie_name,moviesAL);
        moviesLV.setAdapter(moviesAdapter);
        return rootView;
    }
}
