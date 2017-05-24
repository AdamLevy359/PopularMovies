package com.example.android.popularmovies.asyncTasks;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.android.popularmovies.ExpandableHeightListView;
import com.example.android.popularmovies.Review;
import com.example.android.popularmovies.adapters.ReviewAdapter;
import com.example.android.popularmovies.adapters.TrailerAdapter;
import com.example.android.popularmovies.utilities.JsonUtils;

import org.json.JSONException;

import java.util.ArrayList;

public class DetailsLoaderManager implements LoaderManager.LoaderCallbacks<ArrayList<String>> {

    private Context context;
    private TextView trailersLabel;
    private ExpandableHeightListView trailersListView;
    private ExpandableHeightListView reviewsListView;
    private TrailerAdapter trailerAdapter;
    private ReviewAdapter reviewsAdapter;
    private ProgressBar mLoadingIndicator;
    ScrollView scrollView;
    ArrayList<String> trailers;
    ArrayList<Review> reviews;

    public DetailsLoaderManager(Context context,
                                ExpandableHeightListView trailersListView,
                                ExpandableHeightListView reviewsListView,
                                TrailerAdapter trailerAdapter,
                                ReviewAdapter reviewsAdapter,
                                ScrollView scrollView,
                                ProgressBar loadingIndicator,
                                ArrayList<String> trailers,
                                ArrayList<Review> reviews){
        this.context = context;
        this.trailersListView = trailersListView;
        this.reviewsListView = reviewsListView;
        this.trailerAdapter = trailerAdapter;
        this.reviewsAdapter = reviewsAdapter;
        this.scrollView = scrollView;
        this.mLoadingIndicator = loadingIndicator;
        this.trailers = trailers;
        this.reviews = reviews;
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
        return new DetailsAsyncTaskLoader(context, args, mLoadingIndicator, trailersListView);
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