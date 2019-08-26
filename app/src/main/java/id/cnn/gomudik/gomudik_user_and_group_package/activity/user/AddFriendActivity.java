package id.cnn.gomudik.gomudik_user_and_group_package.activity.user;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import id.cnn.gomudik.gomudik_user_and_group_package.adapter.ListAddFriendAdapter;
import id.cnn.gomudik.R;
import id.cnn.gomudik.api.GoMudikAPI;
import id.cnn.gomudik.api.GoMudikInterface;
import id.cnn.gomudik.api.GetDataApiDefault;
import id.cnn.gomudik.gomudik_user_and_group_package.model.ListUsers;
import id.cnn.gomudik.firebase.GoMudikFirebase;
import id.cnn.gomudik.util.LoadingProgress;
import id.cnn.gomudik.util.SharedPreferenceCheckerActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddFriendActivity extends SharedPreferenceCheckerActivity {
    private getDataTask mGetDataTask = null;
    private RecyclerView cage;
    private static final String TAG = "AddFriendActivity";
    ArrayList<ListUsers.Data> list =  new ArrayList<>();
    private ListAddFriendAdapter listAddFriendAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RelativeLayout templateError, templateNoData;
    private android.support.v4.app.DialogFragment loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("New Friends");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        cage = findViewById(R.id.cage);
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                prepareGetData();
            }
        });
        templateError = findViewById(R.id.template_error);
        templateNoData = findViewById(R.id.template_no_data);
        loadingDialog = new LoadingProgress();
        cage.setLayoutManager(new LinearLayoutManager(this));
        cage.setHasFixedSize(true);
        listAddFriendAdapter = new ListAddFriendAdapter(list, AddFriendActivity.this);
        cage.setAdapter(listAddFriendAdapter);
        prepareGetData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_default,menu);
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
                listAddFriendAdapter.filterList(filteredList);
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

    public void prepareGetData(){
        templateError.setVisibility(View.GONE);
        templateNoData.setVisibility(View.GONE);
        cage.setVisibility(View.VISIBLE);
        mGetDataTask = new getDataTask(getCurrId(),getCurrToken());
        mGetDataTask.execute((Void)null);
    }

    public void dataSelection(final String id, final String name){

        final AlertDialog.Builder dialog = new AlertDialog.Builder(AddFriendActivity.this);
        dialog.setTitle("Add friend request")
                .setMessage("Are you sure want to add "+name+" as a friend?")
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        loadingDialog.show(getSupportFragmentManager(),"Loading Add user");
                        GoMudikInterface goMudikInterface = GoMudikAPI.getAPI().create(GoMudikInterface.class);
                        Call<GetDataApiDefault> call_addUser = goMudikInterface.addUser(getCurrToken(),getCurrId(),id);
                        call_addUser.enqueue(new Callback<GetDataApiDefault>() {
                            @Override
                            public void onResponse(Call<GetDataApiDefault> call, Response<GetDataApiDefault> response) {
                                new GoMudikFirebase().updateNotif(id);
                                loadingDialog.dismiss();
                                prepareGetData();
                                Toast.makeText(AddFriendActivity.this,response.body().getMessage(),Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(Call<GetDataApiDefault> call, Throwable t) {
                                loadingDialog.dismiss();
                                Toast.makeText(AddFriendActivity.this,"Failed add friend caused: "+t.toString(),Toast.LENGTH_SHORT).show();
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

    public void showData(ListUsers response){
        int limit = response.getTotal_data();
        list.clear();
        for(int i = 0; i < limit; i++){
            list.add(i, new ListUsers.Data(response.getData().get(i).getUsers_id(), response.getData().get(i).getUsers_name(),response.getData().get(i).getUsers_email(),response.getData().get(i).getUsers_image_link()));
        }
        listAddFriendAdapter.notifyDataSetChanged();
    }

    public void showProgress(Boolean show){
        if(show){
            swipeRefreshLayout.setRefreshing(true);
        } else {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    public class getDataTask extends AsyncTask<Void,Void,Boolean>{

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
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                return false;
            }
            GoMudikInterface goMudikInterface = GoMudikAPI.getAPI().create(GoMudikInterface.class);
            Call<ListUsers> call = goMudikInterface.getListAddUser(this.token,this.id_user);
            try {
                response = call.execute();
                Log.d(TAG, "doInBackground: run");
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "doInBackground: failed: "+e.toString());
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
                        templateNoData.setVisibility(View.VISIBLE);
                        Toast.makeText(AddFriendActivity.this,"Body null",Toast.LENGTH_SHORT).show();
                    } else{
                        if(response.body().getTotal_data() == 0){
                            cage.setVisibility(View.GONE);
                            templateNoData.setVisibility(View.VISIBLE);
                        } else{
                            showData(response.body());
                            Log.d(TAG, "onPostExecute: success result");
                        }
                    }
                } else {
                    Toast.makeText(AddFriendActivity.this,""+response.message(),Toast.LENGTH_LONG).show();
                }
            } else {
                cage.setVisibility(View.GONE);
                templateError.setVisibility(View.VISIBLE);
                Log.d(TAG, "onPostExecute: failed result ");
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