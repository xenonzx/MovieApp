package com.luxtech_eg.movieapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by ahmed on 31/12/15.
 */
public class MoviesContract {
    public static final String CONTENT_AUTHORITY = "com.luxtech_eg.movieapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MOVIE = "movie";

    public static class MovieEntry implements BaseColumns{


        // CONTENT_URI ="content://com.luxtech_eg.movieapp/movie"
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();
        //ContentResolver.CURSOR_DIR_BASE_TYPE  Android platform's base MIME type for a content: URI containing a Cursor of zero or more items.
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        //ContentResolver.CURSOR_ITEM_BASE_TYPE Android platform's base MIME type for a content: URI containing a Cursor of a single item.
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static final String TABLE_NAME = "movie";

        public static final String COLUMN_ORIGINAL_TITLE= "original_title";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "releaseDate";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_CITY_THUMN_RELATIVE_LINK ="thumbnailRelativeLink";


    }
}