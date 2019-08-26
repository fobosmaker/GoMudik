package id.cnn.gomudik.gomudik_user_and_group_package.activity.group_chat;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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

import id.cnn.gomudik.gomudik_user_and_group_package.adapter.ListContactAdapter;
import id.cnn.gomudik.R;
import id.cnn.gomudik.api.GoMudikAPI;
import id.cnn.gomudik.api.GoMudikInterface;
import id.cnn.gomudik.gomudik_user_and_group_package.model.ListUsers;
import id.cnn.gomudik.gomudik_user_and_group_package.activity.user.AddFriendActivity;
import id.cnn.gomudik.util.SharedPreferenceCheckerActivity;
import retrofit2.Call;
import retrofit2.Response;

public class AddGroupStep1Activity extends SharedPreferenceCheckerActivity {
    Toolbar toolbar;
    ListContactAdapter listContactAdapter;
    ArrayList<ListUsers.Data> list = new ArrayList<>();
    ArrayList<ListUsers.Data> selected = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView cage;
    private getDataTask mGetDataTask = null;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RelativeLayout templateError, templateNoData, cageAddGroup;
    private Button buttonNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group_step1);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("New group");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
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
        Button buttonNoData = findViewById(R.id.buttonNoData);
        buttonNoData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AddGroupStep1Activity.this, AddFriendActivity.class));
            }
        });
        buttonNext = findViewById(R.id.button_next);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selected.isEmpty()){
                    Toast.makeText(AddGroupStep1Activity.this,"Select at least 1 participant to create group",Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(AddGroupStep1Activity.this, AddGroupStep2Activity.class);
                    Bundle b = new Bundle();
                    b.putSerializable("data",selected);
                    intent.putExtras(b);
                    startActivity(intent);
                }
            }
        });
        cage.setLayoutManager(linearLayoutManager);
        cage.setHasFixedSize(true);
        listContactAdapter = new ListContactAdapter(list, AddGroupStep1Activity.this);
        cage.setAdapter(listContactAdapter);
        init();
    }

    public void init(){
        templateNoData.setVisibility(View.GONE);
        templateError.setVisibility(View.GONE);
        cageAddGroup.setVisibility(View.GONE);
        mGetDataTask = new getDataTask(getCurrId(),getCurrToken());
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
                for (ListUsers.Data listUsers : list) {
                    if (listUsers.getUsers_name().toLowerCase().contains(newText.toLowerCase()) || listUsers.getUsers_email().toLowerCase().contains(newText.toLowerCase())) {
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
            selected.add(list.get(position));
            updateCounter(selected.size());
        } else {
            selected.remove(list.get(position));
            updateCounter(selected.size());
        }
    }

    public void updateCounter(int counter){
        if(counter <= 0){
            toolbar.setSubtitle("0 participant selected");
            buttonNext.setEnabled(false);
        } else {
            if(counter == 1){
                toolbar.setSubtitle(counter+" participant selected");
            } else {
                toolbar.setSubtitle(counter+" participants selected");
            }
            buttonNext.setEnabled(true);
        }
    }
    public void showData(ListUsers response){
        list.clear();
        buttonNext.setEnabled(false);
        int limit = response.getTotal_data();
        for(int i = 0; i < limit; i++){
            list.add(i, new ListUsers.Data(response.getData().get(i).getUsers_id(), response.getData().get(i).getUsers_name(),response.getData().get(i).getUsers_email(),response.getData().get(i).getUsers_image_link()));
        }
        updateCounter(selected.size());
        listContactAdapter.notifyDataSetChanged();
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
        Response<ListUsers> response;

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
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }
            GoMudikInterface goMudikInterface = GoMudikAPI.getAPI().create(GoMudikInterface.class);
            Call<ListUsers> call = goMudikInterface.getListUserFriend(this.token,this.id_user);
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
                        Toast.makeText(AddGroupStep1Activity.this,"Body null",Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(AddGroupStep1Activity.this,""+response.message(),Toast.LENGTH_LONG).show();
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