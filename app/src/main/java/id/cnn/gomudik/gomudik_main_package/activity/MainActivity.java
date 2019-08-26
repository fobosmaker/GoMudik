package id.cnn.gomudik.gomudik_main_package.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import id.cnn.gomudik.PrayTime;
import id.cnn.gomudik.R;
import id.cnn.gomudik.api.GoMudikAPI;
import id.cnn.gomudik.api.GoMudikInterface;
import id.cnn.gomudik.gomudik_main_package.model.GetNews;
import id.cnn.gomudik.gomudik_main_package.model.MainActivityModel;
import id.cnn.gomudik.gomudik_main_package.model.YoutubeGetPlaylistError;
import id.cnn.gomudik.gomudik_user_and_group_package.activity.group_chat.ListChatActivity;
import id.cnn.gomudik.gomudik_user_and_group_package.activity.user.ListContactActivity;
import id.cnn.gomudik.gomudik_user_and_group_package.activity.user.NotificationActivity;
import id.cnn.gomudik.gomudik_user_and_group_package.activity.user.ProfileUserActivity;
import id.cnn.gomudik.gomudik_user_and_group_package.model.ListStatus;
import id.cnn.gomudik.gomudik_main_package.adapter.MainAdapter;
import id.cnn.gomudik.gomudik_main_package.model.MenuAds;
import id.cnn.gomudik.gomudik_main_package.model.MenuJadwalSalatModel;
import id.cnn.gomudik.gomudik_main_package.model.MenuButtonModel;
import id.cnn.gomudik.gomudik_main_package.model.MenuProfile;
import id.cnn.gomudik.util.BaseGoMudikActivity;
import id.cnn.gomudik.util.Session;
import retrofit2.Call;
import retrofit2.Response;

