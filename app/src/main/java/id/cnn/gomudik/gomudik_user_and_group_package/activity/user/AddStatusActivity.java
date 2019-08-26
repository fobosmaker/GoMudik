package id.cnn.gomudik.gomudik_user_and_group_package.activity.user;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

import id.cnn.gomudik.R;
import id.cnn.gomudik.api.GoMudikAPI;
import id.cnn.gomudik.api.GoMudikInterface;
import id.cnn.gomudik.api.GetDataApiDefault;
import id.cnn.gomudik.gomudik_main_package.activity.StatusActivity;
import id.cnn.gomudik.gomudik_user_and_group_package.model.AddUserStatus;
import id.cnn.gomudik.gomudik_user_and_group_package.model.ListStatus;
import id.cnn.gomudik.util.GetFilePath;
import id.cnn.gomudik.util.LoadingProgress;
import id.cnn.gomudik.util.SharedPreferenceCheckerActivity;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;


public class AddStatusActivity extends SharedPreferenceCheckerActivity {
    RelativeLayout cageStatusPicture, buttonClose, cageStatusPrivacy;
    ImageView upload_image;
    Uri uri;
    //private static final String TAG = "AddStatusActivity";
    private ImageView iconStatusPrivacy;
    private TextView iconStatusContent, iconStatusDescription, locationContent;
    private EditText userStatusContent;
    private String imagePath = null;
    private ProgressBar progressBar;
    private static final String TAG = "AddStatusActivity";
    private int status_privacy = 1;
    private Double mLatitude, mLongitude = null;
    private Switch switchLocation;
    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;
    private String idStatus = "";
    private String addressPhone = "";
    private String imageLinkTemp = "";
    private android.support.v4.app.DialogFragment loadingDialog;
    private AtomicBoolean isEdit = new AtomicBoolean(false);

