package id.cnn.gomudik.gomudik_main_package.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codetroopers.betterpickers.OnDialogDismissListener;
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.github.msarhan.ummalqura.calendar.UmmalquraCalendar;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import id.cnn.gomudik.PrayTime;
import id.cnn.gomudik.R;
import id.cnn.gomudik.api.GoMudikAPI;
import id.cnn.gomudik.api.GoMudikInterface;
import id.cnn.gomudik.gomudik_ads.DialogAds;
import id.cnn.gomudik.gomudik_main_package.adapter.JadwalSalatAdapter;
import id.cnn.gomudik.gomudik_ads.model.GetAds;
import id.cnn.gomudik.gomudik_main_package.model.MenuJadwalSalatModel;
import id.cnn.gomudik.util.BaseGoMudikActivity;
import id.cnn.gomudik.util.DifferenceTime;
import retrofit2.Call;
import retrofit2.Response;

public class JadwalSalatActivity extends BaseGoMudikActivity {
    private TextView nextShalatSchedule, timeRemaining, tanggalSekarang, tanggalHijriah, kota;
    private static final String FRAG_TAG_DATE_PICKER = "007";
    private static final String TAG = "JadwalSalatActivity";
    private getLocationTask mGetLocationTask;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout contentFragment;
    private RelativeLayout topAds, bottomAds;
    private ImageView topAdsImage, bottomAdsImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jadwal_salat);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Jadwal Salat");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        nextShalatSchedule = findViewById(R.id.nextShalatSchedule);
        timeRemaining = findViewById(R.id.timeRemaining);
        tanggalSekarang = findViewById(R.id.tanggalSekarang);
        tanggalHijriah = findViewById(R.id.tanggalHijriah);
        contentFragment = findViewById(R.id.contentFragment);
        contentFragment.setVisibility(View.GONE);
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                checkGPS();
            }
        });
        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            final CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment()
                    .setFirstDayOfWeek(Calendar.SUNDAY)
                    .setDoneText("Select")
                    .setOnDateSetListener(new CalendarDatePickerDialogFragment.OnDateSetListener() {
                        @Override
                        public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
                            dialog.getSelectedDay();
                            Calendar newCalendar = Calendar.getInstance();
                            newCalendar.set(year,monthOfYear,dayOfMonth);
                            generateData(newCalendar, getLatitude(), getLongitude());
                        }
                    })
                    .setOnDismissListener(new OnDialogDismissListener() {
                        @Override
                        public void onDialogDismiss(DialogInterface dialoginterface) {
                            dialoginterface.dismiss();
                        }
                    });
            cdp.show(getSupportFragmentManager(), FRAG_TAG_DATE_PICKER);
            }
        });
        kota = findViewById(R.id.kota);

        topAds = findViewById(R.id.topAds);
        bottomAds = findViewById(R.id.bottomAds);
        topAdsImage = findViewById(R.id.top_ads_image);
        bottomAdsImage = findViewById(R.id.bottom_ads_image);

        checkGPS();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void init(){
        requestLocationUpdates();
        mGetLocationTask = new getLocationTask();
        mGetLocationTask.execute((Void)null);
    }

    public void settingGPS(){
        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(myIntent,123);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        finish();
        startActivity(this.getIntent());
    }

    public void checkGPS(){
        if (isGPSOn()) {
            init();
        } else {
            Log.d(TAG, "showAlert: Start");
            final AlertDialog.Builder dialog = new AlertDialog.Builder(JadwalSalatActivity.this);
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
                            locationNotFound(0);
                        }
                    });
            dialog.show();
        }
    }

    public void generateData(Calendar currCal,Double latitude, Double longitude){
        double timezone = (double) (Calendar.getInstance().getTimeZone().getOffset(Calendar.getInstance().getTimeInMillis())) / (1000 * 60 * 60);
        PrayTime prayers = new PrayTime();
        prayers.setTimeFormat(prayers.Time24);
        prayers.setCalcMethod(prayers.Custom);
        prayers.setAsrJuristic(prayers.Shafii);
        prayers.setAdjustHighLats(prayers.AngleBased);
        int[] offsets = {2, -4, 2, 2, 2, 2, 2}; //{Fajr,Sunrise,Dhuhr,Asr,Sunset,Maghrib,Isha}
        prayers.tune(offsets);
        generateJadwalSalat(prayers.getPrayerTimes(currCal, latitude, longitude, timezone));
        SimpleDateFormat format = new SimpleDateFormat("dd MMMM yyyy",Locale.ENGLISH);
        Calendar arabicCal = new UmmalquraCalendar(Locale.ENGLISH);
        arabicCal.setTime(currCal.getTime());

        //Format tanggal Hijriah
        UmmalquraCalendar hijrCal = new UmmalquraCalendar();
        hijrCal.setTime(currCal.getTime());
        String dateInHijr = hijrCal.get(Calendar.DAY_OF_MONTH) + " " + hijrCal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH) + ", " + hijrCal.get(Calendar.YEAR);
        tanggalHijriah.setText(dateInHijr);
        tanggalSekarang.setText(format.format(currCal.getTime()));

        //set kota
        kota.setText(getKota(latitude, longitude));
    }

    public void generateJadwalSalat(ArrayList prayers){
        //generate next jadwal salat & time remaining
        getNextSalatAndTime(prayers);
        //generate jadwal salat
        ArrayList<MenuJadwalSalatModel> list = new ArrayList<>();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
        try {
            Date d = df.parse(prayers.get(0).toString());
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            cal.add(Calendar.MINUTE, -10);
            list.add(new MenuJadwalSalatModel(R.drawable.jadwal_imsak, getString(R.string.imsak),df.format(cal.getTime()),true));
            list.add(new MenuJadwalSalatModel(R.drawable.jadwal_subuh,getString(R.string.subuh),prayers.get(0).toString(),true));
            list.add(new MenuJadwalSalatModel(R.drawable.jadwal_dzuhur,getString(R.string.dzuhur),prayers.get(2).toString(),true));
            list.add(new MenuJadwalSalatModel(R.drawable.jadwal_ashar,getString(R.string.ashar),prayers.get(3).toString(),true));
            list.add(new MenuJadwalSalatModel(R.drawable.jadwal_maghrib,getString(R.string.maghrib),prayers.get(5).toString(),true));
            list.add(new MenuJadwalSalatModel(R.drawable.jadwal_isya,getString(R.string.isya),prayers.get(6).toString(),true));
            RecyclerView.LayoutManager grid = new GridLayoutManager(this,3);
            grid.setAutoMeasureEnabled(true);
            RecyclerView cage = findViewById(R.id.cage);
            cage.setLayoutManager(grid);
            cage.setHasFixedSize(true);
            JadwalSalatAdapter adapter = new JadwalSalatAdapter(list);
            cage.setAdapter(adapter);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private String getKota(Double latitude, Double longitude){
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(JadwalSalatActivity.this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latitude,longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            if(addresses.size() > 0) {
                return addresses.get(0).getLocality();
            } else {
                return "Unknown Location";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Failed to get location, try again";
        }
    }

    private void getNextSalatAndTime(ArrayList prayers){
        SimpleDateFormat df = new SimpleDateFormat("HH:mm",Locale.ENGLISH);
        Date currentTime = Calendar.getInstance().getTime();
        String timeNow = df.format(currentTime);
        try {
            if (df.parse(timeNow).compareTo(df.parse(prayers.get(0).toString()))<0) {
                nextShalatSchedule.setText(R.string.subuh);
                DifferenceTime dt = new DifferenceTime(prayers.get(0).toString());
                dt.executeTimeRemaining();
                timeRemaining.setText(dt.getResultDefault());
            } else if (df.parse(timeNow).compareTo(df.parse(prayers.get(2).toString()))<0 && df.parse(timeNow).compareTo(df.parse(prayers.get(0).toString()))>0) {
                nextShalatSchedule.setText(R.string.dzuhur);
                DifferenceTime dt = new DifferenceTime(prayers.get(2).toString());
                dt.executeTimeRemaining();
                timeRemaining.setText(dt.getResultDefault());
            } else if (df.parse(timeNow).compareTo(df.parse(prayers.get(3).toString()))<0 && df.parse(timeNow).compareTo(df.parse(prayers.get(2).toString()))>0) {
                nextShalatSchedule.setText(R.string.ashar);
                DifferenceTime dt = new DifferenceTime(prayers.get(3).toString());
                dt.executeTimeRemaining();
                timeRemaining.setText(dt.getResultDefault());
            } else if (df.parse(timeNow).compareTo(df.parse(prayers.get(5).toString()))<0 && df.parse(timeNow).compareTo(df.parse(prayers.get(3).toString()))>0) {
                nextShalatSchedule.setText(R.string.maghrib);
                DifferenceTime dt = new DifferenceTime(prayers.get(5).toString());
                dt.executeTimeRemaining();
                timeRemaining.setText(dt.getResultDefault());
            } else if (df.parse(timeNow).compareTo(df.parse(prayers.get(6).toString()))<0 && df.parse(timeNow).compareTo(df.parse(prayers.get(5).toString()))>0) {
                nextShalatSchedule.setText(R.string.isya);
                DifferenceTime dt = new DifferenceTime(prayers.get(6).toString());
                dt.executeTimeRemaining();
                timeRemaining.setText(dt.getResultDefault());
            } else {
                nextShalatSchedule.setText(R.string.subuh);
                timeRemaining.setText("Esok hari");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void locationNotFound(int code){
        String message;
        if(code == 0){
            message = "We need to access your location, please turn on your GPS location and try again";
        } else {
            message = "We can't find your location, try again later.";
        }
        int dur = Toast.LENGTH_SHORT;
        Toast.makeText(JadwalSalatActivity.this, message, dur).show();
    }

    public void showProgress(Boolean show){
        if(show){
            swipeRefreshLayout.setRefreshing(true);
        } else {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void generateAds(GetAds response){
        if(response.getTotal_data() == 0){
            topAds.setVisibility(View.GONE);
            bottomAds.setVisibility(View.GONE);
        } else {
            if(response.getData().get(0).getId_active().equalsIgnoreCase("1")){
                topAds.setVisibility(View.VISIBLE);
                Picasso.get().load(Uri.parse("http:gomudik.id:81/".concat(response.getData().get(0).getImage_link()))).into(topAdsImage);
            } else {
                topAds.setVisibility(View.GONE);
            }

            if(response.getData().get(1).getId_active().equalsIgnoreCase("1")){
                bottomAds.setVisibility(View.VISIBLE);
                Picasso.get().load(Uri.parse("http:gomudik.id:81/".concat(response.getData().get(1).getImage_link()))).into(bottomAdsImage);
            } else {
                bottomAds.setVisibility(View.GONE);
            }

            if(response.getData().get(2).getId_active().equalsIgnoreCase("1")){
                new DialogAds(JadwalSalatActivity.this,response.getData().get(2).getImage_link());
            }
        }
    }

    public class getLocationTask extends AsyncTask<Void,Void,Boolean> {
        Response<GetAds> responseAds;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress(true);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                GoMudikInterface goMudikInterface2 = GoMudikAPI.getAPI().create(GoMudikInterface.class);
                Call<GetAds> callAds = goMudikInterface2.adsOnlyActivity(JadwalSalatActivity.class.getSimpleName());
                try {
                    responseAds = callAds.execute();
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
                //search location in max 5 secs
                for(int i = 0; i < 5; i++){
                    // Simulate network access.
                    Thread.sleep(1000);
                    Log.d(TAG, "doInBackground: count "+i);
                    if(getIsFound()) {
                        Log.d(TAG, "doInBackground: location is found");
                        startService();
                        //break;
                        return true;
                    }
                }
            } catch (InterruptedException e) {
               e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            Log.d(TAG, "onPostExecute: start");
            super.onPostExecute(success);
            mGetLocationTask = null;
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            if(success){
                if(responseAds.isSuccessful()) {
                    if(responseAds.body()!=null) {
                        generateAds(responseAds.body());
                        contentFragment.setVisibility(View.VISIBLE);
                        generateData(cal, getLatitude(), getLongitude());
                        Log.d(TAG, "onPostExecute: lokasi ketemu");
                    } else {
                        Log.d(TAG, "onPostExecute: response body null");
                    }
                }
            } else {
                if(isLocationFound()){
                    if(responseAds.isSuccessful()) {
                        if (responseAds.body() != null) {
                            contentFragment.setVisibility(View.VISIBLE);
                            generateAds(responseAds.body());
                            generateData(cal, getLatitude(), getLongitude());
                            Log.d(TAG, "onPostExecute: lokasi terakhir");
                        } else {
                            Log.d(TAG, "onPostExecute: response body null");
                            Toast.makeText(JadwalSalatActivity.this, "" + getResources().getString(R.string.error_loading), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(JadwalSalatActivity.this, ""+getResources().getString(R.string.error_loading), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    locationNotFound(1);
                }
            }
            showProgress(false);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            mGetLocationTask = null;
        }
    }
}