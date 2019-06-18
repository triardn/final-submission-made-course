package com.buruhkoding.secondsubmission.helper;

import android.database.Cursor;

import com.buruhkoding.secondsubmission.Movie;

import java.util.ArrayList;

import static android.provider.BaseColumns._ID;
import static com.buruhkoding.secondsubmission.db.DatabaseContract.FavoriteColumns.AVG_VOTE;
import static com.buruhkoding.secondsubmission.db.DatabaseContract.FavoriteColumns.DESC;
import static com.buruhkoding.secondsubmission.db.DatabaseContract.FavoriteColumns.IMG_URL;
import static com.buruhkoding.secondsubmission.db.DatabaseContract.FavoriteColumns.TITLE;
import static com.buruhkoding.secondsubmission.db.DatabaseContract.FavoriteColumns.VOTE_COUNT;

public class MappingHelper {

    public static ArrayList<Movie> mapCursorToArrayList(Cursor movieCursor) {
        ArrayList<Movie> movieList = new ArrayList<>();

        while (movieCursor.moveToNext()) {
            int id = movieCursor.getInt(movieCursor.getColumnIndexOrThrow(_ID));
            String title = movieCursor.getString(movieCursor.getColumnIndexOrThrow(TITLE));
            String description = movieCursor.getString(movieCursor.getColumnIndexOrThrow(DESC));
            String img = movieCursor.getString(movieCursor.getColumnIndexOrThrow(IMG_URL));
            int vote_count = movieCursor.getInt(movieCursor.getColumnIndexOrThrow(VOTE_COUNT));
            Double vote_avg = movieCursor.getDouble(movieCursor.getColumnIndexOrThrow(AVG_VOTE));
            movieList.add(new Movie(id, title, description, img, vote_count, vote_avg));
        }

        return movieList;
    }
}
