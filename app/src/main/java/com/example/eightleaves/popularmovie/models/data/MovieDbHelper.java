package com.example.eightleaves.popularmovie.models.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.eightleaves.popularmovie.models.data.MovieContract.MovieEntry;
import com.example.eightleaves.popularmovie.models.data.MovieContract.SortEntry;
/**
 * Created by gkhera on 19/02/2016.
 */
class MovieDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION=2;
    static final String DATABASE_NAME = "movie.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_SORT_TABLE = "CREATE TABLE " + SortEntry.TABLE_NAME + "(" +
                SortEntry._ID +" INTEGER PRIMARY KEY,"+
                SortEntry.COLUMN_SORT_SETTING + " TEXT NOT NULL" + ")";

        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + "(" +
                MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                MovieEntry.COLUMN_SORT_KEY + " INTEGER NOT NULL, "+
                MovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, "+
                MovieEntry.COLUMN_TITLE +" TEXT NOT NULL, "+
                MovieEntry.COLUMN_OVERVIEW +" TEXT NOT NULL, "+
                MovieEntry.COLUMN_POSTER_PATH +" TEXT NOT NULL, "+
                MovieEntry.COLUMN_RELEASE_DATE +" TEXT NOT NULL, "+
                MovieEntry.COLUMN_RATING +" TEXT NOT NULL, "+

                " FOREIGN KEY (" + MovieEntry.COLUMN_SORT_KEY + ") REFERENCES " +
                SortEntry.TABLE_NAME + " (" + SortEntry._ID + "), " +

                // To assure the application have just one movie entry per sort key
                //it's created a UNIQUE constraint with REPLACE strategy
                " UNIQUE (" + MovieEntry.COLUMN_MOVIE_ID + ","
                + MovieEntry.COLUMN_SORT_KEY + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_SORT_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SortEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
