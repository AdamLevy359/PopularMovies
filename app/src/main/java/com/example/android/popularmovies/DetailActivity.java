package com.example.android.popularmovies;


import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.android.popularmovies.asyncTasks.DetailsAsyncTaskLoader;
import com.example.android.popularmovies.adapters.ReviewAdapter;
import com.example.android.popularmovies.adapters.TrailerAdapter;
import com.example.android.popularmovies.asyncTasks.DetailsLoaderManager;
import com.example.android.popularmovies.utilities.JsonUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import java.util.ArrayList;

import static com.example.android.popularmovies.utilities.JsonUtils.MOVIE_ID;

public class DetailActivity extends AppCompatActivity{
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
    private ImageButton imageButton;
    private boolean isFavorite = false;

    Movie movie;
    ArrayList<String> trailers;
    ArrayList<Review> reviews;
    ScrollView scrollView;

    DetailsLoaderManager detailsLoaderManager;

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
        imageButton = (ImageButton) findViewById(R.id.imageButton);

        imageButton.setOnClickListener(new ImageButton.OnClickListener(){
            public void onClick(View view) {
                if(!isFavorite) {
                    imageButton.setImageResource(R.mipmap.fullheart);
                    isFavorite = true;
                } else {
                    imageButton.setImageResource(R.mipmap.emptyheart);
                    isFavorite = false;
                }
            }
        });

        try {
            loadMovie(getIntent());
            trailers = new ArrayList<>();
            trailerAdapter = new TrailerAdapter(this, trailers);
            trailersListView.setAdapter(trailerAdapter);

            trailersListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                public void onItemClick(AdapterView<?> parent, View v, int position, long id){
                    String trailer = trailers.get(position);
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + trailer)));
                }
            });

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
            detailsLoaderManager = new DetailsLoaderManager(getApplicationContext(), trailersListView, reviewsListView,
                    trailerAdapter, reviewsAdapter, scrollView, mLoadingIndicator, trailers, reviews);
            loaderManager.initLoader(TRAILER_REVIEW_SEARCH_LOADER, queryBundle, detailsLoaderManager);
        } else {
            loaderManager.restartLoader(TRAILER_REVIEW_SEARCH_LOADER, queryBundle, detailsLoaderManager);
        }
    }

}
