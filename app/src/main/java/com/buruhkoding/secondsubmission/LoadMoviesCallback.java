package com.buruhkoding.secondsubmission;

import android.database.Cursor;

import java.util.ArrayList;

public interface LoadMoviesCallback {
    void preExecute();
    void postExecute(Cursor notes);
}
