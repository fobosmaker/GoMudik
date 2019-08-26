package id.cnn.gomudik.gomudik_user_and_group_package.activity.user;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import id.cnn.gomudik.api.GetDataApiDefault;
import id.cnn.gomudik.api.GoMudikAPI;
import id.cnn.gomudik.api.GoMudikInterface;
import id.cnn.gomudik.gomudik_user_and_group_package.activity.group_chat.AddGroupStep1Activity;
import id.cnn.gomudik.gomudik_user_and_group_package.adapter.GroupTitleAdapter;
import id.cnn.gomudik.gomudik_user_and_group_package.model.GroupContact;
import id.cnn.gomudik.gomudik_user_and_group_package.model.GroupTitle;
import id.cnn.gomudik.R;
import id.cnn.gomudik.gomudik_user_and_group_package.model.ListContact;
import id.cnn.gomudik.firebase.GoMudikFirebase;
import id.cnn.gomudik.util.LoadingProgress;
import id.cnn.gomudik.util.SharedPreferenceCheckerActivity;
import retrofit2.Call;
import retrofit2.Response;

public class ListContactActivity extends SharedPreferenceCheckerActivity {
    private RecyclerView cage;
    private getDataTask mGetDataTask = null;
    private sendDataTask mSendDataTask = null;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<GroupContact> group = new ArrayList<>();
    private List<GroupContact> friend = new ArrayList<>();
    private List<GroupContact> new_invite = new ArrayList<>();
    private List<GroupTitle> list_judul = new ArrayList<>();
    GroupTitleAdapter adapterJudul;
    private static final String TAG = "ListContactActivity";
    private RelativeLayout templateError, templateNoData;
    private android.support.v4.app.DialogFragment loadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_contact);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Friends");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        cage = findViewById(R.id.cage);
        templateError = findViewById(R.id.template_error);
        templateNoData = findViewById(R.id.template_no_data);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        loadingDialog = new LoadingProgress();
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                prepareData();
            }
        });
        Button buttonNoData = findViewById(R.id.buttonNoData);
        buttonNoData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ListContactActivity.this, AddFriendActivity.class));
            }
        });
        prepareData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list_contact,menu);
        MenuItem menuItem = menu.findItem(R.id.item_search);
        android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "onQueryTextSubmit: run");
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                List<GroupContact> filteredListContact = new ArrayList<>();
                for (GroupContact listUsers:friend){
                    if(listUsers.getName().toLowerCase().contains(newText.toLowerCase()) || listUsers.getDescription().toLowerCase().contains(newText.toLowerCase())){
                        filteredListContact.add(listUsers);
                    }
                }
                List<GroupContact> filteredListGroup = new ArrayList<>();
                for (GroupContact listUsers:group){
                    if(listUsers.getName().toLowerCase().contains(newText.toLowerCase()) || listUsers.getDescription().toLowerCase().contains(newText.toLowerCase())){
                        filteredListGroup.add(listUsers);
                    }
                }
                List<GroupTitle> list_judul = new ArrayList<>();
                if(new_invite.size() > 0){
                    List<GroupContact> filteredListInvited = new ArrayList<>();
                    for (GroupContact listUsers:new_invite){
                        if(listUsers.getName().toLowerCase().contains(newText.toLowerCase()) || listUsers.getDescription().toLowerCase().contains(newText.toLowerCase())){
                            filteredListInvited.add(listUsers);
                        }
                    }
                    list_judul.add(new GroupTitle("New invite",filteredListInvited));
                }
                if(filteredListGroup.size() != 0) {
                    list_judul.add(new GroupTitle(generateString("Group",filteredListGroup.size()),filteredListGroup));
                }
                if(filteredListContact.size() !=0) {
                    list_judul.add(new GroupTitle(generateString("Friend",filteredListContact.size()),filteredListContact));
                }

                showData(list_judul);
                return false;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_add_friend:
                startActivity(new Intent(ListContactActivity.this, AddFriendActivity.class));
                break;
            case R.id.item_add_group:
                startActivity(new Intent(ListContactActivity.this, AddGroupStep1Activity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void prepareData(){
        cage.setVisibility(View.VISIBLE);
        templateNoData.setVisibility(View.GONE);
        templateError.setVisibility(View.GONE);
        mGetDataTask = new getDataTask(getCurrId(),getCurrToken());
        mGetDataTask.execute((Void)null);
    }
    public void generateData(ListContact response) {
        //clear data
        list_judul.clear();
        group.clear();
        friend.clear();
        new_invite.clear();

        Integer total_group, total_friends, total_invited;
        //Data invited
        total_invited = response.getTotal_data_invited();
        if(total_invited > 0){

            for (int i = 0; i< total_invited; i++){
                int x;
                if(response.getData_invited().get(i).getDescription().contains("@")){
                    x = 2;
                } else {
                    x = 1;
                }
                new_invite.add(i,new GroupContact(response.getData_invited().get(i).getId(),response.getData_invited().get(i).getName(),response.getData_invited().get(i).getDescription(),response.getData_invited().get(i).getImage_link(),response.getData_invited().get(i).getCode(),x,2));
            }
            list_judul.add(new GroupTitle("New invite",new_invite));
        }
        //Data group
        total_group = response.getTotal_data_group();
        if (total_group > 0){
            for (int i = 0; i < total_group; i++) {
                group.add(i,new GroupContact(response.getData_group().get(i).getId(), response.getData_group().get(i).getName(), response.getData_group().get(i).getDescription(), response.getData_group().get(i).getImage_link(), response.getData_group().get(i).getCode(), 1, 1));
            }
            list_judul.add(new GroupTitle(generateString("Group",total_group),group));
        }
        //Data friend
        total_friends = response.getTotal_data_friend();
        if(total_friends > 0) {
            for (int i = 0; i < total_friends; i++) {
                friend.add(i,new GroupContact(response.getData_friend().get(i).getId(), response.getData_friend().get(i).getName(), response.getData_friend().get(i).getDescription(), response.getData_friend().get(i).getImage_link(), response.getData_friend().get(i).getCode(), 2, 1));
            }
            list_judul.add(new GroupTitle(generateString("Friend",total_friends),friend));
        }
        showData(list_judul);
    }
    public void showData(List<GroupTitle> data){
        adapterJudul = new GroupTitleAdapter(data,ListContactActivity.this);
        cage.setLayoutManager(new LinearLayoutManager(ListContactActivity.this));
        cage.setAdapter(adapterJudul);
    }

    public void showProgress(Boolean show){
        if(show){
            swipeRefreshLayout.setRefreshing(true);
        } else {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    public void responseButton(final Integer type, final String id, final Boolean isAccepted, final String code){
        String subject, userResponse;
        if(type == 1){
            subject = "Group";
        } else {
            subject = "Friend";
        }

        final int responseUser;
        if(isAccepted){
            userResponse = "Accept";
            responseUser = 2;
        } else {
            userResponse = "Reject";
            responseUser = 3;
        }
        final AlertDialog.Builder dialog = new AlertDialog.Builder(ListContactActivity.this);
        dialog.setTitle(subject+" Request")
                .setMessage("Are you sure want to "+userResponse.toLowerCase()+" this "+subject.toLowerCase()+" request?")
                .setPositiveButton(userResponse, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        mSendDataTask = new sendDataTask(getCurrId(),getCurrToken(),id,type,responseUser);
                        mSendDataTask.execute((Void)null);
                        if(type == 1){
                            if(isAccepted){
                                new GoMudikFirebase().addUserUnseenGroupMessage(getCurrId(),code);
                            }
                        }
                        if(type == 2){
                            new GoMudikFirebase().updateNotif(id);
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                    }
                });
        dialog.show();
    }

    public String generateString(String content, Integer totalData){
        if(totalData == 0){
            return content+" ("+totalData+")";
        } else {
            return content+"s ("+totalData+")";
        }
    }

    public class getDataTask extends AsyncTask<Void,Void,Boolean> {

        private String id_user;
        private String token;
        Response<ListContact> response;

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
            Call<ListContact> call = goMudikInterface.getListContact(this.token, this.id_user);
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
                        if (response.body().getTotal_data_friend() == 0 && response.body().getTotal_data_group() == 0 && response.body().getTotal_data_invited() == 0) {
                            cage.setVisibility(View.GONE);
                            templateError.setVisibility(View.GONE);
                            templateNoData.setVisibility(View.VISIBLE);
                        } else {
                            cage.setVisibility(View.VISIBLE);
                            generateData(response.body());
                        }
                    } else {
                        Toast.makeText(ListContactActivity.this, ""+getResources().getString(R.string.error_loading), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ListContactActivity.this,""+response.message(),Toast.LENGTH_SHORT).show();
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

    public class sendDataTask extends AsyncTask<Void,Void,Boolean> {

        private String id_user, token, id_friend;
        private Integer type, responseUser;
        Response<GetDataApiDefault> response;

        public sendDataTask(String id_user, String token, String id_friend, Integer type, Integer responseUser ){
            this.id_user = id_user;
            this.token = token;
            this.id_friend = id_friend;
            this.type = type;
            this.responseUser = responseUser;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog.show(getSupportFragmentManager(),"Loading send confirm");
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
            Call<GetDataApiDefault> call = goMudikInterface.responseAdd(this.token, this.id_user, id_friend,type,responseUser);
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
            mSendDataTask = null;
            if(success){
                if(response.isSuccessful()) {
                    if(response.body() != null) {
                        prepareData();
                        Toast.makeText(ListContactActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ListContactActivity.this, ""+getResources().getString(R.string.error_loading), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ListContactActivity.this, ""+response.message(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(ListContactActivity.this,"Connection error, try again later",Toast.LENGTH_SHORT).show();
            }
            loadingDialog.dismiss();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            mSendDataTask = null;
            showProgress(false);
        }
    }
}