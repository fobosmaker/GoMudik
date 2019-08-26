package id.cnn.gomudik.gomudik_user_and_group_package.activity.group_chat;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import id.cnn.gomudik.R;
import id.cnn.gomudik.api.GetDataApiDefault;
import id.cnn.gomudik.api.GoMudikAPI;
import id.cnn.gomudik.api.GoMudikInterface;
import id.cnn.gomudik.firebase.firebase_model.Firebase_login;
import id.cnn.gomudik.gomudik_user_and_group_package.activity.user.AddFriendActivity;
import id.cnn.gomudik.gomudik_user_and_group_package.adapter.ListContactInviteAdapter;
import id.cnn.gomudik.gomudik_user_and_group_package.model.ListUsers;
import id.cnn.gomudik.util.LoadingProgress;
import id.cnn.gomudik.util.SharedPreferenceCheckerActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InviteActivity extends SharedPreferenceCheckerActivity {
    Toolbar toolbar;
    ListContactInviteAdapter listContactAdapter;
    ArrayList<ListUsers.Data> list = new ArrayList<>();
    List<String> list_id = new ArrayList<>();
    List<Firebase_login> memberGroup = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView cage;
    private getDataTask mGetDataTask = null;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RelativeLayout templateError, templateNoData, cageAddGroup;
    private Button buttonNext;
    private String id_group;
    private static final String TAG = "InviteActivity";
    private DialogFragment loadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);
        toolbar = findViewById(R.id.toolbar);
        if(getIntent().getStringExtra("group_name") != null && getIntent().getStringExtra("group_id") != null){
            String title = "Invite to "+getIntent().getStringExtra("group_name");
            id_group = getIntent().getStringExtra("group_id");
            toolbar.setTitle(title);

        } else {
            toolbar.setTitle("Invite to group");
        }

        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        linearLayoutManager = new LinearLayoutManager(this);
        cage = findViewById(R.id.cage);
        cageAddGroup = findViewById(R.id.cage_add_group);
        templateError = findViewById(R.id.template_error);
        templateNoData = findViewById(R.id.template_no_data);
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                init();
            }
        });
        loadingDialog = new LoadingProgress();
        Button buttonNoData = findViewById(R.id.buttonNoData);
        buttonNoData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(InviteActivity.this, AddFriendActivity.class));
            }
        });
        buttonNext = findViewById(R.id.button_next);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if(list_id.isEmpty()){
                Toast.makeText(InviteActivity.this,"Select at least 1 participant to invite to group",Toast.LENGTH_SHORT).show();
            } else {
                loadingDialog.show(getSupportFragmentManager(),"Invite to group");
                String member = String.valueOf(TextUtils.join(",", list_id));
                GoMudikInterface goMudikInterface = GoMudikAPI.getAPI().create(GoMudikInterface.class);
                Call<GetDataApiDefault> call = goMudikInterface.inviteToGroup(getCurrToken(),id_group,member);
                call.enqueue(new Callback<GetDataApiDefault>() {
                    @Override
                    public void onResponse(Call<GetDataApiDefault> call, Response<GetDataApiDefault> response) {
                        loadingDialog.dismiss();
                        if(response.isSuccessful()){
                            if(response.body() != null) {
                                Toast.makeText(InviteActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                if (response.body().getIs_success()) {
                                    Intent resultIntent = new Intent();
                                    setResult(Activity.RESULT_OK, resultIntent);
                                    finish();
                                }
                            } else {
                                Toast.makeText(InviteActivity.this, ""+getResources().getString(R.string.error_loading), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(InviteActivity.this, ""+response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<GetDataApiDefault> call, Throwable t) {
                        loadingDialog.dismiss();
                        Toast.makeText(InviteActivity.this, "Connection error, try again later!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            }
        });
        init();
    }

    public void init(){
        templateNoData.setVisibility(View.GONE);
        templateError.setVisibility(View.GONE);
        cageAddGroup.setVisibility(View.GONE);
        mGetDataTask = new getDataTask(getCurrId(),getCurrToken(), id_group);
        mGetDataTask.execute((Void)null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_group_add_contact,menu);
        MenuItem menuItem = menu.findItem(R.id.item_search);
        android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                List<ListUsers.Data> filteredList = new ArrayList<>();
                for (ListUsers.Data listUsers:list){
                    if(listUsers.getUsers_name().toLowerCase().contains(newText.toLowerCase()) || listUsers.getUsers_email().toLowerCase().contains(newText.toLowerCase())){
                        filteredList.add(listUsers);
                    }
                }
                listContactAdapter.filterList(filteredList);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void prepareSelection(View view,int position){
        if(((CheckBox)view).isChecked()){
            list_id.add(list.get(position).getUsers_id());
        } else {
            list_id.remove(list.get(position).getUsers_id());
        }
        updateCounter(list_id.size());
    }

    public void showData(ListUsers response){
        list.clear();
        buttonNext.setEnabled(false);
        Log.d(TAG, "showData: "+memberGroup.size());
            int limit = response.getTotal_data();
            for (int i = 0; i < limit; i++) {
                list.add(i, new ListUsers.Data(response.getData().get(i).getUsers_id(), response.getData().get(i).getUsers_name(), response.getData().get(i).getUsers_email(), response.getData().get(i).getUsers_image_link()));
            }
            cage.setLayoutManager(linearLayoutManager);
            cage.setHasFixedSize(true);
            listContactAdapter = new ListContactInviteAdapter(list, InviteActivity.this);
            cage.setAdapter(listContactAdapter);
            updateCounter(list_id.size());
            listContactAdapter.notifyDataSetChanged();
    }

    public void updateCounter(int counter){
        if(counter <= 0){
            toolbar.setSubtitle("0 friend selected");
            buttonNext.setEnabled(false);
        } else {
            if(counter == 1){
                toolbar.setSubtitle(counter+" friend selected");
            } else {
                toolbar.setSubtitle(counter+" friends selected");
            }
            buttonNext.setEnabled(true);
        }
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
        private String id_group;
        Response<ListUsers> response;

        public getDataTask(String id_user, String token, String id_group){
            this.id_user = id_user;
            this.token = token;
            this.id_group = id_group;
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
            Call<ListUsers> call = goMudikInterface.getListInvite(token,id_group,id_user);
            Log.d(TAG, "doInBackground: token:"+token+" id_group:"+id_group+" id_user:"+id_user);
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
                        cageAddGroup.setVisibility(View.GONE);
                        templateNoData.setVisibility(View.VISIBLE);
                    } else{
                        if(response.body().getTotal_data() == 0){
                            cageAddGroup.setVisibility(View.GONE);
                            templateError.setVisibility(View.GONE);
                            templateNoData.setVisibility(View.VISIBLE);
                        } else{
                            cageAddGroup.setVisibility(View.VISIBLE);
                            showData(response.body());
                        }
                    }
                } else {
                    cageAddGroup.setVisibility(View.GONE);
                    templateNoData.setVisibility(View.GONE);
                    templateError.setVisibility(View.VISIBLE);
                }
            } else {
                cageAddGroup.setVisibility(View.GONE);
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
}