    private getLocationTask mGetLocationTask;
    private sendStatus mSendStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_status);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("New Status");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        iconStatusPrivacy = findViewById(R.id.icon_status_privacy);
        iconStatusContent = findViewById(R.id.icon_status_content);
        iconStatusDescription = findViewById(R.id.icon_status_description);
        cageStatusPicture = findViewById(R.id.cage_status_picture);
        upload_image = findViewById(R.id.upload_image);
        userStatusContent = findViewById(R.id.user_status_content);
        progressBar = findViewById(R.id.progress_bar);

        FloatingActionButton fab = findViewById(R.id.fab_photo);
        fab.hide();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED){
                        //permission not granted, request it.
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        //show popup for runtime permission
                        requestPermissions(permissions, PERMISSION_CODE);
                    }else{
                        //permission already granted
                        pickImagefromGallery();
                    }
                }else{
                    //system os less then marshmallow
                    pickImagefromGallery();
                }
            }
        });

        buttonClose = findViewById(R.id.button_close);
        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Picasso.get().invalidate(uri);
                imagePath = null;
                imageLinkTemp = "";
                cageStatusPicture.setVisibility(View.GONE);
            }
        });
        locationContent = findViewById(R.id.status_location_description);
        switchLocation = findViewById(R.id.switch_status_location);
        switchLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    checkGPS();
                } else {
                    locationContent.setText("Enabling your current location right now ");
                    addressPhone = null;
                    mLatitude = null;
                    mLongitude = null;
                }
            }
        });
        cageStatusPrivacy = findViewById(R.id.cage_status_privacy);
        cageStatusPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                String[] arrayStatusAction = {"Public","Only Me"};
                AlertDialog.Builder builder = new AlertDialog.Builder(AddStatusActivity.this);
                builder.setTitle("Status Privacy");
                builder.setItems(arrayStatusAction, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int position) {
                        switch (position){
                            case 0:
                                iconStatusPrivacy.setImageResource(R.drawable.ic_public_red_24dp);
                                iconStatusContent.setText("Public");
                                iconStatusDescription.setText("Public status will seen with all over people");
                                status_privacy = 1;
                                break;
                            case 1:
                                iconStatusPrivacy.setImageResource(R.drawable.ic_lock_red_24dp);
                                iconStatusContent.setText("Only Me");
                                iconStatusDescription.setText("Only you can see this post");
                                status_privacy = 2;
                                break;
                        }
                    }
                });
                builder.show();
            }
        });
        loadingDialog = new LoadingProgress();

        Intent b = getIntent();
        if(b.hasExtra("data")){
            ListStatus.Data data = (ListStatus.Data) b.getSerializableExtra("data");
            userStatusContent.setText(data.getContent());
            isEdit.set(true);
            toolbar.setTitle("Edit Status");
            if(data.getImage_link() != null){
                Uri uri = Uri.parse("http://gomudik.id:81".concat(data.getImage_link().substring(1)));
                Picasso.get().load(uri).fit().centerInside().placeholder(R.drawable.ex_thumbnail).error(R.drawable.ex_thumbnail).into(upload_image);
                cageStatusPicture.setVisibility(View.VISIBLE);
                imageLinkTemp = data.getImage_link();
            }

            if(data.getAddress() != null){
                locationContent.setText(data.getAddress());
                addressPhone = data.getAddress();
                switchLocation.setChecked(true);
                mLatitude = Double.valueOf(data.getLatitude());
                mLongitude = Double.valueOf(data.getLongitude());
            }

            if(data.getId_status() != null){
                idStatus = data.getId_status();
            }

            if(data.getId_status_privacy().equalsIgnoreCase("1")){
                iconStatusPrivacy.setImageResource(R.drawable.ic_public_red_24dp);
                iconStatusContent.setText("Public");
                iconStatusDescription.setText("Public status will seen with all over people");
                status_privacy = 1;
            } else {
                iconStatusPrivacy.setImageResource(R.drawable.ic_lock_red_24dp);
                iconStatusContent.setText("Only Me");
                iconStatusDescription.setText("Only you can see this post");
                status_privacy = 2;
            }
        }
    }

    public void generateData(){
        progressBar.setVisibility(View.VISIBLE);
        startService();
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(AddStatusActivity.this, Locale.getDefault());
        try {
            mLatitude = getLatitude();
            mLongitude = getLongitude();
            addresses = geocoder.getFromLocation(mLatitude, mLongitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            locationContent.setText(addresses.get(0).getAddressLine(0));
            addressPhone = addresses.get(0).getAddressLine(0);
            progressBar.setVisibility(View.GONE);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(AddStatusActivity.this,"Error to get current location, please try again later",Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            switchLocation.setChecked(false);
            addressPhone = "";
            mLatitude = null;
            mLongitude = null;
        }
    }

    //handle result of runtime permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSION_CODE:{
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //permission was granted
                    pickImagefromGallery();
                }else{
                    //permission was denied
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void settingGPS(){
        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(myIntent,123);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE){
            if(data != null) {
                Uri uri = data.getData();
                upload_image.setImageURI(uri);
                cageStatusPicture.setVisibility(View.VISIBLE);
                try {
                    imagePath = GetFilePath.getFilePath(AddStatusActivity.this,uri);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                    Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    imagePath = null;
                }
            }
        } else if(requestCode == 123){
            checkGPS();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_status, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_send:
                if(TextUtils.isEmpty(userStatusContent.getText())){
                    Toast.makeText(AddStatusActivity.this,"Status content cannot be empty...",Toast.LENGTH_SHORT).show();
                } else {
                    MultipartBody.Part body;
                    if (imagePath != null) {
                        File file = new File(imagePath);
                        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                        body = MultipartBody.Part.createFormData("image_file", file.getName(), requestBody);
                    } else {
                        body = MultipartBody.Part.createFormData("image_file", "");
                    }
                    AddUserStatus dataUserStatus = new AddUserStatus(MultipartBody.Part.createFormData("id_status", idStatus), MultipartBody.Part.createFormData("id_users", getCurrId()), MultipartBody.Part.createFormData("id_status_privacy", String.valueOf(status_privacy)), MultipartBody.Part.createFormData("latitude", String.valueOf(mLatitude)), MultipartBody.Part.createFormData("longitude", String.valueOf(mLongitude)), MultipartBody.Part.createFormData("address", addressPhone), body, MultipartBody.Part.createFormData("content", String.valueOf(userStatusContent.getText())));
                    mSendStatus = new sendStatus(getCurrToken(), dataUserStatus);
                    mSendStatus.execute();
                }
                break;
            case R.id.item_add_picture:
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED){
                        //permission not granted, request it.
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        //show popup for runtime permission
                        requestPermissions(permissions, PERMISSION_CODE);
                    }else{
                        //permission already granted
                        pickImagefromGallery();
                    }
                }else{
                    //system os less then marshmallow
                    pickImagefromGallery();
                }
                break;
                default:break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void pickImagefromGallery(){
        //intent to pick image
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    public void checkGPS(){
        if (isGPSOn()) {
            requestLocationUpdates();
            mGetLocationTask = new getLocationTask();
            mGetLocationTask.execute((Void)null);
        } else {
            Log.d(TAG, "showAlert: Start");
            final AlertDialog.Builder dialog = new AlertDialog.Builder(AddStatusActivity.this);
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
                            Toast.makeText(AddStatusActivity.this, "Failed to get your location. Please turn on your GPS and try again", Toast.LENGTH_SHORT).show();
                            switchLocation.setChecked(false);
                        }
                    });
            dialog.show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public class getLocationTask extends AsyncTask<Void,Void,Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                for(int i = 0; i < 15; i++){
                    // Simulate network access.
                    Thread.sleep(1000);
                    Log.d(TAG, "doInBackground: count "+i);
                    if(getIsFound()) {
                        Log.d(TAG, "doInBackground: location is found");
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
            super.onPostExecute(success);
            mGetLocationTask = null;
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            if(success){
                generateData();
            } else {
                //get latest loc
                if(isLocationFound()){
                    generateData();
                } else {
                    Toast.makeText(AddStatusActivity.this, "Failed to get your location, try again later.", Toast.LENGTH_SHORT).show();
                    switchLocation.setChecked(false);
                }
            }
            progressBar.setVisibility(View.GONE);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            mGetLocationTask = null;
        }
    }

    public class sendStatus extends AsyncTask<Void,Void,Boolean> {
        String token;
        AddUserStatus data;
        Response<GetDataApiDefault> response;

        public sendStatus(String token, AddUserStatus data){
            this.token = token;
            this.data = data;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog.show(getSupportFragmentManager(),"postSatus");
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            GoMudikInterface goMudikInterface = GoMudikAPI.getAPI().create(GoMudikInterface.class);
            Call<GetDataApiDefault> call = goMudikInterface.postStatus(token, data.getImage_file(), data.getId_user(), data.getId_status_privacy(), data.getLatitude(), data.getLongitude(), data.getAddress(), data.getContent(), data.getId_status(), MultipartBody.Part.createFormData("image_link_temp", imageLinkTemp));
            try {
                response = call.execute();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "doInBackground: "+e.getMessage());
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            mSendStatus = null;
            loadingDialog.dismiss();
            if(success) {
                if (response.isSuccessful()) {
                    if(response.body() != null) {
                        Toast.makeText(AddStatusActivity.this, "" + response.body().getMessage(), Toast.LENGTH_LONG).show();
                        if (response.body().getIs_success()) {
                            if (isEdit.get()) {
                                isEdit.set(false);
                                onBackPressed();
                            } else {
                                startActivity(new Intent(AddStatusActivity.this, StatusActivity.class));
                                finish();
                            }
                        }
                    } else {
                        Toast.makeText(AddStatusActivity.this, "Failed: Empty body response from server", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(AddStatusActivity.this, "Failed: " + response.errorBody(), Toast.LENGTH_LONG).show();
                    Log.d(TAG, "onPostExecute: " + response.errorBody());
                }
            } else {
                Toast.makeText(AddStatusActivity.this, "Failed: connection lost, please try again...", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            mSendStatus = null;
        }
    }
}