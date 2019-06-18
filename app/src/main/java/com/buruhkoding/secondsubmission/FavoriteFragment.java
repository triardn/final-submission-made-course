package com.buruhkoding.secondsubmission;


import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.buruhkoding.secondsubmission.db.MovieHelper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import static com.buruhkoding.secondsubmission.db.DatabaseContract.FavoriteColumns.CONTENT_URI;
import static com.buruhkoding.secondsubmission.helper.MappingHelper.mapCursorToArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class FavoriteFragment extends Fragment implements View.OnClickListener, LoadMoviesCallback {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private MovieAdapter movieAdapter;
    private MovieHelper movieHelper;

    private static HandlerThread handlerThread;
    private DataObserver myObserver;

    private ArrayList<Movie> movies = new ArrayList<>();

    public FavoriteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorite, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle(getString(R.string.favorite));

        recyclerView = view.findViewById(R.id.favorite_recylerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        movieHelper = MovieHelper.getInstance(getActivity().getApplicationContext());
        movieHelper.open();

        progressBar = view.findViewById(R.id.progress_bar);

        handlerThread = new HandlerThread("DataObserver");
        handlerThread.start();
        Handler handler = new Handler(handlerThread.getLooper());
        myObserver = new DataObserver(handler, getContext());
        getContext().getContentResolver().registerContentObserver(CONTENT_URI, true, myObserver);

        movieAdapter = new MovieAdapter(getActivity());
        recyclerView.setAdapter(movieAdapter);

        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Intent movieDetail = new Intent(getActivity(), MovieDetailActivity.class);
                Uri uri = Uri.parse(CONTENT_URI + "/" + movieAdapter.getListMovie().get(position).getId());
                movieDetail.setData(uri);
                startActivity(movieDetail);
            }
        });

        if (savedInstanceState == null) {
            new LoadMovieAsync(getContext(), this).execute();
        } else {
            movies = savedInstanceState.getParcelableArrayList("movie");
            if (movies != null) {
                movieAdapter.setListMovie(movies);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("movie", movieAdapter.getListMovie());
    }

    @Override
    public void preExecute() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void postExecute(Cursor movies) {
        progressBar.setVisibility(View.INVISIBLE);

        ArrayList<Movie> listMovies = mapCursorToArrayList(movies);
        if (listMovies.size() > 0) {
            movieAdapter.setListMovie(listMovies);
        } else {
            movieAdapter.setListMovie(new ArrayList<Movie>());
            showSnackbarMessage("Tidak ada data saat ini");
        }
    }

    @Override
    public void onClick(View v) {

    }

    private static class LoadMovieAsync extends AsyncTask<Void, Void, Cursor> {
        private final WeakReference<Context> weakContext;
        private final WeakReference<LoadMoviesCallback> weakCallback;

        private LoadMovieAsync(Context context, LoadMoviesCallback callback) {
            weakContext = new WeakReference<>(context);
            weakCallback = new WeakReference<>(callback);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            weakCallback.get().preExecute();
        }

        @Override
        protected Cursor doInBackground(Void... voids) {
            Context context = weakContext.get();
            return context.getContentResolver().query(CONTENT_URI, null, null, null, null);
        }

        @Override
        protected void onPostExecute(Cursor movies) {
            super.onPostExecute(movies);

            weakCallback.get().postExecute(movies);
        }
    }

    private void showSnackbarMessage(String message) {
        Snackbar.make(recyclerView, message, Snackbar.LENGTH_SHORT).show();
    }

    public static class DataObserver extends ContentObserver {
        final Context context;
        public DataObserver(Handler handler, Context context) {
            super(handler);
            this.context = context;
        }
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            new LoadMovieAsync(context, (LoadMoviesCallback) context).execute();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        movieHelper.close();
    }

    @Override
    public void onResume() {
        new LoadMovieAsync(getContext(), this).execute();
        super.onResume();
    }
}
