package id.cnn.gomudik.gomudik_user_and_group_package.activity.user;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.net.URISyntaxException;

import de.hdodenhof.circleimageview.CircleImageView;
import id.cnn.gomudik.R;
import id.cnn.gomudik.api.GetDataApiDefault;
import id.cnn.gomudik.api.GoMudikAPI;
import id.cnn.gomudik.api.GoMudikInterface;
import id.cnn.gomudik.gomudik_user_and_group_package.fragment.UsersFriendFragment;
import id.cnn.gomudik.gomudik_user_and_group_package.fragment.UsersGroupFragment;
import id.cnn.gomudik.gomudik_user_and_group_package.fragment.UsersStatusFragment;
import id.cnn.gomudik.gomudik_user_and_group_package.model.ListStatus;
import id.cnn.gomudik.util.GetFilePath;
import id.cnn.gomudik.util.LoadingProgress;
import id.cnn.gomudik.util.SharedPreferenceCheckerActivity;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileUserActivity extends SharedPreferenceCheckerActivity {
    private ViewPager mViewPager;
    private static final String TAG = "GomudikGroupActivity";
    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;
    private CircleImageView userPhoto;
    private android.support.v4.app.DialogFragment loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_user);
        Log.d(TAG, "onCreate: start");
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        loadingDialog = new LoadingProgress();
        userPhoto = findViewById(R.id.user_photo);
        if(getCurrImageLink() != null){
            Log.d(TAG, "onCreate: "+getCurrImageLink());
            Uri uri = Uri.parse("http://gomudik.id:81".concat(getCurrImageLink().substring(1)));
            Picasso.get().load(uri).fit().centerInside().placeholder(R.drawable.no_photo).error(R.drawable.no_photo).into(userPhoto);
        } else {
            Picasso.get().load(R.drawable.no_photo).into(userPhoto);
        }
        TextView user_name = findViewById(R.id.user_name);
        user_name.setText(getCurrName());

        TextView user_email = findViewById(R.id.user_email);
        user_email.setText(getCurrEmail());

        TextView user_username = findViewById(R.id.user_username);
        user_username.setText(getCurrUsername());

        // Create the adapter that will return a fragment for each of the three
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart: true");
        finish();
        startActivity(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile_default,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_change_picture:
                changeProfilePicture();
                break;
            case R.id.action_change_detail:
                startActivity(new Intent(ProfileUserActivity.this, EditProfileActivity.class));
                break;
            default: break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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

    //handle result picked image
    private Uri img;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE){
            if(data != null) {
                //set image to image view
                img = data.getData();
                Toast.makeText(ProfileUserActivity.this, "Updating profile picture...", Toast.LENGTH_SHORT).show();
                MultipartBody.Part body, id_users;
                if (img != null) {
                    final String image_link;
                    try {
                        image_link = GetFilePath.getFilePath(ProfileUserActivity.this,img);
                        if(image_link != null) {
                            File file = new File(image_link);
                            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                            id_users = MultipartBody.Part.createFormData("id_users", getCurrId());
                            body = MultipartBody.Part.createFormData("image_file", file.getName(), requestBody);
                            GoMudikInterface goMudikInterface = GoMudikAPI.getAPI().create(GoMudikInterface.class);
                            Call<GetDataApiDefault> call = goMudikInterface.updateProfilePicture(getCurrToken(), body, id_users);
                            call.enqueue(new Callback<GetDataApiDefault>() {
                                @Override
                                public void onResponse(Call<GetDataApiDefault> call, Response<GetDataApiDefault> response) {
                                    if (response.isSuccessful()) {
                                        if(response.body() != null) {
                                            if (response.body().getIs_success()) {
                                                userUpdateProfilePicture(response.body().getMessage());
                                                Toast.makeText(ProfileUserActivity.this, "Change profile picture success", Toast.LENGTH_SHORT).show();
                                                userPhoto.setImageURI(img);
                                                //reload activity;
                                                finish();
                                                startActivity(getIntent());
                                            }
                                        }
                                    } else {
                                        Toast.makeText(ProfileUserActivity.this, "" + response.message(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<GetDataApiDefault> call, Throwable t) {
                                    Toast.makeText(ProfileUserActivity.this, "Failure: " + t.toString(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(ProfileUserActivity.this, "Failed to get image from your media", Toast.LENGTH_SHORT).show();
                        }
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                        Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ProfileUserActivity.this, "Failure: to get image from your media", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public String getUserToken(){
        return getCurrToken();
    }

    public String getUserId(){
        return  getCurrId();
    }
    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position){
                case 0:
                    fragment = new UsersFriendFragment();
                    break;
                case 1:
                    fragment = new UsersGroupFragment();
                    break;
                case 2:
                    fragment = new UsersStatusFragment();
                    break;
            }
            return  fragment;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: start");
    }

    private void changeProfilePicture(){
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

    private void pickImagefromGallery(){
        //intent to pick image
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    public void showDialogStatusAction(final ListStatus.Data item){
        String[] arrayStatusAction = {"Edit","Delete"};
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileUserActivity.this);
        builder.setItems(arrayStatusAction, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int position) {
                switch (position){
                    case 0:
                        editStatus(item);
                        break;
                    case 1:
                        deleteStatus(item);
                        break;
                }
            }
        });
        builder.show();
    }

    private void editStatus(ListStatus.Data item){
        Intent intent = new Intent(ProfileUserActivity.this, AddStatusActivity.class);
        intent.putExtra("data", item);
        startActivity(intent);
    }

    private void deleteStatus(final ListStatus.Data item){
        final AlertDialog.Builder dialog = new AlertDialog.Builder(ProfileUserActivity.this);
        dialog.setTitle("Delete")
                .setMessage("Are you sure want to delete this status?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        loadingDialog.show(getSupportFragmentManager(), "Delete status from profile");
                        GoMudikInterface goMudikInterface = GoMudikAPI.getAPI().create(GoMudikInterface.class);
                        Call<GetDataApiDefault> call = goMudikInterface.deleteStatus(getCurrToken(), item.getId_status());
                        call.enqueue(new Callback<GetDataApiDefault>() {
                            @Override
                            public void onResponse(Call<GetDataApiDefault> call, Response<GetDataApiDefault> response) {
                                if(response.isSuccessful()){
                                    if(response.body() != null) {
                                        Toast.makeText(ProfileUserActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                        loadingDialog.dismiss();
                                        //reload
                                        finish();
                                        startActivity(getIntent());
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<GetDataApiDefault> call, Throwable t) {
                                loadingDialog.dismiss();
                                Toast.makeText(ProfileUserActivity.this,"Failed: "+t.toString(),Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    }
                });
        dialog.show();
    }
}