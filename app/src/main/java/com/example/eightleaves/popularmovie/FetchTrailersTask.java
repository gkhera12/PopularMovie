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
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Vector;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * Created by gkhera on 11/03/2016.
 */
public class FetchTrailersTask extends AsyncTask<String,Void, List<Object>> {

    final String MOVIE_BASE_URL = "http://api.themoviedb.org/3";
    private final Context mContext;
    private RestAdapter restAdapter;
    private TasksInterface listener;

    public FetchTrailersTask(Context context, TasksInterface listener) {
        mContext = context;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        Gson gson = new GsonBuilder()
                .disableHtmlEscaping()
                .setPrettyPrinting()
                .serializeNulls()
                .create();
        restAdapter = new RestAdapter.Builder()
                .setEndpoint(MOVIE_BASE_URL)
                .setConverter(new GsonConverter(gson))
                .build();
    }

    @Override
    protected void onPostExecute(List<Object> result){
        listener.onTaskCompleted(result);
    }

    protected List<Object> doInBackground(String... params) {
        String movieId = params[0];
        List<Object> objects = new ArrayList<>();
        //Implementation using Retrofit
        restAdapter.setLogLevel(RestAdapter.LogLevel.FULL);
        MovieApiMethods methods = restAdapter.create(MovieApiMethods.class);
        TrailersResult trailersResult = methods.getTrailers(movieId, BuildConfig.THE_MOVIE_DB_API_KEY);
        ReviewResults reviewResults = methods.getReviews(movieId,BuildConfig.THE_MOVIE_DB_API_KEY);
        objects.add(0, trailersResult);
        objects.add(1,reviewResults);
        return objects;
    }
}
