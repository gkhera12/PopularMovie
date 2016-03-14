package com.example.eightleaves.popularmovie;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.eightleaves.popularmovie.data.MovieContract;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, TasksInterface, View.OnClickListener {
    private static final int DETAIL_LOADER=1;
    private ImageView imageView;
    private TextView titleText;
    private TextView overviewText;
    private TextView ratingText;
    private TextView releaseDateText;
    private RecyclerView trailersListView;
    private RecyclerView reviewListView;
    private TrailerAdapter trailerAdapter;
    private ReviewAdapter reviewAdapter;
    private List<Trailer> trailerList;
    private List<Review> reviewList;

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
        trailersListView = (RecyclerView)rootView.findViewById(R.id.list_item_movie_trailers_list);
        reviewListView = (RecyclerView)rootView.findViewById(R.id.list_item_movie_reviews_list);
        return rootView;
    }

    private void setupTrailerRecyclerView() {
        TrailerAdapter adapter = new TrailerAdapter(this.getContext(),trailerList);
        trailersListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        /*This solution is taken To get Recycler View height dynamically
        http://stackoverflow.com/questions/32337403/making-recyclerview-fixed-height-and-scrollable*/
        trailersListView.setLayoutManager(new MyLinearLayoutManager(this.getContext(),1,false));
        //trailersListView.setLayoutManager(new LinearLayoutManager(this.getContext()));
    }

    private void setupReviewRecyclerView() {
        ReviewAdapter adapter = new ReviewAdapter(this.getContext(),reviewList);
        reviewListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        /*This solution is taken To get Recycler View height dynamically
        http://stackoverflow.com/questions/32337403/making-recyclerview-fixed-height-and-scrollable*/
        reviewListView.setLayoutManager(new MyLinearLayoutManager(this.getContext(),1,false));
        //trailersListView.setLayoutManager(new LinearLayoutManager(this.getContext()));
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
            String movieId = String.valueOf(data.getLong(COL_MOVIE_MOVIE_ID));
            FetchTrailersTask trailersTask = new FetchTrailersTask(getActivity(),this);
            trailersTask.execute(movieId);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onTaskCompleted(List<Object> result) {
        trailerList = (List)((TrailersResult)result.get(0)).getResults();
        reviewList = (List)((ReviewResults)result.get(1)).getResults();
        if(getActivity()!= null) {
            if (!trailerList.isEmpty() && trailerList != null) {
                setupTrailerRecyclerView();
            }
        }
        if(getActivity()!= null) {
            if (!trailerList.isEmpty() && trailerList != null) {
                setupReviewRecyclerView();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
         }
    }
}
