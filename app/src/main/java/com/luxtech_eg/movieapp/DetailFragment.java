package com.luxtech_eg.movieapp;

import android.app.Fragment;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.luxtech_eg.movieapp.data.Movie;
import com.squareup.picasso.Picasso;

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
 * Created by ahmed on 26/12/15.
 */
public class DetailFragment extends Fragment {
    final static String TAG=DetailFragment.class.getSimpleName();
    final static String APIKEY= "52665819dfedc14d67605e2bb09ed729";

    public final static String MOVIE_OBJECT_KEY="movie_key";
    ImageView movieThumb;
    ImageButton favButton;
    TextView title;
    TextView overview;
    TextView releaseDate;
    TextView rating;
    ListView videos;
    ListView reviews;
    ArrayList<String> videosAL;
    ArrayList<String> reviewsAL;
    ArrayAdapter videosAdapter;
    ArrayAdapter reviewsAdapter;


    Movie m;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        //TODO restore instance state if its a a must
        super.onCreate(savedInstanceState);
        videosAL=new ArrayList<String>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.detail_fragment_layout,container,false);
        //// TODO: add protection if object is null
        m=(Movie)getActivity().getIntent().getExtras().get(MOVIE_OBJECT_KEY);

        //// TODO: ADD find view by id
        title= (TextView)rootView.findViewById(R.id.tv_detail_movie_title);
        overview=(TextView)rootView.findViewById(R.id.tv_detail_movie_overview);
        releaseDate= (TextView)rootView.findViewById(R.id.tv_detail_movie_release_date);
        rating=(TextView)rootView.findViewById(R.id.tv_detail_movie_rating);
        movieThumb=(ImageView)rootView.findViewById(R.id.iv_detail_movie_thumb);
        favButton=(ImageButton)rootView.findViewById(R.id.b_detail_movie_favorite);
        videos=(ListView)rootView.findViewById(R.id.lv_videos);
        reviews=(ListView)rootView.findViewById(R.id.lv_reviews);
        // TODO set images and texts
        title.setText(m.getOriginalTitle());
        overview.setText(m.getOverview());
        rating.setText(m.getRating());
        releaseDate.setText(m.getReleaseDate());
        Picasso.with(getActivity()).load(m.getImageUrl()).into(movieThumb);
        applyFavIconState();
        favButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //toggle state
                if (isFavoriteMovie()) {
                    //toggle state undo favorite
                    unFavorite();

                } else {
                    //toggle state ad to favorites
                    favorite();
                }
            }
        });
        videosAdapter= new ArrayAdapter<String>( getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, videosAL );
        // // TODO:  add transparency change to action bar

        getVideos();
        return rootView;
    }
    void getVideos(){
        String url = buildVideosUrl(m);
        new FetchMovieVideos().execute(url);

    }
    String buildVideosUrl(Movie movie){
        //http://api.themoviedb.org/3/movie/102899/videos?api_key=[]
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http").authority("api.themoviedb.org")
                .appendPath("3")
                .appendPath("movie")
                .appendPath("" + movie.getId())
                .appendPath("videos")
                .appendQueryParameter("api_key", APIKEY );

        return builder.build().toString();
    }
    private void unFavorite() {

        //TODO add the Body of UnFavorite function and change iconon success
        favButton.setImageResource(R.drawable.star_true);
    }
    private void favorite() {
        //TODO add the Body of favorite function
        favButton.setImageResource(R.drawable.star_false);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //Todo save instance state
        super.onSaveInstanceState(outState);
    }
    boolean isFavoriteMovie(){
        //todo add body to this function
        //if(m.getId()is in favorits)
        return true;
    }
    void applyFavIconState(){
        // TODO change star icon to hart icon
        if(isFavoriteMovie()) {
            favButton.setImageResource(R.drawable.star_true);
        }
        else {
            favButton.setImageResource(R.drawable.star_false);
        }
    }
    static class FetchMovieVideos extends AsyncTask<String,Void,String>{

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
                Log.v(TAG, " url passedto AsyncTask" + url[0]);
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

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.v(TAG,s);
            //// TODO: 26/12/15 add parsing to get youtube string
        }
    }

}
