package com.buruhkoding.secondsubmission;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class UpcomingFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<Movie>> {

    RecyclerView recyclerView;
    ProgressBar progressBar;
    MovieAdapter movieAdapter;

    static final String EXTRAS_MODE = "EXTRAS_MODE";
    private ArrayList<Movie> movies = new ArrayList<>();
    private Bundle bundle = new Bundle();

    public UpcomingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_upcoming, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle(getString(R.string.upcoming));

        progressBar = view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

        movieAdapter = new MovieAdapter(getActivity());
        movieAdapter.notifyDataSetChanged();

        recyclerView = view.findViewById(R.id.nowplaying_recylerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(movieAdapter);

        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Movie item = movies.get(position);

                Intent movieDetail = new Intent(getActivity(), MovieDetailActivity.class);
                Movie movie = new Movie();
                movie.setId(item.getId());
                movie.setTitle(item.getTitle());
                movie.setDesc(item.getDesc());
                movie.setImg(item.getImage());
                movie.setVoteCount(item.getVoteCount());
                movie.setVoteAverage(item.getVoteAverage());
                movieDetail.putExtra(MovieDetailActivity.EXTRA_MOVIE, movie);

                startActivity(movieDetail);
            }
        });

        bundle.putString(EXTRAS_MODE, "upcoming");

        if (savedInstanceState == null) {
            getLoaderManager().initLoader(0, bundle, this);
        } else {
            movies = savedInstanceState.getParcelableArrayList("movie");
            movieAdapter.refill(movies);
        }
    }

    @NonNull
    @Override
    public Loader<ArrayList<Movie>> onCreateLoader(int id, @Nullable Bundle args) {
        String mode = "";

        if (args != null) {
            mode = args.getString(EXTRAS_MODE);
        }

        progressBar.setVisibility(View.VISIBLE);

        return new MyAsyncTaskLoader(getActivity(), mode);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<Movie>> loader, ArrayList<Movie> data) {
        movies.clear();
        movies.addAll(data);

        progressBar.setVisibility(View.GONE);

        movieAdapter.setData(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<Movie>> loader) {
        progressBar.setVisibility(View.GONE);

        movieAdapter.setData(null);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelableArrayList("movie", movies);

        super.onSaveInstanceState(outState);
    }
}
