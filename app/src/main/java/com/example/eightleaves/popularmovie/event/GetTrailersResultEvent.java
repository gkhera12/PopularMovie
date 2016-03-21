package com.example.eightleaves.popularmovie.event;

import com.example.eightleaves.popularmovie.models.TrailersResult;

/**
 * Created by gkhera on 18/03/2016.
 */
public class GetTrailersResultEvent {
    public TrailersResult getTrailersResult() {
        return trailersResult;
    }

    public void setTrailersResult(TrailersResult trailersResult) {
        this.trailersResult = trailersResult;
    }

    private TrailersResult trailersResult;
}
