package com.example.android.popularmovies;

public class Movie {
    String movieTitle;
    String moviePosterURL;
    String moviePlot;
    String movieRating;
    String movieReleaseDate;

    public Movie(String title, String posterURL, String plot, String rating, String releaseDate){
        movieTitle = title;
        moviePosterURL = "http://image.tmdb.org/t/p/w185"+posterURL;
        moviePlot = plot;
        movieRating = rating;
        movieReleaseDate = releaseDate;
    }
}