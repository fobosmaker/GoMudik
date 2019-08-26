package id.cnn.gomudik.gomudik_user_and_group_package.activity.group_chat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import id.cnn.gomudik.gomudik_user_and_group_package.activity.user.AddStatusActivity;
import id.cnn.gomudik.gomudik_user_and_group_package.adapter.AddGroupAdapter;
import id.cnn.gomudik.R;
import id.cnn.gomudik.api.GoMudikAPI;
import id.cnn.gomudik.api.GoMudikInterface;
import id.cnn.gomudik.api.GetDataApiDefault;
import id.cnn.gomudik.gomudik_user_and_group_package.model.ListUsers;
import id.cnn.gomudik.firebase.GoMudikFirebase;
import id.cnn.gomudik.util.GetFilePath;
import id.cnn.gomudik.util.LoadingProgress;
import id.cnn.gomudik.util.SaltString;
import id.cnn.gomudik.util.SharedPreferenceCheckerActivity;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddGroupStep2Activity extends SharedPreferenceCheckerActivity {
    private ArrayList<ListUsers.Data> selected = new ArrayList<>();
    private static final String TAG = "AddGroupStep2Activity";
    private List<String> list_id_user = new ArrayList<>();
    private EditText groupTitle;
    //Uri uri;
    private CircleImageView groupImage;
    private String imagePath;

    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

    private android.support.v4.app.DialogFragment loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group_step2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("New group");
        getSupportActionBar().setSubtitle("Add group subject");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        groupImage = findViewById(R.id.group_image);
        groupTitle = findViewById(R.id.group_title);
        RelativeLayout groupName = findViewById(R.id.group_name);
        loadingDialog = new LoadingProgress();
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

        Bundle b = this.getIntent().getExtras();
        if(b!=null){
            selected = (ArrayList<ListUsers.Data>) b.getSerializable("data");
            selected.add(0,new ListUsers.Data(getCurrId(), getCurrName(),getCurrEmail(),getCurrImageLink()));
            generateMember();
        } else{
            Log.d(TAG, "onCreate: error");
        }
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(list_id_user.size() > 1){
                    loadingDialog.show(getSupportFragmentManager(),"Create group");
                    final String rand = new SaltString().getSaltString();
                    MultipartBody.Part group_image, group_name, group_member, id, id_chat_room;
                    if(imagePath != null){
                        File file = new File(imagePath);
                        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"),file);
                        group_image = MultipartBody.Part.createFormData("image_file",file.getName(),requestBody);
                    } else{
                        group_image = MultipartBody.Part.createFormData("image_file","null");
                    }
                    group_name = MultipartBody.Part.createFormData("group_name",String.valueOf(groupTitle.getText()));
                    group_member = MultipartBody.Part.createFormData("group_member",String.valueOf(TextUtils.join(",", list_id_user)));
                    id =  MultipartBody.Part.createFormData("id",getCurrId());
                    id_chat_room = MultipartBody.Part.createFormData("id_chat_room",rand);

                    GoMudikInterface goMudikInterface = GoMudikAPI.getAPI().create(GoMudikInterface.class);
                    Call<GetDataApiDefault> call = goMudikInterface.createGroup(getCurrToken(),group_image,id,group_member,group_name, id_chat_room);
                    call.enqueue(new Callback<GetDataApiDefault>() {
                        @Override
                        public void onResponse(Call<GetDataApiDefault> call, Response<GetDataApiDefault> response) {
                            if(response.isSuccessful()) {
                                if(response.body() != null) {
                                    Toast.makeText(AddGroupStep2Activity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                    if (response.body().getIs_success()) {
                                        //insert
                                        new GoMudikFirebase().addUserUnseenGroupMessage(getCurrId(), rand);
                                        loadingDialog.dismiss();
                                        startActivity(new Intent(AddGroupStep2Activity.this, ListChatActivity.class));
                                        finish();
                                    } else {
                                        loadingDialog.dismiss();
                                        Toast.makeText(AddGroupStep2Activity.this, "Gagal", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(AddGroupStep2Activity.this, "Failed: Empty body response from server", Toast.LENGTH_LONG).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<GetDataApiDefault> call, Throwable t) {
                            loadingDialog.dismiss();
                            Toast.makeText(AddGroupStep2Activity.this,"Failure: "+t.toString(),Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    loadingDialog.dismiss();
                    Toast.makeText(AddGroupStep2Activity.this,"Select at least 1 participant to create group",Toast.LENGTH_SHORT).show();
                }
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
            if(data != null) {
                Uri uri = data.getData();
                Picasso.get().load(uri).fit().centerInside().into(groupImage);
                try {
                    imagePath = GetFilePath.getFilePath(AddGroupStep2Activity.this, uri);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                    Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    imagePath = null;
                }
            }
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

    public void removeMember(View view, int position){
        Toast.makeText(AddGroupStep2Activity.this,""+position,Toast.LENGTH_SHORT).show();
        selected.remove(position);
        generateMember();
    }

    public void generateMember(){
        list_id_user.clear();
        for(ListUsers.Data list:selected){
            list_id_user.add(list.getUsers_id());
        }
        RecyclerView.LayoutManager grid = new GridLayoutManager(this,4);
        RecyclerView cage = findViewById(R.id.cage);
        cage.setLayoutManager(grid);
        cage.setHasFixedSize(true);
        AddGroupAdapter addGroupAdapter = new AddGroupAdapter(selected,AddGroupStep2Activity.this);
        cage.setAdapter(addGroupAdapter);
    }

    private void pickImagefromGallery(){
        //intent to pick image
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }
}