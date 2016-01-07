package com.luxtech_eg.movieapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.luxtech_eg.movieapp.data.Video;

import java.util.ArrayList;

/**
 * Created by ahmed on 25/12/15.
 */
public class VideosAdapter extends BaseAdapter {
    Context mContext;
    ArrayList<Video>videoArrayList;
    LayoutInflater layoutInflater;

    VideosAdapter(){}
    VideosAdapter(Context context, ArrayList<Video> videoArrayList){
        this.mContext=context;
        this.videoArrayList=videoArrayList;
        layoutInflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return videoArrayList.size();
    }

    @Override
    public Video getItem(int position) {
        return videoArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        view= layoutInflater.inflate(R.layout.list_item_video,viewGroup,false);
        TextView videoName=(TextView)view.findViewById(R.id.tv_video_title);
        videoName.setText(videoArrayList.get(position).getName());
        return view;
    }

}
