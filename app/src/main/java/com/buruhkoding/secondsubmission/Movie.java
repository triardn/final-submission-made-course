package com.buruhkoding.secondsubmission;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.buruhkoding.secondsubmission.db.DatabaseContract;

import org.json.JSONObject;

import static android.provider.BaseColumns._ID;
import static com.buruhkoding.secondsubmission.db.DatabaseContract.getColumnInt;
import static com.buruhkoding.secondsubmission.db.DatabaseContract.getColumnString;

public class Movie implements Parcelable {
    private int id, vote_count;
    private double vote_average;
    private String title, img, desc;

    public Movie () {

    }

    public Movie(JSONObject object) {
        try {
            int id = object.getInt("id");
            int vote_count = object.getInt("vote_count");
            double vote_average = object.getDouble("vote_average");
            String img = "https://image.tmdb.org/t/p/w342" + object.getString("poster_path");
            String desc = object.getString("overview");

            String title = "";
            if(object.has("title")){
                title = object.getString("title");
            } else {
                title = object.getString("name");
            }

            this.id = id;
            this.vote_count = vote_count;
            this.vote_average = vote_average;
            this.title = title;
            this.img = img;
            this.desc = desc;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVoteCount() { return vote_count; }

    public void setVoteCount(int vote_count) { this.vote_count = vote_count; }

    public double getVoteAverage() { return vote_average; }

    public void setVoteAverage(double vote_average) { this.vote_average = vote_average; }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) { this.title = title; }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) { this.desc = desc; }

    public String getImage() {
        return img;
    }

    public void setImg(String img) { this.img = img; }

    public Movie(int id, String title, String description, String image, int vote_count, double vote_average) {
        this.id = id;
        this.title = title;
        this.desc = description;
        this.img = image;
        this.vote_count = vote_count;
        this.vote_average = vote_average;
    }

    public Movie(Cursor cursor) {
        this.id = getColumnInt(cursor, _ID);
        this.title = getColumnString(cursor, DatabaseContract.FavoriteColumns.TITLE);
        this.desc = getColumnString(cursor, DatabaseContract.FavoriteColumns.DESC);
        this.img = getColumnString(cursor, DatabaseContract.FavoriteColumns.IMG_URL);
        this.vote_count = getColumnInt(cursor, DatabaseContract.FavoriteColumns.VOTE_COUNT);
        this.vote_average = getColumnInt(cursor, DatabaseContract.FavoriteColumns.AVG_VOTE);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.vote_count);
        dest.writeDouble(this.vote_average);
        dest.writeString(this.title);
        dest.writeString(this.img);
        dest.writeString(this.desc);
    }

    protected Movie(Parcel in) {
        this.id = in.readInt();
        this.vote_count = in.readInt();
        this.vote_average = in.readDouble();
        this.title = in.readString();
        this.img = in.readString();
        this.desc = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
