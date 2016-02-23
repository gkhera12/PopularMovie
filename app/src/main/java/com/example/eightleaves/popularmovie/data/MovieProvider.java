package com.example.eightleaves.popularmovie.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;


/**
 * Created by gkhera on 19/02/2016.
 */
public class MovieProvider extends ContentProvider{

    private static final SQLiteQueryBuilder sMovieBySortSettingQueryBuilder;
    private MovieDbHelper movieDbHelper;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    static final int MOVIE = 200;
    static final int MOVIE_WITH_SORT_SETTING = 201;
    static final int MOVIE_WITH_SORT_SETTING_AND_MOVIE_ID = 202;
    static final int SORT_SETTING = 300;

    static{
        sMovieBySortSettingQueryBuilder = new SQLiteQueryBuilder();

        sMovieBySortSettingQueryBuilder.setTables(
                MovieContract.MovieEntry.TABLE_NAME + " INNER JOIN " +
                        MovieContract.SortEntry.TABLE_NAME +
                        " ON " + MovieContract.MovieEntry.TABLE_NAME +
                        "." + MovieContract.MovieEntry.COLUMN_SORT_KEY +
                        " = " + MovieContract.SortEntry.TABLE_NAME +
                        "." + MovieContract.SortEntry._ID);
    }

    //sort.sort_setting = ?
    private static final String sSortSettingSelection =
            MovieContract.SortEntry.TABLE_NAME+
                    "." + MovieContract.SortEntry.COLUMN_SORT_SETTING + " = ? ";

    //sort.sort_setting = ? AND movie_id = ?
    private static final String sSortSettingAndMovieSelection =
            MovieContract.SortEntry.TABLE_NAME +
                    "." + MovieContract.SortEntry.COLUMN_SORT_SETTING + " = ? AND " +
                    MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ? ";

    private Cursor getMovieBySortSetting(Uri uri, String[] projection, String sortOrder) {
        String sortSetting = MovieContract.MovieEntry.getSortSettingFromUri(uri);
        String[] selectionArgs;
        String selection;

        selection = sSortSettingSelection;
        selectionArgs = new String[]{sortSetting};


        return sMovieBySortSettingQueryBuilder.query(movieDbHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getMovieBySortSettingAndMovieId(
            Uri uri, String[] projection, String sortOrder) {
        String sortSetting = MovieContract.MovieEntry.getSortSettingFromUri(uri);
        long movieId = MovieContract.MovieEntry.getMovieIdFromUri(uri);

        return sMovieBySortSettingQueryBuilder.query(movieDbHelper.getReadableDatabase(),
                projection,
                sSortSettingAndMovieSelection,
                new String[]{sortSetting, Long.toString(movieId)},
                null,
                null,
                sortOrder
        );
    }

    static UriMatcher buildUriMatcher() {
        UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sURIMatcher.addURI(MovieContract.CONTENT_AUTHORITY,MovieContract.PATH_MOVIE,MOVIE);
        sURIMatcher.addURI(MovieContract.CONTENT_AUTHORITY,MovieContract.PATH_MOVIE+"/*",MOVIE_WITH_SORT_SETTING);
        sURIMatcher.addURI(MovieContract.CONTENT_AUTHORITY,MovieContract.PATH_MOVIE+"/*/#",MOVIE_WITH_SORT_SETTING_AND_MOVIE_ID);
        sURIMatcher.addURI(MovieContract.CONTENT_AUTHORITY,MovieContract.PATH_SORT_SETTING,SORT_SETTING);
        return sURIMatcher;
    }


    @Override
    public boolean onCreate() {
        movieDbHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = movieDbHelper.getReadableDatabase();

        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "movie/*/*"
            case MOVIE_WITH_SORT_SETTING_AND_MOVIE_ID:
            {
                retCursor = getMovieBySortSettingAndMovieId(uri, projection, sortOrder);
                break;
            }
            // "movie/*"
            case MOVIE_WITH_SORT_SETTING: {
                retCursor = getMovieBySortSetting(uri, projection, sortOrder);
                break;
            }
            // "movie"
            case MOVIE: {
                retCursor = db.query(MovieContract.MovieEntry.TABLE_NAME,
                        projection,selection,selectionArgs,null, null, sortOrder);
                break;
            }
            // "sortsetting"
            case SORT_SETTING: {
                retCursor = db.query(MovieContract.SortEntry.TABLE_NAME,
                        projection,selection,selectionArgs,null, null, sortOrder);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIE_WITH_SORT_SETTING_AND_MOVIE_ID:
                return  MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            case MOVIE_WITH_SORT_SETTING:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case MOVIE:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case SORT_SETTING:
                return MovieContract.SortEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MOVIE: {

                long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.MovieEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case SORT_SETTING: {
                long _id = db.insert(MovieContract.SortEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.SortEntry.buildSortSettingUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        db.close();
        return returnUri;

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int returnRows;
        if (selection == null){selection="1";}
        switch (match) {
            case MOVIE: {
                returnRows = db.delete(MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case SORT_SETTING: {
                returnRows = db.delete(MovieContract.SortEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if(returnRows !=0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        db.close();
        return returnRows;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int returnRows;
        switch (match) {
            case MOVIE: {
                returnRows = db.update(MovieContract.MovieEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case SORT_SETTING: {
                returnRows = db.update(MovieContract.SortEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if(returnRows !=0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        db.close();
        return returnRows;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIE:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        movieDbHelper.close();
        super.shutdown();
    }
}
