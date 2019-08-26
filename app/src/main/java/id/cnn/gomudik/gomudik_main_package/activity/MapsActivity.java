package id.cnn.gomudik.gomudik_main_package.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import id.cnn.gomudik.R;
//import id.cnn.gomudik.api.GoMudikAPI;
//import id.cnn.gomudik.api.GoMudikInterface;
import id.cnn.gomudik.api.MapsAPI;
import id.cnn.gomudik.api.MapsInterface;
import id.cnn.gomudik.gomudik_main_package.adapter.GroupMemberLocationAdapter;
//import id.cnn.gomudik.gomudik_main_package.adapter.ListNearbyAdapter;
import id.cnn.gomudik.gomudik_main_package.adapter.ListNearbyByGoogleAdapter;
import id.cnn.gomudik.gomudik_main_package.model.GetLocationByGroup;
import id.cnn.gomudik.gomudik_main_package.model.GetNearby;
import id.cnn.gomudik.gomudik_main_package.model.MapsGetNearby;
import id.cnn.gomudik.gomudik_user_and_group_package.activity.group_chat.GroupListActivity;
import id.cnn.gomudik.util.BaseGoMudikActivity;
import id.cnn.gomudik.util.DifferenceTime;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends BaseGoMudikActivity implements OnMapReadyCallback,View.OnClickListener{

    private static final String TAG = "MapsActivity";
    //permission
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final String PROVIDER = "GoMudik";
    //code
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final String DEV_KEY = "AIzaSyBd9lbrJaky_AkV4bQ1HvlMREvlWn2TYyI";
    private static final String DEFAULT_RADIUS = "5000";
    //vars
    private boolean mPermissionGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mfusedLocationProviderClient;
    private static final float DEFAULT_ZOOM = 15f;
    private Location mLoc;
    private LocationRequest mLocationRequest;
    //widget
    private EditText mSearch;
    private BottomSheetBehavior bottomSheetBehavior, bottomSheetList, bottomSheetZoomNearby;
    private Marker marker;
    private RelativeLayout loadingMap, relLayout1, cage_image;
    private FloatingActionButton currLoc, exploreTap, fab;
    private AtomicBoolean loadWidget = new AtomicBoolean(false);
    private AtomicBoolean onSearch = new AtomicBoolean(false);
    private AtomicBoolean onZoomIn = new AtomicBoolean(false);
    private AtomicBoolean onGetNearby = new AtomicBoolean(false);
    private ImageView ic_magnify;
    //private List<GetNearby.Data> listGetNearby = new ArrayList<>();
    private List<MapsGetNearby.Results> listGetNearbyByGoogle = new ArrayList<>();
    private TextView zoomInPlaceName, zoomInPlaceAddress, zoomInPlacePhone, zoomInPlaceDistance;

    //private String url="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mSearch = findViewById(R.id.input_search);
        ic_magnify = findViewById(R.id.ic_magnify);
        loadingMap = findViewById(R.id.loading_map);
        relLayout1 = findViewById(R.id.relLayout1);
        cage_image = findViewById(R.id.cage_image);
        cage_image.setOnClickListener(this);
        RelativeLayout buttonPolice = findViewById(R.id.button_police);
        buttonPolice.setOnClickListener(this);
        RelativeLayout buttonHospital = findViewById(R.id.button_hospital);
        buttonHospital.setOnClickListener(this);
        RelativeLayout buttonGasStation = findViewById(R.id.button_gas_station);
        buttonGasStation.setOnClickListener(this);
        RelativeLayout buttonMore = findViewById(R.id.button_more);
        buttonMore.setOnClickListener(this);
        currLoc = findViewById(R.id.button_curr_loc);
        fab = findViewById(R.id.button_group);
        exploreTap = findViewById(R.id.button_explore);
        currLoc.hide();
        fab.hide();
        exploreTap.hide();
        relLayout1.setVisibility(View.GONE);

        final View bottomsheet = findViewById(R.id.bottomSheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomsheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        final View bottomsheetlist = findViewById(R.id.bottomSheetList);
        final ImageView arrow = bottomsheetlist.findViewById(R.id.image_close);
        arrow.setOnClickListener(this);
        bottomSheetList = BottomSheetBehavior.from(bottomsheetlist);
        bottomSheetList.setState(BottomSheetBehavior.STATE_HIDDEN);

        final View bottom_sheet_zoom_nearby = findViewById(R.id.bottomSheetZoomNearby);
        zoomInPlaceName = bottom_sheet_zoom_nearby.findViewById(R.id.placeName);
        zoomInPlaceAddress = bottom_sheet_zoom_nearby.findViewById(R.id.placeAddress);
        zoomInPlacePhone = bottom_sheet_zoom_nearby.findViewById(R.id.placeTelephone);
        zoomInPlaceDistance = bottom_sheet_zoom_nearby.findViewById(R.id.placeDistance);
        bottomSheetZoomNearby = BottomSheetBehavior.from(bottom_sheet_zoom_nearby);
        bottomSheetZoomNearby.setState(BottomSheetBehavior.STATE_HIDDEN);
        getLocationPermission();
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    //Check Permission Maps
    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: Getting Location Permission");
        String[] permission = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "getLocationPermission: Getting Location Permission Pass");
                mPermissionGranted = true;
                initMaps();
            } else {
                Log.d(TAG, "getLocationPermission: Request COARSE");
                ActivityCompat.requestPermissions(this, permission, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            Log.d(TAG, "getLocationPermission: REQUEST FINE");
            ActivityCompat.requestPermissions(this, permission, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    //If there is no permission
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: Called");
        mPermissionGranted = false;
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                            Log.d(TAG, "onRequestPermissionsResult: Permission Failed");
                            mPermissionGranted = false;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: Permission Granted");
                    mPermissionGranted = true;
                    initMaps();
                }
            }
        }
    }

    //Initialize Maps
    public void initMaps() {
        Log.d(TAG, " initMaps: Initialize Map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapsActivity.this);
    }

    //Where Maps Ready
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: Map is Ready");
        loadingMap.setVisibility(View.GONE);
        mMap = googleMap;
        if (mPermissionGranted) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mfusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MapsActivity.this);
            mLoc = new Location(PROVIDER);
            checkGPS();
        }
    }

    public void checkGPS(){
        if (isGPSOn()) {
            if(mPermissionGranted){
                loadingMap.setVisibility(View.VISIBLE);
                startService();
                startLocationUpdate();
                if (isLocationFound()) {
                    Log.d(TAG, "onMapReady: loc ready");
                    mLoc.setLatitude(getLatitude());
                    mLoc.setLongitude(getLongitude());
                    moveCamera(new LatLng(getLatitude(), getLongitude()));
                    if(!loadWidget.get()){
                        loadWidget.set(true);
                        generateDisplay();
                    }
                }
            }
        } else {
            Log.d(TAG, "showAlert: Start");
            final AlertDialog.Builder dialog = new AlertDialog.Builder(MapsActivity.this);
            dialog.setTitle("Enable Location")
                    .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                            "use this app")
                    .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            Log.d(TAG, "showAlert: go to location settings");
                            settingGPS();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            int dur = Toast.LENGTH_SHORT;
                            Toast.makeText(MapsActivity.this, "We need to access your location, please turn on your GPS location and try again", dur).show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startActivity(new Intent(MapsActivity.this, MainActivity.class));
                                    finish();
                                }
                            },dur+1000);
                        }
                    });
            dialog.show();
        }
    }

    public void generateDisplay(){
        currLoc.show();
        exploreTap.show();
        fab.show();
        relLayout1.setVisibility(View.VISIBLE);
        initSearch();
        currLoc.setOnClickListener(this);
        fab.setOnClickListener(this);
        exploreTap.setOnClickListener(this);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetList.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState){
                    case BottomSheetBehavior.STATE_HIDDEN:
                        if(!onZoomIn.get()){
                            onGetNearby.set(false);
                            defaultDisplay();
                        }
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        stopLocationUpdates();
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        stopLocationUpdates();
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                Log.d(TAG, "onSlide: "+slideOffset);
            }
        });
    }

    public void settingGPS(){
        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(myIntent,123);
    }

    //Move GMap's Camera View
    private void moveCamera(LatLng latLng){
        if(loadingMap.getVisibility() == View.VISIBLE) {
            loadingMap.setVisibility(View.GONE);
        }
        Log.d(TAG,"moveCamera initialized: lat:"+latLng.latitude+" long:"+latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,DEFAULT_ZOOM));
    }

    //Search Features
    private void initSearch(){
        Log.d(TAG,"init: Initializing");
        mSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                //return false;
                if(actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN || event.getAction() == KeyEvent.KEYCODE_ENTER){
                    //execute method
                    geoLocate();
                }
                return false;
            }
        });
        mSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable str) {
                if(str.toString().trim().length()>0){
                    ic_magnify.setImageResource(R.drawable.ic_close);
                    onSearch.set(true);
                }else{
                    ic_magnify.setImageResource(R.drawable.ic_magnify);
                    onSearch.set(false);
                }
            }
        });
    }

    private void geoLocate(){
        Log.d(TAG,"geoLocate: GeoLocating...");
        closeKeyboard();
        if(bottomSheetList.getState() != BottomSheetBehavior.STATE_HIDDEN){
            bottomSheetList.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
        if(bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN){
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
        loadingMap.setVisibility(View.VISIBLE);
        String searchString = mSearch.getText().toString();
        Geocoder geocoder = new Geocoder(MapsActivity.this);
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(searchString,1);
        } catch(IOException e){
            Toast.makeText(this,"Error: "+ e.getMessage(),Toast.LENGTH_SHORT).show();
        }
        if(list.size() > 0){
            showSearch();
            Address address = list.get(0);

            Log.d(TAG,"geoLocate: Places= "+list);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(new LatLng(address.getLatitude(),address.getLongitude()));
            markerOptions.title(address.getFeatureName());
            marker = mMap.addMarker(markerOptions);
            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()));
        } else {
            Toast.makeText(this,"No place found...",Toast.LENGTH_SHORT).show();
        }
    }

    private void startLocationUpdate(){
        mLocationRequest  = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10000) //10sec
                .setFastestInterval(5000); //5sec
        // Request location updates
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                mfusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                mMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                getLocationPermission();
            }
        }
        else {
            mfusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            mMap.setMyLocationEnabled(true);
        }
    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if(!onGetNearby.get()) {
                if (locationList.size() > 0) {
                    //The last location in the list is the newest
                    Location location = locationList.get(locationList.size() - 1);
                    mLoc = location;
                    Log.i("MapsActivity", "Location: " + location.getLatitude() + " " + location.getLongitude());
                    storeLocation(location.getLatitude(), location.getLongitude());
                    if (!loadWidget.get()) {
                        loadWidget.set(true);
                        generateDisplay();
                    }
                    moveCamera(new LatLng(location.getLatitude(), location.getLongitude()));
                }
            }
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_curr_loc:
                moveCamera(new LatLng(mLoc.getLatitude(),mLoc.getLongitude()));
                break;
            case R.id.button_more:
                startActivityForResult(new Intent(MapsActivity.this, CategoryActivity.class),100);
                break;
            case R.id.button_police:
                getNearbyByGoogleAPI("Polsek/Polres",generateURLForGetNearby("police","Police station"));
                //getNearbyByGoMudikAPI("5","Polsek/Polres");
                break;
            case R.id.button_hospital:
                getNearbyByGoogleAPI("Rumah Sakit/Klinik",generateURLForGetNearby("hospital","General hospital"));
                //getNearbyByGoMudikAPI("10","Rumah Sakit/Klinik");
                break;
            case R.id.button_gas_station:
                getNearbyByGoogleAPI("SPBU",generateURLForGetNearby("gas_station","Gas station"));
                //getNearbyByGoMudikAPI("3","SPBU");
                break;
            case R.id.listTitleCage:
                if(bottomSheetList.getState() != BottomSheetBehavior.STATE_HIDDEN){
                    if(bottomSheetList.getState() == BottomSheetBehavior.STATE_COLLAPSED){
                        bottomSheetList.setState(BottomSheetBehavior.STATE_EXPANDED);
                    } else {
                        bottomSheetList.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }
                }
                break;
            case R.id.button_explore:
                if(bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN || bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED ){
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                }
                break;
            case R.id.image_close:
                if(bottomSheetList.getState() != BottomSheetBehavior.STATE_HIDDEN){
                    bottomSheetList.setState(BottomSheetBehavior.STATE_HIDDEN);
                    onGetNearby.set(false);
                    defaultDisplay();
                }
                break;
            case R.id.button_group:
                if(bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN){
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                }
                startActivityForResult(new Intent(MapsActivity.this, GroupListActivity.class),200);
                break;
            case R.id.cage_image:
                if(onSearch.get()){
                    closeSearch();
                } if(onZoomIn.get()){
                    //getNearbyData();
                    getNearbyDataByGoogle();
                    if(bottomSheetZoomNearby.getState() != BottomSheetBehavior.STATE_HIDDEN ){
                        bottomSheetZoomNearby.setState(BottomSheetBehavior.STATE_HIDDEN);
                    }
                    onZoomIn.set(false);
                }
                break;
            default:break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 100:
                if(data != null) {
                    //getNearbyByGoMudikAPI(data.getStringExtra("id"),data.getStringExtra("content"));
                    getNearbyByGoogleAPI(data.getStringExtra("content"), generateURLForGetNearby(data.getStringExtra("type"), data.getStringExtra("keyword")));
                }
                break;
            case 200:
                if(data != null) {
                    stopLocationUpdates();
                    Bundle b = data.getExtras();
                    ArrayList<GetLocationByGroup.Data> selected;
                    if (b != null) {
                        selected = (ArrayList<GetLocationByGroup.Data>) b.getSerializable("data");
                        removeMarker();
                        int limit = selected.size();
                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        for (int i = 0; i < limit; i++) {
                            if (selected.get(i).getLatitude() != null && selected.get(i).getLongitude() != null) {
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(new LatLng(Double.valueOf(selected.get(i).getLatitude()), Double.valueOf(selected.get(i).getLongitude())));
                                markerOptions.title(selected.get(i).getFullname());
                                DifferenceTime dt = new DifferenceTime(selected.get(i).getCreated());
                                dt.executeTimeChat();
                                marker = mMap.addMarker(markerOptions);
                                builder.include(marker.getPosition());
                            }
                        }
                        //add user position
                        builder.include(new LatLng(mLoc.getLatitude(), mLoc.getLongitude()));
                        moveMarker(builder.build());
                        LinearLayoutManager layoutManager = new LinearLayoutManager(MapsActivity.this);
                        RecyclerView cageListNearby = findViewById(R.id.cage_list_nearby);
                        cageListNearby.setHasFixedSize(true);
                        cageListNearby.setLayoutManager(layoutManager);
                        GroupMemberLocationAdapter adapter = new GroupMemberLocationAdapter(selected, MapsActivity.this);
                        cageListNearby.setAdapter(adapter);
                        showDisplay();
                    } else {
                        Log.d(TAG, "onCreate: error");
                        Toast.makeText(MapsActivity.this, "data is empty", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case 123:
                checkGPS();
                break;
            default:
                break;
        }
    }

    private void removeMarker(){
        if(marker != null){
            mMap.clear();
        }
    }

    private void moveMarker(LatLngBounds bounds){
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width*0.25);
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,width,height,padding);
        mMap.animateCamera(cu);
    }

    private void getNearbyByGoogleAPI(final String content, String url){
        onGetNearby.set(true);
        loadingMap.setVisibility(View.VISIBLE);
        if(mLoc != null){
            stopLocationUpdates();
            MapsInterface maps = MapsAPI.getMapsAPI().create(MapsInterface.class);
            Call<MapsGetNearby> call = maps.getNearby(url);
            call.enqueue(new Callback<MapsGetNearby>() {
                @Override
                public void onResponse(@NonNull Call<MapsGetNearby> call, @NonNull Response<MapsGetNearby> response) {
                    if(response.isSuccessful()){
                        if(response.body() != null) {
                            if(response.body().getStatus().equalsIgnoreCase("OK")) {
                                Log.d(TAG, "onResponse: success");
                                if (!listGetNearbyByGoogle.isEmpty()) {
                                    listGetNearbyByGoogle.clear();
                                }
                                listGetNearbyByGoogle = response.body().getResults();
                                getNearbyDataByGoogle();
                            } else {
                                Toast.makeText(MapsActivity.this,"No "+content+" found around you, try again later!",Toast.LENGTH_SHORT).show();
                                cancelDisplay();
                            }
                        } else {
                            Toast.makeText(MapsActivity.this,"No data",Toast.LENGTH_SHORT).show();
                            cancelDisplay();
                        }
                    } else {
                        Toast.makeText(MapsActivity.this,"Response failure from server",Toast.LENGTH_SHORT).show();
                        cancelDisplay();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<MapsGetNearby> call, @NonNull Throwable t) {
                    Log.d(TAG, "onFailure: Fail get data: "+t.toString());
                    Toast.makeText(MapsActivity.this,"Connection Failure: Failed to get around you, try again later.",Toast.LENGTH_SHORT).show();
                    cancelDisplay();
                }
            });
        } else {
            Toast.makeText(MapsActivity.this,"Failed to get "+content+" around you, because your location is not found! ",Toast.LENGTH_SHORT).show();
            cancelDisplay();
        }
    }

//    private void getNearbyByGoMudikAPI(String id_categories, final String content){
//        Log.d(TAG, "getNearbyByGoMudikAPI: start");
//        onGetNearby.set(true);
//        loadingMap.setVisibility(View.VISIBLE);
//        if(mLoc != null){
//            stopLocationUpdates();
//            GoMudikInterface goMudikInterface = GoMudikAPI.getAPI().create(GoMudikInterface.class);
//            Call<GetNearby> call = goMudikInterface.findNearby(mLoc.getLatitude(),mLoc.getLongitude(),1,id_categories);
//            call.enqueue(new Callback<GetNearby>() {
//                @Override
//                public void onResponse(Call<GetNearby> call, Response<GetNearby> response) {
//                    if(response.isSuccessful() && response.body() != null){
//                        if(response.body().getTotal_data() > 0) {
//                            if (!listGetNearby.isEmpty()) {
//                                listGetNearby.clear();
//                            }
//                            listGetNearby = response.body().getData();
//                            getNearbyData();
//                        } else {
//                            Toast.makeText(MapsActivity.this,"No "+content+" found around you, try again later!",Toast.LENGTH_SHORT).show();
//                            cancelDisplay();
//                        }
//                    } else {
//                        Log.d(TAG, "onResponse: get nearby is not success!");
//                        Toast.makeText(MapsActivity.this,"Failed to get "+content+" around you, try again later!",Toast.LENGTH_SHORT).show();
//                        cancelDisplay();
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<GetNearby> call, Throwable t) {
//                    Log.d(TAG, "onFailure: Fail get data: "+t.toString());
//                    Toast.makeText(MapsActivity.this,"Connection Failure: Failed to get around you, try again later.",Toast.LENGTH_SHORT).show();
//                    cancelDisplay();
//                }
//            });
//        } else {
//            Toast.makeText(MapsActivity.this,"Failed to get "+content+" around you, because your location is not found! ",Toast.LENGTH_SHORT).show();
//            cancelDisplay();
//        }
//    }

//    private void getNearbyData(){
//        Log.d(TAG, "onResponse: success");
//        int limit = listGetNearby.size();
//        if(limit > 0){
//            Log.d(TAG, "onResponse: get nearby success with total result: "+ limit);
//            removeMarker();
//            LatLngBounds.Builder buider = new LatLngBounds.Builder();
//            for(int i = 0; i<limit; i++){
//                Log.d(TAG, "onResponse: latlng["+i+"]: "+listGetNearby.get(i).getLatitude()+","+listGetNearby.get(i).getLongitude());
//                MarkerOptions markerOptions = new MarkerOptions();
//                markerOptions.position(new LatLng(listGetNearby.get(i).getLatitude(),listGetNearby.get(i).getLongitude()));
//                markerOptions.title(listGetNearby.get(i).getName());
//                marker = mMap.addMarker(markerOptions);
//                buider.include(marker.getPosition());
//            }
//            //add user position
//            buider.include(new LatLng(mLoc.getLatitude(),mLoc.getLongitude()));
//            moveMarker(buider.build());
//            LinearLayoutManager layoutManager = new LinearLayoutManager(MapsActivity.this);
//            RecyclerView cageListNearby = findViewById(R.id.cage_list_nearby);
//            cageListNearby.setHasFixedSize(true);
//            cageListNearby.setLayoutManager(layoutManager);
//            ListNearbyAdapter adapter = new ListNearbyAdapter(listGetNearby, MapsActivity.this);
//            cageListNearby.setAdapter(adapter);
//            showDisplay();
//        } else{
//            Log.d(TAG, "onResponse: No place found around you "+limit);
//            Toast.makeText(MapsActivity.this,"No place found around you, try again later!",Toast.LENGTH_SHORT).show();
//            cancelDisplay();
//        }
//    }

    private void getNearbyDataByGoogle(){
        Log.d(TAG, "onResponse: success");
        int limit = listGetNearbyByGoogle.size();
        if(limit > 0){
            Log.d(TAG, "onResponse: get nearby success with total result: "+ limit);
            removeMarker();
            LatLngBounds.Builder buider = new LatLngBounds.Builder();
            for(int i = 0; i<limit; i++){
                Log.d(TAG, "onResponse: latlng["+i+"]: "+listGetNearbyByGoogle.get(i).getGeometry().getLocation().getLat()+","+listGetNearbyByGoogle.get(i).getGeometry().getLocation().getLng());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(new LatLng(listGetNearbyByGoogle.get(i).getGeometry().getLocation().getLat(),listGetNearbyByGoogle.get(i).getGeometry().getLocation().getLng()));
                markerOptions.title(listGetNearbyByGoogle.get(i).getName());
                marker = mMap.addMarker(markerOptions);
                buider.include(marker.getPosition());
            }
            //add user position
            buider.include(new LatLng(mLoc.getLatitude(),mLoc.getLongitude()));
            moveMarker(buider.build());
            LinearLayoutManager layoutManager = new LinearLayoutManager(MapsActivity.this);
            RecyclerView cageListNearby = findViewById(R.id.cage_list_nearby);
            cageListNearby.setHasFixedSize(true);
            cageListNearby.setLayoutManager(layoutManager);
            ListNearbyByGoogleAdapter adapter = new ListNearbyByGoogleAdapter(listGetNearbyByGoogle, MapsActivity.this);
            cageListNearby.setAdapter(adapter);
            showDisplay();
        } else{
            Log.d(TAG, "onResponse: No place found around you "+limit);
            Toast.makeText(MapsActivity.this,"No place found around you, try again later!",Toast.LENGTH_SHORT).show();
            cancelDisplay();
        }
    }

    private void stopLocationUpdates(){
        Log.d(TAG, "stopLocationUpdates: start");
        if(mfusedLocationProviderClient != null){
            mfusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
        }
    }

    private void cancelDisplay(){
        loadingMap.setVisibility(View.GONE);
        startLocationUpdate();
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private void defaultDisplay(){
        relLayout1.setVisibility(View.VISIBLE);
        removeMarker();
        startLocationUpdate();
        currLoc.show();
        fab.show();
        exploreTap.show();
    }

    private void showDisplay(){
        relLayout1.setVisibility(View.GONE);
        bottomSheetList.setState(BottomSheetBehavior.STATE_COLLAPSED);
        loadingMap.setVisibility(View.GONE);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        currLoc.hide();
        fab.hide();
        exploreTap.hide();
    }

    private void showSearch(){
        removeMarker();
        loadingMap.setVisibility(View.GONE);
        stopLocationUpdates();
        ic_magnify.setImageResource(R.drawable.ic_close);
        onSearch.set(true);
        currLoc.hide();
        fab.hide();
        exploreTap.hide();
    }

    private void closeSearch(){
        onSearch.set(false);
        ic_magnify.setImageResource(R.drawable.ic_magnify);
        mSearch.getText().clear();
        moveCamera(new LatLng(mLoc.getLatitude(),mLoc.getLongitude()));
        defaultDisplay();
    }

    public void closeKeyboard(){
        View view = MapsActivity.this.getCurrentFocus();
        if(view != null){
            InputMethodManager imm = (InputMethodManager)MapsActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
            if(imm != null){
                imm.hideSoftInputFromWindow(view.getWindowToken(),0);
            }
        }
    }

    public void zoomInGroupMember(Double lat, Double longt){
        LatLngBounds.Builder buider = new LatLngBounds.Builder();
        buider.include(new LatLng(lat,longt));
        buider.include(new LatLng(mLoc.getLatitude(),mLoc.getLongitude()));
        moveMarker(buider.build());
    }

    public void zoomInNearby(GetNearby.Data data){
        onZoomIn.set(true);
        removeMarker();

        if(bottomSheetList.getState() != BottomSheetBehavior.STATE_HIDDEN){
            bottomSheetList.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
        if(relLayout1.getVisibility() != View.VISIBLE){
            relLayout1.setVisibility(View.VISIBLE);
            mSearch.setText(data.getName());
        }
        LatLngBounds.Builder buider = new LatLngBounds.Builder();
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(data.getLatitude(),data.getLongitude()));
        markerOptions.title(data.getName());
        marker = mMap.addMarker(markerOptions);
        buider.include(marker.getPosition());
        buider.include(new LatLng(data.getLatitude(),data.getLongitude()));
        buider.include(new LatLng(mLoc.getLatitude(),mLoc.getLongitude()));
        moveMarker(buider.build());

        zoomInPlaceName.setText(data.getName());
        zoomInPlaceAddress.setText(data.getAddress());
        zoomInPlacePhone.setText(data.getTelephone());
        DecimalFormat round = new DecimalFormat("#.##");
        String dist = round.format(data.getDistance())+" km";
        zoomInPlaceDistance.setText(dist);
        if(bottomSheetZoomNearby.getState() != BottomSheetBehavior.STATE_EXPANDED){
            bottomSheetZoomNearby.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    public void zoomInNearbyByGoogle(MapsGetNearby.Results data){
        onZoomIn.set(true);
        removeMarker();

        if(bottomSheetList.getState() != BottomSheetBehavior.STATE_HIDDEN){
            bottomSheetList.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
        if(relLayout1.getVisibility() != View.VISIBLE){
            relLayout1.setVisibility(View.VISIBLE);
            mSearch.setText(data.getName());
        }
        LatLngBounds.Builder buider = new LatLngBounds.Builder();
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(data.getGeometry().getLocation().getLat(),data.getGeometry().getLocation().getLng()));
        markerOptions.title(data.getName());
        marker = mMap.addMarker(markerOptions);
        buider.include(marker.getPosition());
        buider.include(new LatLng(data.getGeometry().getLocation().getLat(),data.getGeometry().getLocation().getLng()));
        buider.include(new LatLng(mLoc.getLatitude(),mLoc.getLongitude()));
        moveMarker(buider.build());

        zoomInPlaceName.setText(data.getName());
        zoomInPlaceAddress.setText(data.getVicinity());
        zoomInPlacePhone.setVisibility(View.GONE);
        DecimalFormat round = new DecimalFormat("#.##");
        String dist = round.format(countDistance(data.getGeometry().getLocation().getLat(),data.getGeometry().getLocation().getLng()))+" km";
        zoomInPlaceDistance.setText(dist);
        if(bottomSheetZoomNearby.getState() != BottomSheetBehavior.STATE_EXPANDED){
            bottomSheetZoomNearby.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    public String generateURLForGetNearby(String type, String keyword){
        return "nearbysearch/json?location="+mLoc.getLatitude()+","+mLoc.getLongitude()+"&radius="+DEFAULT_RADIUS+"&type="+type+"&keyword="+keyword+"&key="+DEV_KEY;
    }

    public Double countDistance(Double latitude, Double longitude){
        return ( 3959 * Math.acos( Math.cos( Math.toRadians(mLoc.getLatitude()) ) * Math.cos( Math.toRadians( latitude ) ) * Math.cos( Math.toRadians( longitude ) - Math.toRadians(mLoc.getLongitude()) ) + Math.sin( Math.toRadians(mLoc.getLatitude()) ) * Math.sin( Math.toRadians( latitude ) ) ) );
    }

    @Override
    protected void onDestroy() {
        stopLocationUpdates();
        super.onDestroy();
    }
}