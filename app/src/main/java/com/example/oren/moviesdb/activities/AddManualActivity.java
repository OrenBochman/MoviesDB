package com.example.oren.moviesdb.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Switch;

import com.example.oren.moviesdb.database.MovieDBHelper;
import com.example.oren.moviesdb.R;
import com.example.oren.moviesdb.beans.Movie;
import com.example.oren.moviesdb.helpers.CameraUtils;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Intent.ACTION_SEND;
//import static android.content.pm.PackageManager.FEATURE_CAMERA_ANY;
import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

@SuppressWarnings("WeakerAccess")
public class AddManualActivity extends AppCompatActivity {

    /** Constants *********************************************************************************/
    private static final String TAG = AddManualActivity.class.getName();
    public static final String MOVIE = "MOVIE";
    public static final String EDIT_MODE = "EDIT_MODE";

    /** Rapid access to activity views via ButterKnife compile time binding ***********************/
    @BindView(R.id.manualEditImageUrl)  protected EditText manualEditImageUrl;
    @BindView(R.id.manualEditTitle)     protected EditText manualEditTitle;
    @BindView(R.id.manualEditOverview)  protected EditText manualEditOverview;

    @BindView(R.id.manualRatingBar)     protected RatingBar manualRatingBar;
    @BindView(R.id.manualImageView)     protected ImageView manualImageView;
    @BindView(R.id.manualSwitchWatched) protected Switch manualSwitchWatched;

    @BindView(R.id.manualButtonShow)    protected Button manualButtonShow;
    @BindView(R.id.manualButtonSave)    protected Button manualButtonSave;

    @BindView(R.id.manualButtonShare)   protected ImageButton manualButtonShare;
    @BindView(R.id.manualButtonShoot)   protected ImageButton manualButtonShoot;
    @BindView(R.id.manualButtonYouTube) protected ImageButton manualButtonYouTube;
    @BindView(R.id.toolbarManual)       protected Toolbar toolbar;

    /** Private members ***************************************************************************/
    private Movie movie;    // movie recieved in intent
    private boolean editMode= false;

    /** Activity Lifecycle Handlers ***************************************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_manual);
        ButterKnife.bind(this);
        Log.i(TAG, "onCreate: ");

        //getActionBar().setDisplayHomeAsUpEnabled(true);
        setSupportActionBar(toolbar);

        // check for intent from web activity, TODO: refactor to the new Navigation API
        movie = getIntent().getParcelableExtra("movie");
        editMode=movie != null;

        if(editMode) initFields(movie);
        initializeManualButtonShare();
        initializeManualButtonYouTube();
        initializeManualButtonShoot();
        initializeManualButtonShow();
        initializeMovieButtonSave();
    }

    private void initFields(@NotNull Movie movie) {
        //set the edit fields
        manualEditImageUrl.setText(movie.getImagePath());
        manualEditTitle.setText(movie.getTitle());
        //manualEditBackgroundUrl.setText(movie.getBackgroundPath());
        manualEditOverview.setText(movie.getOverview());
        manualSwitchWatched.setChecked(movie.isWatched());
        manualRatingBar.setRating(movie.getRating());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CameraUtils.REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            int height = manualImageView.getHeight();
            int width = manualImageView.getWidth();

            manualEditImageUrl.setText(CameraUtils.photoURI.toString());
            Bitmap imageBitmap=CameraUtils.resizePic(height,width,CameraUtils.mCurrentPhotoPath);
            manualImageView.setVisibility(VISIBLE);
            manualImageView.setImageBitmap(imageBitmap);
            CameraUtils.galleryAddPic(getApplicationContext());
        }
    }

    /**  Save state to handle runtime and configuration changes ***********************************/
    @Override
    public void onSaveInstanceState(@NotNull Bundle state) {
        super.onSaveInstanceState(state);

        //save the movie
        state.putSerializable(MOVIE, movie);
        //save edit state
        state.putSerializable(EDIT_MODE, editMode);

    }

