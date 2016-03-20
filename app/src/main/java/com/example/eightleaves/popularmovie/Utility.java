package com.example.eightleaves.popularmovie;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by gkhera on 20/02/16.
 */
class Utility {
    public static String getPreferredSortSetting(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_sort_key),
                context.getString(R.string.pref_sort_default));
    }

    public static String getYearFromDate(String date){
        return date.split("-")[0];
    }

    public static String getImageUrl(String posterPath) {
        String baseUrl = "http://image.tmdb.org/t/p/";
        String size = "w185/";
        return baseUrl+size+posterPath;
    }

    public static String getTrailerNumber(Context context,int position){
        int formatId = R.string.format_trailer_number;
        return String.format(context.getString(
                formatId,
                position));

    }

    public static String formatLink(Context context, String key) {
        int formatId = R.string.format_youtube_link;
        String baseUrl = context.getResources().getString(R.string.youtube_link);
        return String.format(context.getString(formatId, baseUrl,key));
    }
}

