package frendo.movementTypeLearner.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Oliver on 06.03.2016.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Locations.db";

    public static final String SQL_CREATE_LOCATIONS =
            "CREATE TABLE " + DBLocations.TABLE_NAME + " (" +
                    DBLocations.COLUMN_NAME_TIMESTAMP + " INT," +
                    DBLocations.COLUMN_NAME_LATITUDE + " REAL," +
                    DBLocations.COLUMN_NAME_LONGITUDE + " REAL," +
                    DBLocations.COLUMN_NAME_PROVIDER + " INT" +
                    ");";

    public static final String SQL_DELETE_LOCATIONS =
            "DROP TABLE IF EXISTS " + DBLocations.TABLE_NAME;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_LOCATIONS);
    }

    public void insertLocations(long ts, double lat, double lon, int VALUE_PROVIDER) {
        Log.d("DBHelper", "insertLocations");
        SQLiteDatabase db = getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(DBLocations.COLUMN_NAME_TIMESTAMP, ts);
        values.put(DBLocations.COLUMN_NAME_LATITUDE, lat);
        values.put(DBLocations.COLUMN_NAME_LONGITUDE, lon);
        values.put(DBLocations.COLUMN_NAME_PROVIDER, VALUE_PROVIDER);

        db.insert(DBLocations.TABLE_NAME, "null", values);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_LOCATIONS);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}
