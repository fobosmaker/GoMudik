package id.cnn.gomudik.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesLocation {
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";
    private Context _context;
    private final String SHARE_NAME = "locationsession";
    private final int MODE_PRIVATE = 0;
    private static final String TAG = "SPLocation";
    public SharedPreferencesLocation(Context context){
        this._context = context;
        sp = context.getSharedPreferences(SHARE_NAME, MODE_PRIVATE);
        editor = sp.edit();
    }

    public void storeLocation(Double latitude, Double longitude){
        editor.putString(KEY_LATITUDE,String.valueOf(latitude));
        editor.putString(KEY_LONGITUDE,String.valueOf(longitude));
        editor.commit();
    }

    public Double getLatitude(){
        return Double.valueOf(sp.getString(KEY_LATITUDE,"0"));
    }

    public Double getLongitude(){
        return Double.valueOf(sp.getString(KEY_LONGITUDE,"0"));
    }

    public void removeLocation(){
        editor.clear();
        editor.commit();
    }
}