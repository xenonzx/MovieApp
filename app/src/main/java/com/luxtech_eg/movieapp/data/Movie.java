package com.luxtech_eg.movieapp.data;

/**
 * Created by ahmed on 25/12/15.
 */
public class Movie {
    int id;
    String originalTitle;

    String overview;
    String releaseDate;
    //vote_average in the api
    String rating;
    //relative thumbnil link
    String thumbnilLink;
    Movie(int id){
        this .id=id;
    }

    public Movie(int id, String originalTitle,  String overview, String releaseDate, String rating, String thumbnilLink) {
        this.id = id;
        this.originalTitle = originalTitle;

        this.overview = overview;
        this.releaseDate = releaseDate;
        this.rating = rating;
        this.thumbnilLink = thumbnilLink;
    }


    public String getThumbnilLink() {
        return thumbnilLink;
    }

    public void setThumbnilLink(String thumbnilLink) {
        this.thumbnilLink = thumbnilLink;
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
        return originalTitle+" "+thumbnilLink;
    }
}
