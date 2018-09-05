package com.example.oren.moviesdb.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.oren.moviesdb.beans.Movie;

import java.util.ArrayList;

public class MovieDBHelper extends SQLiteOpenHelper {

    //  Constants //////////////////////////////////////////////////////////

    private final static String TAG = MovieDBHelper.class.getName();

    //TODO: extract to a constants class
    // Database Info
    private static final String DATABASE_NAME = "MoviesDatabase";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_NAME = "movies";

    // Movie Table Columns
     private static final String COL_ID = "id";
    private static final String COL_NAME = "title";
    private static final String COL_OVERVIEW = "overview";
    private static final String COL_IMAGE = "imagePath";
    private static final String COL_BACKGROUND = "backgroundPath";
    private static final String COL_WATCHED = "watched";
    private static final String COL_RATING = "rating";


    //  Singleton Pattern /////////////////////////////////////////////////
    private static MovieDBHelper sInstance;

    public static synchronized MovieDBHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new MovieDBHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    //  Constructors //////////////////////////////////////////////////////

    private MovieDBHelper(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

//    private MovieDBHelper(Context context,
//                         String name,
//                         SQLiteDatabase.CursorFactory factory,
//                         int version) {
//        super(context, name, factory, version);
//    }

    // Overrides for create & upgrade ////////////////////////////////////
    /*  create the Data Base. */

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_MOVIE_TABLE =
                "CREATE TABLE " + TABLE_NAME +
                    " ( " +
                        COL_ID         + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        COL_NAME       + " TEXT, " +
                        COL_OVERVIEW   + " TEXT, " +
                        COL_IMAGE      + " TEXT, " +
                        COL_BACKGROUND + " TEXT, " +
                        COL_RATING     + " REAL, " +
                        COL_WATCHED    + " INTEGER " +
                     ");";
        db.execSQL(CREATE_MOVIE_TABLE);
    }

    /*  Standard boilerplate implementation of onUpgrade. */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String DROP_TABLE = String.format("DROP TABLE IF EXISTS %s;", MovieDBHelper.TABLE_NAME);
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }

    //TODO: Extract DAO operations to a DAO class

    // DAO CRUD /////////////////////////////////////////////////////////////

    /**
     * Insert movie into Database.
     * <p>
     * TODO: move off main thread
     *
     * @param movie to create
     */
    public long create(Movie movie) {

        long insert = 0;

        try (SQLiteDatabase db = getWritableDatabase()) {

            ContentValues values = new ContentValues();
            values.put(COL_NAME, movie.getTitle());
            values.put(COL_OVERVIEW, movie.getOverview());
            values.put(COL_IMAGE, movie.getImagePath());
            values.put(COL_BACKGROUND, movie.getBackgroundPath());
            values.put(COL_RATING, movie.getRating());
            values.put(COL_WATCHED, movie.isWatched() ? 1 : 0);

            insert = db.insert(TABLE_NAME, null, values);

        } catch (Exception e) {
            Log.i(TAG, "create: failed to create: " + insert, e);
        }
        return insert;
    }

    /**
     * Read movies from Database.
     * <p>
     * TODO: move off main thread
     *
     * @return list of movies.
     */
    public ArrayList<Movie> read() {
        ArrayList<Movie> movies = new ArrayList<>();

        try (SQLiteDatabase db = getReadableDatabase();
             Cursor cursor = db.query(TABLE_NAME, null,
                     null, null,
                     null, null, null)){

            while (cursor.moveToNext()) {
                Movie movie = new Movie();
                movie.setId(cursor.getLong(cursor.getColumnIndex(COL_ID)));
                movie.setTitle(cursor.getString(cursor.getColumnIndex(COL_NAME)));
                movie.setOverview(cursor.getString(cursor.getColumnIndex(COL_OVERVIEW)));
                movie.setImagePath(cursor.getString(cursor.getColumnIndex(COL_IMAGE)));
                movie.setBackgroundPath(cursor.getString(cursor.getColumnIndex(COL_BACKGROUND)));
                movie.setRating(cursor.getInt(cursor.getColumnIndex(COL_RATING)));
                movie.setWatched(cursor.getInt(cursor.getColumnIndex(COL_WATCHED)) == 1);
                movies.add(movie);
            }
        }
        return movies;

    }


    /**
     * Update movie in Database.
     * <p>
     * TODO: move off main thread
     *
     * @param movie to update
     */
    public int update(Movie movie) {
        int update=0;
        try (SQLiteDatabase db = getWritableDatabase()) {

            ContentValues values = new ContentValues();

            values.put(COL_NAME, movie.getTitle());
            values.put(COL_OVERVIEW, movie.getOverview());
            values.put(COL_IMAGE, movie.getImagePath());
            values.put(COL_BACKGROUND, movie.getBackgroundPath());
            values.put(COL_RATING, movie.getRating());
            values.put(COL_WATCHED, movie.isWatched() ? 1 : 0);

            String[] args = {String.valueOf(movie.getId())};

            update = db.update(TABLE_NAME, values, COL_ID + "=?", args);

        } catch (Exception e) {
            Log.i(TAG, "updateMovie: failed to update " + movie ,e);
        }
        return update;
    }


    /**
     * Delete movie from the Database.
     * <p>
     * TODO: move off main thread
     *
     * @param ID to delete
     * @return rows affected
     */
    public int delete(long ID) {
        int rows = 0;
        try (SQLiteDatabase db = getWritableDatabase()) {
            rows = db.delete(
                    MovieDBHelper.TABLE_NAME,
                    MovieDBHelper.COL_ID + "=?",
                    new String[]{String.valueOf(ID)});
        } catch (Exception e) {
            Log.i(TAG, "delete: failed to delete " +  ID , e);
        }
        return rows;
    }

    /**
     * Delete all movies from the Database.
     * <p>
     * TODO: move off main thread
     *
     * @return rows affected
     */
    public int deleteAll() {
        int rows = 0;
        try (SQLiteDatabase db = getWritableDatabase()) {

            rows = db.delete(
                    MovieDBHelper.TABLE_NAME,
                    null,
                //    new String[]{"*"});
                    null);
        } catch (Exception e) {
            Log.i(TAG, "deleteAll: failed to delete " + rows + " rows", e);
        }
        return rows;
    }

    //TODO: testing - add a mock data generation method
}
