package com.example.android.popularmovies;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.utilities.TheMovieDbJsonUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

public class DetailActivity extends AppCompatActivity {

    private TextView movieTitle;
    private TextView movieReleaseDate;
    private TextView moviePlot;
    private TextView movieRating;
    private ImageView moviePoster;

    Movie movie;
    int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        movieTitle = (TextView) findViewById(R.id.movieTitle);
        movieReleaseDate = (TextView) findViewById(R.id.movieReleaseDate);
        moviePlot = (TextView) findViewById(R.id.moviePlot);
        movieRating = (TextView) findViewById(R.id.movieRating);
        moviePoster = (ImageView) findViewById(R.id.moviePoster);

        Intent intent = getIntent();
        if(intent != null){
            if(intent.hasExtra("json") && intent.hasExtra("position")){
                String jsonMoviesString = intent.getStringExtra("json");
                int position = intent.getIntExtra("position",0);
                try {
                    Movie[] movies = TheMovieDbJsonUtils.getMoviesFromJson(jsonMoviesString);
                    movieTitle.setText("\nTitle:\n" + movies[position].movieTitle);
                    movieReleaseDate.setText("\nRelease Date:    \n" + movies[position].movieReleaseDate);
                    movieRating.setText("\nRating:    \n"+ movies[position].movieRating);
                    moviePlot.setText("\nSummary:    \n" + movies[position].moviePlot);
                    Picasso.with(this).load(movies[position].moviePosterURL).into(moviePoster);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
