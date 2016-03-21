package com.example.eightleaves.popularmovie.event;

/**
 * Created by gkhera on 21/03/2016.
 */
public class GetMovieDataEvent {
    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    String sortBy;
}
