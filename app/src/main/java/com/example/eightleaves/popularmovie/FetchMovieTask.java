package com.example.eightleaves.popularmovie;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.eightleaves.popularmovie.data.MovieContract;
import com.google.gson.Gson;

import java.util.Vector;

import retrofit.RestAdapter;

import retrofit.converter.GsonConverter;

/**
 * Created by gkhera on 16/02/2016.
 */
class FetchMovieTask extends AsyncTask<String,Void, Void> {

    private final String MOVIE_BASE_URL = "http://api.themoviedb.org/3";
    private final Context mContext;
    private RestAdapter restAdapter;
    public FetchMovieTask(Context context){
            mContext = context;
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

    @Override
    protected void onPreExecute (){
        Gson gson = new Gson();
        restAdapter = new RestAdapter.Builder()
                .setEndpoint(MOVIE_BASE_URL)
                .setConverter(new GsonConverter(gson))
                .build();

    }
        protected Void doInBackground(String... params) {
            String sortBy = params[0];
            int numOfPage =1;
            MovieResults movieResults;
            MovieApiMethods movieApiMethods = restAdapter.create(MovieApiMethods.class);
            movieResults = movieApiMethods.getMovieData(BuildConfig.THE_MOVIE_DB_API_KEY,
                    sortBy,Integer.toString(numOfPage));
            addMovieData(movieResults, sortBy);
            return null;
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

}

