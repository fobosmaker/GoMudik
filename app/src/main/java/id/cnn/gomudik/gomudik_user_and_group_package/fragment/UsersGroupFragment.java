package id.cnn.gomudik.gomudik_user_and_group_package.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
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
import id.cnn.gomudik.gomudik_user_and_group_package.activity.user.ProfileUserActivity;
import id.cnn.gomudik.gomudik_user_and_group_package.adapter.GroupMemberAdapter;
import id.cnn.gomudik.gomudik_user_and_group_package.model.ListUsers;
import retrofit2.Call;
import retrofit2.Response;

public class UsersGroupFragment extends Fragment {
    private RecyclerView cage;
    private getDataTask mGetDataTask = null;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RelativeLayout templateError, templateNoData;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_users_contact, container, false);
        cage = v.findViewById(R.id.cage);
        swipeRefreshLayout = v.findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                init();
            }
        });
        templateError = v.findViewById(R.id.template_error);
        templateNoData = v.findViewById(R.id.template_no_data);
        init();
        return v;
    }

    public void init() {
        templateError.setVisibility(View.GONE);
        templateNoData.setVisibility(View.GONE);
        cage.setVisibility(View.VISIBLE);
        mGetDataTask = new getDataTask(((ProfileUserActivity) getActivity()).getUserToken(), ((ProfileUserActivity) getActivity()).getUserId());
        mGetDataTask.execute((Void) null);
    }

    public void showData(Response<ListUsers> response) {
        RecyclerView.LayoutManager grid = new GridLayoutManager(getContext(), 3);
        cage.setLayoutManager(grid);
        cage.setHasFixedSize(true);
        GroupMemberAdapter adapter = new GroupMemberAdapter(response.body().getData(), getContext());
        cage.setAdapter(adapter);
    }

    public void showProgress(Boolean show) {
        if (show) {
            swipeRefreshLayout.setRefreshing(true);
        } else {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    public class getDataTask extends AsyncTask<Void, Void, Boolean> {

        private String token;
        private String id_users;
        Response<ListUsers> response;

        public getDataTask(String token, String id_users) {
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
            Call<ListUsers> call = goMudikInterface.getListUserGroupById(token, id_users);
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
            if (success) {
                if (response.isSuccessful()) {
                    if (response.body().getTotal_data() == 0) {
                        cage.setVisibility(View.GONE);
                        templateError.setVisibility(View.GONE);
                        templateNoData.setVisibility(View.VISIBLE);
                    } else {
                        cage.setVisibility(View.VISIBLE);
                        templateError.setVisibility(View.GONE);
                        templateNoData.setVisibility(View.GONE);
                        showData(response);
                    }
                } else {
                    Toast.makeText(getContext(), "" + response.message(), Toast.LENGTH_SHORT).show();
                    init();
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
}