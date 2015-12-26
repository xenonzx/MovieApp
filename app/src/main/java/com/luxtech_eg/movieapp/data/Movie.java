package com.luxtech_eg.movieapp.data;

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
}
