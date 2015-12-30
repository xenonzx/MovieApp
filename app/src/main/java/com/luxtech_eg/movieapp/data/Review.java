package com.luxtech_eg.movieapp.data;

/**
 * Created by ahmed on 29/12/15.
 */
public class Review {
    String id;
    String author;
    String url;
    String content;

    public Review(String id,String author,String url,String content){

    }
    public String getAuthor() {
        return author;
    }

    public String getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getContent() {
        return content;
    }



}
