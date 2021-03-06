package com.example.eightleaves.popularmovie.models.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by gkhera on 19/02/2016.
 */
public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.example.eightleaves.popularmovie";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MOVIE = "movie";
    public static final String PATH_SORT_SETTING = "sort";

    public static final class SortEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SORT_SETTING).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SORT_SETTING;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SORT_SETTING;

        public static Uri buildSortSettingUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static final String TABLE_NAME = "sort";
        public static final String COLUMN_SORT_SETTING = "sort_setting";
    }


    public static final class MovieEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;



        public static final String TABLE_NAME = "movie";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_OVERVIEW =  "overview";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_SORT_KEY = "sort_id";
        public static final String COLUMN_RATING = "user_rating";

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildMovieSort(String sortSetting) {
            return CONTENT_URI.buildUpon().appendPath(sortSetting).build();
        }

        public static Uri buildMovieSortWithMovieId(String sortSetting, long movieId) {
            return CONTENT_URI.buildUpon().appendPath(sortSetting)
                    .appendPath(Long.toString(movieId)).build();
        }

        public static String getSortSettingFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static long getMovieIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(2));
        }
    }
}
