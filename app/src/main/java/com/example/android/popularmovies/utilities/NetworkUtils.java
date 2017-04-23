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
    private static final String TAG = NetworkUtils.class.getSimpleName();

    //private static final String POPULAR_BASE_URL = "http://api.themoviedb.org/3/movie/popular";
    //private static final String TOP_RATED_BASE_URL = "http://api.themoviedb.org/3/movie/top_rated";
    private static final String MOVIE_BASE_URL ="http://api.themoviedb.org/3/movie/";

    private static final String apiKey = "PLACE_YOUR_THEMOVIEDB_API_KEY_HERE";
    final static String KEY_PARAM = "api_key";

    /**
     * Builds the URL used to talk to the movie server.
     *
     * @param userSortPreference The users sort preference that will be queried for.
     * @return The URL to use to query the movie server.
     */
    public static URL buildUrl(String userSortPreference) {
        String baseURL =  MOVIE_BASE_URL + userSortPreference;
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
     * Builds the URL used to talk to the weather server using latitude and longitude of a
     * location.
     *
     * @param lat The latitude of the location
     * @param lon The longitude of the location
     * @return The Url to use to query the weather server.
     */
    public static URL buildUrl(Double lat, Double lon) {
        /** This will be implemented in a future lesson **/
        return null;
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
