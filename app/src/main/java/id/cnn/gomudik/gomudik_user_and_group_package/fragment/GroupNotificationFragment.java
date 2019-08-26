package id.cnn.gomudik.gomudik_user_and_group_package.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import id.cnn.gomudik.gomudik_user_and_group_package.activity.group_chat.GomudikGroupActivity;
import id.cnn.gomudik.R;
import id.cnn.gomudik.api.GoMudikAPI;
import id.cnn.gomudik.api.GoMudikInterface;
import id.cnn.gomudik.gomudik_user_and_group_package.adapter.ListGroupNotificationAdapter;
import id.cnn.gomudik.gomudik_user_and_group_package.model.ListNotification;
import retrofit2.Call;
import retrofit2.Response;

public class GroupNotificationFragment extends Fragment {
    //private static final String TAG = "GroupNotificationFragment";
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView cage;
    private getDataTask mGetDataTask = null;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RelativeLayout errorResponse,noDataResponse;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_group_notification,container,false);
        linearLayoutManager = new LinearLayoutManager(getContext());
        cage = v.findViewById(R.id.cage);
        swipeRefreshLayout = v.findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                init();
            }
        });
        errorResponse = v.findViewById(R.id.template_error);
        noDataResponse = v.findViewById(R.id.template_no_data);
        init();
        return v;
    }
    public void init(){
        cage.setVisibility(View.VISIBLE);
        errorResponse.setVisibility(View.GONE);
        noDataResponse.setVisibility(View.GONE);
        mGetDataTask = new getDataTask(((GomudikGroupActivity)getActivity()).getGroupId(),((GomudikGroupActivity)getActivity()).getUserToken());
        mGetDataTask.execute((Void)null);
    }
    public void showData(Response<ListNotification> response){
        cage.setLayoutManager(linearLayoutManager);
        cage.setHasFixedSize(true);
        ListGroupNotificationAdapter adapter = new ListGroupNotificationAdapter((ArrayList<ListNotification.Data>)response.body().getData());
        cage.setAdapter(adapter);
    }

    public void showProgress(Boolean show){
        if(show){
            swipeRefreshLayout.setRefreshing(true);
        } else {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    public class getDataTask extends AsyncTask<Void,Void,Boolean> {

        private String id_group;
        private String token;
        Response<ListNotification> response;

        public getDataTask(String id_group, String token){
            this.id_group = id_group;
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
            Call<ListNotification> call = goMudikInterface.getGroupNotification(token,id_group);
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
                    if(response.body().getTotal_data() == 0){
                        cage.setVisibility(View.GONE);
                        errorResponse.setVisibility(View.GONE);
                        noDataResponse.setVisibility(View.VISIBLE);
                    } else{
                        cage.setVisibility(View.VISIBLE);
                        showData(response);
                    }
                } else {
                    Toast.makeText(getContext(),""+response.message(),Toast.LENGTH_SHORT).show();
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