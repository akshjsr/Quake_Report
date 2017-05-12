package com.example.android.quakereport;

/**
 * Created by Akash on 26-03-2017.
 */

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class QueryUtils {

    public static final String LOG_TAG = QueryUtils.class.getSimpleName();
   //REQUEST CONNECTION TO USGS WEBSITE URL


    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }
// returning one earthquake to update list
    public static List<Earthquake_info> fetchEarthquakedata(String requestUrl){
        URL url = createUrl(requestUrl);
        String jsonResponse = null;
        try
        {
            jsonResponse = makeHttpRequest(url);
        }catch (IOException e){
            Log.e(LOG_TAG,"Error in making http request",e);
        }
        List<Earthquake_info> earthquakes = extractEarthquakes(jsonResponse);
        return earthquakes;
    }
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.connect();
            //REQUEST IS SUCCESSFUL THEN READ FROM STREAM
            if(urlConnection.getResponseCode() == 200)
            {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }
            else
            {
                Log.e(LOG_TAG,"Error Status Code : "+urlConnection.getResponseCode());
            }

        } catch (IOException e) {
            // TODO: Handle the exception
            Log.e(LOG_TAG,"Caught IO Exception: "+e,e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // function must handle java.io.IOException here
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = null;
            try {
                line = reader.readLine();

                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
            }
        return output.toString();
    }

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Return a list of {@link Earthquake_info} objects that has been built up from
     * parsing a JSON response.
     */
    public static List<Earthquake_info> extractEarthquakes(String earthquakeJSON) {

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(earthquakeJSON)) {
            return null;
        }
        // Create an empty ArrayList that we can start adding earthquakes to
        List<Earthquake_info> earthquakes = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
             JSONObject root = new JSONObject(earthquakeJSON);

             JSONArray quakes = root.optJSONArray("features");

             for(int i=0;i<quakes.length();i++)
             {
                 JSONObject current_quake = quakes.getJSONObject(i);
                 JSONObject prop = current_quake.getJSONObject("properties");
                 String mag = prop.optString("mag");
                 String url= prop.optString("url");
                 double magnitude=prop.getDouble("mag");
                 String loc = prop.optString("place");
                 // Extract the value for the key called "url"

                    //seperating the location components in loc string
                    String placeobject = new String(loc);

                 //converting time in milli seconds to readable date and time
                 long timeinmillisec = prop.getLong("time");
                 Date dateobject= new Date(timeinmillisec);
                 SimpleDateFormat dateFormatter = new SimpleDateFormat("MM-DD ,yyyy");
                 String datetodisplay;
                 datetodisplay = dateFormatter.format(dateobject);
                 //passing the strings to EarthquakeAdapter.java to display in EarthquakeActivity.java using Earthquake_info.java
                 earthquakes.add(new Earthquake_info(magnitude,loc,timeinmillisec,url));


             }

            // TODO: Parse the response given by the SAMPLE_JSON_RESPONSE string and
            // build up a list of Earthquake objects with the corresponding data.

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        // Return the list of earthquakes
        return earthquakes;
    }
}
