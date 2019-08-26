package id.cnn.gomudik.gomudik_user_and_group_package.activity.group_chat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.net.URISyntaxException;

import de.hdodenhof.circleimageview.CircleImageView;
import id.cnn.gomudik.R;
import id.cnn.gomudik.api.GetDataApiDefault;
import id.cnn.gomudik.api.GoMudikAPI;
import id.cnn.gomudik.api.GoMudikInterface;
import id.cnn.gomudik.gomudik_user_and_group_package.activity.user.AddStatusActivity;
import id.cnn.gomudik.util.GetFilePath;
import id.cnn.gomudik.util.SharedPreferenceCheckerActivity;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditGroupActivity extends SharedPreferenceCheckerActivity {
    private String group_id, group_image, group_name;
    private String imagePath;
    private CircleImageView groupImage;
    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Edit group");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        groupImage = findViewById(R.id.group_image);
        final EditText groupTitle = findViewById(R.id.group_title);
        if(getIntent().getStringExtra("id_group")!= null){
            group_id = getIntent().getStringExtra("id_group");
            group_image = getIntent().getStringExtra("group_image");
            group_name = getIntent().getStringExtra("group_name");
            groupTitle.setText(group_name);
            if(group_image != null){
                Uri uri = Uri.parse("http://gomudik.id:81".concat(group_image.substring(1)));
                Picasso.get().load(uri).fit().centerInside().placeholder(R.drawable.no_photo).error(R.drawable.no_photo).into(groupImage);
            } else {
                Picasso.get().load(R.drawable.no_photo).fit().centerInside().placeholder(R.drawable.no_photo).error(R.drawable.no_photo).into(groupImage);
            }
        }

        RelativeLayout groupName = findViewById(R.id.group_name);
        groupName.setOnClickListener(new View.OnClickListener() {
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

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                MultipartBody.Part group_image, group_name, id, id_group;
                if(imagePath != null){
                    File file = new File(imagePath);
                    RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"),file);
                    group_image = MultipartBody.Part.createFormData("image_file",file.getName(),requestBody);
                } else{
                    group_image = MultipartBody.Part.createFormData("image_file","null");
                }
                group_name = MultipartBody.Part.createFormData("group_name",String.valueOf(groupTitle.getText()));
                id =  MultipartBody.Part.createFormData("id",getCurrId());
                id_group = MultipartBody.Part.createFormData("id_group",group_id);

                GoMudikInterface goMudikInterface = GoMudikAPI.getAPI().create(GoMudikInterface.class);
                Call<GetDataApiDefault> call = goMudikInterface.editGroup(getCurrToken(),group_image,group_name,id,id_group);
                call.enqueue(new Callback<GetDataApiDefault>() {
                    @Override
                    public void onResponse(Call<GetDataApiDefault> call, Response<GetDataApiDefault> response) {
                        if(response.isSuccessful()) {
                            if(response.body() != null) {
                                Toast.makeText(EditGroupActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                if (response.body().getIs_success()) {
                                    Snackbar.make(view, "Edit group success", Snackbar.LENGTH_SHORT)
                                            .setAction("Close", null).show();
                                    startActivity(new Intent(EditGroupActivity.this, ListChatActivity.class));
                                    finish();
                                } else {
                                    Toast.makeText(EditGroupActivity.this, "Failed to edit group", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(EditGroupActivity.this, "Failed: Empty body response from server", Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<GetDataApiDefault> call, Throwable t) {
                        Toast.makeText(EditGroupActivity.this,"Failure: "+t.toString(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE){
            Uri uri = data.getData();
            Picasso.get().load(uri).fit().centerInside().into(groupImage);
            try {
                imagePath = GetFilePath.getFilePath(EditGroupActivity.this, uri);
            } catch (URISyntaxException e) {
                e.printStackTrace();
                Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                imagePath = null;
            }
        }
    }

    private void pickImagefromGallery(){
        //intent to pick image
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }
}