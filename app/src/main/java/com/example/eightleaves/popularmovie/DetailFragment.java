package com.example.eightleaves.popularmovie;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.eightleaves.popularmovie.adapters.ReviewAdapter;
import com.example.eightleaves.popularmovie.adapters.TrailerAdapter;
import com.example.eightleaves.popularmovie.data.MovieContract;
import com.example.eightleaves.popularmovie.event.EventExecutor;
import com.example.eightleaves.popularmovie.event.GetReviewsResultEvent;
import com.example.eightleaves.popularmovie.event.GetTrailersAndReviewsEvent;
import com.example.eightleaves.popularmovie.event.GetTrailersResultEvent;
import com.example.eightleaves.popularmovie.event.MarkFavouriteEvent;
import com.example.eightleaves.popularmovie.models.MovieDataUpdator;
import com.example.eightleaves.popularmovie.models.Review;
import com.example.eightleaves.popularmovie.models.Trailer;
import com.example.eightleaves.popularmovie.otto.MovieBus;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener{
    static final String DETAIL_URI = "URI";
    private Uri mUri;
    private static final int DETAIL_LOADER=1;
    private ImageView imageView;
    private TextView titleText;
    private TextView overviewText;
    private TextView ratingText;
    private TextView releaseDateText;
    private RecyclerView trailersListView;
    private RecyclerView reviewListView;
    private ArrayList<Trailer> trailerList;
    private ArrayList<Review> reviewList;
    private Cursor mCursor;
    private ShareActionProvider mShareActionProvider;
    private Intent mShareIntent;
    private EventExecutor executor;
    private MovieDataUpdator movieDataUpdator;
    private static final String TRAILERS_KEY = "trailers";
    private static final String REVIEWS_KEY = "reviews";

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
        MovieBus.getInstance().register(this);

    }

    @Override
    public void onDestroy(){
        MovieBus.getInstance().unregister(this);
        super.onDestroy();
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DetailFragment.DETAIL_URI);
        }
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        imageView = (ImageView)rootView.findViewById(R.id.list_item_movie_image);
        titleText = (TextView) rootView.findViewById(R.id.list_item_movie_title);
        overviewText = (TextView) rootView.findViewById(R.id.list_item_movie_overview);
        ratingText = (TextView) rootView.findViewById(R.id.list_item_movie_rating);
        releaseDateText = (TextView)rootView.findViewById(R.id.list_item_movie_year);
        trailersListView = (RecyclerView)rootView.findViewById(R.id.list_item_movie_trailers_list);
        reviewListView = (RecyclerView)rootView.findViewById(R.id.list_item_movie_reviews_list);
        Button favoriteButton = (Button) rootView.findViewById(R.id.list_item_movie_favorite);
        favoriteButton.setOnClickListener(this);

        if(savedInstanceState != null && savedInstanceState.containsKey(TRAILERS_KEY)
                && savedInstanceState.containsKey(REVIEWS_KEY)){
            trailerList = savedInstanceState.getParcelableArrayList(TRAILERS_KEY);
            reviewList = savedInstanceState.getParcelableArrayList(REVIEWS_KEY);
            setupReviewRecyclerView();
            setupTrailerRecyclerView();
        }
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(TRAILERS_KEY, trailerList);
        outState.putParcelableArrayList(REVIEWS_KEY, reviewList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.share_trailer, menu);
        MenuItem item = menu.findItem(R.id.menu_item_share);

        // Fetch and store ShareActionProvidermShareActionProvider = new ShareActionProvider();
        mShareActionProvider = new ShareActionProvider(getContext());
        MenuItemCompat.setActionProvider(item, mShareActionProvider);
             //   mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_item_share:
                createShareIntent();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void createShareIntent() {
        mShareIntent = new Intent();
        mShareIntent.setAction(Intent.ACTION_SEND);
        mShareIntent.setType("text/plain");
        mShareIntent.putExtra(Intent.EXTRA_TEXT,
                Utility.formatLink(getContext(), trailerList.get(0).getKey()));
        mShareActionProvider.setShareIntent(mShareIntent);
    }


    private void setupTrailerRecyclerView() {
        TrailerAdapter trailerAdapter = new TrailerAdapter(this.getContext(), trailerList);
        trailersListView.setAdapter(trailerAdapter);

        /*This solution is taken To get Recycler View height dynamically
        http://stackoverflow.com/questions/32337403/making-recyclerview-fixed-height-and-scrollable*/
        trailersListView.setLayoutManager(new MyLinearLayoutManager(this.getContext(), 1, false));
       // trailersListView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        trailerAdapter.notifyDataSetChanged();
    }

    private void setupReviewRecyclerView() {
        ReviewAdapter reviewAdapter = new ReviewAdapter(this.getContext(), reviewList);
        reviewListView.setAdapter(reviewAdapter);
       // reviewAdapter.notifyDataSetChanged();
        /*This solution is taken To get Recycler View height dynamically
        http://stackoverflow.com/questions/32337403/making-recyclerview-fixed-height-and-scrollable*/
       reviewListView.setLayoutManager(new MyLinearLayoutManager(this.getContext(),1,false));
        //reviewListView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        reviewAdapter.notifyDataSetChanged();
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if ( null != mUri ) {
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    MOVIE_DETAIL_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data !=null && data.moveToFirst()) {
            mCursor = data;
            String title = data.getString(COL_MOVIE_TITLE);
            titleText.setText(title);
            String overview = data.getString(COL_MOVIE_OVERVIEW);
            overviewText.setText(overview);
            String rating = data.getString(COL_MOVIE_RATING);
            ratingText.setText(getContext().getString(R.string.format_rating, rating));
            String releaseDate = Utility.getYearFromDate(data.getString(COL_MOVIE_RELEASE_DATE));
            releaseDateText.setText(releaseDate);
            String posterPath = data.getString(COL_MOVIE_POSTER_PATH);
            String imageUrl = Utility.getImageUrl(posterPath);
            Picasso.with(getActivity()).load(imageUrl)
                    .placeholder(R.mipmap.ic_launcher).into(imageView);
            String movieId = String.valueOf(data.getLong(COL_MOVIE_MOVIE_ID));
            if(trailerList == null || reviewList == null){
                getTrailersAndReviews(movieId);
            }
        }
    }

    private void getTrailersAndReviews(String movieId){
        movieDataUpdator = new MovieDataUpdator(getContext());
        executor = new EventExecutor(getContext());
        GetTrailersAndReviewsEvent event = new GetTrailersAndReviewsEvent();
        event.setMovieId(movieId);
        MovieBus.getInstance().post(event);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Subscribe
    public void getTrailersResult(GetTrailersResultEvent event){
        trailerList = event.getTrailersResult().getResults();
        if (getActivity() != null) {
            if (!trailerList.isEmpty() && trailerList != null) {
                 setupTrailerRecyclerView();
            }
        if(mShareActionProvider!=null && !trailerList.isEmpty()) {
            createShareIntent();
            mShareActionProvider.setShareIntent(mShareIntent);
            }
        }
    }

    @Subscribe
    public void getReviewsResult(GetReviewsResultEvent event){
        reviewList = event.getReviewResults().getResults();
        if (getActivity() != null) {
            if (!reviewList.isEmpty() && reviewList != null) {
                setupReviewRecyclerView();
            }
        }
    }


    private long addSortSetting(String sortSetting) {
        long sortSettingId;
        Cursor cur = this.getContext().getContentResolver().query(
                MovieContract.SortEntry.CONTENT_URI,
                new String[]{MovieContract.SortEntry._ID},
                MovieContract.SortEntry.COLUMN_SORT_SETTING + "=?",
                new String[]{sortSetting},
                null);

        if (cur != null && cur.moveToFirst()) {
            int sortIndex = cur.getColumnIndex(MovieContract.SortEntry._ID);
            sortSettingId = cur.getLong(sortIndex);
            cur.close();
        } else {
            ContentValues values = new ContentValues();
            values.put(MovieContract.SortEntry.COLUMN_SORT_SETTING, sortSetting);

            Uri sortSettingUri = this.getContext().getContentResolver().insert(
                    MovieContract.SortEntry.CONTENT_URI, values);
            sortSettingId = ContentUris.parseId(sortSettingUri);
        }
        return sortSettingId;
    }

    @Override
    public void onClick(View v) {
        movieDataUpdator = new MovieDataUpdator(getContext());
        executor = new EventExecutor(getContext());
        MarkFavouriteEvent event = new MarkFavouriteEvent();
        event.setSortBy("favorite");
        event.setPosterPath(mCursor.getString(COL_MOVIE_POSTER_PATH));
        event.setOverview(mCursor.getString(COL_MOVIE_OVERVIEW));
        event.setReleaseDate(mCursor.getString(COL_MOVIE_RELEASE_DATE));
        event.setTitle(mCursor.getString(COL_MOVIE_TITLE));
        event.setVoteAverage(mCursor.getString(COL_MOVIE_RATING));
        event.setId(mCursor.getString(COL_MOVIE_MOVIE_ID));
        MovieBus.getInstance().post(event);
    }

    public void onSortSettingChanged(String sortSetting) {
        if(null != mUri){
            long movieId =  MovieContract.MovieEntry.getMovieIdFromUri(mUri);
            mUri = MovieContract.MovieEntry.buildMovieSortWithMovieId(sortSetting,movieId);
            getLoaderManager().restartLoader(DETAIL_LOADER,null,this);
        }
    }
}
