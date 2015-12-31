package com.luxtech_eg.movieapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.luxtech_eg.movieapp.data.Review;

import java.util.ArrayList;

/**
 * Created by ahmed on 30/12/15.
 */
public class ReviewAdapter extends BaseAdapter{

    Context mContext;
    ArrayList <Review> reviewsAL;
    LayoutInflater layoutInflater;
    int itemLayout=R.layout.list_item_review;


    ReviewAdapter(Context context, ArrayList<Review> objects){
        this.mContext=context;
        this.reviewsAL =objects;
        layoutInflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return reviewsAL.size();
    }

    @Override
    public Object getItem(int position) {
        return reviewsAL.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        view= layoutInflater.inflate(itemLayout,viewGroup,false);

        TextView content=(TextView) view.findViewById(R.id.tv_review_content);
        TextView author=(TextView) view.findViewById(R.id.tv_review_author);

        content.setText(reviewsAL.get(position).getContent());
        author.setText(reviewsAL.get(position).getAuthor());
        Log.v("ReviewAdapter", "getview" + reviewsAL.get(position).getAuthor() + reviewsAL.get(position).getContent());
        return view;

    }
}
