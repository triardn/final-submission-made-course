package com.buruhkoding.secondsubmission;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.CategoryViewHolder> {
    private ArrayList<Movie> movieData = new ArrayList<>();
    private Context context;

    MovieAdapter(Context context) {
        this.context = context;
    }

    public void refill(ArrayList<Movie> items) {
        movieData.addAll(items);

        notifyDataSetChanged();
    }

    public void setData(ArrayList<Movie> items) {
        movieData.clear();
        movieData.addAll(items);
        notifyDataSetChanged();
    }

    public ArrayList<Movie> getListMovie() {
        return movieData;
    }

    public void setListMovie(ArrayList<Movie> movieData) {
        if (movieData.size() > 0) {
            this.movieData.clear();
        }

        this.movieData.addAll(movieData);
        notifyDataSetChanged();
    }

    public void addItem(Movie movie) {
        this.movieData.add(movie);
        notifyItemInserted(movieData.size() - 1);
    }
    public void updateItem(int position, Movie movie) {
        this.movieData.set(position, movie);
        notifyItemChanged(position, movie);
    }
    public void removeItem(int position) {
        this.movieData.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, movieData.size());
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemRow = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.movie_items, viewGroup, false);

        return new CategoryViewHolder(itemRow);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder categoryViewHolder, int position){
        categoryViewHolder.textViewTitle.setText(getListMovie().get(position).getTitle());
        categoryViewHolder.textViewDesc.setText(getListMovie().get(position).getDesc());

        Glide.with(context)
                .load(getListMovie().get(position).getImage())
                .override(100,100)
                .crossFade()
                .into(categoryViewHolder.circleImageViewPoster);
    }

    @Override
    public int getItemCount(){
        return movieData.size();
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        TextView textViewDesc;
        CircleImageView circleImageViewPoster;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewTitle = itemView.findViewById(R.id.tv_movie_title);
            textViewDesc = itemView.findViewById(R.id.tv_movie_desc);
            circleImageViewPoster = itemView.findViewById(R.id.img_movie);
        }
    }
}
