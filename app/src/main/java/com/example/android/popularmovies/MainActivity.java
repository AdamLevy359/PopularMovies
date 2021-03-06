package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.CursorLoader;
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
import com.example.android.popularmovies.adapters.MovieCursorAdapter;
import com.example.android.popularmovies.data.MovieProvider;
import com.example.android.popularmovies.utilities.DatabaseUtils;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.example.android.popularmovies.utilities.JsonUtils;
import org.json.JSONException;
import java.net.URL;
import java.util.ArrayList;

import static com.example.android.popularmovies.utilities.JsonUtils.SORT_EXTRA;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    private LoaderManager.LoaderCallbacks<ArrayList<Movie>> movieLoaderCallbacks =
            new LoaderManager.LoaderCallbacks<ArrayList<Movie>>() {
                @Override
                public Loader<ArrayList<Movie>> onCreateLoader(int id, final Bundle args) {
                    return new AsyncTaskLoader<ArrayList<Movie>>(getApplicationContext()) {

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
                                String jsonMoviesString = NetworkUtils.getResponseFromHttpUrl(movieRequestUrl);
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
            };

    private static final int MOVIEDB_SEARCH_LOADER = 1;
    private static final int FAVORITE_CURSOR_LOADER = 4;
    private GridView mGridView;
    private MovieAdapter mMovieAdapter;
    private MovieCursorAdapter mMovieCursorAdapter;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;
    String sortPreference;
    ArrayList<Movie> mMovies;
    Spinner sortSpinner;


    private void getSavedData(Bundle savedInstanceState) throws JSONException {
        if (savedInstanceState.containsKey(SORT_EXTRA)) {
            sortPreference = savedInstanceState.getString(SORT_EXTRA);
            ArrayList<Movie> savedMovies = savedInstanceState.getParcelableArrayList("mMovies");
            mMovies.clear();
            mMovies.addAll(savedMovies);

            mMovieAdapter = new MovieAdapter(this, mMovies);
            mGridView.setAdapter(mMovieAdapter);
            showMovieGridView();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sortPreference = getString(R.string.popularSort);
        mMovies = new ArrayList<>();




        mGridView = (GridView) findViewById(R.id.movies_grid);
        mErrorMessageDisplay = (TextView) findViewById(R.id.movie_error_message_display);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        //Create the spinner and set the dropdown to show the current sort order.
        sortSpinner = (Spinner) findViewById(R.id.sort_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.movies_array, R.layout.spinner_layout);
        adapter.setDropDownViewResource(R.layout.spinner_layout);
        sortSpinner.setAdapter(adapter);
        if(sortPreference.equals(getString(R.string.popularSort)))
            sortSpinner.setSelection(0,false);
        else if(sortPreference.equals(getString(R.string.topRatedSort)))
            sortSpinner.setSelection(1,false);
        else
            sortSpinner.setSelection(2,false);

        //Create the listener for the spinner
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                sortPreference = adapterView.getItemAtPosition(i).toString();
                loadData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        //Create the listener for the gridview
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Class detailActivityClass = DetailActivity.class;
                Intent intent = new Intent(MainActivity.this, detailActivityClass);
                intent.putExtra("movie", mMovies.get(position));
                startActivity(intent);

            }
        });

        if(savedInstanceState != null){
            try{
                getSavedData(savedInstanceState);
            }catch (JSONException e){
                e.printStackTrace();
                showErrorMessage();
                return;
            }
        }else{
            loadData();
        }
    }

    private void loadData(){
        mMovies.clear();
        if(sortPreference.equalsIgnoreCase(getString(R.string.favoriteSort))){
            loadFavoriteData();

        }else{
            loadMovieData();
        }
    }

    private void loadMovieData() {
        mMovieAdapter = new MovieAdapter(this, mMovies);
        mGridView.setAdapter(mMovieAdapter);

        Bundle queryBundle = new Bundle();
        queryBundle.putString(SORT_EXTRA, sortPreference);
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<ArrayList<Movie>> githubSearchLoader = loaderManager.getLoader(MOVIEDB_SEARCH_LOADER);
        if (githubSearchLoader == null) {
            loaderManager.initLoader(MOVIEDB_SEARCH_LOADER, queryBundle, movieLoaderCallbacks);
        } else {
            loaderManager.restartLoader(MOVIEDB_SEARCH_LOADER, queryBundle, movieLoaderCallbacks);
        }
    }

    private void loadFavoriteData(){
        mMovieCursorAdapter = new MovieCursorAdapter(getApplicationContext(), null);
        mGridView.setAdapter(mMovieCursorAdapter);

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<Cursor> favoritesLoader = loaderManager.getLoader(FAVORITE_CURSOR_LOADER);
        if(favoritesLoader == null) {
            loaderManager.initLoader(FAVORITE_CURSOR_LOADER, null, this);
        }else{
            loaderManager.restartLoader(FAVORITE_CURSOR_LOADER, null, this);
        }

    }

    private void showMovieGridView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mGridView.setVisibility(View.VISIBLE);

        if(!sortPreference.equals(getString(R.string.favoriteSort)))
            mMovieAdapter.notifyDataSetChanged();
    }

    private void showErrorMessage() {
        mGridView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        outState.putParcelableArrayList("mMovies",mMovies);
        outState.putString(SORT_EXTRA, sortPreference);
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, MovieProvider.Movies.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMovies.clear();
        mMovies.addAll(DatabaseUtils.getMoviesArrayFromCursor(data));
        mMovieCursorAdapter.swapCursor(data);
        showMovieGridView();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMovieCursorAdapter.swapCursor(null);

    }
}
