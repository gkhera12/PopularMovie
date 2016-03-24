package com.example.eightleaves.popularmovie.models;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.example.eightleaves.popularmovie.data.MovieContract;
import com.example.eightleaves.popularmovie.event.GetMovieDataResultEvent;
import com.example.eightleaves.popularmovie.event.MarkFavouriteEvent;
import com.example.eightleaves.popularmovie.event.MovieUpdateSuccessEvent;
import com.example.eightleaves.popularmovie.otto.MovieBus;
import com.squareup.otto.Subscribe;

import java.util.Vector;

/**
 * Created by gkhera on 21/03/2016.
 */
public class MovieDataUpdator {
    private Context mContext;

    public MovieDataUpdator(Context context) {
        mContext = context;
        MovieBus.getInstance().register(this);
    }

    @Subscribe
    public void updateMovieData(GetMovieDataResultEvent event){
        addMovieData(event.getMovieResults(), event.getSortBy());
        MovieBus.getInstance().post(new MovieUpdateSuccessEvent());
    }

    private long addSortSetting(String sortSetting) {
        long sortSettingId;
        Cursor cur = mContext.getContentResolver().query(
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

            Uri sortSettingUri = mContext.getContentResolver().insert(
                    MovieContract.SortEntry.CONTENT_URI, values);
            sortSettingId = ContentUris.parseId(sortSettingUri);
        }
        return sortSettingId;
    }

    private void addMovieData(MovieResults movieResults, String sortBy) {
        Vector<ContentValues> cVVector = new Vector<>(movieResults.getResults().size());
        for(int i = 0; i < movieResults.getResults().size(); i++) {
            String posterPath;
            String overview;
            String releaseDate;
            String title;
            String voteAverage;
            String id;
            long sortId = addSortSetting(sortBy);
            Movie movieItem = movieResults.getResults().get(i);

            posterPath = movieItem.getPosterPath();
            overview = movieItem.getOverview();
            releaseDate = movieItem.getReleaseDate();
            title = movieItem.getTitle();
            voteAverage = Double.toString(movieItem.getVoteAverage());
            id = Integer.toString(movieItem.getId());

            ContentValues movieValues = new ContentValues();

            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, id);
            movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, posterPath);
            movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, overview);
            movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, title);
            movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, releaseDate);
            movieValues.put(MovieContract.MovieEntry.COLUMN_SORT_KEY, sortId);
            movieValues.put(MovieContract.MovieEntry.COLUMN_RATING, voteAverage);
            cVVector.add(movieValues);
        }
        int inserted = 0;
        // add to database
        if (cVVector.size() > 0) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            inserted = mContext.getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cvArray);
        }

        Log.d("Popular MOvie", "FetchMovie Complete. " + inserted + " Inserted");

    }

    @Subscribe
    public void getMarkFavoriteEvent(MarkFavouriteEvent event){
        long sortId = addSortSetting(event.getSortBy());
        ContentValues movieValues = new ContentValues();

        movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, event.getId());
        movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH,event.getPosterPath());
        movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, event.getOverview());
        movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, event.getTitle());
        movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, event.getReleaseDate());
        movieValues.put(MovieContract.MovieEntry.COLUMN_SORT_KEY, sortId);
        movieValues.put(MovieContract.MovieEntry.COLUMN_RATING, event.getVoteAverage());

        Uri inserted = mContext.getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, movieValues);
        if(inserted != null)
        {
            Toast.makeText(mContext, "Favourite Movie Added", Toast.LENGTH_SHORT).show();
        }
    }

    public void onDestroy(){
        MovieBus.getInstance().unregister(this);
    }
}
