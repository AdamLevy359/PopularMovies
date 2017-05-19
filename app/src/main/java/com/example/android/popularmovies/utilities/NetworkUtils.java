package com.example.android.popularmovies.utilities;


import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {

    //TODO Replace the apiKey string with your API key from TheMovieDb
    private static final String apiKey = "PLACE_YOUR_THEMOVIEDB_API_KEY_HERE";

    private static final String TAG = NetworkUtils.class.getSimpleName();
    private static final String BASE_URL = "http://api.themoviedb.org/3/movie/";
    private static final String POPULAR_MOVIES_BASE_URL = BASE_URL + "popular/";
    private static final String TOP_RATED_MOVIES_BASE_URL = BASE_URL + "top_rated/";

    final static String KEY_PARAM = "api_key";

    /**
     * Builds the url to get the top rated moves
     * @return The URL to query the movie server for top rated movies
     */
    public static URL buildTopRatedURL(){
        return buildUrl(TOP_RATED_MOVIES_BASE_URL);
    }

    /**
     * Builds the URL to get the most popular movies
     * @return The URL to query the movie server for most popular movies
     */
    public static URL buildPopularMoviesURL(){
        return buildUrl(POPULAR_MOVIES_BASE_URL);
    }

    /**
     * Builds the URL to get the trailers for a movie
     * @param videoId The id of the movie
     * @return The URL to query the list of trailers for a movie
     */
    public static URL buildTrailersURL(long videoId){
        return buildUrl(BASE_URL + videoId + "/videos");
    }

    /**
     * Builds the URL to the get the reviews for a movie
     * @param videoID The id of the movie
     * @return The URL to query the list of reviews for a movie
     */
    public static URL buildReviewsURL(long videoID){
        return buildUrl(BASE_URL + videoID + "/reviews");
    }

    /**
     * Builds the URL used to talk to the movie server.
     *
     * @param baseURL The base url for either popular movies or top rated.
     * @return The URL to use to query the movie server.
     */
    private static URL buildUrl(String baseURL) {
        Uri builtUri = Uri.parse(baseURL).buildUpon()
                .appendQueryParameter(KEY_PARAM, apiKey)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