public class MainActivity extends BaseGoMudikActivity {
    private static final String TAG = "MainActivity";
    private SwipeRefreshLayout swipeRefreshLayout;
    private RelativeLayout buttonBackToTop;
    public ArrayList<MenuButtonModel> menuButton = new ArrayList<>();
    public ArrayList<MenuJadwalSalatModel> menuJadwalSalat = new ArrayList<>();
    public ArrayList<GetNews.Data> menuNews = new ArrayList<>();
    public ArrayList<ListStatus.Data> menuStatus = new ArrayList<>();
    public ArrayList<MenuAds> menuAds = new ArrayList<>();
    private getDataTask mGetDataTask = null;
    private Session session;
    private RecyclerView cage;
    private long backPressed;
    private Toast backToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        session = new Session(MainActivity.this);
        buttonBackToTop = findViewById(R.id.button_back_to_top);
        buttonBackToTop.setVisibility(View.GONE);
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                init();
            }
        });
        cage = findViewById(R.id.cage);
        init();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart: true");
        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(session.login()){
            getMenuInflater().inflate(R.menu.menu_default,menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(session.login()){
            switch (item.getItemId()){
                case R.id.action_chat:
                    startActivity(new Intent(MainActivity.this, ListChatActivity.class));
                    break;
                case R.id.action_notif:
                    startActivity(new Intent(MainActivity.this, NotificationActivity.class));
                    break;
                case R.id.action_profile:
                    startActivity(new Intent(MainActivity.this, ProfileUserActivity.class));
                    break;
                case R.id.action_friends:
                    startActivity(new Intent(MainActivity.this, ListContactActivity.class));
                    break;
                case R.id.action_help:
                    startActivity(new Intent(MainActivity.this,HelpActivity.class));
                    break;
                case R.id.action_logout:
                    final AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                    dialog.setTitle("Log Out")
                            .setMessage("Are you sure to log out?")
                            .setPositiveButton("Log Out", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                    session.logout();
                                    finish();
                                    startActivity(new Intent(MainActivity.this, MainActivity.class));
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                                }
                            });
                    dialog.show();
                    break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(backPressed + 2000 > System.currentTimeMillis()){
            backToast.cancel();
            super.onBackPressed();
            return;
        } else {
            backToast = Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressed = System.currentTimeMillis();
    }

    public void menuButtonClick(int position){
        switch (position){
            case 0:
                startActivity(new Intent(MainActivity.this, MapsActivity.class));
                break;
            case 1:
                startActivity(new Intent(MainActivity.this, CCTVActivity.class));
                break;
            case 2:
                startActivity(new Intent(MainActivity.this, GoMudikNewsActivity.class));
                break;
            case 3:
                startActivity(new Intent(MainActivity.this, GoMudikLiveActivity.class));
                break;
            case 4:
                startActivity(new Intent(MainActivity.this, id.cnn.gomudik.gomudik_main_package.activity.JadwalSalatActivity.class));
                break;
            case 5:
                startActivity(new Intent(MainActivity.this, StatusActivity.class));
                break;
        }
    }

    public void onMore(String holder){
        switch (holder){
            case "gomudikNews":
                startActivity(new Intent(MainActivity.this,GoMudikNewsActivity.class));
                break;
            case "jadwalImsak":
                startActivity(new Intent(MainActivity.this, id.cnn.gomudik.gomudik_main_package.activity.JadwalSalatActivity.class));
                break;
            case "status":
                startActivity(new Intent(MainActivity.this, StatusActivity.class));
                break;
        }
    }

    public void onZoom(Uri uri){
        Toast.makeText(MainActivity.this,""+uri,Toast.LENGTH_SHORT).show();
    }

    public void init(){
        cage.setVisibility(View.GONE);
        cage.removeAllViewsInLayout();
        requestLocationUpdates();
        mGetDataTask = new getDataTask();
        mGetDataTask.execute((Void)null);
    }

    public void generateMenuButton(){
        if(!menuButton.isEmpty()){
            menuButton.clear();
        }
        menuButton.add(new MenuButtonModel("button1","Place",R.drawable.icon_maps));
        menuButton.add(new MenuButtonModel("button2","CCTV",R.drawable.icon_cctv));
        menuButton.add(new MenuButtonModel("button3","News",R.drawable.icon_news));
        menuButton.add(new MenuButtonModel("button6","Live",R.drawable.icon_live));
        menuButton.add(new MenuButtonModel("button7","Jadwal Salat",R.drawable.icon_jadwal_salat));
        menuButton.add(new MenuButtonModel("button8","Status",R.drawable.icon_status));
    }

    public void generateJadwalSalat(){
        if(!menuJadwalSalat.isEmpty()){
            menuJadwalSalat.clear();
        }
        double timezone = (double) (Calendar.getInstance().getTimeZone().getOffset(Calendar.getInstance().getTimeInMillis())) / (1000 * 60 * 60);
        PrayTime prayers = new PrayTime();
        prayers.setTimeFormat(prayers.Time24);
        prayers.setCalcMethod(prayers.Custom);
        prayers.setAsrJuristic(prayers.Shafii);
        prayers.setAdjustHighLats(prayers.AngleBased);
        int[] offsets = {2, -4, 2, 2, 2, 2, 2}; // {Fajr,Sunrise,Dhuhr,Asr,Sunset,Maghrib,Isha}
        prayers.tune(offsets);
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        ArrayList prayerTime = prayers.getPrayerTimes(cal, getLatitude(), getLongitude(), timezone);
        SimpleDateFormat df = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
        Date d;
        try {
            d = df.parse(prayerTime.get(0).toString());
            Calendar newcal = Calendar.getInstance();
            newcal.setTime(d);
            newcal.add(Calendar.MINUTE, -10);
            menuJadwalSalat.add(new MenuJadwalSalatModel(R.drawable.jadwal_imsak, getString(R.string.imsak), df.format(newcal.getTime()), true));
            menuJadwalSalat.add(new MenuJadwalSalatModel(R.drawable.jadwal_subuh, getString(R.string.subuh), prayerTime.get(0).toString(), true));
            menuJadwalSalat.add(new MenuJadwalSalatModel(R.drawable.jadwal_maghrib, getString(R.string.maghrib), prayerTime.get(5).toString(), true));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<MenuProfile> getProfile(){
        ArrayList<MenuProfile> data = new ArrayList<>();

        if(session.login()){
            HashMap<String, String> map = session.getData();
            data.add(new MenuProfile(map.get(session.KEY_ID),map.get(session.KEY_NAME),map.get(session.KEY_EMAIL),map.get(session.KEY_IMAGE_LINK),true));
        } else {
            data.add(new MenuProfile("","Login or register","Try our new features like group chat, find member's location and share status about your mudik",null,false));
        }
        return data;
    }

    public ArrayList<MenuButtonModel> getMenuButtonData(){
        return menuButton;
    }

    public ArrayList<MenuJadwalSalatModel> getMenuJadwalSalatData(){
        return menuJadwalSalat;
    }

    public ArrayList<GetNews.Data> getMenuNews(){ return menuNews; }

    public ArrayList<ListStatus.Data> getMenuStatus() { return menuStatus; }

    public void showProgress(Boolean show){
        if(show){
            swipeRefreshLayout.setRefreshing(true);
        } else {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    public void generateData(MainActivityModel response){
        //menu button
        generateMenuButton();
        //jadwal salat
        if(isGPSOn()){
            if(getIsFound()){
                Log.d(TAG, "generateData: lokasi ketemu");
                generateJadwalSalat();
                startService();
            } else if(isLocationFound()){
                Log.d(TAG, "generateData: lokasi lama");
                generateJadwalSalat();
            } else {
                menuJadwalSalat.add(new MenuJadwalSalatModel(0, "Unknown Location", "We can't find your location, pull to refresh to try again", false));
            }
        } else {
            menuJadwalSalat.add(new MenuJadwalSalatModel(0, "Enable Your Location", "For most relevant imsyakiah time based on your location", false));
        }
        if(response.getNews().getTotal_data() > 0) {
            Log.d(TAG, "generateData: ada data");
            if(!menuNews.isEmpty()){
                menuNews.clear();
            }
            int limit = response.getNews().getTotal_data();
            for(int i = 0; i < limit; i++){
                menuNews.add(response.getNews().getData().get(i));
            }
        }
        if(response.getStatus().getTotal_data() > 0) {
            Log.d(TAG, "generateData: ada data");
            if(!menuStatus.isEmpty()){
                menuStatus.clear();
            }
            int limit = response.getStatus().getTotal_data();
            for(int i = 0; i < limit; i++){
                menuStatus.add(response.getStatus().getData().get(i));
            }
        }
        if(response.getAds().getTotal_data() > 0) {
            Log.d(TAG, "generateData: ada data");
            if(!menuAds.isEmpty()){
                menuAds.clear();
            }
            int limit = response.getAds().getTotal_data();
            for(int i = 0; i < limit; i++){
                menuAds.add(i,new MenuAds(response.getAds().getData().get(i).getImage_link(),response.getAds().getData().get(i).getId_active()));
            }
        }
        showData();
    }

    public void showData(){
        //generate menu button
        ArrayList<Object> data = new ArrayList<>();
        data.add(getProfile().get(0));
        data.add(menuButton.get(0));

        //generate ads
        if(menuAds.size() > 1){ data.add(menuAds.get(0)); }

        //generate jadwal salat
        data.add(menuJadwalSalat.get(0));
        //generate status
        if(menuStatus.size() > 0) { data.add(menuStatus.get(0)); }

        //generate ads
        if(menuAds.size() > 2){ data.add(menuAds.get(1)); }

        //generate youtube playlist
        if(menuNews.size() > 0) {
            data.add(menuNews.get(0));
        } else {
            //if youtube error
            ArrayList<YoutubeGetPlaylistError> youtubeError = new ArrayList<>();
            youtubeError.add(new YoutubeGetPlaylistError("0","error to get youtube"));
            data.add(youtubeError.get(0));
        }
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, OrientationHelper.VERTICAL,false);
        cage.setLayoutManager(new LinearLayoutManager(this, OrientationHelper.VERTICAL,false));
        cage.setItemAnimator(new DefaultItemAnimator());
        cage.setAdapter(new MainAdapter(data, MainActivity.this));
    }

    public void moveToChat(){
        startActivity(new Intent(MainActivity.this, ListChatActivity.class));
    }

    public void moveToNotif(){
        startActivity(new Intent(MainActivity.this, NotificationActivity.class));
    }

    public void showSnackbar(){
        Snackbar snackbar = Snackbar.make(cage, "Failed to connect to server, please try again.", Snackbar.LENGTH_LONG);
        snackbar.setAction("Reload", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                init();
            }
        });
        snackbar.show();
    }

    public class getDataTask extends AsyncTask<Void,Void,Boolean>{
        Response<MainActivityModel> response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress(true);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            GoMudikInterface goMudikInterface = GoMudikAPI.getAPI().create(GoMudikInterface.class);
            Call<MainActivityModel> call = goMudikInterface.getDataMainActivity(MainActivity.class.getSimpleName());
            try {
                response = call.execute();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

            //search location in max 3 secs
            if(isGPSOn()) {
                try {
                    for (int i = 0; i < 3; i++) {
                        // Simulate network access.
                        Thread.sleep(1000);
                        Log.d(TAG, "doInBackground: count " + i);
                        if (getIsFound()) {
                            Log.d(TAG, "doInBackground: location is found");
                            break;
                        }
                    }
                } catch (InterruptedException e) {
                    Log.d(TAG, "doInBackground: " + e.getMessage());
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            mGetDataTask = null;
            cage.setVisibility(View.VISIBLE);
            if(success){
                if(response.isSuccessful()) {
                    if(response.body()!=null) {
                        generateData(response.body());
                    } else {
                        Toast.makeText(MainActivity.this, ""+getResources().getString(R.string.error_loading), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Response from server is failure", Toast.LENGTH_SHORT).show();
                }
            } else {
                cage.setVisibility(View.GONE);
                showSnackbar();
                Log.d(TAG, "onPostExecute: failed result ");
            }
            showProgress(false);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            mGetDataTask = null;
            showProgress(false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        init();
    }

    public void settingGPS(){
        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(myIntent,123);
    }
}