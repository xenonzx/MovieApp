package com.luxtech_eg.movieapp;

import android.content.Context;
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
    Context mContext;
    ArrayList<Movie>movieArrayList;
    LayoutInflater layoutInflater;

    MoviesAdapter(){}
    MoviesAdapter(Context context,ArrayList<Movie> movieArrayList){
        this.mContext=context;
        this.movieArrayList=movieArrayList;
        layoutInflater = LayoutInflater.from(context);
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
        //TODO clean code
        //TextView title=(TextView)view.findViewById(R.id.tv_movie_title);
        ImageView thumb=(ImageView)view.findViewById(R.id.iv_movie_thumb);
        //title.setText(movieArrayList.get(position).getOriginalTitle());
        Picasso.with(mContext).load(movieArrayList.get(position).getImageUrl()).into(thumb);
        //thumb.setImageResource(R.drawable.temp);
        return view;
    }

}