    /**  Restore state to handle runtime and configuration changes ********************************/
    @Override
    public void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);

        this.movie = state.getParcelable(MOVIE);
        this.editMode=state.getBoolean(EDIT_MODE);

        if(editMode && movie!=null) {
            initFields( movie);
            //TODO: add logic for button/control visibility based on actual mode!
        }
    }


    /**
     * initialization for show button
     */
    private void initializeManualButtonShow() {
        manualButtonShow.setOnClickListener(v -> {

            String imagePath = manualEditImageUrl.getText().toString();
            if (imagePath != null && !imagePath.isEmpty()) {
                Picasso.get()
                        .load(imagePath)
                        .error(R.drawable.ic_launcher_background)
                        .into(manualImageView);
            }else{
                Exception e = new IllegalArgumentException("imagePath cannot be empty");
                Log.e(TAG, "initializeManualButtonShow: "+e.getMessage(), e);
            }
        });

        try{
            Uri uri= Uri.parse(manualEditImageUrl.getText().toString());
            if(uri!=null) {
                manualButtonShow.callOnClick();
            }
        }catch(Exception e){
            Log.i(TAG, "initializeManualButtonShow: failed to parse URI");
        }
    }

    /**
     * initialization for YouTube button.
     */
    private void initializeManualButtonYouTube() {
        manualButtonYouTube.setOnClickListener(v -> {
            String query =manualEditTitle.getText().toString();
            Intent intent = new Intent(Intent.ACTION_SEARCH);
            intent.setPackage("com.google.android.youtube");
            intent.putExtra("query", query);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });
    }


    /**
     *  initialization for share button
     */
    private void initializeManualButtonShare() {
        if(movie==null)
            this.manualButtonShare.setVisibility(INVISIBLE);

        Context context = getApplicationContext();
        if(context.getPackageManager().hasSystemFeature(android.content.pm.PackageManager.FEATURE_CAMERA_ANY)){
            manualButtonShare.setVisibility(VISIBLE);

            manualButtonShare.setOnClickListener(v -> {
                //create a share intent
                Intent intent = new Intent(ACTION_SEND);
                intent.setType("text/plain");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

                // Add data to the intent, the receiving app will decide what to do with it.
                intent.putExtra(Intent.EXTRA_SUBJECT, "MovieDBApplication App Sharing "+ movie.getTitle());
                intent.putExtra(Intent.EXTRA_TEXT, movie.toString());

                //start a chooser
                startActivity(Intent.createChooser(intent, "How do you want to share?"));
            });
        }else{
            manualButtonShare.setVisibility(INVISIBLE);
        }
    }

    /**
     *  initialization for shoot photo button
     */
    private void initializeManualButtonShoot() {
        manualButtonShoot.setOnClickListener(v -> {
            CameraUtils.dispatchTakePictureIntent(this);
        });
    }


    /**
     *   initialization for save button
     */
    private void initializeMovieButtonSave() {
        manualButtonSave.setOnClickListener(v -> {

            Intent intent = getIntent();
            Movie originalMovie = getIntent().getParcelableExtra("movie");
            Movie movie;
            if(originalMovie!=null){
                 movie = new Movie(
                        originalMovie.getId(),
                        manualEditTitle.getText().toString(),
                        manualEditOverview.getText().toString(),
                        manualEditImageUrl.getText().toString(),
                        "",
                        manualRatingBar.getRating(),
                        manualSwitchWatched.isChecked());
            }else{
             movie = new Movie(
                    manualEditTitle.getText().toString(),
                    manualEditOverview.getText().toString(),
                    manualEditImageUrl.getText().toString(),
                    "",
                    manualRatingBar.getRating(),
                    manualSwitchWatched.isChecked());
            }
            //return movie to calling intent
            intent.putExtra("movie",(Parcelable) movie);
            setResult(RESULT_OK,intent);
            //close this activity
            finish();
        });
    }
}
