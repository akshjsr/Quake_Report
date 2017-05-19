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

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.view.View;
import android.widget.AdapterView;
import java.util.ArrayList;
import java.util.List;
import android.net.Uri;
import android.content.Intent;
import android.widget.ProgressBar;
import android.widget.TextView;


public class EarthquakeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Earthquake_info>> {

    public static final String USGS_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&eventtype=earthquake&orderby=time&minmag=6&limit=10";
    public static final String LOG_TAG = EarthquakeActivity.class.getName();
    private static final int EARTHQUAKE_LOADER_ID = 1;
    private EarthquakeAdapter mAdapter;
    private TextView empty_state;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);


    // Find a reference to the {@link ListView} in the layout
    ListView earthquakeListView = (ListView) findViewById(R.id.list);

        empty_state = (TextView) findViewById(R.id.empty_view);
        earthquakeListView.setEmptyView(empty_state);
        // Create a new adapter that takes an empty list of earthquakes as input
       mAdapter = new EarthquakeAdapter(this, new ArrayList<Earthquake_info>());

        // Set the adapter on the {@link ListView}
    earthquakeListView.setAdapter(mAdapter);

    // Set an item click listener on the ListView, which sends an intent to a web browser
    // to open a website with more information about the selected earthquake.
    earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener()

    {

        public void onItemClick (AdapterView < ? > adapterView, View view,int position, long l){
        // Find the current earthquake that was clicked on

        Earthquake_info currentEarthquake = mAdapter.getItem(position);

        // Convert the String URL into a URI object (to pass into the Intent constructor)
        Uri earthquakeUri = Uri.parse(currentEarthquake.getUrl());
        // Create a new intent to view the earthquake URI
        Intent websiteIntent = new Intent(Intent.ACTION_VIEW, earthquakeUri);

        // Send the intent to launch a new activity
        startActivity(websiteIntent);
    }
    });
        /*EarthquakeAsyncTask task = new EarthquakeAsyncTask();
          task.execute(USGS_REQUEST_URL); */

        // Get a reference to the LoaderManager, in order to interact with loaders.
       /*
      */
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        NetworkInfo networkInfo=cm.getActiveNetworkInfo();
        if(networkInfo!=null && networkInfo.isConnected())
        {
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, this);
        }
         else
        {
            //hide the loading bar
            View loadingbar=(ProgressBar) findViewById(R.id.loadingbar);
            loadingbar.setVisibility(View.GONE);

            //display no interenet connection message
            empty_state.setText(R.string.no_internet);
        }
    }

    @Override
    public Loader onCreateLoader(int i, Bundle bundle) {
        // Create a new loader for the given URL
        Log.e(LOG_TAG,"Loader is being created");
            return new EarthquakeLoader(this, USGS_REQUEST_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<Earthquake_info>> loader, List<Earthquake_info> earthquakes) {
        Log.e(LOG_TAG,"Loader has finished loading .");
        // Clear the adapter of previous earthquake data

        mAdapter.clear();
        View loadingbar=(ProgressBar) findViewById(R.id.loadingbar);
        loadingbar.setVisibility(View.GONE);
        // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (earthquakes != null && !earthquakes.isEmpty()) {

            //hided the progressbar on successful load of list
               View loadingindicator=(ProgressBar) findViewById(R.id.loadingbar);
               loadingindicator.setVisibility(View.GONE);
            Log.e(LOG_TAG,"Earthquakes are added to list from loader");
            mAdapter.addAll(earthquakes);
        }
        empty_state.setText(R.string.no_earthquakes);
    }




    @Override
    public void onLoaderReset(Loader loader) {
           Log.e(LOG_TAG,"New Loader is being created !");
            mAdapter.clear();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}