package com.example.eightleaves.popularmovie.event;

/**
 * Created by gkhera on 18/03/2016.
 */
public class GetTrailersAndReviewsEvent {
    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    private String movieId;
}
