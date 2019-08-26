package id.cnn.gomudik.util;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;


public class BaseGoMudikActivity extends AppCompatActivity {
    private static final String TAG = "BaseGoMudikActivity";
    private SharedPreferencesLocation spLoc;
    private Intent serviceGPS;
    private FusedLocationProviderClient mfusedLocationProviderClient;
    private LocationRequest mLocationRequest;
    private AtomicBoolean isFound = new AtomicBoolean(false);
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final int REQUEST_PERMISSION_SETTING = 4789;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: start ");
        spLoc = new SharedPreferencesLocation(BaseGoMudikActivity.this);
        getLocationPermission();
    }

    public Double getLatitude (){ return spLoc.getLatitude(); }

    public Double getLongitude(){ return spLoc.getLongitude(); }

    public void storeLocation(Double latitude, Double longitude){
        spLoc.storeLocation(latitude,longitude);
    }

    public Boolean isLocationFound(){
        return !getLatitude().equals(0.0) && !getLongitude().equals(0.0);
    }

    public Boolean isGPSOn(){
        LocationManager mLocationManager= (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public void startService(){
        if(!isServiceRunning()) {
            serviceGPS = new Intent(BaseGoMudikActivity.this, ServiceGPS.class);
            startService(serviceGPS);
        }
    }

    public void stopService(){
        if(isServiceRunning()) {
            stopService(serviceGPS);
        }
    }

    private boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("id.gomudik.gomudik.util.ServiceGPS".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public Boolean getIsFound(){
        return isFound.get();
    }

    public void requestLocationUpdates() {
        Log.d(TAG, "startLocationUpdate: start");
        mfusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationRequest  = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(1000) //1sec
                .setFastestInterval(500); //0.5sec
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
                Log.d(TAG, "Location: " + location.getLatitude() + " " + location.getLongitude());
                isFound.set(true);
                storeLocation(location.getLatitude(), location.getLongitude());
                removeLocationUpdates();
            }
        }
    };

    private void removeLocationUpdates(){
        Log.d(TAG, "stopLocationUpdates: start");
        mfusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
    }

    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: Getting Location Permission");
        String[] permission = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "getLocationPermission: Getting Location Permission Pass");
            } else {
                Log.d(TAG, "getLocationPermission: Request COARSE");
                ActivityCompat.requestPermissions(this, permission, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            Log.d(TAG, "getLocationPermission: REQUEST FINE");
            ActivityCompat.requestPermissions(this, permission, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: Called");
        //mPermissionGranted = false;
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        String permission = permissions[0];
                        if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                            Log.d(TAG, "onRequestPermissionsResult: Permission Failed");
                            //mPermissionGranted = false;
                            boolean showRationale = shouldShowRequestPermissionRationale(permission);
                            if (!showRationale) {
                                final AlertDialog.Builder dialog = new AlertDialog.Builder(BaseGoMudikActivity.this);
                                dialog.setTitle("Permission Denied")
                                        .setMessage("Without this permission GoMudik is unable to retrieve information that you need based on your location. Please allow the app to access your location.")
                                        .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                                intent.setData(uri);
                                                startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                                                paramDialogInterface.dismiss();
                                            }
                                        });
                                dialog.show();
                            } else {
                                Toast.makeText(this, "We need to access your device location, please try again", Toast.LENGTH_SHORT).show();
                            }
                        } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                            Log.d(TAG, "onRequestPermissionsResult: Permission granted");
                            startActivity(this.getIntent());
                        } else {
                            Toast.makeText(this, "Unknown request....", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_PERMISSION_SETTING:
                startActivity(this.getIntent());
                getLocationPermission();
                break;
            default:
                break;
        }
    }
}