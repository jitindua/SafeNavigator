package com.app.nwhacks.safenavigator;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
//import java.net.URLConnection;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String org, dest;
    private static final String LOG_TAG = "MAP";
    DataExtractor dataExtractor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        try {

            dataExtractor = new DataTask().execute(R.raw.crimedata).get();
        }catch (Exception e){
            Log.i(LOG_TAG, e.toString());
        }

    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            org = extras.getString("org");
            dest = extras.getString("dest");
        }
        String YOUR_API_KEY = this.getString(R.string.google_maps_key);
        org = org.replaceAll(" ","%20");
        dest = dest.replaceAll(" ","%20");
        //String route = "https://maps.googleapis.com/maps/api/directions/json?origin="+org+"&destination="+dest+"&key="+YOUR_API_KEY;
        String route = "https://maps.googleapis.com/maps/api/directions/json?alternatives=true&origin="+org+"&destination="+dest;
        //route = "https://maps.googleapis.com/maps/api/directions/json?origin=Toronto&destination=Montreal";//&key="+"AIzaSyDweSI2IGmUOj80B22rk0oExx7MeLQ5LRs";
        //JSONObject jsonObject = getLocationInfo(route);
        //RetrieveFeedTask temp = new RetrieveFeedTask();
        JSONObject jsonObject = null;
        try {

                jsonObject = new RetrieveFeedTask().execute(route).get();
            ;
        }catch (Exception e){
            Log.i(LOG_TAG, e.toString());
        }
        if(jsonObject == null)
        {
            Log.i(LOG_TAG,"NULL json 1");
            Log.i(LOG_TAG,YOUR_API_KEY);
        }
        else {

            Log.i(LOG_TAG, "JSON Size:\n" + jsonObject.toString());
        }

        //
        Log.i(LOG_TAG, "Before Extractor \n");
        //InputStream is = getResources().openRawResource(R.raw.model);
        //InputStream is = getResources().openRawResource(R.raw.crimedata);
        //DataExtractor dataExtractor = new DataExtractor(is);

        Map<Integer, Double> scoreMap = null;

        try {

            scoreMap = new CalcTask().execute(jsonObject).get();
            ;
        }catch (Exception e){
            Log.i(LOG_TAG, e.toString());
        }

        ArrayList<ArrayList<LatLng>> pointsArrayList =  dataExtractor.GetLatLangList();
        Log.i(LOG_TAG, "After Extractor \n");

        int numRoutes = scoreMap.size();

        int minIndex = 0;
        Log.i(LOG_TAG, "#Route Score: " + numRoutes);
        for(int i=0; i<numRoutes; i++){
            if(scoreMap.get(i)!=null)
            {
                if(scoreMap.get(i)<scoreMap.get(minIndex))
                {
                    minIndex = i;
                }
                Log.i(LOG_TAG, "Score for " + i + ": " + scoreMap.get(i));
            }
        }




        // Add a marker in Sydney and move the camera
        /*
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        */
        int currIndex = 0;
        for(ArrayList<LatLng> routeLatLng : pointsArrayList) {

            PolylineOptions polyLineOptions = null;

            if(currIndex == minIndex)
            {
                polyLineOptions = new PolylineOptions().width(10).color(Color.BLUE).geodesic(true);
            }
            else
            {
                polyLineOptions = new PolylineOptions().width(10).color(Color.GRAY).geodesic(true);
            }


            for (LatLng latLng : routeLatLng) {
                polyLineOptions.add(latLng);
            }
            mMap.addPolyline(polyLineOptions);

            currIndex++;
        }
        //mMap.clear();
        LatLng source = pointsArrayList.get(0).get(0);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(source));


    }

    public JSONObject getLocationInfo(String route) {
        StringBuffer buffer = new StringBuffer();
        try {
            long currentTime = System.currentTimeMillis();
            //URL url = new URL("http://maps.google.com/maps/api/geocode/json?address=" + "Delhi" + "&sensor=false");
            URL url = new URL(route);
           //URL url = new URL("https://maps.googleapis.com/maps/api/geocode/json?address=" + "Delhi" +"&key=" + this.getString(R.string.google_maps_key));
            //URL url = new URL("https://maps.googleapis.com/maps/api/geocode/json?address=" + "Delhi");

            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {

                BufferedReader reader = null;

                InputStream inputStream = connection.getInputStream();
                //buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    Log.i(LOG_TAG, "NULL 1");
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    long elapsedTime = System.currentTimeMillis();
                    if(elapsedTime-currentTime>=5000) {
                        Log.i(LOG_TAG, "NULL 2");
                        return null;
                    }
                    buffer.append(line + "\n");
                }

                Log.i(LOG_TAG, buffer.toString());

                if (buffer.length() == 0) {
                    Log.i(LOG_TAG, "NULL 3");
                    return null;
                }

                //Log.d("Test", buffer.toString());
                //return buffer.toString();
            }
            else {
                //Log.i(TAG, "Unsuccessful HTTP Response Code: " + responseCode);
                Log.i(LOG_TAG, "NULL 4");
                return null;
            }
        } catch (MalformedURLException e) {
            Log.i(LOG_TAG, "NULL 5");
            return null;
        } catch (IOException e) {
            Log.i(LOG_TAG, "NULL 6");
            return null;
        } catch (Exception e) {
            Log.i(LOG_TAG, "NULL 7" + e.toString());
            return null;
        }


        //return false;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(buffer.toString());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.i(LOG_TAG, "NULL 8");
            return  null;
        }
        if(jsonObject!=null)
             Log.i(LOG_TAG, "Not null");
        else {
            Log.i(LOG_TAG, "NULL 9");
        }
        Log.i(LOG_TAG, "NULL 10");

        return jsonObject;
    }





    class RetrieveFeedTask extends AsyncTask<String, Void, JSONObject> {

        private Exception exception;

        protected JSONObject doInBackground(String... urls) {
            try {
                return getLocationInfo(urls[0]);
            } catch (Exception e) {
                this.exception = e;
                return null;
            }
        }

        protected void onPostExecute(JSONObject json) {
            // TODO: check this.exception
            // TODO: do something with the feed
        }
    }


    class DataTask extends AsyncTask<Integer, Void, DataExtractor> {

        private Exception exception;

        protected DataExtractor doInBackground(Integer... ids) {
            try {
                InputStream is = getResources().openRawResource(ids[0]);
                dataExtractor = new DataExtractor(is);
                return dataExtractor;
                //getLocationInfo(urls[0]);
            } catch (Exception e) {
                this.exception = e;
                return null;
            }
        }

        protected void onPostExecute(DataExtractor json) {
            // TODO: check this.exception
            // TODO: do something with the feed
        }
    }


    class CalcTask extends AsyncTask<JSONObject, Void, Map<Integer, Double>> {

        private Exception exception;

        protected Map<Integer, Double> doInBackground(JSONObject... jsos) {
            try {
                Map<Integer, Double> scoreMap = dataExtractor.calcScore(jsos[0]);
                return scoreMap;

                //getLocationInfo(urls[0]);
            } catch (Exception e) {
                this.exception = e;
                return null;
            }
        }

        protected void onPostExecute(DataExtractor json) {
            // TODO: check this.exception
            // TODO: do something with the feed
        }
    }


}



