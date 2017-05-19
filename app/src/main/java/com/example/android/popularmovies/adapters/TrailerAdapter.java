package com.example.android.popularmovies.adapters;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.adapters.MovieAdapter;

import java.util.List;

public class TrailerAdapter extends ArrayAdapter<String> {
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
        int pos = position + 1;
        movieTitleView.setText("Trailer " + pos);
        return convertView;
    }
}
