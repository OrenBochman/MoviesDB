package com.example.oren.moviesdb.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.oren.moviesdb.R;
import com.example.oren.moviesdb.beans.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieAdapter extends ArrayAdapter<Movie> {

    private static final String TAG = MovieAdapter.class.getName();
    private final LayoutInflater inflater;


    public MovieAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Movie> movies) {
        super(context, resource,movies);
         inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        ViewHolder holder;

        final Movie movie = getItem(position);
        // is the view created?
        if (view != null) {
            //it is so get it via its tag
            holder = (ViewHolder) view.getTag();
        } else {
            //it is not - so inflate the xml layout file into Java objects
            view = inflater.inflate(R.layout.movie_list_adapter, parent, false);
            holder = new ViewHolder(view);
            //tag it for reuse
            view.setTag(holder);
        }

        if (movie != null){
            holder.textView.setText(movie.getTitle());
            holder.ratingBar.setRating(movie.getRating());
            //holder.ratingBar.setEnabled(false);
            holder.ratingBar.setIsIndicator(true);

            Log.i(TAG, "getView: " + movie.getImagePath());

            if (holder.imageView != null && !movie.getImagePath().isEmpty()) {
                Picasso
                        .get()
                        .load(movie.getImagePath())
                        .into(holder.imageView);
            }else{
                holder.imageView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_place));
            }
        }
        return view;
    }

    static class ViewHolder {

        @BindView(R.id.movieIcon)       ImageView imageView;
        @BindView(R.id.movieName)       TextView textView;
        @BindView(R.id.ratingBarManual) RatingBar ratingBar;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
