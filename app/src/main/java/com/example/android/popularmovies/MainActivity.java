package com.example.android.popularmovies;

import android.content.Intent;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.android.popularmovies.adapters.MovieAdapter;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.example.android.popularmovies.utilities.JsonUtils;
import org.json.JSONException;
import java.net.URL;
import java.util.ArrayList;

import static com.example.android.popularmovies.utilities.JsonUtils.JSON_EXTRA;
import static com.example.android.popularmovies.utilities.JsonUtils.POSITION_EXTRA;
import static com.example.android.popularmovies.utilities.JsonUtils.SORT_EXTRA;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<ArrayList<Movie>> {
    private static final int MOVIEDB_SEARCH_LOADER = 1;
    private GridView mGridView;
    private MovieAdapter mMovieAdapter;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;
    String jsonMoviesString;
    String sortPreference;
    ArrayList<Movie> mMovies;
    Spinner sortSpinner;


    private void getSavedData(Bundle savedInstanceState) throws JSONException {
        if (savedInstanceState.containsKey(JSON_EXTRA) &&
                savedInstanceState.containsKey(SORT_EXTRA)) {
            jsonMoviesString = savedInstanceState.getString(JSON_EXTRA);
            sortPreference = savedInstanceState.getString(SORT_EXTRA);
            ArrayList<Movie> savedMovies = JsonUtils.getMoviesFromJson(jsonMoviesString);
            mMovies.clear();
            mMovies.addAll(savedMovies);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sortPreference = getString(R.string.popularSort);
        mMovies = new ArrayList<>();

        if(savedInstanceState != null){
            try{
                getSavedData(savedInstanceState);
            }catch (JSONException e){
                e.printStackTrace();
                showErrorMessage();
                return;
            }
        }


        mGridView = (GridView) findViewById(R.id.movies_grid);
        mErrorMessageDisplay = (TextView) findViewById(R.id.movie_error_message_display);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        //Create the spinner and set the dropdown to show the current sort order.
        sortSpinner = (Spinner) findViewById(R.id.sort_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.movies_array, R.layout.spinner_layout);
        adapter.setDropDownViewResource(R.layout.spinner_layout);
        sortSpinner.setAdapter(adapter);
        if(sortPreference == getString(R.string.popularSort))
            sortSpinner.setSelection(0,false);
        else
            sortSpinner.setSelection(1,false);

        //Create the listener for the spinner
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


        //Create custom movie adapter and gridview
        mMovieAdapter = new MovieAdapter(this, mMovies);
        mGridView.setAdapter(mMovieAdapter);

        //Create the listener for the gridview
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Class detailActivityClass = DetailActivity.class;
                Intent intent = new Intent(MainActivity.this, detailActivityClass);
                intent.putExtra(JSON_EXTRA, jsonMoviesString);
                intent.putExtra(POSITION_EXTRA, position);
                startActivity(intent);

            }
        });

        loadMovieData();
    }

    private void loadMovieData() {
        Bundle queryBundle = new Bundle();
        queryBundle.putString(SORT_EXTRA, sortPreference);
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<ArrayList<Movie>> githubSearchLoader = loaderManager.getLoader(MOVIEDB_SEARCH_LOADER);
        if (githubSearchLoader == null) {
            loaderManager.initLoader(MOVIEDB_SEARCH_LOADER, queryBundle, this);
        } else {
            loaderManager.restartLoader(MOVIEDB_SEARCH_LOADER, queryBundle, this);
        }
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

    @Override
    public Loader<ArrayList<Movie>> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<ArrayList<Movie>>(this) {

            ArrayList<Movie> mMovieDbData;

            @Override
            protected void onStartLoading() {

                if (args == null) {
                    return;
                }
                if(mMovieDbData != null){
                    deliverResult(mMovieDbData);
                }else {
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    mGridView.setVisibility(View.GONE);
                    forceLoad();
                }
            }

            @Override
            public ArrayList<Movie> loadInBackground() {
                String sortOrder = args.getString(SORT_EXTRA);
                URL movieRequestUrl;

                if(sortOrder.equals(getString(R.string.popularSort)))
                    movieRequestUrl = NetworkUtils.buildPopularMoviesURL();
                else
                    movieRequestUrl = NetworkUtils.buildTopRatedURL();

                try {
                    jsonMoviesString = NetworkUtils.getResponseFromHttpUrl(movieRequestUrl);
                    ArrayList<Movie> movieData = JsonUtils.getMoviesFromJson(jsonMoviesString);
                    return movieData;

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void deliverResult(ArrayList<Movie> movieDbData) {
                mMovieDbData = movieDbData;
                super.deliverResult(movieDbData);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Movie>> loader, ArrayList<Movie> data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        if (data != null) {
            mMovies.clear();
            mMovies.addAll(data);
            showMovieGridView();
        } else {
            showErrorMessage();
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Movie>> loader) {

    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        outState.putString(JSON_EXTRA, jsonMoviesString);
        outState.putString(SORT_EXTRA, sortPreference);
        super.onSaveInstanceState(outState);
    }
}
