package frendo.movementTypeLearner;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.IBinder;
import android.provider.SyncStateContract;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import frendo.movementTypeLearner.db.DBHelper;
import frendo.movementTypeLearner.db.DBLocations;
import frendo.movementTypeLearner.loc.LocationCallback;
import frendo.movementTypeLearner.loc.LocationHandler;
import frendo.movementTypeLearner.util.Constants;

public class MovementTypeLearnerService extends Service {

    private LocationHandler locationHandler = null;
    private DBHelper dbHelper = null;

    public MovementTypeLearnerService() {
        Log.d("MovementTypeService", "Initializing service...");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("MovementTypeService", "onStartCommand...");
        dbHelper = new DBHelper(this);

        locationHandler = new LocationHandler(this, new LocationCallback() {
            public void doCallback(Location location) {
                dbHelper.insertLocations(
                        //System.currentTimeMillis(),
                        location.getLatitude(),
                        location.getLongitude(),
                        (location.getProvider().equals("gps") ? DBLocations.VALUE_PROVIDER_GPS : DBLocations.VALUE_PROVIDER_NETWORK)
                );

                sendBroadcast(location);
                //"Current location: " + location.getLatitude() + "/" + location.getLongitude()
            }
        });

        return START_NOT_STICKY;
    }



    public void sendBroadcast(Location location) {
        double[] data = {
                System.currentTimeMillis(),
                location.getLongitude(),
                location.getLatitude(),
                location.getSpeed()
        };

        Intent broadcastIntent =
                new Intent(Constants.BROADCAST_LOCATION_ACTION)
                    .putExtra(Constants.BROADCAST_LOCATION_DATA, data);

        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
    }

    @Override
    public void onDestroy() {
        Log.d("MovementTypeService", "onDestroy");
        locationHandler.destroy();
        dbHelper.close();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }
}
