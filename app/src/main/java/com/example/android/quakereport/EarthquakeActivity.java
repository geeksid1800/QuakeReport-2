/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.quakereport;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


/* Implementing the LoaderCallbacks in our activity is a little more complex. First we need to say that EarthquakeActivity
   implements the LoaderCallbacks interface, along with a generic parameter specifying what the loader will return
    (in this case an Earthquake).
 */
public class EarthquakeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Quake>> {

    /**
     * Constant value for the earthquake loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int EARTHQUAKE_LOADER_ID = 1;

    /** query URL for earthquake data from the USGS dataset */

    /** let's modify the value of the USGS_REQUEST_URL constant in the EarthquakeActivity class to the base URI.
     *  Later we’ll use UriBuilder.appendQueryParameter() methods to add  additional parameters to the URI
     *  (such as JSON response format, 10 earthquakes requested, minimum magnitude value, and sort order).
     */
    private static final String USGS_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query";


    public static final String TAG = EarthquakeActivity.class.getName();

    /** TextView that is displayed when the list is empty */
    private TextView mEmptyStateTextView;



    //Made it global as it needs to be accessed by other class too
    private QuakeAdapter mAdapter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //To use the menu in your activity, you need to inflate the menu resource
        // (convert the XML resource into a programmable object) using MenuInflater.inflate()
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_settings){
            Intent SettingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(SettingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG,"OnCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        // Find a reference to the {@link ListView} in the layout
        ListView earthquakeListView = (ListView) findViewById(R.id.list);

        // Create a new adapter that takes an empty list of earthquakes as input
        mAdapter = new QuakeAdapter(this, new ArrayList<Quake>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        earthquakeListView.setAdapter(mAdapter);

        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        earthquakeListView.setEmptyView(mEmptyStateTextView);



        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {

            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getSupportLoaderManager();
            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            Log.e(TAG,"loaderManager.initLoader()");
            loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            // Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }


        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected earthquake.
        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current earthquake that was clicked on
                Quake currentEarthquake = mAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri earthquakeUri = Uri.parse(currentEarthquake.getLink());

                // Create a new intent to view the earthquake URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, earthquakeUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });


    }

    /*  We can replace the body of onCreateLoader() method to read the user’s latest preferences for the minimum magnitude,
        construct a proper URI with their preference, and then create a new Loader for that URI.
     */
    @NonNull
    @Override
    public Loader<List<Quake>> onCreateLoader(int i, Bundle bundle) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String minMagnitude = sharedPrefs.getString(
                getString(R.string.settings_min_magnitude_key),
                getString(R.string.settings_min_magnitude_default));

        String noOfResults = sharedPrefs.getString(
                getString(R.string.settings_number_of_results_key),
                getString(R.string.settings_number_of_results_default));

        Uri baseUri = Uri.parse(USGS_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("format", "geojson");
        uriBuilder.appendQueryParameter("limit", noOfResults);
        uriBuilder.appendQueryParameter("minmag", minMagnitude);
        uriBuilder.appendQueryParameter("orderby", "time");

        TextView urlText = findViewById(R.id.URLtext);
        urlText.setText(uriBuilder.toString());
        //put a text at the bottom to show the query used

        return new EarthquakeLoader(this, uriBuilder.toString());
        //from here the new loader is passed to EarthquakeLoader's constructor
    }

    @Override
    public void onLoadFinished(Loader<List<Quake>> loader, List<Quake> earthquakes) {

        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        // Clear the adapter of previous earthquake data
        mAdapter.clear();

        // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (earthquakes != null && !earthquakes.isEmpty()) {
            mAdapter.addAll(earthquakes);
            mAdapter.notifyDataSetChanged();
            Log.e(TAG,"OnLoadFinished");
        }



    }


    @Override
    public void onLoaderReset(@NonNull Loader<List<Quake>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
        Log.e(TAG,"OnLoaderReset");
    }


}
