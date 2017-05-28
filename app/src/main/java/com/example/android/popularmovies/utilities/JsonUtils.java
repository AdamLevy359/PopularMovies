package com.example.android.popularmovies.utilities;

import com.example.android.popularmovies.Movie;
import com.example.android.popularmovies.Review;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JsonUtils {
    public static final String JSON_EXTRA = "json";
    public static final String SORT_EXTRA = "sort";
    public static final String POSITION_EXTRA = "position";
    public static final String MOVIE_ID = "id";

    private static final String STATUS_CODE = "status_code";
    private static final String RESULTS_LIST = "results";

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

        JSONArray moviesJsonArray = moviesJsonObject.getJSONArray(RESULTS_LIST);

        for(int i=0; i<moviesJsonArray.length(); i++){
            //Get the JSON object representing the movie
            JSONObject movieJsonObject = moviesJsonArray.getJSONObject(i);
            String movieTitle = movieJsonObject.getString(MOVIE_TITLE);
            String posterPath = movieJsonObject.getString(MOVIE_POSTER_PATH);
            String moviePlot = movieJsonObject.getString(MOVIE_OVERVIEW);
            String movieRating = movieJsonObject.getString(MOVIE_RATING);
            String movieReleaseDate = movieJsonObject.getString(MOVIE_RELEASE_DATE);
            long movieId = movieJsonObject.getLong(MOVIE_ID);

            //Create a movie object from the Json data and add it to the ArrayList
            Movie movie = new Movie(movieTitle, NetworkUtils.buildMoviePosterAddress(posterPath), moviePlot, movieRating, movieReleaseDate, movieId);
            movies.add(movie);
        }
        return movies;

    }

    public static ArrayList<String> getTrailersFromJson(String trailersJsonString) throws JSONException{
        final String TRAILER_KEY = "key";
        final String SITE_KEY = "site";
        final String TYPE_KEY = "type";
        ArrayList<String> trailers = new ArrayList<>();
        JSONObject trailersJsonObject = new JSONObject(trailersJsonString);
        /* Is there an error? */
        if (trailersJsonObject.has(STATUS_CODE)) {
            return null;
        }
        JSONArray trailersJsonArray = trailersJsonObject.getJSONArray(RESULTS_LIST);

        for(int i=0; i<trailersJsonArray.length(); i++){

            //Get the JSON object representing the trailer
            JSONObject trailerJsonObject = trailersJsonArray.getJSONObject(i);

            //Get the site and make sure it is YouTube
            String site = trailerJsonObject.getString(SITE_KEY);
            String type = trailerJsonObject.getString(TYPE_KEY);
            if(site.equalsIgnoreCase("YouTube") && type.equalsIgnoreCase("Trailer")) {
                //If it is youtube trailer then add it to the list of trailers
                String trailer = trailerJsonObject.getString(TRAILER_KEY);
                trailers.add(trailer);
            }
        }
        return trailers;

    }

    public static ArrayList<Review> getReviewsFromJson(String reviewsJsonString) throws JSONException{
        final String AUTHOR_KEY = "author";
        final String CONTENT_KEY = "content";
        ArrayList<Review> reviews = new ArrayList<>();
        JSONObject reviewsJsonObject = new JSONObject(reviewsJsonString);

        if (reviewsJsonObject.has(STATUS_CODE)) {
            return null;
        }
        JSONArray reviewsJsonArray = reviewsJsonObject.getJSONArray(RESULTS_LIST);
        for(int i=0; i<reviewsJsonArray.length(); i++){
            JSONObject reviewJsonObject = reviewsJsonArray.getJSONObject(i);

            String author = reviewJsonObject.getString(AUTHOR_KEY);
            String comment = reviewJsonObject.getString(CONTENT_KEY);

            Review review = new Review(author,comment);
            reviews.add(review);
        }
        return reviews;
    }
}


