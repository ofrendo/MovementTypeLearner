package frendo.movementTypeLearner.loc;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

/**
 * Created by Oliver on 05.03.2016.
 */
public class LocationHandler implements LocationListener {

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 1 meters
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 1; // 1 sec

    private Context context;
    private LocationCallback callback;

    protected LocationManager locationManager;
    private boolean gps_enabled;
    private boolean network_enabled;

    public LocationHandler(Context context, LocationCallback callback) {
        this.context = context;
        this.callback = callback;
        locationManager =  (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            Log.d("LocationHandler", "Init locationHandler...");
            //onLocationChanged(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));

            /*Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setAltitudeRequired(false);
            criteria.setBearingRequired(false);
            criteria.setCostAllowed(false);
            criteria.setSpeedRequired(false);
            criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);*/

            /*locationManager.requestLocationUpdates(
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES,
                    criteria, this, null);*/
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES,
                    this);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES,
                    this);
        }
        else {
            Log.d("LocationHandler", "Permissions not granted!");
        }
    }

    public void destroy() {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            locationManager.removeUpdates(this);
            Log.d("LocationHandler", "destroy");

        }

    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            Log.d("LocationHandler", "Sending location from provider " + location.getProvider() + " to callback...");
            callback.doCallback(location);
        } else {
            Log.d("LocationHandler", "Received null as location");
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("LocationHandler", "onProviderDisabled");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("LocationHandler", "onProviderEnabled");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras){
        Log.d("LocationHandler", "onStatusChanged: " + status);
    }


}
