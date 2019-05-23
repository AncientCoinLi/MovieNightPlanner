package au.edu.rmit.movienightplanner.model;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

public class Distance extends AsyncTask<Context, Void, HashMap<Event, Integer>> {

    private static final String API_KEY = "AIzaSyCj86LmvJThMBnYQPSr29gzbRpRPJ3RzKc";
    private List<Event> events;

    public Distance(List<Event> events) {
        this.events = events;
    }

    public boolean isOneHourTravelFarAway(Event event) {


        return false;
    }


    @SuppressLint("MissingPermission")
    private Location getCurrentLocation(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        for (String s : providers) {
            System.out.println(s);
        }
        String locationProvider = null;
        if (providers.contains(LocationManager.GPS_PROVIDER)) {
            locationProvider = LocationManager.GPS_PROVIDER;
        } else if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            locationProvider = LocationManager.NETWORK_PROVIDER;
        } else {
            return null;
        }
        Intent intent = new Intent();
        locationManager.requestSingleUpdate(locationProvider, PendingIntent.getActivity(context,
                                                                                        0,intent,
                                                                                        0));
        Location location = locationManager.getLastKnownLocation(locationProvider);
        System.out.println("location == null "+(location == null) );
        return location;
    }


    /**
     * Override this method to perform a computation on a background thread. The specified
     * parameters are the parameters passed to {@link #execute} by the caller of this task.
     * <p>
     * This method can call {@link #publishProgress} to publish updates on the UI thread.
     *
     * @param contexts The parameters of the task.
     * @return A result, defined by the subclass of this task.
     * @see #onPreExecute()
     * @see #onPostExecute
     * @see #publishProgress
     */
    @Override
    protected HashMap<Event, Integer> doInBackground(Context... contexts) {
        Location currLocation;
        DecimalFormat df = new DecimalFormat("0.000000");
        HashMap<Event, Integer> eventDistance = new HashMap<>();
        HttpURLConnection httpURLConnection;
        try{
            for (Event event : events) {
                currLocation = getCurrentLocation(contexts[0]);
                if (currLocation == null) System.out.println("CURRENT LOCATION NULL");
                double latitude = 0;
                double longitude = 0;
                if (currLocation != null) {
                    latitude = currLocation.getLatitude();
                    longitude = currLocation.getLongitude();
                }
                String currLocationString = df.format(latitude) + "," + df.format(longitude);
                String urlString = String.format(
                        "https://maps.googleapis.com/maps/api/distancematrix/" +
                                "json?units=imperial&origins=%s&destinations=%s" +
                                "&mode=driving&key=%s",
                        currLocationString, event.getLocationString(),
                        API_KEY);
                System.out.println(urlString);
                URL url = new URL(urlString);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.connect();
                BufferedReader bufferedReader =
                        new BufferedReader(new InputStreamReader( httpURLConnection.getInputStream()));
                String result;
                StringBuffer sb = new StringBuffer();
                while ((result = bufferedReader.readLine()) != null) {
                    sb.append(result + System.lineSeparator());
                }
                //System.out.println(sb.toString().trim());
                JSONObject jsonObject = new JSONObject(sb.toString());
                int duration = jsonObject.getJSONArray("rows").getJSONObject(0).getJSONArray(
                        "elements").getJSONObject(0).getJSONObject("duration").getInt("value");
                System.out.println("DURATION = " + duration);
                httpURLConnection.disconnect();
                eventDistance.put(event, duration);
                System.out.println(event.getTitle() + " "+ duration);
            }
            return eventDistance;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (JSONException e) {
            e.printStackTrace();
        }




        return null;
    }
}
