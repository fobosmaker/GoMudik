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

import id.cnn.gomudik.R;
import id.cnn.gomudik.api.GoMudikAPI;
import id.cnn.gomudik.api.GoMudikInterface;
import id.cnn.gomudik.gomudik_main_package.adapter.ListUsersStatusAdapter;
import id.cnn.gomudik.gomudik_user_and_group_package.activity.user.ProfileUserActivity;
import id.cnn.gomudik.gomudik_user_and_group_package.model.ListStatus;
import retrofit2.Call;
import retrofit2.Response;

public class UsersStatusFragment extends Fragment {
    private RecyclerView cage;
    private getDataTask mGetDataTask = null;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RelativeLayout errorResponse,noDataResponse;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_users_status,container,false);
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
        mGetDataTask = new getDataTask(((ProfileUserActivity)getActivity()).getUserId(),((ProfileUserActivity)getActivity()).getUserToken());
        mGetDataTask.execute((Void)null);
    }
    public void showData(Response<ListStatus> response){
        LinearLayoutManager linearLayoutManager =  new LinearLayoutManager(getActivity());
        cage.setLayoutManager(linearLayoutManager);
        cage.setHasFixedSize(true);
        ListUsersStatusAdapter listStatusAdapter = new ListUsersStatusAdapter(response, getActivity(),Integer.valueOf(response.body().getTotal_data()));
        cage.setAdapter(listStatusAdapter);
    }

    public void showProgress(Boolean show){
        if(show){
            swipeRefreshLayout.setRefreshing(true);
        } else {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    public class getDataTask extends AsyncTask<Void,Void,Boolean> {

        private String id_users;
        private String token;
        Response<ListStatus> response;

        public getDataTask(String id_users, String token){
            this.id_users = id_users;
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
            Call<ListStatus> call = goMudikInterface.getListUserStatusById(token, id_users);
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
                    init();
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