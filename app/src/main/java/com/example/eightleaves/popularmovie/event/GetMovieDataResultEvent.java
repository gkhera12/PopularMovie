package com.example.eightleaves.popularmovie.event;

import com.example.eightleaves.popularmovie.models.MovieResults;

/**
 * Created by gkhera on 21/03/2016.
 */
public class GetMovieDataResultEvent {
    private MovieResults movieResults;

    private String sortBy;

    public MovieResults getMovieResults() {
        return movieResults;
    }

    public void setMovieResults(MovieResults movieResults) {
        this.movieResults = movieResults;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

}
