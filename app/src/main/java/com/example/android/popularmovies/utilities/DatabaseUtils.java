package com.example.android.popularmovies.utilities;

import android.database.Cursor;

import com.example.android.popularmovies.Movie;
import com.example.android.popularmovies.data.MovieColumns;

import java.util.ArrayList;

public class DatabaseUtils {

    public static Movie GetMovieFromCursor(Cursor cursor){
        String title = cursor.getString(cursor.getColumnIndex(MovieColumns.TITLE));
        String poster = cursor.getString(cursor.getColumnIndex(MovieColumns.moviePosterURL));
        String plot = cursor.getString(cursor.getColumnIndex(MovieColumns.moviePlot));
        String rating = cursor.getString(cursor.getColumnIndex(MovieColumns.movieRating));
        String release = cursor.getString(cursor.getColumnIndex(MovieColumns.movieReleaseDate));
        String stringID = cursor.getString(cursor.getColumnIndex(MovieColumns.MOVIE_DB_ID));
        long id = Long.parseLong(stringID);
        return new Movie(title, poster, plot, rating, release, id);
    }

    public static ArrayList<Movie> getMoviesArrayFromCursor(Cursor cursor){
        ArrayList<Movie> movies = new ArrayList<Movie>();
        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            do{
                movies.add(GetMovieFromCursor(cursor));
            }while (cursor.moveToNext());
        }
        return movies;
    }
}
