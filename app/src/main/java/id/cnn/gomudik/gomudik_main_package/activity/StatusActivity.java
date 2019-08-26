package id.cnn.gomudik.gomudik_main_package.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import id.cnn.gomudik.R;
import id.cnn.gomudik.api.GetDataApiDefault;
import id.cnn.gomudik.api.GoMudikAPI;
import id.cnn.gomudik.api.GoMudikInterface;
import id.cnn.gomudik.gomudik_ads.DialogAds;
import id.cnn.gomudik.gomudik_ads.model.GetAds;
import id.cnn.gomudik.gomudik_main_package.adapter.ListStatusAdapter2;
import id.cnn.gomudik.gomudik_main_package.model.StatusActivityModel;
import id.cnn.gomudik.gomudik_user_and_group_package.activity.user.AddStatusActivity;
import id.cnn.gomudik.gomudik_user_and_group_package.model.ListStatus;
import id.cnn.gomudik.util.Session;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatusActivity extends AppCompatActivity {
    private getDataTask mGetDataTask;
    private RecyclerView cage;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RelativeLayout templateError,templateNoData;
    private RelativeLayout topAds, bottomAds;
    private ImageView topAdsImage, bottomAdsImage;
    private String currUserId, currToken = null;
    private ListStatusAdapter2 listStatusAdapter;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<ListStatus.Data> dataStatus = new ArrayList<>();
    private boolean isScroll = false;

    private static final String TAG = "StatusActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Status");
        getSupportActionBar().setSubtitle("People share about their mudik");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        cage = findViewById(R.id.cage);
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!dataStatus.isEmpty()){
                    dataStatus.clear();
                    listStatusAdapter.notifyDataSetChanged();
                }
                cageAddListener();
                init();
            }
        });
        templateError = findViewById(R.id.template_error);
        templateNoData = findViewById(R.id.template_no_data);
        Session session = new Session(StatusActivity.this);
        if(session.login()){
            HashMap<String, String> map = session.getData();
            currUserId = map.get(session.KEY_ID);
            currToken = map.get(session.KEY_TOKEN);
        }
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StatusActivity.this, AddStatusActivity.class));
            }
        });

        topAds = findViewById(R.id.topAds);
        bottomAds = findViewById(R.id.bottomAds);
        topAdsImage = findViewById(R.id.top_ads_image);
        bottomAdsImage = findViewById(R.id.bottom_ads_image);
        linearLayoutManager =  new LinearLayoutManager(StatusActivity.this);
        cage.setLayoutManager(linearLayoutManager);
        listStatusAdapter = new ListStatusAdapter2(dataStatus, StatusActivity.this,dataStatus.size());
        cage.setAdapter(listStatusAdapter);
        cageAddListener();
        init();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart: true");
        if(!dataStatus.isEmpty()){
            dataStatus.clear();
            listStatusAdapter.notifyDataSetChanged();
        }
        init();
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
                    init();
                }
            }
        });
    }

    public void init(){
        cage.setVisibility(View.VISIBLE);
        templateNoData.setVisibility(View.GONE);
        templateError.setVisibility(View.GONE);
        mGetDataTask = new getDataTask(dataStatus.size());
        mGetDataTask.execute((Void)null);
    }

    public void showData(StatusActivityModel response){
        GetAds ads = response.getAds();
        generateAds(ads);
        int limit = response.getStatus().getTotal_data();
        int index = dataStatus.size();
        for (int i = 0; i < limit; i++) {
            ListStatus.Data data = response.getStatus().getData().get(i);
            dataStatus.add(index + i, data);
            Log.d(TAG, "showData: " + data.getId_status() + " ");
        }
        Log.d(TAG, "showData: " + dataStatus.size());
        listStatusAdapter.notifyItemRangeInserted(index, dataStatus.size() - 1);
        if (dataStatus.size() >= response.getStatus().getAll_data()) {
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

    public void showProfile(ListStatus.Data user){
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.template_profile, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                paramDialogInterface.dismiss();
            }
        });
        TextView name = dialogView.findViewById(R.id.profil_name);
        TextView email = dialogView.findViewById(R.id.profil_email);
        CircleImageView profileImage = dialogView.findViewById(R.id.profile_image);
        name.setText(user.getUsers_name());
        email.setText(user.getUsers_email());
        if(user.getUsers_image_link() != null){
            Uri uri = Uri.parse("http://gomudik.id:81".concat(user.getUsers_image_link().substring(1)));
            Picasso.get().load(uri).fit().centerInside().placeholder(R.drawable.no_photo).error(R.drawable.no_photo).into(profileImage);
        } else {
            Picasso.get().load(R.drawable.no_photo).fit().centerInside().into(profileImage);
        }

        /*Session session = new Session(StatusActivity.this);
        if(session.login()){
            HashMap<String, String> map = session.getData();
            if(!user.getId_users().equalsIgnoreCase(map.get(session.KEY_ID))) {
                String statusFriend = "Add Friend";
                dialogBuilder.setPositiveButton(statusFriend, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Toast.makeText(StatusActivity.this, "Add Friend", Toast.LENGTH_SHORT).show();
                        paramDialogInterface.dismiss();
                    }
                });
            }
        }*/

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    public void showDialogStatusAction(final ListStatus.Data item){
        String[] arrayStatusAction = {"Edit","Delete"};
        AlertDialog.Builder builder = new AlertDialog.Builder(StatusActivity.this);
        builder.setItems(arrayStatusAction, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int position) {
                        switch (position){
                            case 0:
                                editStatus(item);
                                break;
                            case 1:
                                deleteStatus(item);
                                break;
                        }
                    }
                });
        builder.show();
    }

    private void editStatus(ListStatus.Data item){
        Intent intent = new Intent(StatusActivity.this, AddStatusActivity.class);
        intent.putExtra("data", item);
        startActivity(intent);
    }

    private void deleteStatus(final ListStatus.Data item){
        final AlertDialog.Builder dialog = new AlertDialog.Builder(StatusActivity.this);
        dialog.setTitle("Delete")
                .setMessage("Are you sure want to delete this status?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        //Toast.makeText(StatusActivity.this, "Delete status id: "+item.getId_status(),Toast.LENGTH_SHORT).show();
                        showProgress(true);
                        GoMudikInterface goMudikInterface = GoMudikAPI.getAPI().create(GoMudikInterface.class);
                        Call<GetDataApiDefault> call = goMudikInterface.deleteStatus(currToken, item.getId_status());
                        call.enqueue(new Callback<GetDataApiDefault>() {
                            @Override
                            public void onResponse(Call<GetDataApiDefault> call, Response<GetDataApiDefault> response) {
                                if(response.isSuccessful()){
                                    showProgress(false);
                                    Toast.makeText(StatusActivity.this,""+response.body().getMessage(),Toast.LENGTH_SHORT).show();
                                    init();
                                }
                            }

                            @Override
                            public void onFailure(Call<GetDataApiDefault> call, Throwable t) {
                                showProgress(false);
                                Toast.makeText(StatusActivity.this,"Failed: "+t.toString(),Toast.LENGTH_SHORT).show();
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

    public void showStatusComment(ListStatus.Data item){
        Intent intent = new Intent(StatusActivity.this, StatusCommentActivity.class);
        intent.putExtra("data", item);
        startActivity(intent);
    }

    public void displayProfile(){
        Log.d(TAG, "displayProfile: onClicked");
    }

    private void generateAds(GetAds data){
        if(data.getTotal_data() == 0){
            topAds.setVisibility(View.GONE);
            bottomAds.setVisibility(View.GONE);
        } else {
            if(data.getData().get(0).getId_active().equalsIgnoreCase("1")){
                topAds.setVisibility(View.VISIBLE);
                Picasso.get().load(Uri.parse("http:gomudik.id:81/".concat(data.getData().get(0).getImage_link()))).into(topAdsImage);
            } else {
                topAds.setVisibility(View.GONE);
            }

            if(data.getData().get(1).getId_active().equalsIgnoreCase("1")){
                bottomAds.setVisibility(View.VISIBLE);
                Picasso.get().load(Uri.parse("http:gomudik.id:81/".concat(data.getData().get(1).getImage_link()))).into(bottomAdsImage);
            } else {
                bottomAds.setVisibility(View.GONE);
            }

            if(data.getData().get(2).getId_active().equalsIgnoreCase("1")){
                new DialogAds(StatusActivity.this,data.getData().get(2).getImage_link());
            }
        }
    }

    public String getCurrUserId(){
        return currUserId;
    }

    public class getDataTask extends AsyncTask<Void,Void,Boolean> {

        Response<StatusActivityModel> response;
        int index_at;

        public getDataTask(int index_at){
            this.index_at = index_at;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress(true);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            GoMudikInterface goMudikInterface = GoMudikAPI.getAPI().create(GoMudikInterface.class);
            Call<StatusActivityModel> call = goMudikInterface.getDataStatusActivity(StatusActivity.class.getSimpleName(),index_at);
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
                        if (response.body().getStatus().getTotal_data() == 0) {
                            cage.setVisibility(View.GONE);
                            templateError.setVisibility(View.GONE);
                            templateNoData.setVisibility(View.VISIBLE);
                        } else {
                            showData(response.body());
                        }
                    } else {
                        Toast.makeText(StatusActivity.this,"No data found",Toast.LENGTH_SHORT).show();
                        cage.setVisibility(View.GONE);
                        templateError.setVisibility(View.VISIBLE);
                        templateNoData.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(StatusActivity.this,""+response.message(),Toast.LENGTH_SHORT).show();
                    cage.setVisibility(View.GONE);
                    templateError.setVisibility(View.VISIBLE);
                    templateNoData.setVisibility(View.GONE);
                }
            } else {
                cage.setVisibility(View.GONE);
                templateError.setVisibility(View.VISIBLE);
                templateNoData.setVisibility(View.GONE);
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}