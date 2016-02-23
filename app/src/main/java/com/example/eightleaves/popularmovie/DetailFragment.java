package com.example.eightleaves.popularmovie;

import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.example.eightleaves.popularmovie.data.MovieContract;
import com.squareup.picasso.Picasso;


public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int DETAIL_LOADER=1;
    private ImageView imageView;
    private TextView titleText;
    private TextView overviewText;
    private TextView ratingText;
    private TextView releaseDateText;

    static final int COL_MOVIE_ID = 0;
    static final int COL_MOVIE_MOVIE_ID = 1;
    private static final int COL_MOVIE_POSTER_PATH = 2;
    private static final int COL_MOVIE_TITLE = 3;
    private static final int COL_MOVIE_RELEASE_DATE = 4;
    private static final int COL_MOVIE_OVERVIEW = 5;
    static final int COL_SORT_KEY = 6;
    private static final int COL_MOVIE_RATING = 7;
    static final int COL_SORT_SETTING = 8;
    private static final String[] MOVIE_DETAIL_COLUMNS = {
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_SORT_KEY,
            MovieContract.MovieEntry.COLUMN_RATING,
            MovieContract.SortEntry.COLUMN_SORT_SETTING
    };

    public DetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        imageView = (ImageView)rootView.findViewById(R.id.list_item_movie_image);
        titleText = (TextView) rootView.findViewById(R.id.list_item_movie_title);
        overviewText = (TextView) rootView.findViewById(R.id.list_item_movie_overview);
        ratingText = (TextView) rootView.findViewById(R.id.list_item_movie_rating);
        releaseDateText = (TextView)rootView.findViewById(R.id.list_item_movie_year);

        return rootView;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Intent intent = getActivity().getIntent();
        if(intent ==null){
            return null;
        }
        Uri mUri = Uri.parse(intent.getDataString());
        return new CursorLoader(getActivity(), mUri,MOVIE_DETAIL_COLUMNS,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data !=null && data.moveToFirst()) {
            String title = data.getString(COL_MOVIE_TITLE);
            titleText.setText(title);
            String overview = data.getString(COL_MOVIE_OVERVIEW);
            overviewText.setText(overview);
            String rating = data.getString(COL_MOVIE_RATING);
            ratingText.setText(getContext().getString(R.string.format_rating,rating));
            String releaseDate = Utility.getYearFromDate(data.getString(COL_MOVIE_RELEASE_DATE));
            releaseDateText.setText(releaseDate);
            String posterPath = data.getString(COL_MOVIE_POSTER_PATH);
            String imageUrl = Utility.getImageUrl(posterPath);
            Picasso.with(getActivity()).load(imageUrl)
                    .placeholder(R.mipmap.ic_launcher).into(imageView);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
