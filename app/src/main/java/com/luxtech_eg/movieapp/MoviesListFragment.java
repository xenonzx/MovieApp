package com.luxtech_eg.movieapp;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.luxtech_eg.movieapp.data.Movie;
import com.luxtech_eg.movieapp.data.MoviesContract;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

import static android.database.DatabaseUtils.dumpCursor;

/**
 * Created by ahmed on 05/12/15.
 */
public class MoviesListFragment extends Fragment {
    public final static boolean FAVORITE_MOVIES=true;
    public final static boolean ONLINE_MOVIES=false;
    String SHOW_FAV_MOVIES_KEY="show_fav_movies";
    GridView moviesLV;
    Menu menu;

    static ArrayList<Movie> moviesAL = new ArrayList<Movie>();
    static MoviesAdapter moviesAdapter;

    final static String APIKEY= ApiKeyHolder.getAPIKEY();
    final static String TAG=MoviesListFragment.class.getSimpleName();
    SharedPreferences sp;
    boolean showFavMovies;// this variable (showFavMovies)is to switch between retrieving favorite movies or using async task to getpopular or top rated movies
    // todo add showFavMovies in on save and on restore and set it using meanu
    ConnectionDetector connectionDetector;

    public MoviesListFragment(){

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v(TAG,"onCreate");
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        sp= PreferenceManager.getDefaultSharedPreferences(getActivity());
        showFavMovies=ONLINE_MOVIES;
        connectionDetector=new ConnectionDetector(getActivity());
    }

    @Override
    public void onStart() {
        Log.v(TAG, "onStart");
        super.onStart();
        getMovies();

    }
    @Override
    public void onResume() {
        Log.v(TAG, "onResume");
        super.onResume();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            showFavMovies = savedInstanceState.getBoolean(SHOW_FAV_MOVIES_KEY);
            Log.d(TAG,"savedInstanceState of showFavMovies " +showFavMovies);
        }
        Log.v(TAG,"onCreateView");
        View rootView = inflater.inflate(R.layout.movies_list_fragment_layout,container,false);
        moviesLV= (GridView) rootView.findViewById(R.id.listview_movies);
        //TODO remove commented line after implementing your adapter correctly
        //moviesAdapter= new ArrayAdapter<String>(getActivity(),R.layout.list_item_movie,R.id.tv_movie_title,moviesAL);
        moviesAdapter= new MoviesAdapter(getActivity(),moviesAL);
        moviesLV.setAdapter(moviesAdapter);
        //TODO remove the following lines later start

