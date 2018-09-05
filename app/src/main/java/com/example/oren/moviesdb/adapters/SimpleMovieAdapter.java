package com.example.oren.moviesdb.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.oren.moviesdb.R;
import com.example.oren.moviesdb.beans.Movie;
import com.example.oren.moviesdb.helpers.ColorUtils;

import java.util.ArrayList;
import java.util.List;

import static android.graphics.Color.argb;

public class SimpleMovieAdapter extends ArrayAdapter<Movie> {

    private static final String TAG = SimpleMovieAdapter.class.getName();
    private static final float MAX_RATING = 5;

    private LayoutInflater inflater;
    private int resource;
    private Activity activity;

    public SimpleMovieAdapter(@NonNull Activity activity, int resource, @NonNull ArrayList<Movie> movies) {
        super(activity, resource, movies);
        inflater = LayoutInflater.from(activity);
        this.resource = resource;
    //    List<Movie> movies1 = movies;
    }

    public void setMovies(final List<Movie> movieList) {
        //Log.d(TAG, "setMovies() called with: movies = [" + movies + "]");
        clear();
        for (Movie movie : movieList) {
            add(movie);
        }
        //       this.addAll(movieList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {

        TextView tv;
        Movie movie = getItem(position);
        if (view == null) {
            tv = (TextView) inflater.inflate(this.resource, parent, false);
        }else{
            tv = (TextView) view;
        }
        tv.setText(movie.getTitle());
        tv.setTextColor(ColorUtils.getScaledColor(movie.getRating() / MAX_RATING));
        tv.setBackgroundColor(getContext().getResources().getColor(R.color.colorPrimaryDark));
        return tv;
    }

//    @Override
//    public int getCount() {
//        return movies.size();
//    }
//
//    @Nullable
//    @Override
//    public Movie getItem(int position) {
//        return movies.get(position);
//    }
}
