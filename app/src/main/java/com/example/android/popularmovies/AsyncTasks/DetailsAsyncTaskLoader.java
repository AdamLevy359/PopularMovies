package com.example.android.popularmovies.asyncTasks;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.example.android.popularmovies.ExpandableHeightListView;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.utilities.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;

import static com.example.android.popularmovies.utilities.JsonUtils.MOVIE_ID;

public class DetailsAsyncTaskLoader extends AsyncTaskLoader<ArrayList<String>>  {
    Bundle args;
    ProgressBar mLoadingIndicator;
    ExpandableHeightListView trailersListView;

    public DetailsAsyncTaskLoader(Context context, final Bundle args, ProgressBar mLoadingIndicator, ExpandableHeightListView trailersListView) {
        super(context);
        this.args = args;
        this.mLoadingIndicator = mLoadingIndicator;
        this. trailersListView = trailersListView;
    }

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
}
