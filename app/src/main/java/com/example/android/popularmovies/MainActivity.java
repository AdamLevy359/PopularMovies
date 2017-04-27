package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.android.popularmovies.utilities.NetworkUtils;
import com.example.android.popularmovies.utilities.TheMovieDbJsonUtils;

import org.json.JSONException;

import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private GridView mGridView;
    private MovieAdapter mMovieAdapter;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;
    Spinner sortSpinner;
    String sortPreference;
    ArrayList<Movie> mMovies;
    boolean initialized;
    String jsonMoviesString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean jsonParseError = false;
        if(!initialized && savedInstanceState != null){
            if(savedInstanceState.containsKey("json")  && savedInstanceState.containsKey("sort")){

                jsonMoviesString = savedInstanceState.getString("json");
                sortPreference = savedInstanceState.getString("sort");
                try {
                    updateMoviesArray(jsonMoviesString);
                    initialized = true;
                } catch (JSONException e) {
                    e.printStackTrace();
                    jsonParseError = true;
                }
            }
        }
        else if(!initialized){
            mMovies = new ArrayList<Movie>();
            sortPreference = getString(R.string.popularSort);
        }


        setContentView(R.layout.activity_main);

        mGridView = (GridView) findViewById(R.id.movies_grid);
        mErrorMessageDisplay = (TextView) findViewById(R.id.movie_error_message_display);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        sortSpinner = (Spinner) findViewById(R.id.sort_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.movies_array, R.layout.spinner_layout);
        adapter.setDropDownViewResource(R.layout.spinner_layout);
        sortSpinner.setAdapter(adapter);
        if(sortPreference == getString(R.string.popularSort))
            sortSpinner.setSelection(0,false);
        else
            sortSpinner.setSelection(1,false);

        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                sortPreference = adapterView.getItemAtPosition(i).toString();
                loadMovieData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mMovies = new ArrayList<Movie>();
        mMovieAdapter = new MovieAdapter(this, mMovies);
        mGridView.setAdapter(mMovieAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Class detailActivityClass = DetailActivity.class;
                Intent intent = new Intent(MainActivity.this, detailActivityClass);
                intent.putExtra("json", jsonMoviesString);
                intent.putExtra("position", position);
                startActivity(intent);

            }
        });

        if(jsonParseError){
            showErrorMessage();
        }
        else if (initialized){
            showMovieGridView();
        }
        else {
            loadMovieData();
        }
    }

    private void loadMovieData() {
        new FetchMoviesTask().execute(sortPreference);
    }

    private void showMovieGridView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mGridView.setVisibility(View.VISIBLE);
        mMovieAdapter.notifyDataSetChanged();
    }

    private void showErrorMessage() {
        mGridView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, Movie[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
            mGridView.setVisibility(View.GONE);
        }

        @Override
        protected Movie[] doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            String sortOrder = params[0];
            URL movieRequestUrl = NetworkUtils.buildUrl(sortOrder);

            try {
                jsonMoviesString = NetworkUtils.getResponseFromHttpUrl(movieRequestUrl);
                Movie[] movieData = TheMovieDbJsonUtils.getMoviesFromJson(jsonMoviesString);
                return movieData;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Movie[] movies) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (movies != null) {
                mMovies.clear();
                for (Movie m: movies) {
                    mMovies.add(m);
                }

                showMovieGridView();
            } else {
                showErrorMessage();
            }
        }
    }

    private void updateMoviesArray (String jsonMoviesString) throws JSONException {
        Movie[] movies = TheMovieDbJsonUtils.getMoviesFromJson(jsonMoviesString);
        if(movies != null){
            mMovieAdapter.clear();
            for (Movie m: movies) {
                mMovies.add(m);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        outState.putString("json", jsonMoviesString);
        outState.putString("sortPreference", sortPreference);
        super.onSaveInstanceState(outState);
    }
}
