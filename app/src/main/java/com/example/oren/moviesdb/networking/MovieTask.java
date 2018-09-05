package com.example.oren.moviesdb.networking;

import android.annotation.SuppressLint;
import android.net.http.HttpResponseCache;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.oren.moviesdb.R;
import com.example.oren.moviesdb.activities.AddWebActivity;
import com.example.oren.moviesdb.beans.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MovieTask extends AsyncTask<String, Integer, ArrayList<Movie>> {

    /**
     * Constants
     *********************************************************************************/
    private static final String imgPrefix = "http://image.tmdb.org/t/p/w200";
    private static final String backDropPrefix = "http://image.tmdb.org/t/p/w400";
    private static final String TAG = MovieTask.class.getName();
    private static final String KEY = "770fe7d7b2510c8d2430d205ec463a4d";

    private final WeakReference<AddWebActivity> weakActivity;
    //private final ArrayAdapter<Movie> adapter;

    /**
     * Constructor.
     * <p>
     * Uses a <code>WeakReference</code> to the calling activity to avoid leaking the context.
     *
     * @param activity - the context of the calling activity.
     * //@param adapter  - the adaptor that holds the results.
     */
    public MovieTask(AddWebActivity activity/**, ArrayAdapter<Movie> adapter*/) {
        this.weakActivity = new WeakReference<>(activity);
        //  this.weakActivity = new WeakReference<>(activity);
        //  this.adapter = adapter;
    }

    // -- called from the publish progress
    // -- notice that the datatype of the second param gets passed to this method
    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        Log.i(TAG, "onProgressUpdate(): " + String.valueOf(values[0]));

        AddWebActivity activity = weakActivity.get();
        if (activity == null
                || activity.isFinishing()
                || Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1
                && activity.isDestroyed())
            return;

        ProgressBar progressBar=activity.findViewById(R.id.progressBar);
        progressBar.setProgress(values[0]);//.setText((values[0] * 2) + "%");

    }
    /**
     * Gets movie data from TMDB search api.
     * <p>
     * Has cancellation support
     * <p>
     * TODO: test happy path - successful search of "rocky"
     * TODO: test failure - scenario 1 cancellation (rotation during api request)
     * TODO: test failure - scenario 2 cancellation (device rotation during parsing)
     * TODO: test failure - scenario 3 malformed uri
     * TODO: test failure - scenario 4 no network access / permission
     * TODO: test failure - scenario 5 connection interrupted during api call
     * TODO: test failure - scenario 6 bad JSON from api call
     *
     * @param strings - search term.
     * @return - list of movies matching search criteria.
     */



    @Override
    protected ArrayList<Movie> doInBackground(String... strings) {
        Log.i(TAG, "doInBackground: ");

        //TODO url encode string[0]
        String address = "http://api.themoviedb.org/3/search/movie?api_key=" + KEY + "&query=" + strings[0];
        Log.i(TAG, "doInBackground: calling: "+ address);

        ArrayList<Movie> movies=null;
        HttpURLConnection urlConnection = null;
        HttpResponseCache cache;
        InputStream inputStream;

        try {
            URL url = new URL(address);
            urlConnection = (HttpURLConnection) url.openConnection();
            int maxStale = 60 * 60 * 24 * 28; //tolerate 4 weeks stale
            urlConnection.addRequestProperty("Cache-Control", "max-stale=" + maxStale);
            cache = HttpResponseCache.getInstalled();
            if (cache != null) {
                String cacheInfo = new StringBuilder()
                        .append("Request count: ").append(cache.getRequestCount())
                        .append(", hit count ").append(cache.getHitCount())
                        .append(", network count ").append(cache.getNetworkCount())
                        .append("   size = ").append(cache.size())
                        .append(" <-----------------").toString();
                Log.i(TAG, cacheInfo);
            }

            // if (HttpURLConnection.HTTP_OK == urlConnection.getResponseCode()) {
            if (urlConnection.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST) {
                inputStream = urlConnection.getInputStream();
            } else {
                inputStream = urlConnection.getErrorStream();
            }
            StringBuilder response = new StringBuilder();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line ;
            //read input line by line
            while ((line = bufferedReader.readLine()) != null && !isCancelled()) {
                response.append(line);
            }

            publishProgress(5);
            if(isCancelled()){
                Log.i(TAG, "doInBackground: canceled by user during web request");
                return null;
            }
            if(urlConnection.getResponseCode()!=HttpURLConnection.HTTP_OK) {
                Log.e(TAG, "doInBackground: connection error: "+ response.toString() );
                return null;
            }
            /// set the JSON root of all search result
            JSONObject movieJson = new JSONObject(response.toString());

            movies= parse(movieJson);

        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException URI Parameters", e);
        } catch (IOException e) {
            Log.e(TAG, "IOException when calling TMDB api ", e);
        } catch (JSONException e) {
            Log.e(TAG, "Malformed JSON when parsing TMDB response ", e);
        } finally {
            // close resources
            if (urlConnection != null)
                urlConnection.disconnect();
        }
        return movies;
    }


    private ArrayList<Movie>  parse(JSONObject movieJson) throws JSONException {
        // create the arrays
        ArrayList<Movie> movies = new ArrayList<>();
        JSONArray rootArray = movieJson.getJSONArray("results");
        Movie movie = new Movie();
        for (int i = 0; i < rootArray.length(); i++) { // cancellation point 2

            if (isCancelled()) {
                Log.i(TAG, "doInBackground: canceled by user during parse");
                break;
            }
            publishProgress(i*100 / rootArray.length());

            // set the JSON root of a specific search result
            JSONObject movieObj = rootArray.getJSONObject(i);

            // Parse the JSON :
            //String release_date = movieObj.getString("release_date");

            movie.setBackgroundPath((MovieTask.imgPrefix + movieObj.getString("backdrop_path")));
            movie.setImagePath(MovieTask.backDropPrefix + movieObj.getString("poster_path"));
            movie.setId(Integer.parseInt(movieObj.getString("id")));
            movie.setOverview(movieObj.getString("overview"));
            movie.setTitle(movieObj.getString("title"));
            movie.setRating((Float.parseFloat(movieObj.getString("vote_average"))) / 2);
            //movie.setRating((Float.parseFloat(movieObj.getString("rating"))) / 2);
            movie.setWatched(false);
            Log.i(TAG, String.format("doInBackground: movie - %s", movie.toString()));
            movies.add(movie);
            movie = new Movie();
        }
        return movies;
    }

    @Override
    protected  void onPreExecute(){
        Log.i(TAG, "onPreExecute: ");
        AddWebActivity activity = weakActivity.get();

        if (activity == null
                || activity.isFinishing()
                || Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1
                && activity.isDestroyed())
            return;

        Button button=activity.findViewById(R.id.buttonSearch);
        button.setText(activity.getText(R.string.cancel));

    }

    /**
     * Update the adapter if activity is ready.
     * <p>
     * TO avoid an ANR error I check for that the activity is alive before updating ui
     *
     * @param movies - the list of movies returned by doInBackground.
     */
    @SuppressLint("DefaultLocale")
    @Override
    protected void onPostExecute(ArrayList<Movie> movies) {
        super.onPostExecute(movies);
        Log.i(TAG, "onPostExecute: ");
        AddWebActivity activity = weakActivity.get();

        if (activity == null
                || activity.isFinishing()
                || Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1
                && activity.isDestroyed())
            return;

        Button button=activity.findViewById(R.id.buttonSearch);
        button.setText(activity.getText(R.string.search));

        if (movies == null) {
            //update user
            Toast.makeText(activity, " Error no movies found", Toast.LENGTH_SHORT).show();
        } else {
            activity.setMovies(movies);
            //update user
            Toast.makeText(activity, String.format(" Found %d items", movies.size()),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Handler for cancellation event.
     *
     * @param movies movies from doInBackground
     */
    @Override
    protected void onCancelled(ArrayList<Movie> movies) {
        super.onCancelled(movies);
        Log.i(TAG, "onCancelled: ");
        // No clean up needed.

        AddWebActivity activity = weakActivity.get();
        if (  activity == null
           || activity.isFinishing()
           || Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed())
            return;

        Button button=activity.findViewById(R.id.buttonSearch);
        button.setText(activity.getText(R.string.search));
    }
}