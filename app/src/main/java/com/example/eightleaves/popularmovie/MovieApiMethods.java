package com.example.eightleaves.popularmovie;

/**
 * Created by gkhera on 7/03/16.
 */

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface MovieApiMethods {
    @GET("/discover/movie")
    MovieResults getMovieData(@Query("api_key")String apiKey,@Query("sort_by") String sortBy,@Query("page") String pageNum);
    @GET("/movie/{id}/videos")
    TrailersResult getTrailers(@Path("id") String id,@Query("api_key")String apiKey);
    @GET("/movie/{id}/reviews")
    ReviewResults getReviews(@Path("id") String id,@Query("api_key")String apiKey);
}
