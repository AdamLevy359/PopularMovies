package com.example.android.popularmovies;

public class Movie {
    public String movieTitle;
    public String moviePosterURL;
    public String moviePlot;
    public String movieRating;
    public String movieReleaseDate;
    long movieId;

    public Movie(String title, String posterURL, String plot, String rating, String releaseDate, long id){
        movieTitle = title;
        moviePosterURL = "http://image.tmdb.org/t/p/w185"+posterURL;
        moviePlot = plot;
        movieRating = rating;
        movieReleaseDate = releaseDate;
        movieId = id;
    }
}
