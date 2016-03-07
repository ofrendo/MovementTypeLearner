package frendo.movementTypeLearner.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Created by Oliver on 06.03.2016.
 */
public class DBHelper extends SQLiteOpenHelper {

    private final Context context;
    public static final int DATABASE_VERSION = 4;
    public static final String DATABASE_NAME = "Locations.db";

    public static final String SQL_CREATE_LOCATIONS =
            "CREATE TABLE " + DBLocations.TABLE_NAME + " (" +
                    DBLocations.COLUMN_NAME_TIMESTAMP + " DATETIME DEFAULT(STRFTIME('%Y-%m-%d %H:%M:%f', 'NOW'))," +
                    DBLocations.COLUMN_NAME_LATITUDE + " REAL," +
                    DBLocations.COLUMN_NAME_LONGITUDE + " REAL," +
                    DBLocations.COLUMN_NAME_PROVIDER + " INTEGER" +
                    ");";

    public static final String SQL_DELETE_LOCATIONS =
            "DROP TABLE IF EXISTS " + DBLocations.TABLE_NAME;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_LOCATIONS);
    }

    public void insertLocations(double lat, double lon, int VALUE_PROVIDER) {
        Log.d("DBHelper", "insertLocations lat=" + lat + ", lon=" + lon);
        SQLiteDatabase db = getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        //values.put(DBLocations.COLUMN_NAME_TIMESTAMP, ts);
        values.put(DBLocations.COLUMN_NAME_LATITUDE, lat);
        values.put(DBLocations.COLUMN_NAME_LONGITUDE, lon);
        values.put(DBLocations.COLUMN_NAME_PROVIDER, VALUE_PROVIDER);

        db.insert(DBLocations.TABLE_NAME, "null", values);
    }

    public long getDBSize() {
        File f = context.getDatabasePath(DATABASE_NAME);
        return f.length();
    }

    public void exportDB() {
        Log.d("DBHelper", "Exporting DB...");
        File sd = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File data = Environment.getDataDirectory();
        FileChannel source = null;
        FileChannel destination = null;
        String currentDBPath = "/data/"+ "frendo.movementTypeLearner" +"/databases/"+ DATABASE_NAME;
        String backupDBPath = "V" + DATABASE_VERSION + "-" + DATABASE_NAME;
        File currentDB = new File(data, currentDBPath);
        File backupDB = new File(sd, backupDBPath);
        try {
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
            backupDB.setReadable(true, false);
            backupDB.setWritable(true);
            MediaScannerConnection.scanFile(
                    context,
                    new String[]{backupDB.getAbsolutePath()},
                    null, null);
            Toast.makeText(context, "DB Exported!", Toast.LENGTH_LONG).show();
            Log.d("DBHelper", "DB exported to " + backupDB.getAbsolutePath());
        } catch(IOException e) {
            Log.d("DBHelper", "Error exporting DB.");
            Toast.makeText(context, "Error exporting DB", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        Log.d("DBHelper", "onUpgrade");
        exportDB();
        db.execSQL(SQL_DELETE_LOCATIONS);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}
