package com.luxtech_eg.movieapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.luxtech_eg.movieapp.data.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by ahmed on 25/12/15.
 */
public class MoviesAdapter extends BaseAdapter {
    String TAG= MoviesAdapter.class.getSimpleName();
    Context mContext;
    ArrayList<Movie>movieArrayList;
    LayoutInflater layoutInflater;
    boolean showFavMovies;

    MoviesAdapter(){}
    MoviesAdapter(Context context,ArrayList<Movie> movieArrayList){
        this.mContext=context;
        this.movieArrayList=movieArrayList;
        layoutInflater = LayoutInflater.from(context);
        showFavMovies=MoviesListFragment.ONLINE_MOVIES;
    }
    MoviesAdapter(Context context,ArrayList<Movie> movieArrayList,boolean showFavMovies){
        this.mContext=context;
        this.movieArrayList=movieArrayList;
        layoutInflater = LayoutInflater.from(context);
        this.showFavMovies=showFavMovies;
    }
    @Override
    public int getCount() {
        return movieArrayList.size();
    }

    @Override
    public Movie getItem(int position) {
        return movieArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return (long) movieArrayList.get(position).getId();
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        view= layoutInflater.inflate(R.layout.list_item_movie,viewGroup,false);
        ImageView thumb=(ImageView)view.findViewById(R.id.iv_movie_thumb);
        if(isShowFavMovies()==MoviesListFragment.ONLINE_MOVIES) {
            Log.v(TAG,"poster using picasso");
            Picasso.with(mContext).load(movieArrayList.get(position).getImageUrl()).into(thumb);
        }else {
            Log.v(TAG,"poster from db");
           thumb.setImageBitmap(movieArrayList.get(position).getMoviePoster());
        }

        return view;
    }

    public boolean isShowFavMovies() {
        return showFavMovies;
    }

    public void setShowFavMovies(boolean showFavMovies) {
        this.showFavMovies = showFavMovies;
    }



}
