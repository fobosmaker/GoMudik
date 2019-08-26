package id.cnn.gomudik.gomudik_user_and_group_package.activity.group_chat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.IOException;

import id.cnn.gomudik.api.GetDataApiDefault;
import id.cnn.gomudik.firebase.GoMudikFirebase;
import id.cnn.gomudik.gomudik_main_package.activity.MainActivity;
import id.cnn.gomudik.gomudik_user_and_group_package.activity.user.ListContactActivity;
import id.cnn.gomudik.gomudik_user_and_group_package.activity.user.NotificationActivity;
import id.cnn.gomudik.R;
import id.cnn.gomudik.api.GoMudikAPI;
import id.cnn.gomudik.api.GoMudikInterface;
import id.cnn.gomudik.gomudik_user_and_group_package.activity.user.ProfileUserActivity;
import id.cnn.gomudik.gomudik_user_and_group_package.adapter.ListChatAdapter;
import id.cnn.gomudik.gomudik_user_and_group_package.model.ListChatGroup;
import id.cnn.gomudik.gomudik_main_package.activity.HelpActivity;
import id.cnn.gomudik.util.LoadingProgress;
import id.cnn.gomudik.util.SharedPreferenceCheckerActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListChatActivity extends SharedPreferenceCheckerActivity {
    private LinearLayoutManager linearLayoutManager;
    private getDataTask mGetDataTask = null;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView cage;
    private RelativeLayout errorResponse,noDataResponse;
    private DialogFragment loadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_chat);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Group Chat");
        linearLayoutManager = new LinearLayoutManager(ListChatActivity.this);
        cage = findViewById(R.id.cage);
        loadingDialog = new LoadingProgress();
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                init();
            }
        });
        errorResponse = findViewById(R.id.template_error);
        noDataResponse = findViewById(R.id.template_no_data);
        Button buttonNoData = findViewById(R.id.buttonNoData);
        buttonNoData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ListChatActivity.this, AddGroupStep1Activity.class));
            }
        });
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ListChatActivity.this, AddGroupStep1Activity.class));
            }
        });
        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_default,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_chat:
                startActivity(new Intent(ListChatActivity.this, ListChatActivity.class));
                break;
            case R.id.action_notif:
                startActivity(new Intent(ListChatActivity.this, NotificationActivity.class));
                break;
            case R.id.action_profile:
                startActivity(new Intent(ListChatActivity.this, ProfileUserActivity.class));
                break;
            case R.id.action_friends:
                startActivity(new Intent(ListChatActivity.this, ListContactActivity.class));
                break;
            case R.id.action_help:
                startActivity(new Intent(ListChatActivity.this, HelpActivity.class));
                break;
            case R.id.action_logout:
                final AlertDialog.Builder dialog = new AlertDialog.Builder(ListChatActivity.this);
                dialog.setTitle("Log Out")
                        .setMessage("Are you sure to log out?")
                        .setPositiveButton("Log Out", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                userLogOut();
                                startActivity(new Intent(ListChatActivity.this, MainActivity.class));
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
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        startActivity(new Intent(ListChatActivity.this, MainActivity.class));

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void init(){
        cage.setVisibility(View.GONE);
        errorResponse.setVisibility(View.GONE);
        noDataResponse.setVisibility(View.GONE);
        mGetDataTask = new getDataTask(getCurrId(),getCurrToken());
        mGetDataTask.execute((Void)null);
    }

    public void showData(ListChatGroup response){
        cage.setLayoutManager(linearLayoutManager);
        cage.setHasFixedSize(true);
        ListChatAdapter listChatAdapter = new ListChatAdapter(response.getData(), ListChatActivity.this, getCurrId());
        cage.setAdapter(listChatAdapter);
    }

    public void showDialog(final ListChatGroup.Data data){
        String[] arrayStatusAction = {"View group detail","Leave group"};
        AlertDialog.Builder builder = new AlertDialog.Builder(ListChatActivity.this);
        builder.setItems(arrayStatusAction, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int position) {
                switch (position){
                    case 0:
                        viewGroupDetail(data);
                        break;
                    case 1:
                        deleteData(data);
                        break;
                }
            }
        });
        builder.show();
    }

    private void viewGroupDetail(ListChatGroup.Data data){
        Intent intent = new Intent(ListChatActivity.this, GomudikGroupActivity.class);
        intent.putExtra("id_group",data.getId());
        intent.putExtra("group_image",data.getGroup_image());
        intent.putExtra("group_name",data.getGroup_name());
        startActivity(intent);
    }

    private void deleteData(final ListChatGroup.Data data){
        final AlertDialog.Builder dialog = new AlertDialog.Builder(ListChatActivity.this);
        dialog.setTitle("Leave group")
                .setMessage("Are you sure to leave "+data.getGroup_name()+" group?")
                .setPositiveButton("Leave", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        leaveGroup(data.getId(), data.getCode());
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                    }
                });
        dialog.show();
    }

    public void leaveGroup(String id_group, final String id_chat_room){
        loadingDialog.show(getSupportFragmentManager(),"leave group");
        GoMudikInterface goMudikInterface = GoMudikAPI.getAPI().create(GoMudikInterface.class);
        Call<GetDataApiDefault> call = goMudikInterface.leaveGroup(getCurrToken(),id_group,getCurrId());
        call.enqueue(new Callback<GetDataApiDefault>() {
            @Override
            public void onResponse(Call<GetDataApiDefault> call, Response<GetDataApiDefault> response) {
                if(response.isSuccessful()){
                    if(response.body() != null) {
                        if (response.body().getIs_success()) {
                            new GoMudikFirebase().userLeaveGroup(getCurrId(), id_chat_room);
                            Toast.makeText(ListChatActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            loadingDialog.dismiss();
                            init();

                        } else {
                            Toast.makeText(ListChatActivity.this, "Leave group failed!", Toast.LENGTH_SHORT).show();
                            loadingDialog.dismiss();
                        }
                    } else {
                        Toast.makeText(ListChatActivity.this, ""+getResources().getString(R.string.error_loading), Toast.LENGTH_SHORT).show();
                        loadingDialog.dismiss();
                    }
                } else {
                    Toast.makeText(ListChatActivity.this,"Failed: to operate leave group",Toast.LENGTH_SHORT).show();
                    loadingDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<GetDataApiDefault> call, Throwable t) {
                loadingDialog.dismiss();
                Toast.makeText(ListChatActivity.this,"Failed: "+t.toString(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showProgress(Boolean show){
        if(show){
            swipeRefreshLayout.setRefreshing(true);
        } else {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    public class getDataTask extends AsyncTask<Void,Void,Boolean> {

        private String id_user;
        private String token;
        Response<ListChatGroup> response;

        public getDataTask(String id_user, String token){
            this.id_user = id_user;
            this.token = token;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress(true);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                // Simulate network access.
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                return false;
            }
            GoMudikInterface goMudikInterface = GoMudikAPI.getAPI().create(GoMudikInterface.class);
            Call<ListChatGroup> call = goMudikInterface.getListChatGroup(token,id_user);
            try {
                response = call.execute();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            mGetDataTask = null;
            if(success){
                if(response.isSuccessful()){
                    if(response.body() != null) {
                        if (response.body().getTotal_data() == 0) {
                            cage.setVisibility(View.GONE);
                            errorResponse.setVisibility(View.GONE);
                            noDataResponse.setVisibility(View.VISIBLE);
                        } else {
                            cage.setVisibility(View.VISIBLE);
                            errorResponse.setVisibility(View.GONE);
                            noDataResponse.setVisibility(View.GONE);
                            showData(response.body());
                        }
                    } else {
                        Toast.makeText(ListChatActivity.this, ""+getResources().getString(R.string.error_loading), Toast.LENGTH_SHORT).show();
                        cage.setVisibility(View.GONE);
                        noDataResponse.setVisibility(View.GONE);
                        errorResponse.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(ListChatActivity.this,""+response.message(),Toast.LENGTH_SHORT).show();
                    cage.setVisibility(View.GONE);
                    noDataResponse.setVisibility(View.GONE);
                    errorResponse.setVisibility(View.VISIBLE);
                }
            } else {
                cage.setVisibility(View.GONE);
                noDataResponse.setVisibility(View.GONE);
                errorResponse.setVisibility(View.VISIBLE);
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
}