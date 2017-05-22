package com.example.android.popularmovies;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.android.popularmovies.adapters.ReviewAdapter;
import com.example.android.popularmovies.adapters.TrailerAdapter;
import com.example.android.popularmovies.utilities.JsonUtils;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.net.URL;
import java.util.ArrayList;

import static com.example.android.popularmovies.utilities.JsonUtils.MOVIE_ID;

public class DetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<ArrayList<String>>{
    private static final int TRAILER_REVIEW_SEARCH_LOADER = 2;

    private TextView movieTitleReleaseRating;
    private TextView moviePlot;
    private ImageView moviePoster;
    private TextView trailersLabel;
    private ExpandableHeightListView trailersListView;
    private ExpandableHeightListView reviewsListView;
    private TrailerAdapter trailerAdapter;
    private ReviewAdapter reviewsAdapter;
    private ProgressBar mLoadingIndicator;

    Movie movie;
    ArrayList<String> trailers;
    ArrayList<Review> reviews;
    ScrollView scrollView;

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
                movieTitleReleaseRating.setText(
                        "\nTitle:\n" + movie.movieTitle +
                        "\n\nRelease Date:    \n" + movie.movieReleaseDate +
                        "\n\nVote Average:    \n"+ movie.movieRating + "\n");
                moviePlot.setText("\nPlot Synopsis:    \n" + movie.moviePlot + "\n");
                Picasso.with(this).load(movie.moviePosterURL).into(moviePoster);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        movieTitleReleaseRating = (TextView) findViewById(R.id.movieTitle);
        moviePlot = (TextView) findViewById(R.id.moviePlot);
        moviePoster = (ImageView) findViewById(R.id.moviePoster);
        trailersListView = (ExpandableHeightListView) findViewById(R.id.trailersListView);
        reviewsListView = (ExpandableHeightListView) findViewById(R.id.reviewsListView);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.detail_pb_loading_indicator);
        scrollView = (ScrollView) findViewById(R.id.detailScrollView);

        try {
            loadMovie(getIntent());
            trailers = new ArrayList<>();
            trailerAdapter = new TrailerAdapter(this, trailers);
            trailersListView.setAdapter(trailerAdapter);

            reviews = new ArrayList<>();
            reviewsAdapter = new ReviewAdapter(this, reviews);
            reviewsListView.setAdapter(reviewsAdapter);

            loadTrailersAndReviews();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void loadTrailersAndReviews(){
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

    private void showTrailerAndReviewList(){
        trailersListView.setExpanded(true);
        trailersListView.setVisibility(View.VISIBLE);
        trailerAdapter.notifyDataSetChanged();

        reviewsListView.setExpanded(true);
        reviewsListView.setVisibility(View.VISIBLE);
        reviewsAdapter.notifyDataSetChanged();
        scrollView.smoothScrollTo(0,0);
    }

    @Override
    public Loader<ArrayList<String>> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<ArrayList<String>>(this) {

            ArrayList<String> mTrailerReviewData;

            @Override
            protected void onStartLoading() {

                if (args == null) {
                    return;
                }
                if(mTrailerReviewData != null){
                    deliverResult(mTrailerReviewData);
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
                URL reviewsRequestUrl;
                trailerRequestUrl = NetworkUtils.buildTrailersURL(movieId);
                reviewsRequestUrl = NetworkUtils.buildReviewsURL(movieId);

                try {
                    String jsonTrailersString = NetworkUtils.getResponseFromHttpUrl(trailerRequestUrl);
                    String jsonReviewsString = NetworkUtils.getResponseFromHttpUrl(reviewsRequestUrl);
                    if(jsonTrailersString == null || jsonReviewsString == null){
                        return null;
                    }

                    ArrayList<String> trailerReviewData = new ArrayList<>();
                    trailerReviewData.add(jsonTrailersString);
                    trailerReviewData.add(jsonReviewsString);
                    return trailerReviewData;

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void deliverResult(ArrayList<String> trailerReviewData) {
                mTrailerReviewData = trailerReviewData;
                super.deliverResult(trailerReviewData);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<String>> loader, ArrayList<String> data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        if (data != null) {
            try {
                ArrayList<String> trailerData = JsonUtils.getTrailersFromJson(data.get(0));
                ArrayList<Review> reviewData = JsonUtils.getReviewsFromJson(data.get(1));
                trailers.clear();
                trailers.addAll(trailerData);
                reviews.clear();
                reviews.addAll(reviewData);
                showTrailerAndReviewList();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<String>> loader) {

    }


}
