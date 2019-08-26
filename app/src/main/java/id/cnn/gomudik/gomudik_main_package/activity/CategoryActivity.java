package id.cnn.gomudik.gomudik_main_package.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.IOException;

import id.cnn.gomudik.R;
import id.cnn.gomudik.api.GoMudikAPI;
import id.cnn.gomudik.api.GoMudikInterface;
import id.cnn.gomudik.gomudik_main_package.adapter.CategoryAdapter;
import id.cnn.gomudik.gomudik_main_package.model.GetCategories;
import retrofit2.Call;
import retrofit2.Response;

public class CategoryActivity extends AppCompatActivity {
    private RecyclerView cage;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RelativeLayout templateError;
    private getDataTask mGetDataTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Categories");
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
        templateError = findViewById(R.id.template_error);
        init();
    }

    public void init(){
        templateError.setVisibility(View.GONE);
        cage.setVisibility(View.VISIBLE);
        mGetDataTask = new getDataTask();
        mGetDataTask.execute((Void)null);
    }

    public void showData(GetCategories response){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        cage.setLayoutManager(linearLayoutManager);
        cage.setHasFixedSize(true);
        CategoryAdapter adapter = new CategoryAdapter(response.getData(),CategoryActivity.this);
        cage.setAdapter(adapter);
    }

    //public void selectedData(String id, String content){
    public void selectedData(String id, String content, String type, String keyword){
        Intent resultIntent = new Intent();
        resultIntent.putExtra("id", id);
        resultIntent.putExtra("content", content);
        resultIntent.putExtra("type", type);
        resultIntent.putExtra("keyword", keyword);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    public void showProgress(Boolean show){
        if(show){
            swipeRefreshLayout.setRefreshing(true);
        } else {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public class getDataTask extends AsyncTask<Void,Void,Boolean> {

        Response<GetCategories> response;
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
            Call<GetCategories> call = goMudikInterface.getCategories();
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
                        Toast.makeText(CategoryActivity.this, ""+getResources().getString(R.string.error_loading), Toast.LENGTH_SHORT).show();
                        cage.setVisibility(View.GONE);
                        templateError.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(CategoryActivity.this,""+response.message(),Toast.LENGTH_LONG).show();
                    cage.setVisibility(View.GONE);
                    templateError.setVisibility(View.VISIBLE);
                }
            } else {
                cage.setVisibility(View.GONE);
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