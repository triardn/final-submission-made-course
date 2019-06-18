package com.buruhkoding.secondsubmission.app;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.buruhkoding.secondsubmission.Movie;
import com.buruhkoding.secondsubmission.R;
import com.buruhkoding.secondsubmission.db.DatabaseContract;
import com.buruhkoding.secondsubmission.db.MovieHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class StackRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private final List<Bitmap> mWidgetItems = new ArrayList<>();
    private final Context mContext;
    private MovieHelper movieHelper;
    private ArrayList<Movie> movies;
    private ArrayList<Movie> tempMovie = new ArrayList<>();
    Bitmap theBitmap = null;
    Cursor cursor;

    StackRemoteViewsFactory(Context context) {
        mContext = context;
    }

    @Override
    public void onCreate() {
        // cursor = mContext.getContentResolver().query(DatabaseContract.CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onDataSetChanged() {
        movieHelper = MovieHelper.getInstance(mContext);
        movieHelper.open();

        movies = movieHelper.getAllFavoriteMovies();

        if (movies != null) {
            for (int i=0; i < movies.size(); i++) {
                try {
                    Bitmap bitmap = Glide.with(mContext)
                            .load(movies.get(i).getImage())
                            .asBitmap()
                            .error(new ColorDrawable(mContext.getResources().getColor(R.color.colorAccent)))
                            .into(180,250).get();
                    mWidgetItems.add(bitmap);
                }
                catch (InterruptedException|ExecutionException e){
                    Log.d("Widget Load Error","error");
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        movieHelper.close();
    }

    @Override
    public int getCount() {
        return mWidgetItems.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        Bitmap bitmap = null;
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_item);

        rv.setImageViewBitmap(R.id.imageView, mWidgetItems.get(position));

        Bundle extras = new Bundle();
        extras.putInt(FavoriteMovieWidget.EXTRA_ITEM, position);
        Intent fillIntent = new Intent();
        fillIntent.putExtras(extras);

        rv.setOnClickFillInIntent(R.id.imageView, fillIntent);
        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
