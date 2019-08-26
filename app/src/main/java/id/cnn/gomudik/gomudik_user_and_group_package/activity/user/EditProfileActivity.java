package id.cnn.gomudik.gomudik_user_and_group_package.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import id.cnn.gomudik.R;
import id.cnn.gomudik.api.GetDataApiDefault;
import id.cnn.gomudik.api.GoMudikAPI;
import id.cnn.gomudik.api.GoMudikInterface;
import id.cnn.gomudik.util.SharedPreferenceCheckerActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends SharedPreferenceCheckerActivity {
    private EditText userName, userUserName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Edit Profil");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        userName = findViewById(R.id.user_name);
        userName.setText(getCurrName());
        userUserName = findViewById(R.id.user_username);
        userUserName.setText(getCurrUsername());

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(EditProfileActivity.this, "Updating profile...", Toast.LENGTH_SHORT).show();
                updateProfile();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void updateProfile(){
        final String name = userName.getText().toString();
        final String username = userUserName.getText().toString();
        GoMudikInterface goMudikInterface = GoMudikAPI.getAPI().create(GoMudikInterface.class);
        Call<GetDataApiDefault> call = goMudikInterface.updateProfileDetail(getCurrToken(), getCurrId(), name,username);
        call.enqueue(new Callback<GetDataApiDefault>() {
            @Override
            public void onResponse(Call<GetDataApiDefault> call, Response<GetDataApiDefault> response) {
                if(response.isSuccessful()){
                    if(response.body().getIs_success()){
                        userUpdateProfile(name, username);
                        Toast.makeText(EditProfileActivity.this, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(EditProfileActivity.this, ProfileUserActivity.class));
                        finish();
                    } else {
                        Toast.makeText(EditProfileActivity.this, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(EditProfileActivity.this, ""+response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GetDataApiDefault> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, ""+t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
