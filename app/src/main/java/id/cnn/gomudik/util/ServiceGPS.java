package id.cnn.gomudik.util;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.HashMap;
import java.util.List;

import id.cnn.gomudik.api.GetDataApiDefault;
import id.cnn.gomudik.api.GoMudikAPI;
import id.cnn.gomudik.api.GoMudikInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiceGPS extends Service{
    private static final String TAG = "ServiceGPS";
    private FusedLocationProviderClient mfusedLocationProviderClient;
    private LocationRequest mLocationRequest;
    private Session session;
    private SharedPreferencesLocation spLoc;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: start");
        session = new Session(getApplicationContext());
        spLoc = new SharedPreferencesLocation(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: start");
        mfusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        startLocationUpdate();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: start");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: start");
        return null;
    }

    private void startLocationUpdate() {
        Log.d(TAG, "startLocationUpdate: start");
        mLocationRequest  = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(120000)//2min
                .setFastestInterval(60000); //1min

        // Request location updates
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                Log.d(TAG, "startLocationUpdate: permission granted");
                mfusedLocationProviderClient.requestLocationUpdates(mLocationRequest,mLocationCallback , Looper.myLooper());
            } else {
                Log.d(TAG, "startLocationUpdate: permission retry");
            }
        } else {
            Log.d(TAG, "startLocationUpdate: straight permission");
            mfusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        }
    }
    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
        List<Location> locationList = locationResult.getLocations();
        if (locationList.size() > 0) {
            //The last location in the list is the newest
            Location location = locationList.get(locationList.size() - 1);
            Log.i(TAG, "Location: " + location.getLatitude() + " " + location.getLongitude());
            tracker(location);
        }
        }
    };

    public void tracker(Location location){
        //Insert into Shared Preference Location
        Log.d(TAG, "tracker: save to session");
        spLoc.storeLocation(location.getLatitude(), location.getLongitude());
        //Insert into database
        if(session.login()) {
            HashMap<String, String> map = session.getData();
            GoMudikInterface goMudikInterface = GoMudikAPI.getAPI().create(GoMudikInterface.class);
            Call<GetDataApiDefault> call = goMudikInterface.addUserLocation(map.get(session.KEY_TOKEN), map.get(session.KEY_ID), location.getLatitude(), location.getLongitude());
            call.enqueue(new Callback<GetDataApiDefault>() {
                @Override
                public void onResponse(Call<GetDataApiDefault> call, Response<GetDataApiDefault> response) {
                    if(response.isSuccessful()){
                        if(response.body() != null){
                            Log.d(TAG, "insertLocation: "+response.body().getMessage());
                        } else{
                            Log.d(TAG, "onResponse: Response has no body");
                        }
                    } else {
                        Log.d(TAG, "Response is failure");
                    }
                }

                @Override
                public void onFailure(Call<GetDataApiDefault> call, Throwable t) {
                    Log.d(TAG, "onFailure: insertLocation failed " + t.toString());
                }
            });
        }
    }
}