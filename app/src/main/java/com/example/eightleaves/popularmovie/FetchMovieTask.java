package com.example.eightleaves.popularmovie;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.eightleaves.popularmovie.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * Created by gkhera on 16/02/2016.
 */
class FetchMovieTask extends AsyncTask<String,Void, Void> {


    private final Context mContext;

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

        protected Void doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String movieJsonStr;
            String sortBy = params[0];
            int numOfPage =1;
            try {
                final String FORECAST_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
                final String API_KEY_PARAM = "api_key";
                final String SORT_PARAM = "sort_by";
                final String PAGE_PARAM = "page";

                Uri.Builder builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(API_KEY_PARAM,BuildConfig.THE_MOVIE_DB_API_KEY)
                        .appendQueryParameter(SORT_PARAM,sortBy)
                        .appendQueryParameter(PAGE_PARAM, Integer.toString(numOfPage));
                URL url = new URL(builtUri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
                if (inputStream == null) {

                    movieJsonStr = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {

                    buffer.append(line).append("\n");
                }

                if (buffer.length() == 0) {

                    movieJsonStr = null;
                }
                movieJsonStr = buffer.toString();
                try {
                    getMovieDataFromJson(movieJsonStr,sortBy);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                Log.e("MovieFragment", "Error ", e);

                movieJsonStr = null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("MovieFragment", "Error closing stream", e);
                    }
                }
            }
            return null;
        }

    private void getMovieDataFromJson(String movieJsonStr, String sortSetting)throws JSONException {
        final String TMDB_RESULTS = "results";
        final String TMDB_POSTER_PATH = "poster_path";
        final String TMDB_OVERVIEW = "overview";
        final String TMDB_RELEASE_DATE = "release_date";
        final String TMDB_TITLE = "title";
        final String TMDB_VOTE_AVERAGE = "vote_average";
        final String TMDB_ID = "id";
        JSONObject movieJson = new JSONObject(movieJsonStr);
        JSONArray movieArray = movieJson.getJSONArray(TMDB_RESULTS);
        Vector<ContentValues> cVVector = new Vector<>(movieArray.length());
        for(int i = 0; i < movieArray.length(); i++) {
            String posterPath;
            String overview;
            String releaseDate;
            String title;
            String voteAverage;
            String id;
            long sortId = addSortSetting(sortSetting);
            JSONObject movieItem = movieArray.getJSONObject(i);

            posterPath = movieItem.getString(TMDB_POSTER_PATH);
            overview = movieItem.getString(TMDB_OVERVIEW);
            releaseDate = movieItem.getString(TMDB_RELEASE_DATE);
            title = movieItem.getString(TMDB_TITLE);
            voteAverage = movieItem.getString(TMDB_VOTE_AVERAGE);
            id = movieItem.getString(TMDB_ID);

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

