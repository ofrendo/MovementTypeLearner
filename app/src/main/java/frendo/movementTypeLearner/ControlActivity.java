package frendo.movementTypeLearner;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Date;

import frendo.movementTypeLearner.db.DBHelper;
import frendo.movementTypeLearner.util.Constants;

public class ControlActivity extends AppCompatActivity {

    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbHelper = new DBHelper(this);

        onCreateUI();
        startLocationBroadcastListener();

        Log.d("CURRENT TS", "" + System.currentTimeMillis());
    }

    private void onCreateUI() {
        Switch sw = (Switch) findViewById(R.id.switchTrack);
        final TextView tvTrackingStatus = (TextView) findViewById(R.id.textViewTrackingStatus);

        if (isServiceRunning(MovementTypeLearnerService.class)) {
            sw.setChecked(true);
            tvTrackingStatus.setText("Tracking status: TRACKING");
        }
        else {
            tvTrackingStatus.setText("Tracking status: NOT TRACKING");
        }

        final Intent serviceIntent = new Intent(this, MovementTypeLearnerService.class);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    startService(serviceIntent);
                    tvTrackingStatus.setText("Tracking status: TRACKING");
                }
                if (isChecked == false) {
                    stopService(serviceIntent);
                    tvTrackingStatus.setText("Tracking status: NOT TRACKING");
                }
            }
        });

        // Set button listener for exporting the DB
        final Button buttonExportDB = (Button) findViewById(R.id.buttonExportDB);
        buttonExportDB.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dbHelper.exportDB();
            }
        });

        // Set DB Size
        TextView tvDBSize = (TextView) findViewById(R.id.textViewDBSize);
        String dbSizeString = "DB Size on app open: " + String.format("%.2f", (double) dbHelper.getDBSize() / 1024 / 1024) + "MB";
        tvDBSize.setText(dbSizeString);
    }

    private void startLocationBroadcastListener() {
        final TextView trackingHistory = (TextView) findViewById(R.id.textViewTrackingHistory);

        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                double[] data = intent.getDoubleArrayExtra(Constants.BROADCAST_LOCATION_DATA);
                trackingHistory.setText(new Date((long) data[0]) + ": " + data[1] + "/" + data[2] +  " " + data[3] + "m/s");
            }
        };

        IntentFilter locationIntentFilter = new IntentFilter(Constants.BROADCAST_LOCATION_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, locationIntentFilter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_control, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
