package com.example.android.quakereport;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

/**
 * Created by Akash on 12-05-2017.
 */

public class EarthquakeLoader extends AsyncTaskLoader<List<Earthquake_info>> {

    private static final String LOG_TAG = EarthquakeLoader.class.getName();
    private String mUrl;
    public EarthquakeLoader(Context context,String url) {
        super(context);
        mUrl=url;
    }

    @Override
    protected void onStartLoading() {
        Log.e(LOG_TAG,"Loader Initiated");
        forceLoad();
    }

    @Override
    public List<Earthquake_info> loadInBackground() {
        // Don't perform the request if there are no URLs, or the first URL is null.
        if (mUrl == null) {
            Log.e(LOG_TAG,"Connection Problem!");
            return null;

        }
        Log.e(LOG_TAG,"Loader is loading.....");
        List<Earthquake_info> result = QueryUtils.fetchEarthquakedata(mUrl);
        return result;
    }
}
