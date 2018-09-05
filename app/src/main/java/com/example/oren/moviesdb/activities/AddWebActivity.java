package com.example.oren.moviesdb.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;

import com.example.oren.moviesdb.beans.Movie;
import com.example.oren.moviesdb.adapters.MovieAdapter;
import com.example.oren.moviesdb.networking.MovieTask;
import com.example.oren.moviesdb.R;

import android.content.Intent;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;


import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * AddWebActivity is the UI for adding movies based on a search.
 */
@SuppressWarnings("WeakerAccess")
public class AddWebActivity extends AppCompatActivity {

    /** Constants *********************************************************************************/

    private static final String TAG = AddManualActivity.class.getName();

    /** Rapid access to activity views via ButterKnife compile time binding ***********************/

    @BindView(R.id.listMovie)    protected ListView listMovie;
    @BindView(R.id.searchMovie)  protected EditText searchMovie;
    @BindView(R.id.buttonSearch) protected Button buttonSearch;
    @BindView(R.id.progressBar)  protected ProgressBar progressBar;
    @BindView(R.id.toolbarWeb)   protected Toolbar toolbar;

    /** Private members ***************************************************************************/

    private ArrayList<Movie>    movies = new ArrayList<>();
    private ArrayAdapter<Movie> adapter;
    public  ArrayList<Movie>    getMovies() {
        return movies;
    }

    public void setMovies(ArrayList<Movie> movies) {
        this.movies.clear();
        this.movies.addAll(movies);
        if(this.adapter!=null)
            this.adapter.notifyDataSetChanged();
    }

    /** Activity Lifecycle Handlers ***************************************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_web);
        ButterKnife.bind(this);
        Log.i(TAG, "onCreate: ");

        setSupportActionBar(toolbar);

//        getActionBar().setDisplayHomeAsUpEnabled(true);

        initializeButton();
        initializeListView();
    }

    static MovieTask task;

    /**  Save state to handle runtime and configuration changes ***********************************/

    @Override
    public void onSaveInstanceState(@NotNull Bundle state) {
        super.onSaveInstanceState(state);

        //save list
        state.putParcelableArrayList("movies", movies);

        //save search term
        state.putSerializable("search", searchMovie.getText().toString());
    }
    /**  Restore state to handle runtime and configuration changes ********************************/

    @Override
    public void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);


        //restore search term
        searchMovie.setText(state.getString("search"));

        //restore list
        this.movies.clear();
        try {
            ArrayList<Movie> temp = state.getParcelableArrayList("movies");
            this.movies.clear();
            this.movies.addAll(temp);
        } catch (ClassCastException e) {
            Log.i(TAG, "onRestoreInstanceState: Could not restore state");
        }
        this.adapter.notifyDataSetChanged();
    }

    /**
     * Initialize the button.
     */
    private void initializeButton() {
        //do an TMDB api search
        buttonSearch.setOnClickListener(v -> {
            if(task==null || task.getStatus()== AsyncTask.Status.FINISHED ||task.isCancelled()) {
            //    task = new MovieTask(this, adapter);
                task = new MovieTask(this);

            }
            if (buttonSearch.getText() ==  getString(R.string.search)) {
                progressBar.setProgress(0);
                task.execute(searchMovie.getText().toString());
                //    buttonSearch.setText(getString(R.string.cancel)); moved to preExec
            } else {
                //    buttonSearch.setText(getString(R.string.search)); move to postExec/onCancel
                task.cancel(true);
                progressBar.setProgress(100);
            }
            progressBar.setProgress(0);
        });
    }

    /**
     * Initialize the list view.
     * <p>
     * TODO: add a context menu to add a movie from the list.
     * Add movie will add it to the DB
     * View movie will open it in view movie activity
     */
    private void initializeListView(){
        adapter = new MovieAdapter(this, android.R.layout.simple_list_item_1,movies);
        listMovie.setAdapter(adapter);

        //handle clicks on a movie list item by sending it to the manual edit
        listMovie.setOnItemClickListener((parent, view, position, id) -> {
            Log.i(TAG, "add movie #"+id);
            // Toast.makeText(AddWebActivity.this,"add movie #"+id,Toast.LENGTH_LONG);
            Intent intent = new Intent(AddWebActivity.this, AddManualActivity.class);
            intent.putExtra("movie", (Parcelable) adapter.getItem(position));
            startActivityForResult(intent,MainActivity.MANUAL_ACTIVITY_CREATE);
           // startActivity(intent);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Check which request we're responding to
        if (resultCode == RESULT_OK) {
            Movie movie = data.getParcelableExtra("movie");
            if(requestCode== MainActivity.MANUAL_ACTIVITY_CREATE){
                    Intent intent = getIntent();
                    intent.putExtra("movie",(Parcelable) movie);
                    setResult(RESULT_OK,intent);
                    finish();
            }
        }else{
            Log.i(TAG, "onActivityResult: failed!");
        }
    }
}

