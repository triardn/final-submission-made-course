package com.buruhkoding.secondsubmission;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

import static com.buruhkoding.secondsubmission.SearchFragment.EXTRAS_FILTER;

public class MyAsyncTaskLoader extends AsyncTaskLoader<ArrayList<Movie>> {
    private static final String EXTRAS_QUERY = "EXTRAS_QUERY";
    private ArrayList<Movie> movieData;
    private boolean requestHasResult = false;

    private String mode;
    private String url;
    private String movieTitle;
    private String filter;

    public MyAsyncTaskLoader(final Context context, String mode, String query, String filterSearch) {
        super(context);

        onContentChanged();
        this.mode = mode;
        this.movieTitle = query;
        this.filter = "movie";

        if(filterSearch == "TV Shows"){
            this.filter = "tv";
        }
    }

    public MyAsyncTaskLoader(final Context context, String mode) {
        super(context);

        onContentChanged();
        this.mode = mode;
    }

    @Override
    protected void onStartLoading() {
        if(takeContentChanged()) {
            forceLoad();
        } else if (requestHasResult){
            deliverResult(movieData);
        }
    }

    @Override
    public void deliverResult(final ArrayList<Movie> data) {
        movieData = data;
        requestHasResult = true;
        super.deliverResult(data);
    }

    @Override
    protected void onReset() {
        super.onReset();
        onStopLoading();

        if (requestHasResult) {
            movieData = null;
            requestHasResult = false;
        }
    }

    private static final String API_KEY = BuildConfig.TMDB_API_KEY;

    @Override
    public ArrayList<Movie> loadInBackground() {
        SyncHttpClient client = new SyncHttpClient();

        final ArrayList<Movie> allMovies = new ArrayList<>();

        switch (mode) {
            case "nowplaying":
                url = "https://api.themoviedb.org/3/movie/now_playing?api_key=" + API_KEY + "&language=en-US&page=1";
                break;

            case "upcoming":
                url = "https://api.themoviedb.org/3/movie/upcoming?api_key=" + API_KEY + "&language=en-US&page=1";
                break;

            case "search":
                url = "https://api.themoviedb.org/3/search/" + filter + "?api_key=" + API_KEY + "&language=en-US&query=" + movieTitle;
                break;
        }

        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                setUseSynchronousMode(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String result = new String(responseBody);
                    JSONObject responseObject = new JSONObject(result);
                    JSONArray list = responseObject.getJSONArray("results");

                    for (int i = 0 ; i < list.length() ; i++) {
                        JSONObject movie = list.getJSONObject(i);
                        Movie movieItems = new Movie(movie);
                        allMovies.add(movieItems);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // jika gagal, do nothing
            }
        });

        return allMovies;
    }
}
