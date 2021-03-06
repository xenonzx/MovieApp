package com.luxtech_eg.movieapp;

import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.luxtech_eg.movieapp.UI.ExpandableHeightListView;
import com.luxtech_eg.movieapp.data.Movie;
import com.luxtech_eg.movieapp.data.MoviesContract;
import com.luxtech_eg.movieapp.data.Review;
import com.luxtech_eg.movieapp.data.Video;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Created by ahmed on 26/12/15.
 */
public class DetailFragment extends Fragment {
    final static String TAG=DetailFragment.class.getSimpleName();
    final static String APIKEY= ApiKeyHolder.getAPIKEY();
    private static ShareActionProvider mShareActionProvider;
    static Video shareTrailer;

    public final static String MOVIE_OBJECT_KEY="movie_key";
    ImageView movieThumb;
    ImageButton favButton;
    TextView title;
    TextView overview;
    TextView releaseDate;
    TextView rating;
    ExpandableHeightListView videos;
    ExpandableHeightListView reviews;
    static ArrayList<Video> videosAL;
    static ArrayList<Review> reviewAL;
    String ShareTrailer;
    static VideosAdapter videosAdapter;
    static ReviewAdapter reviewAdapter;
    ArrayAdapter reviewsAdapter;

    ConnectionDetector connectionDetector;
    static Movie m;
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        videosAL=new ArrayList<Video>();
        reviewAL=new ArrayList<Review>();
        connectionDetector= new ConnectionDetector(getActivity());
        if(getArguments().containsKey(MOVIE_OBJECT_KEY)){
            m=(Movie)getArguments().get(MOVIE_OBJECT_KEY);
        }
        setHasOptionsMenu(true);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView=null;
        if(m!=null) {
            rootView=inflater.inflate(R.layout.detail_fragment_layout,container,false);
            title= (TextView)rootView.findViewById(R.id.tv_detail_movie_title);
            overview=(TextView)rootView.findViewById(R.id.tv_detail_movie_overview);
            releaseDate= (TextView)rootView.findViewById(R.id.tv_detail_movie_release_date);
            rating=(TextView)rootView.findViewById(R.id.tv_detail_movie_rating);
            movieThumb=(ImageView)rootView.findViewById(R.id.iv_detail_movie_thumb);
            favButton=(ImageButton)rootView.findViewById(R.id.b_detail_movie_favorite);
            videos=(ExpandableHeightListView)rootView.findViewById(R.id.lv_videos);
            reviews=(ExpandableHeightListView)rootView.findViewById(R.id.lv_reviews);


            title.setText(m.getOriginalTitle());
            overview.setText(m.getOverview());
            rating.setText(m.getRating());
            releaseDate.setText(m.getReleaseDate());
            //// TODO: add if temp image ,get poster from api
            if (m.hasBase64Image()){
                movieThumb.setImageBitmap(m.getMoviePoster());
            }else {
                //and since picasso use cashing if the network/internet is cut an image will still be displayed
                Picasso.with(getActivity()).load(m.getImageUrl()).into(movieThumb);
            }
            //Todo get movie from base64 if movie is favorite only
            //
            applyFavIconState();
            favButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.v(TAG, "onClick");
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
            videosAdapter = new VideosAdapter(getActivity(), videosAL);
            reviewAdapter = new ReviewAdapter(getActivity(), reviewAL);

            videos.setAdapter(videosAdapter);
            reviews.setAdapter(reviewAdapter);
            videos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    Log.v(TAG, "onItemClick");
                    Video video = videosAL.get(position);
                    Log.v(TAG, "site is " + video.getSite());
                    if (video.getSite().equals("YouTube")) {
                        playYouTubeVideo(video);
                    }
                }
            });

        }
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(m!=null) {
            //TODO solve share action is sharing name only
            //TODO solv
            getVideoAndReviews();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d(TAG, "onCreateOptionsMenu");
        inflater.inflate(R.menu.detailfragment, menu);
        MenuItem item = menu.findItem(R.id.menu_item_share);

        // for compatibility  MenuItemCompat
        mShareActionProvider =(ShareActionProvider) MenuItemCompat.getActionProvider(item);
        if (shareTrailer!=null&&mShareActionProvider!=null){
            // if we already have a forecast
            mShareActionProvider.setShareIntent(createShareIntent());
        }
        else{
            Log.d(TAG, "share action provider is null");
        }

    }
    void playYouTubeVideo(Video video){

            try{
                Intent intent = new Intent(Intent.ACTION_VIEW, video.getYouTubeVndUri());
                //intent.putExtra("VIDEO_ID", video.getKey());
                startActivity(intent);
            }catch (ActivityNotFoundException ex){
                Log.v(TAG, "activity not found exeption");
                Log.v(TAG, "video uri is " + video.getYouTubeHttpUri());
                Intent intent=new Intent(Intent.ACTION_VIEW,video.getYouTubeHttpUri());
                startActivity(intent);
            }

    }
    void getVideoAndReviews(){
        if(connectionDetector.isConnectingToInternet()){
            getVideos();
            getReviews();
        }
        else{
            Toast.makeText(getActivity(),R.string.toast_message_no_internet_no_videos_no_reviews,Toast.LENGTH_LONG).show();
        }
    }

    void getVideos(){
        Log.v(TAG,"getVideos");
        String url = buildVideosUrl(m);
        new FetchMovieVideos().execute(url);

    }
    void getReviews(){
        Log.v(TAG,"getReviews");
        String url = buildReviewsUrl(m);
        new FetchMovieReviews().execute(url);

    }
    String buildVideosUrl(Movie movie){
        //http://api.themoviedb.org/3/movie/102899/videos?api_key=[]
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http").authority("api.themoviedb.org")
                .appendPath("3")
                .appendPath("movie")
                .appendPath("" + movie.getId())
                .appendPath("videos")
                .appendQueryParameter("api_key", APIKEY);

        return builder.build().toString();
    }
    String buildReviewsUrl(Movie movie){
        //http://api.themoviedb.org/3/movie/102899/reviews?api_key=[]
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http").authority("api.themoviedb.org")
                .appendPath("3")
                .appendPath("movie")
                .appendPath("" + movie.getId())
                .appendPath("reviews")
                .appendQueryParameter("api_key", APIKEY);

        return builder.build().toString();
    }
    private void unFavorite() {
        Log.v(TAG, "unFavorite");

        Uri movieUri = MoviesContract.FavoriteMovieEntry.buildFavoriteMovieUri(m.getId());
        int rowDeleted=getActivity().getContentResolver().delete(movieUri, null, null);
        Log.v(TAG, "rowDeleted "+rowDeleted);
        favButton.setImageResource(R.drawable.fav_false);
    }
    private void favorite() {
        Log.v(TAG, "favorite");

        //once a movie is favorited its image bitmap is converted to base64 formate and base 64 string is added to object to be inserted into db
        // i used setImageBase64 here so content values returned from getInsertContentValues conains image value

        saveMovieThumbToObject();
        Uri inserted = getActivity().getContentResolver().insert(MoviesContract.FavoriteMovieEntry.CONTENT_URI, m.getInsertContentValues());
        Log.v(TAG,inserted.toString());
        favButton.setImageResource(R.drawable.fav_true);


    }
    void saveMovieThumbToObject(){
        String retBase64;
        Picasso.with(getActivity()).load(m.getImageUrl()).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                ByteArrayOutputStream bao = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bao);
                byte[] ba = bao.toByteArray();
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
    }

    Intent createShareIntent(){
        Log.d(TAG, "createShareIntent");
        Intent mShareIntent = new Intent();
        mShareIntent.setAction(Intent.ACTION_SEND);
        mShareIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        mShareIntent.setType("text/plain");
        if(shareTrailer!=null){
            //just in case the array is empty :D
            Log.d(TAG, ""+shareTrailer.getYouTubeHttpUri());
            mShareIntent.putExtra(Intent.EXTRA_TEXT,shareTrailer.getYouTubeHttpString());
        }
        else {
            Log.d(TAG, ""+ m.getOriginalTitle());
            mShareIntent.putExtra(Intent.EXTRA_TEXT, m.getOriginalTitle());
        }

        //mShareIntent.putExtra(Intent.EXTRA_TEXT, getActivity().getIntent().getStringExtra("forecastString") + "#SUNSHINE");
        return  mShareIntent;
    }

    boolean isFavoriteMovie() {

        Uri movieUri = MoviesContract.FavoriteMovieEntry.buildFavoriteMovieUri(m.getId());

        Cursor c = getActivity().getContentResolver().query(movieUri, null, null, null, null);

        if (c.getCount() == 0) {
            Log.v(TAG, "This movie is NOT Favorite");
            return false;
        }
        else {
            if (c.getCount() == 1) {
                Log.v(TAG, "This movie is Favorite");
                return true;
            }
            else if (c.getCount() > 1) {
                Log.v(TAG, "Duplication in data");
                return true;
            }
            else if (c.getCount() < 1) {
                Log.v(TAG, "isFavoriteMovie count is returning negative");
                return true;
            }
            return true;
        }

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
    }
    void applyFavIconState(){
        // TODO change star icon to hart icon
        if(isFavoriteMovie()) {
            favButton.setImageResource(R.drawable.fav_true);
        }
        else {
            favButton.setImageResource(R.drawable.fav_false);
        }
    }


    public void updateShare(Video video) {


        Intent mShareIntent = new Intent();
        mShareIntent.setAction(Intent.ACTION_SEND);
        mShareIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        mShareIntent.setType("text/plain");
        if(videosAL.size()>1){
            //just in case the array is empty :D
            Log.d(TAG, ""+videosAL.get(0).getYouTubeHttpUri());
            mShareIntent.putExtra(Intent.EXTRA_TEXT,video.getYouTubeHttpUri());
        }
        else {
            Log.d(TAG, ""+ m.getOriginalTitle());
            mShareIntent.putExtra(Intent.EXTRA_TEXT, m.getOriginalTitle());
        }

    }

    class FetchMovieVideos extends FetchTaskGET{
        String LogTag=FetchMovieVideos.class.getSimpleName();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            super.setLogTAG(LogTag);
        }

        @Override
        protected void onPostExecute(String s) {
            Log.v(LogTag,"onPostExecute");
            super.onPostExecute(s);
            Log.v(LogTag, s);

            try {
                videosAL.clear();
                videosAL.addAll(getVideosFromJson(s));
                videosAdapter.notifyDataSetChanged();
                if(videosAL.size()>0) {
                    shareTrailer = videosAL.get(0);
                }
                if (shareTrailer!=null&&mShareActionProvider!=null){
                    // if we already have a forecast
                    mShareActionProvider.setShareIntent(createShareIntent());
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        private ArrayList<Video> getVideosFromJson(String videosJsonString) throws JSONException {
            ArrayList<Video>returnedMoviesAl= new ArrayList<Video>();
            Log.v(LogTag,"getMoviesFromJson");

            final String VIDEOS_RESULTS = "results";
            final String VIDEOS_VIDEO_KEY = "key";
            final String VIDEOS_VIDEO_NAME = "name";
            final String VIDEOS_VIDEO_TYPE = "type";
            final String VIDEOS_VIDEO_ID = "id";
            final String VIDEOS_VIDEO_SITE = "site";




            JSONObject videosJson = new JSONObject(videosJsonString);
            JSONArray videosArray = videosJson.getJSONArray(VIDEOS_RESULTS);
            //iterating over result movies
            for(int i = 0; i < videosArray.length(); i++) {
                JSONObject videoJsonObject= videosArray.getJSONObject(i);
                Log.v(LogTag,"movie object "+i+" "+videoJsonObject.toString());
                // making an Movie object with the wanted attributes
                Video m=new Video(videoJsonObject.getString(VIDEOS_VIDEO_ID),
                        videoJsonObject.getString(VIDEOS_VIDEO_SITE),
                        videoJsonObject.getString(VIDEOS_VIDEO_TYPE),
                        videoJsonObject.getString(VIDEOS_VIDEO_NAME),
                        videoJsonObject.getString(VIDEOS_VIDEO_KEY)
                       );
                returnedMoviesAl.add(m);
            }
            return returnedMoviesAl;

        }
    }

    static class FetchMovieReviews extends FetchTaskGET{
        String LogTag=FetchMovieReviews.class.getSimpleName();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            super.setLogTAG(LogTag);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.v(LogTag, "onPostExecute");
            super.onPostExecute(s);
            Log.v(LogTag,s);
            try {
                reviewAL.clear();
                reviewAL.addAll(getReviewsFromJson(s));
                reviewAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        private ArrayList<Review> getReviewsFromJson(String reviewsJsonString) throws JSONException {
            Log.v(TAG, "getReviewsFromJson");
            ArrayList<Review>returnedReviewsAl= new ArrayList<Review>();


            final String REVIEWS_RESULTS = "results";
            final String REVIEWS_REVIEW_ID = "id";
            final String REVIEWS_REVIEW_AUTHOR = "author";
            final String REVIEWS_REVIEW_CONTENT = "content";
            final String REVIEWS_REVIEW_URL = "url";





            JSONObject reviewsJson = new JSONObject(reviewsJsonString);
            JSONArray reviewsArray = reviewsJson.getJSONArray(REVIEWS_RESULTS);
            //iterating over result movies
            for(int i = 0; i < reviewsArray.length(); i++) {
                JSONObject reviewJsonObject= reviewsArray.getJSONObject(i);
                Log.v(TAG, "review object " + i + " " + reviewJsonObject.toString());
                // making an Movie object with the wanted attributes
                Review r=new Review( reviewJsonObject.getString(REVIEWS_REVIEW_ID),
                        reviewJsonObject.getString(REVIEWS_REVIEW_AUTHOR),
                        reviewJsonObject.getString(REVIEWS_REVIEW_URL),
                        reviewJsonObject.getString(REVIEWS_REVIEW_CONTENT)

                );
                returnedReviewsAl.add(r);
            }
            return returnedReviewsAl;

        }

    }

}
