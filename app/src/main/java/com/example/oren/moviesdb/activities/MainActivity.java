
package com.example.oren.moviesdb.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Parcelable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.oren.moviesdb.MovieDBApplication;
import com.example.oren.moviesdb.adapters.SimpleMovieAdapter;
import com.example.oren.moviesdb.beans.Movie;
import com.example.oren.moviesdb.database.MovieDBHelper;
import com.example.oren.moviesdb.R;
import com.example.oren.moviesdb.helpers.ShareUtils;
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressWarnings("WeakerAccess")
public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();

    //ArrayList<Movie> movies = new ArrayList<>();
    private SimpleMovieAdapter adapter;
    private MovieDBHelper helper;

    @BindView(R.id.movieListView) protected ListView listView;
    @BindView(R.id.toolbar) Toolbar toolbar;

    /**
     * Create the main activity.
     * <p>
     * TODO: add context menu to the ListView with view/remove/edit operation for each item.
     * TODO: add app menu with + and remove all
     * TODO: test for empty ListBox on application start
     * TODO: test for non-empty ListBox after adding item to db
     * TODO: test for empty ListBox after remove all
     * TODO: test for empty ListBox after remove an item
     * TODO: test for changed ListBox after movie edit.
     *
     * @param savedInstanceState the saved state from before a prior config change
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // Helper is first created  in the app
        helper = MovieDBApplication.helper;

        setSupportActionBar(toolbar);

        initializeFAB();
        initializeMovieListView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart: ");
        refreshListData();
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: ");
        refreshListData();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ");
    }

    /**
     * non lifecycle event handlers
     ***************************************************************/

    // Context MENU ///////////////////////////////////////////////////////////
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_movielist_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        int menuItemIndex = item.getItemId();
        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;
        Movie movie = adapter.getItem(position);

        switch (menuItemIndex) {
            case R.id.mlDeleteMovieItem:
                this.helper.delete(movie.getId());
                refreshListData();
                break;
            case R.id.mlEditMovieItem:
                this.launchAddManualActivity(movie);
                break;
            case R.id.mlShareMovieItem:
                ShareUtils.shareByMail(this,
                        "The Movie DB App - Movie sharing ",
                        "I'm sharing this movie with you",
                        "the message name is " + movie.getTitle(),
                        movie);
                //this.share(movie.getId());
                break;
            default:
                Log.e(TAG, "onOptionsItemSelected: unhandled case  " + menuItemIndex);
                return super.onContextItemSelected(item);
        }
        return true;
    }

    // Main MENU ///////////////////////////////////////////////////////////


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int menuItemIndex = item.getItemId();

        //noinspection SimplifiableIfStatement

        switch (menuItemIndex) {
            case R.id.ActionAddManual:
                this.launchAddManualActivity(null);
                break;

            case R.id.ActionAddWeb:
                this.launchAddWebActivity();
                break;

            case R.id.ActionDeleteAll:
                MovieDBHelper.getInstance(getApplicationContext()).deleteAll();
                refreshListData();
                //Toast.makeText(MainActivity.this, "short click on " + menuItemIndex, Toast.LENGTH_SHORT).show();
                break;
            default:
                Log.e(TAG, "onOptionsItemSelected: unhandled case  " + menuItemIndex);
                break;
        }
        return true;
    }

    // Save & Restore state //////////////////////////////////////

    /*** Save state to handle runtime and configuration changes ***********************************/

    @Override
    public void onSaveInstanceState(@NotNull Bundle state) {
        super.onSaveInstanceState(state);
        //todo: save fab state
    }

    /*** Restore state to handle runtime and configuration changes ********************************/

    @Override
    public void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        //todo: restore fab state
    }


    /**
     * Navigation - launch activities
     ************************************************************/

    //TODO - export these to a constants
    public final static int WEB_ACTIVITY = 10;
    public final static int MANUAL_ACTIVITY_CREATE = 11;
    public final static int MANUAL_ACTIVITY_EDIT = 12;


    private void launchAddWebActivity() {
        //use an intent that returns a result
        Intent intent = new Intent(this, AddWebActivity.class);

        //use an intent that returns a result + a requestCode
        startActivityForResult(intent, WEB_ACTIVITY);
    }

    private void launchAddManualActivity(Movie movie) {
        Intent intent = new Intent(this, AddManualActivity.class);

        if (movie != null) {
            intent.putExtra("movie", (Parcelable) movie);
        }

        //add different requestCode for edit/create mode
        if (movie != null) {
            startActivityForResult(intent, MANUAL_ACTIVITY_EDIT);
        } else {
            startActivityForResult(intent, MANUAL_ACTIVITY_CREATE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult: ");
        // Check which request we're responding to
        if (resultCode == RESULT_OK) {
            Movie movie = data.getParcelableExtra("movie");

            switch (requestCode) {
                case WEB_ACTIVITY:
                    Log.i(TAG, "onActivityResult: WEB_ACTIVITY");
                    helper.create(movie);
                    break;
                case MANUAL_ACTIVITY_CREATE:
                    helper.create(movie);
                    Log.i(TAG, "onActivityResult: MANUAL_ACTIVITY_CREATE ");
                    break;
                case MANUAL_ACTIVITY_EDIT:
                    helper.update(movie);
                    Log.i(TAG, "onActivityResult: MANUAL_ACTIVITY_EDIT");
                    break;
            }
            //refreshListData();

        } else {
            Log.i(TAG, "onActivityResult: failed!");
        }
    }


    /*** private helpers **************************************************************************/

    /**
     * create a FAB with a speed dial element.
     */
    private void initializeFAB() {

        SpeedDialView speedDialView = findViewById(R.id.fab);
        speedDialView.addActionItem(
                new SpeedDialActionItem.Builder(R.id.fab_add_manual, R.drawable.ic_manual)
                        .setFabBackgroundColor(ResourcesCompat.getColor(getResources(),
                                R.color.actionManual, getTheme()))
                        .setLabel(getString(R.string.add_manual))
                        .setLabelColor(Color.WHITE)
                        .setLabelBackgroundColor(ResourcesCompat.getColor(getResources(),
                                R.color.actionManual, getTheme()))
                        .create()
        );
        speedDialView.addActionItem(
                new SpeedDialActionItem.Builder(R.id.fab_add_web, R.drawable.ic_web)
                        .setFabBackgroundColor(ResourcesCompat.getColor(getResources(),
                                R.color.actionWeb, getTheme()))
                        .setLabel(getString(R.string.add_web))
                        .setLabelColor(Color.WHITE)
                        .setLabelBackgroundColor(ResourcesCompat.getColor(getResources(),
                                R.color.actionWeb, getTheme()))
                        .create()
        );

        speedDialView.setOnActionSelectedListener(new SpeedDialView.OnActionSelectedListener() {
            @Override
            public boolean onActionSelected(SpeedDialActionItem speedDialActionItem) {
                switch (speedDialActionItem.getId()) {
                    case R.id.fab_add_manual:
                        launchAddManualActivity(null);
                        return false; // true to keep the Speed Dial open
                    case R.id.fab_add_web:
                        launchAddWebActivity();
                        return false;
                    default:
                        return false;
                }
            }
        });
    }

    private void initializeMovieListView() {
        registerForContextMenu(listView);
        adapter = new SimpleMovieAdapter(this, android.R.layout.simple_list_item_1, helper.read());
        listView.setAdapter(adapter);
    }

    private void refreshListData() {
        adapter.setMovies(helper.read());
    }

}