        //// TODO: end
        moviesLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent i = new Intent(getActivity(), DetailActivity.class);
                //compromise : badal ma ageeb el base64 images for every item in arraylist i setit once on item clicked
                // compromise 7elwa compromise deh
                final Movie m= moviesAL.get(position);
                Picasso.with(getActivity()).load(m.getImageUrl()).into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        ByteArrayOutputStream bao = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bao);
                        byte [] ba = bao.toByteArray();
                        m.setImageBase64(Base64.encodeToString(ba, Base64.DEFAULT));
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        // todo add temp Bimap
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
                i.putExtra(DetailFragment.MOVIE_OBJECT_KEY, m);
                startActivity(i);
            }
        });
        return rootView;
    }

    void getMovies(){
        String key=getString(R.string.movies_sorting_key);
        String defualtValue=getString(R.string.movies_sorting_default_value);
        String popularMovies=getString(R.string.movies_sorting_popular );
        String highestRatedMovies=getString(R.string.movies_sorting_highest_rated );
        if(showFavMovies){

            moviesAL.clear();
            moviesAL.addAll(getFavoriteMovies());
            moviesAdapter.setShowFavMovies(FAVORITE_MOVIES);
            moviesAdapter.notifyDataSetChanged();
        }
        else {
            if(connectionDetector.isConnectingToInternet()) {
                //if there is connection to internet get movies
                if (sp.getString(key, defualtValue).equals(popularMovies)) {
                    getPopularMovies();
                } else if (sp.getString(key, defualtValue).equals(highestRatedMovies)) {
                    getTopRatedMovies();
                } else {
                    Log.e(TAG, " niether popular nor top ");
                }
            }
            else {
                Toast.makeText(getActivity(),R.string.toast_message_no_internet,Toast.LENGTH_LONG).show();

            }
        }
    }
    void getPopularMovies() {
        Log.v(TAG,"getPopularMovies");
        String popMoviesUrl=buildPopularMoviesUri();
        Log.v(TAG,popMoviesUrl);
        FetchMoviesTask fmt= new FetchMoviesTask();
        fmt.execute(popMoviesUrl);

    }
    void getTopRatedMovies(){
        Log.v(TAG,"getTopRatedMovies");
        String top=buildTopRatedMoviesUri();
        Log.v(TAG,top);
        FetchMoviesTask fmt= new FetchMoviesTask();
        fmt.execute(top);
    }
    String buildPopularMoviesUri(){
        //http://api.themoviedb.org/3/discover/movie?api_key=&sort_by=popularity.desc
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http").authority("api.themoviedb.org")
                .appendPath("3")
                .appendPath("discover")
                .appendPath("movie")
                .appendQueryParameter("api_key", APIKEY )
                .appendQueryParameter("sort_by", "popularity.desc");

        return builder.build().toString();
    }

    String buildTopRatedMoviesUri(){
        //http://api.themoviedb.org/3/discover/movie?api_key=[]&sort_by=vote_average.desc
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http").authority("api.themoviedb.org")
                .appendPath("3")
                .appendPath("discover")
                .appendPath("movie")
                .appendQueryParameter("api_key", APIKEY )
                .appendQueryParameter("sort_by", "vote_average.desc");


        return builder.build().toString();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        this.menu=menu;
        if (showFavMovies==ONLINE_MOVIES) {
            inflater.inflate(R.menu.movie_list_menu, menu);

        }
        else{
            // this means showFavMovies==FAVORITE_MOVIES
            inflater.inflate(R.menu.favorites_list_menu, menu);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.settings){
           startActivity(new Intent(getActivity(), SettingsActivity.class));
           return true;
        }
        else if(item.getItemId()==R.id.menu_favorites){
            showFavMovies=FAVORITE_MOVIES;
            getMovies();
            updateMenu();
            return true;
        }
        else if(item.getItemId()==R.id.menu_home){
            showFavMovies=ONLINE_MOVIES;
            getMovies();
            updateMenu();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void updateMenu(){
        //update menu to be able to choose other case
        this.menu.clear();
        onCreateOptionsMenu(this.menu, getActivity().getMenuInflater());
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putBoolean(SHOW_FAV_MOVIES_KEY,showFavMovies);
        super.onSaveInstanceState(outState);
    }


    Cursor getFavoriteMoviesCursor(){
        //Todo move to back a ground thread
        Log.v(TAG,"getFavoriteMoviesCursor");
        return getActivity().getContentResolver().query(MoviesContract.FavoriteMovieEntry.CONTENT_URI, null, null, null, null);
    }
    ArrayList<Movie>getFavoriteMovies(){
        ArrayList<Movie> retMovies= new ArrayList<Movie>();
        //parse the cursor row into Movie object
        Cursor movieCursor = getFavoriteMoviesCursor();
        // Todo  remove dumpcursor
        dumpCursor(movieCursor);
        // loop over cursor
        if (movieCursor.moveToFirst()){
            //note to self
            do{
                int id=movieCursor.getInt(movieCursor.getColumnIndex(MoviesContract.FavoriteMovieEntry._ID));
                String title = movieCursor.getString(movieCursor.getColumnIndex(MoviesContract.FavoriteMovieEntry.COLUMN_ORIGINAL_TITLE));
                String overview = movieCursor.getString(movieCursor.getColumnIndex(MoviesContract.FavoriteMovieEntry.COLUMN_OVERVIEW));
                String rating = movieCursor.getString(movieCursor.getColumnIndex(MoviesContract.FavoriteMovieEntry.COLUMN_RATING));
                String releaseDate = movieCursor.getString(movieCursor.getColumnIndex(MoviesContract.FavoriteMovieEntry.COLUMN_RELEASE_DATE));
                String thumRelativeLink= movieCursor.getString(movieCursor.getColumnIndex(MoviesContract.FavoriteMovieEntry.COLUMN_THUMB_RELATIVE_LINK));
                String base64image= movieCursor.getString(movieCursor.getColumnIndex(MoviesContract.FavoriteMovieEntry.COLUMN_THUMB_BASE_64));
                Movie m= new Movie(id,title,overview,releaseDate,rating,thumRelativeLink,base64image);
                retMovies.add(m);
            }while(movieCursor.moveToNext());
        }
        movieCursor.close();

        return retMovies;
    }
    private static class FetchMoviesTask extends AsyncTask<String, Void, String> {
        final static String TAG= FetchMoviesTask.class.getSimpleName();
        //ArrayList<Movie> MoviesAL= new ArrayList<Movie>();
        @Override

        protected String doInBackground(String... url) {
            if (url.length == 0) {
                // protection
                return null;
            }
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String moviesJson;
            try{
                URL apiUrl=new URL(url[0]);
                Log.v(TAG," url passedto AsyncTask"+url[0]);
                urlConnection = (HttpURLConnection) apiUrl.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    Log.v(TAG,"no input stream");
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                moviesJson=buffer.toString();
                return moviesJson;


            }catch (MalformedURLException e) {
                Log.e(TAG,"MalformedURLException");
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(TAG, "Error closing stream", e);
                    }
                }
            }
            return null;
        }

        private ArrayList<Movie> getMoviesFromJson(String moviesJsonString) throws JSONException {
            ArrayList<Movie>returnedMoviesAl= new ArrayList<Movie>();
            Log.v(TAG,"getMoviesFromJson");

            final String MOVIE_RESULT_LIST = "results";
            final String MOVIE_THUMBNIL_RELATIVE_PATH = "poster_path";
            final String MOVIE_TITLE = "title";
            final String MOVIE_OVERVIEW = "overview";
            final String MOVIE_ID = "id";
            final String MOVIE_RATING="vote_average";
            final String MOVIE_RELEASE_DATE="release_date";



            JSONObject moviesJson = new JSONObject(moviesJsonString);
            JSONArray resultsArray = moviesJson.getJSONArray(MOVIE_RESULT_LIST);
            //iterating over result movies
            for(int i = 0; i < resultsArray.length(); i++) {
                JSONObject movieJsonObj= resultsArray.getJSONObject(i);
                Log.v(TAG,"movie object "+i+" "+movieJsonObj.toString());
                // making an Movie object with the wanted attributes
                Movie m=new Movie(movieJsonObj.getInt(MOVIE_ID),
                        movieJsonObj.getString(MOVIE_TITLE),
                        movieJsonObj.getString(MOVIE_OVERVIEW),
                        movieJsonObj.getString(MOVIE_RELEASE_DATE),
                        movieJsonObj.getString(MOVIE_RATING),
                        movieJsonObj.getString(MOVIE_THUMBNIL_RELATIVE_PATH));
                returnedMoviesAl.add(m);
            }
            return returnedMoviesAl;

        }
        private ArrayList<String> getMoviesFromJsonAsString(String moviesJsonString) throws JSONException {
            ArrayList<String>returnedMoviesAl= new ArrayList<String>();
            Log.v(TAG,"getMoviesFromJson");

            final String MOVIE_RESULT_LIST = "results";
            final String MOVIE_THUMBNIL_RELATIVE_PATH = "poster_path";
            final String MOVIE_TITLE = "title";
            final String MOVIE_OVERVIEW = "overview";
            final String MOVIE_ID = "id";
            final String MOVIE_RATING="vote_average";
            final String MOVIE_RELEASE_DATE="release_date";



            JSONObject moviesJson = new JSONObject(moviesJsonString);
            JSONArray resultsArray = moviesJson.getJSONArray(MOVIE_RESULT_LIST);
            //iterating over result movies
            for(int i = 0; i < resultsArray.length(); i++) {
                JSONObject movieJsonObj= resultsArray.getJSONObject(i);
                Log.v(TAG,"movie object "+i+" "+movieJsonObj.toString());
                // making an Movie object with the wanted attributes
                Movie m=new Movie(movieJsonObj.getInt(MOVIE_ID),
                        movieJsonObj.getString(MOVIE_TITLE),
                        movieJsonObj.getString(MOVIE_OVERVIEW),
                        movieJsonObj.getString(MOVIE_RELEASE_DATE),
                        movieJsonObj.getString(MOVIE_RATING),
                        movieJsonObj.getString(MOVIE_THUMBNIL_RELATIVE_PATH));
                returnedMoviesAl.add(m.toString());
            }
            return returnedMoviesAl;

        }

        @Override
        protected void onPostExecute(String moviesjson) {
            super.onPostExecute(moviesjson);
            try {
                //TODO solve empty layout bug
                //moviesAL

                moviesAL.clear();
                moviesAL.addAll(getMoviesFromJson(moviesjson));
                moviesAdapter.setShowFavMovies(ONLINE_MOVIES);
                moviesAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
