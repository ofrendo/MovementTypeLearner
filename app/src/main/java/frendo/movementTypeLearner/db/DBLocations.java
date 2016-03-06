package frendo.movementTypeLearner.db;

import android.provider.BaseColumns;

/**
 * Created by Oliver on 06.03.2016.
 */
public class DBLocations implements BaseColumns {

    // Define location
    public static final String TABLE_NAME = "locations";
    public static final String COLUMN_NAME_TIMESTAMP = "ts";
    public static final String COLUMN_NAME_LATITUDE = "lat";
    public static final String COLUMN_NAME_LONGITUDE = "long";
    public static final String COLUMN_NAME_PROVIDER = "provider";

    public static final int VALUE_PROVIDER_GPS = 1;
    public static final int VALUE_PROVIDER_NETWORK = 2;

}
