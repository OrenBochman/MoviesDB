package com.example.oren.moviesdb.beans;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Movie Bean.
 * <p>
 * This class represents a movie
 */
public class Movie implements Serializable, Parcelable {

    private long id;
    private float rating;
    private String title;
    private String overview;
    private String imagePath;
    private String backgroundPath;


    private boolean watched;

    /**
     * Default constructor
     */
    public Movie() {
    }

    /**
     * Id free constructor.
     *
     * @param title     - movie title
     * @param overview  - movie overview
     * @param imagePath - movie image path
     * @param rating    - movie rating
     */
    public Movie(String title, String overview, String imagePath, String backgroundPath,
                 float rating, boolean watched) {
        this();
        setTitle(title);
        setOverview(overview);
        setImagePath(imagePath);
        setBackgroundPath(backgroundPath);
        setRating(rating);
        setWatched(watched);
    }

    /**
     * Fully qualified constructor.
     *
     * @param id        - movie id
     * @param title     - movie title
     * @param overview  - movie overview
     * @param imagePath - movie image path
     * @param rating    - movie rating
     */
    public Movie(long id, String title, String overview, String imagePath, String backgroundPath,
                 float rating, boolean watched) {
        this(title, overview, imagePath, backgroundPath, rating, watched);
        this.id = id;
    }


    protected Movie(Parcel in) {
        id = in.readLong();
        rating = in.readFloat();
        title = in.readString();
        overview = in.readString();
        imagePath = in.readString();
        backgroundPath = in.readString();
        watched = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeFloat(rating);
        dest.writeString(title);
        dest.writeString(overview);
        dest.writeString(imagePath);
        dest.writeString(backgroundPath);
        dest.writeByte((byte) (watched ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getBackgroundPath() {
        return backgroundPath;
    }

    public void setBackgroundPath(String backgroundPath) {
        this.backgroundPath = backgroundPath;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public boolean isWatched() {
        return watched;
    }

    public void setWatched(boolean watched) {
        this.watched = watched;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id=" + getId() +
                ", rating=" + getRating() +
                ", title='" + getTitle() + '\'' +
                ", overview='" + getOverview() + '\'' +
                ", imagePath='" + getImagePath() + '\'' +
                ", backgroundPath='" + getBackgroundPath() + '\'' +
                ", watched=" + isWatched() +
                ", describeContents=" + describeContents() +
                '}';
    }
}

