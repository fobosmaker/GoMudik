package id.cnn.gomudik.gomudik_main_package.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import id.cnn.gomudik.R;
import id.cnn.gomudik.api.GetDataApiDefault;
import id.cnn.gomudik.api.GoMudikAPI;
import id.cnn.gomudik.api.GoMudikInterface;
import id.cnn.gomudik.firebase.GoMudikFirebase;
import id.cnn.gomudik.gomudik_main_package.adapter.ListStatusCommentAdapter;
import id.cnn.gomudik.gomudik_user_and_group_package.activity.user.LoginActivity;
import id.cnn.gomudik.gomudik_user_and_group_package.model.ListStatus;
import id.cnn.gomudik.gomudik_user_and_group_package.model.ListStatusComment;
import id.cnn.gomudik.util.BaseGoMudikActivity;
import id.cnn.gomudik.util.LoadingProgress;
import id.cnn.gomudik.util.Session;
import retrofit2.Call;
import retrofit2.Response;

public class StatusCommentActivity extends BaseGoMudikActivity {
    private RecyclerView cage;
    private SwipeRefreshLayout swipeRefreshLayout;
    private getDataTask mGetDataTask;
    private sendCommentStatusTask mSendComment;
    private String statusId = "";
    private String statusUserId = "";
    private String currUserId = "";
    private String currToken = "";
    public static ListStatusComment.Data currStatus;
    private android.support.v4.app.DialogFragment loadingDialog;
    private static final String TAG = "StatusCommentActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_comment);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comment");
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
        loadingDialog = new LoadingProgress();
        Intent b = getIntent();
        if(b.hasExtra("data")){
            ListStatus.Data data = (ListStatus.Data) b.getSerializableExtra("data");
            statusId = data.getId_status();
            statusUserId = data.getId_users();
            currStatus = new ListStatusComment.Data("","",data.getUsers_name(),data.getUsers_email(),data.getCreated(),data.getUsers_image_link(),data.getContent());
            init();

            Button btnLogin = findViewById(R.id.button_login);
            RelativeLayout chatBox = findViewById(R.id.chat_box);
            Session session = new Session(StatusCommentActivity.this);
            if(session.login()){
                HashMap<String, String> map = session.getData();
                currUserId = map.get(session.KEY_ID);
                currToken = map.get(session.KEY_TOKEN);
                btnLogin.setVisibility(View.GONE);
                chatBox.setVisibility(View.VISIBLE);
            } else {
                btnLogin.setVisibility(View.VISIBLE);
                chatBox.setVisibility(View.GONE);
                btnLogin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(StatusCommentActivity.this, LoginActivity.class));
                    }
                });
            }

            final EditText chat = findViewById(R.id.input_chat);
            final ImageButton send = findViewById(R.id.send_button);
            send.setEnabled(false);
            chat.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

                @Override
                public void afterTextChanged(Editable str) {
                    if(str.toString().trim().length()>0){
                        send.setEnabled(true);
                    }else{
                        send.setEnabled(false);
                    }
                }
            });

            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mSendComment = new sendCommentStatusTask(currToken, currUserId,statusId,chat.getText().toString());
                    mSendComment.execute();
                    chat.getText().clear();
                }
            });
        }
    }
    public void init(){
        if(statusId != null){
            cage.setVisibility(View.VISIBLE);
            mGetDataTask = new getDataTask(statusId);
            mGetDataTask.execute((Void)null);
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void showProgress(Boolean show){
        if(show){
            swipeRefreshLayout.setRefreshing(true);
        } else {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    public void showData(ListStatusComment response){
        ArrayList<ListStatusComment.Data> data = new ArrayList<>();
        data.add(0,currStatus);
        int limit = response.getTotal_data();
        if(limit > 0) {
            for (int i = 0; i < limit; i++) {
                data.add(response.getData().get(i));
            }
        }
        LinearLayoutManager linearLayoutManager =  new LinearLayoutManager(StatusCommentActivity.this);
        cage.setLayoutManager(linearLayoutManager);
        cage.setHasFixedSize(true);
        ListStatusCommentAdapter listStatusCommentAdapter = new ListStatusCommentAdapter(data, StatusCommentActivity.this,data.size());
        cage.setAdapter(listStatusCommentAdapter);
    }

    public class getDataTask extends AsyncTask<Void,Void,Boolean> {

        Response<ListStatusComment> response;
        String id_status;

        public getDataTask(String id_status){
            this.id_status = id_status;
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
            Call<ListStatusComment> call = goMudikInterface.getStatusComment(id_status);
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
                        showData(response.body());
                    } else {
                        Toast.makeText(StatusCommentActivity.this, ""+getResources().getString(R.string.error_loading), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(StatusCommentActivity.this,""+response.message(),Toast.LENGTH_SHORT).show();
                    cage.setVisibility(View.GONE);
                }
            } else {
                cage.setVisibility(View.GONE);
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

    public class sendCommentStatusTask extends AsyncTask<Void,Void,Boolean> {
        String token;
        String id_users;
        String id_status;
        String content;
        Response<GetDataApiDefault> response;

        public sendCommentStatusTask(String token, String id_users, String id_status, String content){
            this.token = token;
            this.id_users = id_users;
            this.id_status = id_status;
            this.content = content;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog.show(getSupportFragmentManager(),"Loading send comment");
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            GoMudikInterface goMudikInterface = GoMudikAPI.getAPI().create(GoMudikInterface.class);
            Call<GetDataApiDefault> call = goMudikInterface.addUserComment(token, id_users, id_status, content);
            try {
                response = call.execute();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "doInBackground: "+e.getMessage());
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            mSendComment = null;
            loadingDialog.dismiss();
            if(success){
                if (response.isSuccessful()) {
                    if(response.body() != null) {
                        Toast.makeText(StatusCommentActivity.this, "" + response.body().getMessage(), Toast.LENGTH_LONG).show();
                        if (response.body().getIs_success()) {
                            new GoMudikFirebase().updateNotif(statusUserId);
                            init();
                        }
                    } else {
                        Toast.makeText(StatusCommentActivity.this, ""+getResources().getString(R.string.error_loading), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(StatusCommentActivity.this, "Failed: "+response.errorBody(), Toast.LENGTH_LONG).show();
                    Log.d(TAG, "onPostExecute: "+response.errorBody());
                }
            } else {
                Log.d(TAG, "onPostExecute: "+getResources().getString(R.string.error_loading));
                Toast.makeText(StatusCommentActivity.this, ""+getResources().getString(R.string.error_loading), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            mSendComment = null;
        }
    }
}
