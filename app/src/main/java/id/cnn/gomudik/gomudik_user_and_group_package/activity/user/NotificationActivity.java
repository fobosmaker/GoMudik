package id.cnn.gomudik.gomudik_user_and_group_package.activity.user;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import id.cnn.gomudik.R;
import id.cnn.gomudik.api.GoMudikAPI;
import id.cnn.gomudik.api.GoMudikInterface;
import id.cnn.gomudik.firebase.GoMudikFirebase;
import id.cnn.gomudik.gomudik_main_package.activity.StatusCommentActivity;
import id.cnn.gomudik.gomudik_user_and_group_package.adapter.ListNotificationAdapter;
import id.cnn.gomudik.gomudik_user_and_group_package.model.ListNotification;
import id.cnn.gomudik.gomudik_user_and_group_package.model.ListStatus;
import id.cnn.gomudik.util.SharedPreferenceCheckerActivity;
import retrofit2.Call;
import retrofit2.Response;

public class NotificationActivity extends SharedPreferenceCheckerActivity {
    private getDataTask mGetDataTask = null;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView cage;
    private boolean isScroll = false;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RelativeLayout templateError, templateNoData;
    private ArrayList<ListNotification.Data> data = new ArrayList<>();
    private ListNotificationAdapter adapter;
    private static final String TAG = "NotificationActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Notification");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        linearLayoutManager = new LinearLayoutManager(this);
        cage = findViewById(R.id.cage);
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!data.isEmpty()){
                    data.clear();
                    adapter.notifyDataSetChanged();
                }
                cageAddListener();
                prepareGetData();
            }
        });
        templateError = findViewById(R.id.template_error);
        templateNoData = findViewById(R.id.template_no_data);

        cage.setLayoutManager(linearLayoutManager);
        cage.setHasFixedSize(true);
        adapter = new ListNotificationAdapter(data,this);
        cage.setAdapter(adapter);
        cageAddListener();

        new GoMudikFirebase().resetUserNotification(getCurrId());
        prepareGetData();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void cageAddListener(){
        cage.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                    isScroll = true;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(isScroll && (linearLayoutManager.getChildCount() + linearLayoutManager.findFirstVisibleItemPosition() == linearLayoutManager.getItemCount())){
                    isScroll = false;
                    prepareGetData();
                }
            }
        });
    }

    public void prepareGetData(){
        templateError.setVisibility(View.GONE);
        templateNoData.setVisibility(View.GONE);
        cage.setVisibility(View.VISIBLE);
        mGetDataTask = new getDataTask(getCurrId(),getCurrToken(),data.size());
        mGetDataTask.execute((Void)null);
    }

    public void showData(ListNotification response){
        int limit = response.getTotal_data();
        int index = data.size();
        for (int i = 0; i < limit; i++) {
            ListNotification.Data row = response.getData().get(i);
            data.add(index + i, row);
        }
        Log.d(TAG, "showData: " + data.size());
        adapter.notifyItemRangeInserted(index, data.size() - 1);
        if (data.size() >= response.getAll_data()) {
            cage.clearOnScrollListeners();
        }

    }

    public void showProgress(Boolean show){
        if(show){
            swipeRefreshLayout.setRefreshing(true);
        } else {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    public void searchStatusById(String id_status){
        getDataStatusTask mGetDataStatus = new getDataStatusTask(id_status,getCurrToken());
        mGetDataStatus.execute((Void)null);
    }

    public class getDataTask extends AsyncTask<Void,Void,Boolean> {

        private String id_user;
        private String token;
        int index_at;

        Response<ListNotification> response;

        public getDataTask(String id_user, String token, int index_at){
            this.id_user = id_user;
            this.token = token;
            this.index_at = index_at;
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
            Call<ListNotification> call = goMudikInterface.getNotificationWithIndex(this.token,this.id_user,index_at);
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
                    if(response.body() == null){
                        cage.setVisibility(View.GONE);
                        templateError.setVisibility(View.GONE);
                        templateNoData.setVisibility(View.VISIBLE);
                    } else{
                        if(response.body().getTotal_data() == 0){
                            cage.setVisibility(View.GONE);
                            templateError.setVisibility(View.GONE);
                            templateNoData.setVisibility(View.VISIBLE);
                        } else{
                            showData(response.body());
                        }
                    }
                } else {
                    Toast.makeText(NotificationActivity.this,""+response.message(),Toast.LENGTH_LONG).show();
                }
            } else {
                cage.setVisibility(View.GONE);
                templateNoData.setVisibility(View.GONE);
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

    public class getDataStatusTask extends AsyncTask<Void,Void,Boolean> {

        private String id_status;
        private String token;
        Response<ListStatus.Data> response;

        public getDataStatusTask(String id_status, String token){
            this.id_status = id_status;
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
            Call<ListStatus.Data> call = goMudikInterface.getUserStatusFromNotification(token,id_status);
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
                        ListStatus.Data item = response.body();
                        Intent intent = new Intent(NotificationActivity.this, StatusCommentActivity.class);
                        intent.putExtra("data", item);
                        Log.d(TAG, "onPostExecute: " + item.getContent());
                        startActivity(intent);
                    } else {
                        Toast.makeText(NotificationActivity.this,""+getResources().getString(R.string.error_loading),Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(NotificationActivity.this,""+response.message(),Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(NotificationActivity.this,""+response.message(),Toast.LENGTH_LONG).show();
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
