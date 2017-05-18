package com.example.android.popularmovies;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies.utilities.JsonUtils;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.net.URL;
import java.util.ArrayList;

import static com.example.android.popularmovies.utilities.JsonUtils.MOVIE_ID;
import static com.example.android.popularmovies.utilities.JsonUtils.SORT_EXTRA;

public class DetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<ArrayList<String>>{
    private static final int TRAILER_REVIEW_SEARCH_LOADER = 2;

    private TextView movieTitle;
    private TextView movieReleaseDate;
    private TextView moviePlot;
    private TextView movieRating;
    private ImageView moviePoster;
    private TextView trailersLabel;
    private ListView trailersListView;
    private TrailerAdapter trailerAdapter;
    private ProgressBar mLoadingIndicator;

    Movie movie;
    ArrayList<String> trailers;

    /**
     * Populates the textviews, which show the movie details: title, release date, etc.
     * @param intent
     */
    void loadMovie(Intent intent) throws JSONException {
        if(intent != null){
            if(intent.hasExtra("json") && intent.hasExtra("position")){
                String jsonMoviesString = intent.getStringExtra("json");
                int position = intent.getIntExtra("position",0);
                ArrayList<Movie> movies = JsonUtils.getMoviesFromJson(jsonMoviesString);
                movie = movies.get(position);
                movieTitle.setText("\nTitle:\n" + movie.movieTitle);
                movieReleaseDate.setText("\nRelease Date:    \n" + movie.movieReleaseDate);
                movieRating.setText("\nVote Average:    \n"+ movie.movieRating);
                moviePlot.setText("Plot Synopsis:    \n" + movie.moviePlot);
                Picasso.with(this).load(movie.moviePosterURL).into(moviePoster);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        movieTitle = (TextView) findViewById(R.id.movieTitle);
        movieReleaseDate = (TextView) findViewById(R.id.movieReleaseDate);
        moviePlot = (TextView) findViewById(R.id.moviePlot);
        movieRating = (TextView) findViewById(R.id.movieRating);
        moviePoster = (ImageView) findViewById(R.id.moviePoster);
        trailersListView = (ListView) findViewById(R.id.trailersListView);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.detail_pb_loading_indicator);

        try {
            loadMovie(getIntent());
            trailers = new ArrayList<>();
            trailerAdapter = new TrailerAdapter(this, trailers);
            trailersListView.setAdapter(trailerAdapter);
            loadTrailers();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void loadTrailers(){
        Bundle queryBundle = new Bundle();
        queryBundle.putLong(MOVIE_ID, movie.movieId);
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<ArrayList<Movie>> githubSearchLoader = loaderManager.getLoader(TRAILER_REVIEW_SEARCH_LOADER);
        if (githubSearchLoader == null) {
            loaderManager.initLoader(TRAILER_REVIEW_SEARCH_LOADER, queryBundle, this);
        } else {
            loaderManager.restartLoader(TRAILER_REVIEW_SEARCH_LOADER, queryBundle, this);
        }
    }

    private void showTrailerList(){
        trailersListView.setVisibility(View.VISIBLE);
        trailerAdapter.notifyDataSetChanged();
    }

    @Override
    public Loader<ArrayList<String>> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<ArrayList<String>>(this) {

            ArrayList<String> mTrailerData;

            @Override
            protected void onStartLoading() {

                if (args == null) {
                    return;
                }
                if(mTrailerData != null){
                    deliverResult(mTrailerData);
                }else {
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    trailersListView.setVisibility(View.GONE);
                    forceLoad();
                }
            }

            @Override
            public ArrayList<String> loadInBackground() {
                long movieId = args.getLong(MOVIE_ID);
                URL trailerRequestUrl;
                trailerRequestUrl = NetworkUtils.buildTrailersURL(movieId);

                try {
                    String jsonTrailersString = NetworkUtils.getResponseFromHttpUrl(trailerRequestUrl);
                    ArrayList<String> trailerData = JsonUtils.getTrailersFromJson(jsonTrailersString);
                    return trailerData;

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void deliverResult(ArrayList<String> trailerData) {
                mTrailerData = trailerData;
                super.deliverResult(trailerData);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<String>> loader, ArrayList<String> data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        if (data != null) {
            trailers.clear();
            trailers.addAll(data);
            showTrailerList();
        } else {
            //showErrorMessage();
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<String>> loader) {

    }


}
