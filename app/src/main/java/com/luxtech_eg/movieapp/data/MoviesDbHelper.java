package com.luxtech_eg.movieapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.luxtech_eg.movieapp.data.MoviesContract.FavoriteMovieEntry;

/**
 * Created by ahmed on 31/12/15.
 */
public class                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    MoviesDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION =2;
    public static final String DATABASE_NAME = "Movies.db";


    MoviesDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // onCreate()
    // 1) defines sql tables creation statement
    // 2) execute tables creation statement
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " + FavoriteMovieEntry.TABLE_NAME + " (" +
                FavoriteMovieEntry._ID + " INTEGER PRIMARY KEY," +
                FavoriteMovieEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, " +
                FavoriteMovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                FavoriteMovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                FavoriteMovieEntry.COLUMN_RATING + " TEXT NOT NULL, " +
                FavoriteMovieEntry.COLUMN_THUMB_RELATIVE_LINK + " TEXT NOT NULL," +
                FavoriteMovieEntry.COLUMN_THUMB_BASE_64 + " TEXT NOT NULL " +
                " );";
        //TODO add  field for saving images for  offline use
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIES_TABLE);
                            
    }
    // onUpgrade()
    // 1)DROP TABLES
    // 2) execute onCreate()
    // note: should re-insert movies again
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavoriteMovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

}
