package com.example.eightleaves.popularmovie;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.eightleaves.popularmovie.adapters.MovieAdapter;
import com.example.eightleaves.popularmovie.models.data.MovieContract;
import com.example.eightleaves.popularmovie.event.EventExecutor;
import com.example.eightleaves.popularmovie.event.GetMovieDataEvent;
import com.example.eightleaves.popularmovie.event.MovieUpdateSuccessEvent;
import com.example.eightleaves.popularmovie.models.data.MovieDataUpdator;
import com.example.eightleaves.popularmovie.otto.MovieBus;
import com.squareup.otto.Subscribe;


public class MovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private ProgressDialog movieProgressDialog;
    private MovieAdapter movieAdapter;
    private static final int MOVIE_LOADER =0;
    private GridView mGridView;
    private int mPosition = mGridView.INVALID_POSITION;
    private static final String SELECTED_KEY = "selected_position";

    static final int COL_MOVIE_ID = 0;
    private static final int COL_MOVIE_MOVIE_ID = 1;
    public static final int COL_MOVIE_POSTER_PATH = 2;
    static final int COL_MOVIE_TITLE = 3;
    static final int COL_MOVIE_RELEASE_DATE = 4;
    static final int COL_MOVIE_OVERVIEW = 5;
    static final int COL_SORT_SETTING = 6;
    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,


    };
    public interface Callback {
        void onItemSelected(Uri Uri);
    }

    public MovieFragment() {
        MovieBus.getInstance().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        movieAdapter = new MovieAdapter(getActivity(),null);
        movieAdapter.notifyDataSetChanged();

        View rootView = inflater.inflate(R.layout.movie_fragment, container, false);
        mGridView = (GridView) rootView.findViewById(R.id.gridview);
        movieProgressDialog = new ProgressDialog(getActivity());
        movieProgressDialog.setTitle("Loading Posters");
        movieProgressDialog.setMessage("Loading ..");
        movieProgressDialog.show();
        mGridView.setAdapter(movieAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                String sortby = Utility.getPreferredSortSetting(getContext());
                ((Callback) getActivity())
                        .onItemSelected(MovieContract.MovieEntry.buildMovieSortWithMovieId(
                                sortby, cursor.getInt(COL_MOVIE_MOVIE_ID)
                        ));
                mPosition = position;
            }
        });
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }
        return rootView;
    }

    @Override
    public void onDestroy(){
        MovieBus.getInstance().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
     if (mPosition != GridView.INVALID_POSITION) {
         outState.putInt(SELECTED_KEY, mPosition);
     }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);

    }

    void onSortSettingChanged( ) {
        if(!Utility.getPreferredSortSetting(getContext()).equals("favorite")) {
            updateMovie();
        }
        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
        mPosition = GridView.INVALID_POSITION;
    }


    private void updateMovie(){
        EventExecutor executor = new EventExecutor(getContext());
        MovieDataUpdator movieDataUpdator = new MovieDataUpdator(getContext());
        String sortBy = Utility.getPreferredSortSetting(getActivity());
        GetMovieDataEvent getMovieDataEvent = new GetMovieDataEvent();
        getMovieDataEvent.setSortBy(sortBy);
        MovieBus.getInstance().post(getMovieDataEvent);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortSetting = Utility.getPreferredSortSetting(getActivity());

        String sortOrder = MovieContract.MovieEntry.COLUMN_MOVIE_ID + " ASC";
        Uri movieForSortSettingUri = MovieContract.MovieEntry.buildMovieSort(
                sortSetting);
        return new CursorLoader(getActivity(),movieForSortSettingUri,MOVIE_COLUMNS,null,null,sortOrder);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        movieProgressDialog.hide();
        if(data == null || data.getCount()==0){
            Toast.makeText(getContext(),"No data available, Try changing the Sort Order",
                    Toast.LENGTH_SHORT).show();
        }
        movieAdapter.swapCursor(data);
        if (mPosition != GridView.INVALID_POSITION) {
            mGridView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        movieAdapter.swapCursor(null);
    }

   @Subscribe
    public void getMovieUpdateEvent(MovieUpdateSuccessEvent event){
       View detailsFrame = getActivity().findViewById(R.id.movie_detail_container);
       Boolean mDualPane;
       mDualPane = detailsFrame != null
               && detailsFrame.getVisibility() == View.VISIBLE;
       String sortby = Utility.getPreferredSortSetting(getContext());
        if (mDualPane && mPosition == GridView.INVALID_POSITION ){
           mGridView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
           Cursor cursor = (Cursor) movieAdapter.getItem(0);

           ((Callback) getActivity())
                   .onItemSelected(MovieContract.MovieEntry.buildMovieSortWithMovieId(
                           sortby, cursor.getInt(COL_MOVIE_MOVIE_ID)
                   ));
           mPosition = 0;
       }
    }

}
