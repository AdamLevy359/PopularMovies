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
    public static final String MOVIE_ID = "id";

    private static final String STATUS_CODE = "status_code";
    private static final String MOVIES_LIST = "results";

    public static ArrayList<Movie> getMoviesFromJson(String moviesJsonString) throws JSONException{
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
            long movieId;

            //Get the JSON object representing the movie
            JSONObject movieJsonObject = moviesJsonArray.getJSONObject(i);
            movieTitle = movieJsonObject.getString(MOVIE_TITLE);
            moviePosterURL = movieJsonObject.getString(MOVIE_POSTER_PATH);
            moviePlot = movieJsonObject.getString(MOVIE_OVERVIEW);
            movieRating = movieJsonObject.getString(MOVIE_RATING);
            movieReleaseDate = movieJsonObject.getString(MOVIE_RELEASE_DATE);
            movieId = movieJsonObject.getLong(MOVIE_ID);

            //Create a movie object from the Json data and add it to the ArrayList
            Movie movie = new Movie(movieTitle, moviePosterURL, moviePlot, movieRating, movieReleaseDate, movieId);
            movies.add(movie);
        }
        return movies;

    }

    public static ArrayList<String> getTrailersFromJson(String trailersJsonString) throws JSONException{
        final String TRAILER_KEY = "key";
        final String SITE_KEY = "site";
        ArrayList<String> trailers = new ArrayList<>();
        JSONObject trailersJsonObject = new JSONObject(trailersJsonString);
        /* Is there an error? */
        if (trailersJsonObject.has(STATUS_CODE)) {
            return null;
        }
        JSONArray trailersJsonArray = trailersJsonObject.getJSONArray(MOVIES_LIST);

        for(int i=0; i<trailersJsonArray.length(); i++){

            //Get the JSON object representing the trailer
            JSONObject trailerJsonObject = trailersJsonArray.getJSONObject(i);

            //Get the site and make sure it is YouTube
            String site = trailerJsonObject.getString(SITE_KEY);
            if(site.equalsIgnoreCase("YouTube")) {
                //If it is youtube then add it to the list of trailers
                String trailer = trailerJsonObject.getString(TRAILER_KEY);
                trailers.add(trailer);
            }
        }
        return trailers;

    }
}


