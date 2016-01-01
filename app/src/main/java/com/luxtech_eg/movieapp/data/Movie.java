package com.luxtech_eg.movieapp.data;

import android.content.ContentValues;

import java.io.Serializable;

/**
 * Created by ahmed on 25/12/15.
 */
public class Movie implements Serializable {
    int id;
    String originalTitle;

    String overview;
    String releaseDate;
    //vote_average in the api
    String rating;
    //relative thumbnil link
    String thumbnailRelativeLink;
    final static String THUMBNAIL_BASE= "http://image.tmdb.org/t/p/";
    final static String IMAGE_SIZE="w185";
    Movie(int id){
        this .id=id;
    }

    public Movie(int id, String originalTitle,  String overview, String releaseDate, String rating, String thumbnilLink) {
        this.id = id;
        this.originalTitle = originalTitle;

        this.overview = overview;
        this.releaseDate = releaseDate;
        this.rating = rating;
        this.thumbnailRelativeLink = thumbnilLink;
    }


    public String getThumbnailRelativeLink() {
        return thumbnailRelativeLink;
    }

    public void setThumbnailRelativeLink(String thumbnailRelativeLink) {
        this.thumbnailRelativeLink = thumbnailRelativeLink;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }



    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }



    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    @Override
    public String toString() {
        return originalTitle+" "+ thumbnailRelativeLink;
    }

    public String getImageUrl(){
        return THUMBNAIL_BASE+IMAGE_SIZE+getThumbnailRelativeLink();
    }

    public ContentValues getInsertContentValues(){
        ContentValues retMovieObject = new ContentValues();
        retMovieObject.put(MoviesContract.FavoriteMovieEntry._ID,getId());
        retMovieObject.put(MoviesContract.FavoriteMovieEntry.COLUMN_ORIGINAL_TITLE,getOriginalTitle());
        retMovieObject.put(MoviesContract.FavoriteMovieEntry.COLUMN_OVERVIEW,getOverview());
        retMovieObject.put(MoviesContract.FavoriteMovieEntry.COLUMN_RELEASE_DATE,getReleaseDate());
        retMovieObject.put(MoviesContract.FavoriteMovieEntry.COLUMN_RATING,getRating());
        retMovieObject.put(MoviesContract.FavoriteMovieEntry.COLUMN_THUMB_RELATIVE_LINK,getThumbnailRelativeLink());

       return retMovieObject;
    }
}
