package com.buruhkoding.secondsubmission;

import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.buruhkoding.secondsubmission.db.MovieHelper;

public class MovieDetailActivity extends AppCompatActivity {
    public static final String EXTRA_ID = "extra_id";
    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_DESC = "extra_desc";
    public static final String EXTRA_IMG = "extra_img";
    public static final String EXTRA_VOTECOUNT = "extra_votecount";
    public static final String EXTRA_VOTEAVG = "extra_voteavg";
    public static final String EXTRA_POSITION = "extra_position";
    public static final String EXTRA_MOVIE = "extra_movie";

    TextView titleView, overviewView, voteCountView, voteAverageView;
    ImageView imgView;

    int movieID = 0;

    private MovieHelper movieHelper;

    Movie movie = new Movie();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        movieHelper = MovieHelper.getInstance(getApplicationContext());
        movieHelper.open();

        titleView = findViewById(R.id.movie_title);
        overviewView = findViewById(R.id.movie_overview);
        voteCountView = findViewById(R.id.movie_count_vote);
        voteAverageView = findViewById(R.id.movie_avg_vote);
        imgView = findViewById(R.id.movie_image);

        Uri uri = getIntent().getData();
        if (uri != null) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) movie = new Movie(cursor);
                cursor.close();
            }
        } else {
            Movie movieData = getIntent().getParcelableExtra(EXTRA_MOVIE);
            this.movieID = movieData.getId();
            String title = movieData.getTitle();
            String desc = movieData.getDesc();
            String img = movieData.getImage();
            int voteCount = movieData.getVoteCount();
            double voteAverage = movieData.getVoteAverage();

            movie.setId(this.movieID);
            movie.setTitle(title);
            movie.setDesc(desc);
            movie.setImg(img);
            movie.setVoteAverage(voteAverage);
            movie.setVoteCount(voteCount);
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(movie.getTitle());
        }

        titleView.setText(movie.getTitle());
        overviewView.setText(movie.getDesc());
        voteCountView.setText(String.valueOf(movie.getVoteCount()));
        voteAverageView.setText(String.valueOf(movie.getVoteAverage()));
        Glide.with(this)
                .load(movie.getImage())
                .override(300, 300)
                .into(imgView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        if (this.isFavorite(this.movieID)) {
            menu.getItem(0).setIcon(R.drawable.ic_star_yellow_24dp);
        } else {
            menu.getItem(0).setIcon(R.drawable.ic_star_white_24dp);
        }

        return true;
    }

    //Handling Action Bar button click
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.favorite_menu:
                if(this.isFavorite(this.movieID)){
                    long result = movieHelper.deleteMovie(this.movieID);

                    if (result > 0) {
                        item.setIcon(R.drawable.ic_star_white_24dp);
                        Toast.makeText(MovieDetailActivity.this, "Berhasil menghapus film ke daftar favorit.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MovieDetailActivity.this, "Gagal menghapus film ke daftar favorit.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    long result = movieHelper.insertMovie(movie);

                    if (result > 0) {
                        item.setIcon(R.drawable.ic_star_yellow_24dp);
                        Toast.makeText(MovieDetailActivity.this, "Berhasil menambahkan film ke daftar favorit.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MovieDetailActivity.this, "Gagal menambahkan film ke daftar favorit.", Toast.LENGTH_SHORT).show();
                    }
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean isFavorite(int id) {
        if (movieHelper.getFavoriteMovie(id) == null) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        movieHelper.close();
    }
}
