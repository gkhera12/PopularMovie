package com.example.eightleaves.popularmovie.event;

import android.content.Context;

import com.example.eightleaves.popularmovie.BuildConfig;
import com.example.eightleaves.popularmovie.MovieApiMethods;
import com.example.eightleaves.popularmovie.ReviewResults;
import com.example.eightleaves.popularmovie.TrailersResult;
import com.example.eightleaves.popularmovie.otto.MovieBus;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.otto.Subscribe;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;
import retrofit.http.GET;

/**
 * Created by gkhera on 18/03/2016.
 */
public class EventExecutor {
    private Context context;
    private RestAdapter restAdapter;
    private MovieApiMethods methods;
    final String MOVIE_BASE_URL = "http://api.themoviedb.org/3";
    public EventExecutor(Context context){
        this.context = context;
        MovieBus.getInstance().register(this);
        Gson gson = new GsonBuilder()
                .disableHtmlEscaping()
                .setPrettyPrinting()
                .serializeNulls()
                .create();
        restAdapter = new RestAdapter.Builder()
                .setEndpoint(MOVIE_BASE_URL)
                .setConverter(new GsonConverter(gson))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        //Implementation using Retrofit
        methods = restAdapter.create(MovieApiMethods.class);

    }

    @Subscribe
    public void getTrailersAndReviews(GetTrailersAndReviewsEvent event){
        getTrailers(event.getMovieId());
        getReviews(event.getMovieId());
    }

    private void getReviews(String movieId) {
        methods.getTrailers(movieId, BuildConfig.THE_MOVIE_DB_API_KEY,
                new Callback<TrailersResult>() {
                    @Override
                    public void success(TrailersResult result, Response response) {
                        GetTrailersResultEvent resultEvent = new GetTrailersResultEvent();
                        resultEvent.setTrailersResult(result);
                        MovieBus.getInstance().post(resultEvent);
                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });
    }

    private void getTrailers(String movieId) {
        methods.getReviews(movieId, BuildConfig.THE_MOVIE_DB_API_KEY, new Callback<ReviewResults>() {
            @Override
            public void success(ReviewResults reviewResults, Response response) {
                GetReviewsResultEvent resultEvent = new GetReviewsResultEvent();
                resultEvent.setReviewResults(reviewResults);
                MovieBus.getInstance().post(resultEvent);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

}
