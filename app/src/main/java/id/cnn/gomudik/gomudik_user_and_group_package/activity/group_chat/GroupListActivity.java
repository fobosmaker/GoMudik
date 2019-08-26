package id.cnn.gomudik.gomudik_user_and_group_package.activity.group_chat;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import id.cnn.gomudik.R;
import id.cnn.gomudik.api.GoMudikAPI;
import id.cnn.gomudik.api.GoMudikInterface;
import id.cnn.gomudik.gomudik_main_package.activity.MapsActivity;
import id.cnn.gomudik.gomudik_user_and_group_package.model.ListChatGroup;
import id.cnn.gomudik.gomudik_main_package.adapter.GroupListAdapter;
import id.cnn.gomudik.gomudik_main_package.model.GetLocationByGroup;
import id.cnn.gomudik.util.SharedPreferenceCheckerActivity;
import retrofit2.Call;
import retrofit2.Response;

public class GroupListActivity extends SharedPreferenceCheckerActivity {
    private RecyclerView cage;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RelativeLayout templateError;
    private getDataTask mGetDataTask;
    private getGroupMemberTask mGetGroupMemberTask;
    private static final String TAG = "GroupListActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("List group");
        getSupportActionBar().setSubtitle("Find member's group location");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        cage = findViewById(R.id.cage);
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                init();
            }
        });
        templateError = findViewById(R.id.template_error);
        init();
    }

    public void init(){
        templateError.setVisibility(View.GONE);
        cage.setVisibility(View.VISIBLE);
        mGetDataTask = new getDataTask();
        mGetDataTask.execute((Void)null);
    }

    public void showData(ListChatGroup response){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        cage.setLayoutManager(linearLayoutManager);
        cage.setHasFixedSize(true);
        GroupListAdapter adapter = new GroupListAdapter(response.getData(), GroupListActivity.this);
        cage.setAdapter(adapter);
    }

    public void selectedData(String id_group){
        Log.d(TAG, "selectedData: "+id_group);
        mGetGroupMemberTask = new getGroupMemberTask(id_group, getLatitude(), getLongitude());
        mGetGroupMemberTask.execute((Void)null);
    }

    public void returnData(Response<GetLocationByGroup> resp){
        ArrayList<GetLocationByGroup.Data> a = new ArrayList<>();
        int limit = resp.body().getTotal_data();
        for (int i = 0; i < limit; i++){
            GetLocationByGroup.Data x = resp.body().getData().get(i);
            a.add(i,new GetLocationByGroup.Data(x.getId(), x.getFullname(), x.getEmail(), x.getUsers_image_link(), x.getLatitude(), x.getLongitude(), x.getDistance(), x.getCreated()));
        }
        Intent resultIntent = new Intent();
        Bundle b = new Bundle();
        b.putSerializable("data",a);
        resultIntent.putExtras(b);
        setResult(Activity.RESULT_OK,resultIntent);
        finish();
    }

    public void showProgress(Boolean show){
        if(show){
            swipeRefreshLayout.setRefreshing(true);
        } else {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(GroupListActivity.this, MapsActivity.class));
        finish();
    }

    public class getDataTask extends AsyncTask<Void,Void,Boolean> {
        Response<ListChatGroup> response;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress(true);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }
            GoMudikInterface goMudikInterface = GoMudikAPI.getAPI().create(GoMudikInterface.class);
            Call<ListChatGroup> call = goMudikInterface.getListChatGroup(getCurrToken(),getCurrId());
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
                        if (response.body().getTotal_data() > 0) {
                            showData(response.body());
                        } else {
                            cage.setVisibility(View.GONE);
                            templateError.setVisibility(View.VISIBLE);
                        }
                    } else {
                        cage.setVisibility(View.GONE);
                        templateError.setVisibility(View.VISIBLE);
                        Toast.makeText(GroupListActivity.this, ""+getResources().getString(R.string.error_loading), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    cage.setVisibility(View.GONE);
                    templateError.setVisibility(View.VISIBLE);
                }
            } else {
                cage.setVisibility(View.GONE);
                templateError.setVisibility(View.VISIBLE);
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

    public class getGroupMemberTask extends AsyncTask<Void,Void,Boolean> {
        private String id_group;
        private Double latitude;
        private Double longitude;
        Response<GetLocationByGroup> response2;

        public getGroupMemberTask(String id_group, Double latitude, Double longitude){
            this.id_group = id_group;
            this.latitude = latitude;
            this.longitude = longitude;

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
                Thread.sleep(2000);
                GoMudikInterface goMudikInterface = GoMudikAPI.getAPI().create(GoMudikInterface.class);
                Call<GetLocationByGroup> call = goMudikInterface.getGroupMemberLocation(getCurrToken(),this.id_group, latitude,longitude);
                try {
                    response2 = call.execute();
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            } catch (InterruptedException e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            mGetGroupMemberTask = null;
            if(success){
                if(response2.isSuccessful()){
                    if(response2.body().getTotal_data() > 0){
                        Log.d(TAG, "onPostExecute: success");
                        returnData(response2);
                    } else {
                        cage.setVisibility(View.GONE);
                        templateError.setVisibility(View.VISIBLE);
                        Log.d(TAG, "onPostExecute: data is 0");
                    }
                } else {
                    cage.setVisibility(View.GONE);
                    templateError.setVisibility(View.VISIBLE);
                    Log.d(TAG, "onPostExecute: response is failed");
                }
            } else {
                cage.setVisibility(View.GONE);
                templateError.setVisibility(View.VISIBLE);
                Log.d(TAG, "onPostExecute: error connection");
            }
            showProgress(false);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            mGetGroupMemberTask = null;
            showProgress(false);
        }
    }
}