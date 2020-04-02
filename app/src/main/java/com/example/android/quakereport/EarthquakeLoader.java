package com.example.android.quakereport;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import java.util.List;

/**
 * Loads a list of earthquakes by using an AsyncTask to perform the
 * network request to the given URL.
 */
public class EarthquakeLoader extends AsyncTaskLoader<List<Quake>> {

    /** Tag for log messages */
    private static final String TAG = EarthquakeLoader.class.getName();

    /** Query URL */
    private String mUrl;

    /**
     * Constructs a new {@link EarthquakeLoader}.
     *
     * @param context of the activity
     * @param url to load data from
     */
    public EarthquakeLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    //This method is automatically called after initLoader()
    //It triggers the loader to start the background work
    @Override
    protected void onStartLoading() {
        forceLoad();
        Log.e(TAG,"OnStartLoading()");
    }

    /**
     * This is on a background thread.
     */
    @Override
    public List<Quake> loadInBackground() {
        Log.e(TAG,"loadInBackground");
        if (mUrl == null) {
            return null;
        }

        // Perform the network request, parse the response, and extract a list of earthquakes.
        List<Quake> earthquakes = QueryUtils.fetchEarthquakeData(mUrl);
        Log.e(TAG,"loadInBackground:fetchEarthquakeData()");
        return earthquakes;
        //this return value is used as the list<Quake> parameter in the onLoadFinished() method in the main thread by default

    }


}