package com.example.eightleaves.popularmovie.event;

import com.example.eightleaves.popularmovie.models.ReviewResults;

/**
 * Created by gkhera on 18/03/2016.
 */
public class GetReviewsResultEvent {
    public ReviewResults getReviewResults() {
        return reviewResults;
    }

    public void setReviewResults(ReviewResults reviewResults) {
        this.reviewResults = reviewResults;
    }

    private ReviewResults reviewResults;
}
