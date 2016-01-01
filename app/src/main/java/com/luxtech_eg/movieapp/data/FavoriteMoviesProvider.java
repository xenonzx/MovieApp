package com.luxtech_eg.movieapp.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by ahmed on 01/01/16.
 */
public class FavoriteMoviesProvider extends ContentProvider {
    // 1) initialize query matcher constants
    static final int FAVORITE_MOVIES = 100;
    static final int FAVORITE_MOVIE_WITH_ID= 101;
    private static final String sMovieIdSelection =
            MoviesContract.FavoriteMovieEntry.TABLE_NAME+
                    "." +MoviesContract.FavoriteMovieEntry._ID+ " = ? ";


    MoviesDbHelper mOpenHelper;

    //3) construct matcher from buildUriMatcher()
    private static final UriMatcher sUriMatcher = buildUriMatcher();


    @Override
    public boolean onCreate() {
        //get instance of dbhelper and return true
        mOpenHelper = new MoviesDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)){
            case FAVORITE_MOVIES:
            {
                retCursor = mOpenHelper.getReadableDatabase().query(MoviesContract.FavoriteMovieEntry.TABLE_NAME
                        ,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            }
            case FAVORITE_MOVIE_WITH_ID:
            {
                long id=MoviesContract.FavoriteMovieEntry.getMovieIdFromUri(uri);
                retCursor= mOpenHelper.getReadableDatabase().query(MoviesContract.FavoriteMovieEntry.TABLE_NAME,
                        projection,
                        sMovieIdSelection,
                        new String[]{ Long.toString(id)},
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case FAVORITE_MOVIES:
                return MoviesContract.FavoriteMovieEntry.CONTENT_TYPE;
            case FAVORITE_MOVIE_WITH_ID:
                return MoviesContract.FavoriteMovieEntry.CONTENT_ITEM_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case FAVORITE_MOVIES:{
                long _id = db.insert(MoviesContract.FavoriteMovieEntry.TABLE_NAME, null, contentValues);
                if ( _id > 0 )
                    returnUri = MoviesContract.FavoriteMovieEntry.buildFavoriteMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case FAVORITE_MOVIE_WITH_ID:{
                long _id = db.insert(MoviesContract.FavoriteMovieEntry.TABLE_NAME, null, contentValues);
                if ( _id > 0 )
                    returnUri = MoviesContract.FavoriteMovieEntry.buildFavoriteMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        //db.close();
        return returnUri;
    }



    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        switch (match) {
            case FAVORITE_MOVIES:
                rowsDeleted = db.delete(
                        MoviesContract.FavoriteMovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case FAVORITE_MOVIE_WITH_ID:
                long id=MoviesContract.FavoriteMovieEntry.getMovieIdFromUri(uri);
                rowsDeleted = db.delete(
                        MoviesContract.FavoriteMovieEntry.TABLE_NAME, sMovieIdSelection, new String[]{ Long.toString(id)});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;
        switch (match) {
            case FAVORITE_MOVIES:
                rowsUpdated = db.update(MoviesContract.FavoriteMovieEntry.TABLE_NAME, contentValues, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }


    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        final String authority = MoviesContract.CONTENT_AUTHORITY;
        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, MoviesContract.PATH_MOVIE, FAVORITE_MOVIES);
        matcher.addURI(authority, MoviesContract.PATH_MOVIE + "/*", FAVORITE_MOVIE_WITH_ID);
        /* in other cases
            * is a number
            # is a string
        matcher.addURI(authority, WeatherContract.PATH_WEATHER + "/*", WEATHER_WITH_LOCATION);
        matcher.addURI(authority, WeatherContract.PATH_WEATHER + "/#", WEATHER_WITH_LOCATION_AND_DATE);
        matcher.addURI(authority, WeatherContract.PATH_LOCATION, LOCATION);
        */
        return matcher;
    }
}
