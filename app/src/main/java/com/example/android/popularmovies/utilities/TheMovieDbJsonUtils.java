package com.example.android.popularmovies.utilities;

import android.content.Context;

import com.example.android.popularmovies.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

public class TheMovieDbJsonUtils {

    public static Movie[] getMoviesFromJson(String moviesJsonString) throws JSONException{
        Movie[] movies = null;
        final String STATUS_CODE = "status_code";
        final String MOVIES_LIST = "results";
        final String MOVIE_POSTER_PATH = "poster_path";
        final String MOVIE_OVERVIEW = "overview";
        final String MOVIE_RELEASE_DATE = "release_date";
        final String MOVIE_TITLE = "title";
        final String MOVIE_RATING = "vote_average";

        JSONObject moviesJsonArray = new JSONObject(moviesJsonString);

        /* Is there an error? */
        if (moviesJsonArray.has(STATUS_CODE)) {
            return null;
        }

        JSONArray movieArray = moviesJsonArray.getJSONArray(MOVIES_LIST);

        movies = new Movie[movieArray.length()];
        for(int i=0; i<movieArray.length(); i++){
            String movieTitle;
            String moviePosterURL;
            String moviePlot;
            String movieRating;
            String movieReleaseDate;

            //Get the JSON object representing the movie
            JSONObject movieJsonObject = movieArray.getJSONObject(i);

            movieTitle = movieJsonObject.getString(MOVIE_TITLE);
            moviePosterURL = movieJsonObject.getString(MOVIE_POSTER_PATH);
            moviePlot = movieJsonObject.getString(MOVIE_OVERVIEW);
            movieRating = movieJsonObject.getString(MOVIE_RATING);
            movieReleaseDate = movieJsonObject.getString(MOVIE_RELEASE_DATE);

            Movie movie = new Movie(movieTitle, moviePosterURL, moviePlot, movieRating, movieReleaseDate);

            movies[i] = movie;
        }
        return movies;

    }
}


