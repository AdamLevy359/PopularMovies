package com.example.android.popularmovies.utilities;

import android.content.Context;

import com.example.android.popularmovies.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

public class JsonUtils {
    public static final String JSON_EXTRA = "json";
    public static final String SORT_EXTRA = "sort";
    public static final String POSITION_EXTRA = "position";

    public static ArrayList<Movie> getMoviesFromJson(String moviesJsonString) throws JSONException{
        final String STATUS_CODE = "status_code";
        final String MOVIES_LIST = "results";
        final String MOVIE_POSTER_PATH = "poster_path";
        final String MOVIE_OVERVIEW = "overview";
        final String MOVIE_RELEASE_DATE = "release_date";
        final String MOVIE_TITLE = "title";
        final String MOVIE_RATING = "vote_average";
        ArrayList<Movie> movies = new ArrayList<>();

        JSONObject moviesJsonObject = new JSONObject(moviesJsonString);

        /* Is there an error? */
        if (moviesJsonObject.has(STATUS_CODE)) {
            return null;
        }

        JSONArray moviesJsonArray = moviesJsonObject.getJSONArray(MOVIES_LIST);

        for(int i=0; i<moviesJsonArray.length(); i++){
            String movieTitle;
            String moviePosterURL;
            String moviePlot;
            String movieRating;
            String movieReleaseDate;

            //Get the JSON object representing the movie
            JSONObject movieJsonObject = moviesJsonArray.getJSONObject(i);
            movieTitle = movieJsonObject.getString(MOVIE_TITLE);
            moviePosterURL = movieJsonObject.getString(MOVIE_POSTER_PATH);
            moviePlot = movieJsonObject.getString(MOVIE_OVERVIEW);
            movieRating = movieJsonObject.getString(MOVIE_RATING);
            movieReleaseDate = movieJsonObject.getString(MOVIE_RELEASE_DATE);

            //Create a movie object from the Json data and add it to the ArrayList
            Movie movie = new Movie(movieTitle, moviePosterURL, moviePlot, movieRating, movieReleaseDate);
            movies.add(movie);
        }
        return movies;

    }
}


