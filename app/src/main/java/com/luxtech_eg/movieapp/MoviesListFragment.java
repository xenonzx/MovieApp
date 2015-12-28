package com.luxtech_eg.movieapp;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.luxtech_eg.movieapp.data.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by ahmed on 05/12/15.
 */
public class MoviesListFragment extends Fragment {

    GridView moviesLV;
    static ArrayList<Movie> moviesAL = new ArrayList<Movie>();
    static MoviesAdapter moviesAdapter;

    final static String APIKEY= "52665819dfedc14d67605e2bb09ed729";
    final static String TAG=MoviesListFragment.class.getSimpleName();

    public MoviesListFragment(){

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.movies_list_fragment_layout,container,false);
        moviesLV= (GridView) rootView.findViewById(R.id.listview_movies);
        //TODO remove commented line after implementing your adapter correctly
        //moviesAdapter= new ArrayAdapter<String>(getActivity(),R.layout.list_item_movie,R.id.tv_movie_title,moviesAL);
        moviesAdapter= new MoviesAdapter(getActivity(),moviesAL);
        moviesLV.setAdapter(moviesAdapter);
        //TODO remove the following lines later start
        getPopularMovies();
        //// TODO: end
        moviesLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent i = new Intent(getActivity(), DetailActivity.class);
                i.putExtra(DetailFragment.MOVIE_OBJECT_KEY,moviesAL.get(position));
                startActivity(i);
            }
        });
        return rootView;
    }
    void populateDummyData(){
        /*moviesAL.add("hha1");
        moviesAL.add("hha2");
        moviesAL.add("hha3");
        moviesAL.add("hha4");
        */

    }
    void getPopularMovies() {
        String popMoviesUrl=buildPopularMoviesUri();
        Log.v(TAG,popMoviesUrl);
        FetchMoviesTask fmt= new FetchMoviesTask();
        fmt.execute(popMoviesUrl);

    }
    void getTopRatedMovies(){

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
            moviesAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

}
