package com.example.eightleaves.popularmovie.otto;

import android.app.Activity;
import android.content.Context;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * Created by gkhera on 18/03/2016.
 */
public class MovieBus extends Bus {
    private static class MovieBusHolder {
        private static MovieBus MOVIE_BUS = new MovieBus(ThreadEnforcer.ANY);
    }
    public MovieBus (ThreadEnforcer enforcer){super (enforcer);}

    public static MovieBus getInstance(){ return MovieBusHolder.MOVIE_BUS;}
    public static void postOnUiThread(Context context, final Object event){
        Activity activity = (Activity)context;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getInstance().post(event);
            }
        });
    }
}
