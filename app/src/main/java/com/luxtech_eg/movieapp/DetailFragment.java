package com.luxtech_eg.movieapp;

import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

    public final static String MOVIE_OBJECT_KEY="movie_key";
    ImageView movieThumb;
    ImageButton favButton;
    TextView title;
    TextView overview;
    TextView releaseDate;
    TextView rating;
    ListView videos;
    ListView reviews;
    static ArrayList<Video> videosAL;
    static ArrayList<Review> reviewAL;

    static VideosAdapter videosAdapter;
    static ReviewAdapter reviewAdapter;
    ArrayAdapter reviewsAdapter;

    ConnectionDetector connectionDetector;
    Movie m;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        //TODO restore instance state if its a a must
        super.onCreate(savedInstanceState);
        videosAL=new ArrayList<Video>();
        reviewAL=new ArrayList<Review>();
        connectionDetector= new ConnectionDetector(getActivity());
        if(getActivity().getIntent().getExtras().containsKey(MOVIE_OBJECT_KEY)){
            m=(Movie)getActivity().getIntent().getExtras().get(MOVIE_OBJECT_KEY);
        }

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
        if(m!=null) {
            title.setText(m.getOriginalTitle());
            overview.setText(m.getOverview());
            rating.setText(m.getRating());
            releaseDate.setText(m.getReleaseDate());
            //// TODO: add if temp image ,get poster from api
            //Picasso.with(getActivity()).load(m.getImageUrl()).into(movieThumb);
            movieThumb.setImageBitmap(m.getMoviePoster());
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
            getVideoAndReviews();
        }
        return rootView;
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
    // // TODO:  add transparency change to action bar
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
        //TODO add the Body of UnFavorite function and change iconon success
        Uri movieUri = MoviesContract.FavoriteMovieEntry.buildFavoriteMovieUri(m.getId());
        int rowDeleted=getActivity().getContentResolver().delete(movieUri,null,null);
        Log.v(TAG, "rowDeleted "+rowDeleted);
        favButton.setImageResource(R.drawable.star_false);
    }
    private void favorite() {
        Log.v(TAG, "favorite");
        //TODO add the Body of favorite function
        //once a movie is favorited its image bitmap is converted to base64 formate and base 64 string is added to object to be inserted into db
        // i used setImageBase64 here so content values returned from getInsertContentValues conains image value

        saveMovieThumbToObject();
        Uri inserted = getActivity().getContentResolver().insert(MoviesContract.FavoriteMovieEntry.CONTENT_URI,m.getInsertContentValues());
        Log.v(TAG,inserted.toString());
        favButton.setImageResource(R.drawable.star_true);


    }
    void saveMovieThumbToObject(){
        String retBase64;
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
    }



    boolean isFavoriteMovie() {
        //todo add body to this function get from db/sp
        Uri movieUri = MoviesContract.FavoriteMovieEntry.buildFavoriteMovieUri(m.getId());

        Cursor c = getActivity().getContentResolver().query(movieUri, null, null, null, null);
        Log.v(TAG, "dumpCursor");
        DatabaseUtils.dumpCursor(c);
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
            else if (c.getCount() > 1) {
                Log.v(TAG, "isFavoriteMovie count is returning negative");
                return true;
            }
            return true;
        }

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        //Todo save instance state
        super.onSaveInstanceState(outState);
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
    static class FetchMovieVideos extends FetchTaskGET{
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
            Log.v(LogTag,s);
            //// TODO: 26/12/15 add parsing to get youtube string
            try {
                videosAL.clear();
                videosAL.addAll(getVideosFromJson(s));
                videosAdapter.notifyDataSetChanged();
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
            //// TODO: 26/12/15 add parsing to get youtube string
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
