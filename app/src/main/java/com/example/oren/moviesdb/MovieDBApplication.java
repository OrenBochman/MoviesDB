package
        com.example.oren.moviesdb;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.net.http.HttpResponseCache;
import android.os.Bundle;
import android.util.Log;

import com.example.oren.moviesdb.database.MovieDBHelper;
import com.example.oren.moviesdb.helpers.CacheUtils;

public class MovieDBApplication extends Application {


    private static final String TAG = MovieDBApplication.class.getName();
    private  final Context mContext = this;
    public static MovieDBHelper helper;

    // Called when the application is starting, before any other application objects have been created.
    // Overriding this method is totally optional!
    @Override
    public void onCreate() {
        super.onCreate();
        // Required initialization logic here!
        //enable caching
        CacheUtils.initializeCache(mContext);

        //create the DB //TODO move this to a worker thread
        helper = MovieDBHelper.getInstance(this);

        // Register for activity lifecycle callbacks,
        // specifically interested in activity stop callbacks.
        registerActivityLifecycleCallbacks(new MovieDBActivityLifeCycleCallbacks());

    }

    // Called by the system when the device configuration changes while your component is running.
    // Overriding this method is totally optional!
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d(TAG, "onConfigurationChanged: " + newConfig);
    }

    // This is called when the overall system is running low on memory,
    // and would like actively running processes to tighten their belts.
    // Overriding this method is totally optional!
//    @Override
//    public void onLowMemory() {
//        super.onLowMemory();
//    }

    protected void onStop() {

        CacheUtils.flushCache();
    }

    private class MovieDBActivityLifeCycleCallbacks implements ActivityLifecycleCallbacks {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        }

        @Override
        public void onActivityStarted(Activity activity) {
        }

        @Override
        public void onActivityResumed(Activity activity) {
        }

        @Override
        public void onActivityPaused(Activity activity) {
        }

        public void onActivityStopped(Activity activity) {
            CacheUtils.flushCache();
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
        }
    }
}
