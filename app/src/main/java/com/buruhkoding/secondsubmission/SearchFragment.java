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
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<Movie>>, OnClickListener, AdapterView.OnItemSelectedListener {
    RecyclerView recyclerView;
    ProgressBar progressBar;
    MovieAdapter movieAdapter;

    EditText searchQuery;
    Button searchButton;
    String query = "avenger";

    static final String EXTRAS_QUERY = "EXTRAS_QUERY";
    static final String EXTRAS_FILTER = "EXTRAS_FILTER";
    private ArrayList<Movie> movies = new ArrayList<>();
    String selectedFilter;

    private Bundle bundle = new Bundle();

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle(getString(R.string.search));

        progressBar = view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

        movieAdapter = new MovieAdapter(getActivity());
        movieAdapter.notifyDataSetChanged();

        recyclerView = view.findViewById(R.id.search_recylerview);
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

        // Spinner element
        Spinner spinner = view.findViewById(R.id.search_filter);

        // Spinner click listener
        spinner.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> filter = new ArrayList<>();
        filter.add("Movies");
        filter.add("TV Shows");

        // Creating adapter for spinner
        ArrayAdapter<String> filterAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, filter);

        // Drop down layout style - list view with radio button
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(filterAdapter);

        searchQuery = view.findViewById(R.id.search_query);
        searchButton = view.findViewById(R.id.btn_search);

        searchButton.setOnClickListener(this);

        String query = searchQuery.getText().toString();
        bundle.putString(EXTRAS_QUERY, query);
        bundle.putString(EXTRAS_FILTER, selectedFilter);

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
        String filter = "Movies";
        if (args != null) {
            query = args.getString(EXTRAS_QUERY);
            filter = args.getString(EXTRAS_FILTER);
        }

        progressBar.setVisibility(View.VISIBLE);

        return new MyAsyncTaskLoader(getActivity(), "search", query, filter);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_search:
                progressBar.setVisibility(View.VISIBLE);
                query = searchQuery.getText().toString();

                if(TextUtils.isEmpty(query)) {
                    return;
                }

                Bundle bundle = new Bundle();
                bundle.putString(EXTRAS_QUERY, query);
                bundle.putString(EXTRAS_FILTER, selectedFilter);
                getLoaderManager().restartLoader(0, bundle, this);
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        this.selectedFilter = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
