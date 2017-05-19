package com.example.android.popularmovies.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.Review;

import java.util.List;


public class ReviewAdapter extends ArrayAdapter<Review>{
    public ReviewAdapter(Activity context, List<Review> reviews){
        super(context, 0, reviews);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Review review = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.review_item, parent, false);
        }
        TextView authorText = (TextView) convertView.findViewById(R.id.reviewAuthor);
        TextView commentText = (TextView) convertView.findViewById(R.id.reviewComment);

        authorText.setText(review.author);
        commentText.setText(review.comment);
        return convertView;
    }
}
