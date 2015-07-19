package mibh.mis.oilandgasrequest.AllService;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;

import mibh.mis.oilandgasrequest.Data.PreferencesManager;

/**
 * Created by ponlakiss on 06/30/2015.
 */

public class GetLocation extends Service implements LocationListener {

    boolean isGPSEnabled = false;
    boolean isNetWorkEnabled = false;
    boolean canGetLoaction = false;

    Location location;
    static double latitude = 0;
    static double longitude = 0;
    static String locationName = "Location not found";

    private PreferencesManager prefManage;

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 5;

    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 3;

    protected LocationManager locationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("TEST SERVICE", "Create Service");
        getLocation();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("TEST SERVICE", "Stop Service");
        stopUsingGPS();
    }

    public Location getLocation() {
        try {
            prefManage = PreferencesManager.getInstance();

            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetWorkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (!isGPSEnabled && !isNetWorkEnabled) {
                //no network enabld
            } else {
                this.canGetLoaction = true;
                if (isNetWorkEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            prefManage.setValue(prefManage.latitude, String.valueOf(latitude));
                            prefManage.setValue(prefManage.longtitude, String.valueOf(longitude));
                            new getLocationName(latitude, longitude).execute();
                        }
                    }
                }
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                                prefManage.setValue(prefManage.latitude, String.valueOf(latitude));
                                prefManage.setValue(prefManage.longtitude, String.valueOf(longitude));
                                new getLocationName(latitude, longitude).execute();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.d("error Location", e.toString());
        }
        return location;
    }

    public void stopUsingGPS() {
        if (locationManager != null) {
            locationManager.removeUpdates(GetLocation.this);
        }
    }

    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }
        return latitude;
    }

    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }
        return longitude;
    }

    public String getName() {
        if (location != null) {
            return locationName;
        }
        return "Location not found";
    }

    public boolean canGetLocation() {
        return this.canGetLoaction;
    }

    @Override
    public void onLocationChanged(Location location) {

        latitude = location.getLatitude();
        longitude = location.getLongitude();

        Log.d("LocationChange", latitude + " " + longitude);

        prefManage = PreferencesManager.getInstance();

        if (canGetLoaction) {
            prefManage.setValue(prefManage.latitude, String.valueOf(latitude));
            prefManage.setValue(prefManage.longtitude, String.valueOf(longitude));
            new getLocationName(latitude, longitude).execute();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public class getLocationName extends AsyncTask<String, String, String> {

        double lat, lng;
        ArrayList<String> listLocationName;

        public getLocationName(double lat, double lng) {
            this.lat = lat;
            this.lng = lng;
            prefManage = PreferencesManager.getInstance();
        }

        @Override
        protected String doInBackground(String[] params) {
            String Locations = "";
            try {
                listLocationName = getFromLocation(lat, lng, 1);
                if (listLocationName.size() > 0) {
                    for (int i = 0; i < listLocationName.size(); ++i) {
                        Locations += (listLocationName.get(i) + " ");
                    }
                } else Locations = "Location not found";
                Log.d("Test Location Name", Locations);
            } catch (Exception e) {
                Log.d("Error Location", "Name");
                Locations = "Location not found";
            }
            prefManage.setValue(prefManage.locationname, Locations);
            return Locations;
        }
    }

    public static ArrayList<String> getFromLocation(double lat, double lng, int maxResult) throws Exception {
        ArrayList<String> AddressList;
        AddressList = new ArrayList<String>();
        String address = String.format(Locale.getDefault(), "http://maps.googleapis.com/maps/api/geocode/json?latlng=%1$f,%2$f&sensor=true&language=th", lat, lng);
        HttpGet httpGet = new HttpGet(address);

  /* Prepare HTTP Parameters */
        HttpParams httpParameters = new BasicHttpParams();

  /* Set the timeout in milliseconds until a connection is established. */
  /* The default value is zero, that means the timeout is not used.  */
        int timeoutConnection = 3000;
        try {
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        } catch (Exception ex) {
            Log.d("march", "err " + ex.getMessage());
        }

        Log.d("march", "timeoutConnection = 3000");
  /* Set the default socket timeout (SO_TIMEOUT)      */
  /* in milliseconds which is the timeout for waiting for data. */
        int timeoutSocket = 5000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
        Log.d("march", "timeoutSocket = 5000;");
        HttpClient client = new DefaultHttpClient(httpParameters);

        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            response = client.execute(httpGet);
            Log.d("march", "client.execute(httpGet)");
            if (response == null) {
                return AddressList;
            }

            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();

            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }

            JSONObject jsonObject = new JSONObject();
            jsonObject = new JSONObject(stringBuilder.toString());
            Log.d("march", "OK.equalsIgnoreCa");
            if ((jsonObject != null) && ("OK".equalsIgnoreCase(jsonObject.getString("status")))) {
            /* Get JSON result */
                JSONArray results = jsonObject.getJSONArray("results");
            /*for(int i = 0; i < results.length(); i++)*/
                int i = 0;
                if (results.length() >= 2) {
                    i = 1;
                }

                {
                    JSONObject jo = results.getJSONObject(i);
                    JSONArray jaa = jo.getJSONArray("address_components");

                    Log.d("march", "jaa" + String.valueOf(jaa.length()));
                    for (int j = 0; j < jaa.length(); j++) {
                        JSONObject jotwo = jaa.getJSONObject(j);

                        String str_long_name = jotwo.getString("long_name");
                        String str_Type = jotwo.getString("types");

                        if ((str_Type.indexOf("postal_code") == -1)
                                && (str_Type.indexOf("country") == -1)) {
                            byte[] encode = str_long_name.getBytes("ISO-8859-1");
                            String addr = new String(encode);

                            AddressList.add(addr);
                        }
                    }
                }
            }
            Log.d("march", "size " + String.valueOf(AddressList.size()));
            return AddressList;

        } catch (ClientProtocolException e) {
            Log.d("march", "Error calling Google geocode webservice.");
        } catch (IOException e) {
            Log.d("march", "Error calling Google geocode webservice.");
        } catch (JSONException e) {
            Log.d("march", "Error parsing Google geocode webservice response.");
        }
        return AddressList;
    }

}


