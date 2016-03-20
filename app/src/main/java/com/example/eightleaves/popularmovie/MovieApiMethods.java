package com.example.eightleaves.popularmovie;

/**
 * Created by gkhera on 7/03/16.
 */

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface MovieApiMethods {
    @GET("/movie/{sort_by}")
    MovieResults getMovieData(@Path("sort_by") String sortBy,@Query("api_key")String apiKey,@Query("page") String pageNum);
    @GET("/movie/{id}/videos")
    void getTrailers(@Path("id") String id,@Query("api_key")String apiKey, Callback<TrailersResult> cb);
    @GET("/movie/{id}/reviews")
    void getReviews(@Path("id") String id,@Query("api_key")String apiKey, Callback<ReviewResults> cb);
}
