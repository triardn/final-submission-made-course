package com.buruhkoding.secondsubmission.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.buruhkoding.secondsubmission.Movie;

import java.util.ArrayList;

import static android.provider.BaseColumns._ID;
import static com.buruhkoding.secondsubmission.db.DatabaseContract.FavoriteColumns.AVG_VOTE;
import static com.buruhkoding.secondsubmission.db.DatabaseContract.FavoriteColumns.DESC;
import static com.buruhkoding.secondsubmission.db.DatabaseContract.FavoriteColumns.IMG_URL;
import static com.buruhkoding.secondsubmission.db.DatabaseContract.FavoriteColumns.TITLE;
import static com.buruhkoding.secondsubmission.db.DatabaseContract.FavoriteColumns.VOTE_COUNT;

public class MovieHelper {
    private static final String DATABASE_TABLE = "favorite";
    private static DatabaseHelper databaseHelper;
    private static MovieHelper INSTANCE;
    private SQLiteDatabase database;

    private MovieHelper(Context context) {
        databaseHelper = new DatabaseHelper(context);
    }

    public static MovieHelper getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (SQLiteOpenHelper.class) {
                if (INSTANCE == null) {
                    INSTANCE = new MovieHelper(context);
                }
            }
        }

        return INSTANCE;
    }

    public void open() throws SQLException {
        database = databaseHelper.getWritableDatabase();
    }

    public void close() {
        databaseHelper.close();

        if (database.isOpen())
            database.close();
    }

    public ArrayList<Movie> getAllFavoriteMovies() {
        ArrayList<Movie> arrayList = new ArrayList<>();
        Cursor cursor = database.query(DATABASE_TABLE, null, null, null, null, null, _ID + " ASC", null);
        cursor.moveToFirst();

        Movie movie;
        if (cursor.getCount() > 0) {
            do {
                movie = new Movie();
                movie.setId(cursor.getInt(cursor.getColumnIndexOrThrow(_ID)));
                movie.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(TITLE)));
                movie.setDesc(cursor.getString(cursor.getColumnIndexOrThrow(DESC)));
                movie.setImg(cursor.getString(cursor.getColumnIndexOrThrow(IMG_URL)));
                movie.setVoteAverage(cursor.getDouble(cursor.getColumnIndexOrThrow(AVG_VOTE)));
                movie.setVoteCount(cursor.getInt(cursor.getColumnIndexOrThrow(VOTE_COUNT)));

                arrayList.add(movie);
                cursor.moveToNext();
            } while (!cursor.isAfterLast());
        }

        cursor.close();
        return arrayList;
    }

    public Movie getFavoriteMovie(int id) {
        Cursor cursor = database.query(DATABASE_TABLE, null, _ID + "=?", new String[]{ String.valueOf(id) }, null, null, null, null);

        Movie selectedMovie = null;
        if (cursor.getCount() != 0 && cursor.moveToFirst()) {
            selectedMovie = new Movie();

            selectedMovie.setId(cursor.getInt(cursor.getColumnIndexOrThrow(_ID)));
            selectedMovie.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(TITLE)));
            selectedMovie.setDesc(cursor.getString(cursor.getColumnIndexOrThrow(DESC)));
            selectedMovie.setImg(cursor.getString(cursor.getColumnIndexOrThrow(IMG_URL)));
            selectedMovie.setVoteAverage(cursor.getDouble(cursor.getColumnIndexOrThrow(AVG_VOTE)));
            selectedMovie.setVoteCount(cursor.getInt(cursor.getColumnIndexOrThrow(VOTE_COUNT)));
        }

        cursor.close();
        return selectedMovie;
    }

    public long insertMovie(Movie movie) {
        ContentValues args = new ContentValues();
        args.put(_ID, movie.getId());
        args.put(TITLE, movie.getTitle());
        args.put(DESC, movie.getDesc());
        args.put(IMG_URL, movie.getImage());
        args.put(AVG_VOTE, movie.getVoteAverage());
        args.put(VOTE_COUNT, movie.getVoteCount());

        return database.insert(DATABASE_TABLE, null, args);
    }

    public int deleteMovie(int id) {
        return database.delete(DATABASE_TABLE, _ID + " = '" + id + "'", null);
    }

    public Cursor queryByIdProvider(String id) {
        return database.query(DATABASE_TABLE, null
                , _ID + " = ?"
                , new String[]{id}
                , null
                , null
                , null
                , null);
    }

    public Cursor queryProvider() {
        return database.query(DATABASE_TABLE
                , null
                , null
                , null
                , null
                , null
                , _ID + " ASC");
    }

    public long insertProvider(ContentValues values) {
        return database.insert(DATABASE_TABLE, null, values);
    }

    public int updateProvider(String id, ContentValues values) {
        return database.update(DATABASE_TABLE, values, _ID + " = ?", new String[]{id});
    }

    public int deleteProvider(String id) {
        return database.delete(DATABASE_TABLE, _ID + " = ?", new String[]{id});
    }
}
