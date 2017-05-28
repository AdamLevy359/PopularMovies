package com.example.android.popularmovies.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.Movie;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.MovieColumns;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class MovieCursorAdapter extends CursorAdapter {

    private static final String LOG_TAG = MovieCursorAdapter.class.getSimpleName();
    private ArrayList<Movie> mMovies;

    public MovieCursorAdapter(Context context, Cursor cursor, ArrayList<Movie> movies){
        super(context, cursor, FLAG_REGISTER_CONTENT_OBSERVER);
        mMovies = movies;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.movie_item,viewGroup,false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String title = cursor.getString(cursor.getColumnIndex(MovieColumns.TITLE));
        String poster = cursor.getString(cursor.getColumnIndex(MovieColumns.moviePosterURL));
        String plot = cursor.getString(cursor.getColumnIndex(MovieColumns.moviePlot));
        String rating = cursor.getString(cursor.getColumnIndex(MovieColumns.movieRating));
        String release = cursor.getString(cursor.getColumnIndex(MovieColumns.movieReleaseDate));
        String stringID = cursor.getString(cursor.getColumnIndex(MovieColumns.MOVIE_DB_ID));
        long id = Long.parseLong(stringID);
        Movie movie = new Movie(title, poster, plot, rating, release, id);
        if(!mMovies.contains(movie))
        mMovies.add(movie);

        ImageView movieImageView = (ImageView) view.findViewById(R.id.movie_image);
        TextView movieTextView = (TextView) view.findViewById(R.id.movie_text);
        movieTextView.setText(movie.movieTitle);
        Picasso.with(context).load(movie.moviePosterURL).into(movieImageView);
    }
}
