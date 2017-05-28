package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

public class Movie implements Parcelable {
    public String movieTitle;
    public String moviePosterURL;
    public String moviePlot;
    public String movieRating;
    public String movieReleaseDate;
    long movieId;

    public Movie(String title, String posterURL, String plot, String rating, String releaseDate, long id){
        movieTitle = title;
        moviePosterURL = posterURL;
        moviePlot = plot;
        movieRating = rating;

        movieReleaseDate = releaseDate;
        movieId = id;
    }

    public int describeContents() {
        return 0;
    }

    private Movie(Parcel in){
        movieId = in.readLong();
        movieTitle = in.readString();
        moviePosterURL = in.readString();
        moviePlot = in.readString();
        movieRating = in.readString();
        movieReleaseDate = in.readString();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(movieId);
        dest.writeString(movieTitle);
        dest.writeString(moviePosterURL);
        dest.writeString(moviePlot);
        dest.writeString(movieRating);
        dest.writeString(movieReleaseDate);
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Movie movie = (Movie) o;

        if (movieId != movie.movieId) return false;
        if (movieTitle != null ? !movieTitle.equals(movie.movieTitle) : movie.movieTitle != null)
            return false;
        if (moviePosterURL != null ? !moviePosterURL.equals(movie.moviePosterURL) : movie.moviePosterURL != null)
            return false;
        if (moviePlot != null ? !moviePlot.equals(movie.moviePlot) : movie.moviePlot != null)
            return false;
        if (movieRating != null ? !movieRating.equals(movie.movieRating) : movie.movieRating != null)
            return false;
        return movieReleaseDate != null ? movieReleaseDate.equals(movie.movieReleaseDate) : movie.movieReleaseDate == null;

    }

    @Override
    public int hashCode() {
        int result = movieTitle != null ? movieTitle.hashCode() : 0;
        result = 31 * result + (moviePosterURL != null ? moviePosterURL.hashCode() : 0);
        result = 31 * result + (moviePlot != null ? moviePlot.hashCode() : 0);
        result = 31 * result + (movieRating != null ? movieRating.hashCode() : 0);
        result = 31 * result + (movieReleaseDate != null ? movieReleaseDate.hashCode() : 0);
        result = 31 * result + (int) (movieId ^ (movieId >>> 32));
        return result;
    }
}
