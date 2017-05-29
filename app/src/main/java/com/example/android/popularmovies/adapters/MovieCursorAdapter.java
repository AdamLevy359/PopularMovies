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
import com.example.android.popularmovies.utilities.DatabaseUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class MovieCursorAdapter extends CursorAdapter {

    private static final String LOG_TAG = MovieCursorAdapter.class.getSimpleName();

    public MovieCursorAdapter(Context context, Cursor cursor){
        super(context, cursor, FLAG_REGISTER_CONTENT_OBSERVER);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.movie_item,viewGroup,false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Movie movie = DatabaseUtils.GetMovieFromCursor(cursor);

        ImageView movieImageView = (ImageView) view.findViewById(R.id.movie_image);
        TextView movieTextView = (TextView) view.findViewById(R.id.movie_text);
        movieTextView.setText(movie.movieTitle);
        Picasso.with(context).load(movie.moviePosterURL).into(movieImageView);
    }
}
