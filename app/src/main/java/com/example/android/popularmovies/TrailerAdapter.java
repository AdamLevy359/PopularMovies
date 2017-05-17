package com.example.android.popularmovies;


import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class TrailerAdapter extends ArrayAdapter<String> {
    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();


    public TrailerAdapter(Activity context, List<String> trailers) {
        super(context, 0, trailers);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Gets the Movie object from the ArrayAdapter at the appropriate position
        String trailer = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.trailer_item, parent, false);
        }

        TextView movieTitleView = (TextView) convertView.findViewById(R.id.trailer_text);
        movieTitleView.setText("Trailer " + position);
        return convertView;
    }
}
