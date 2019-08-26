package id.cnn.gomudik.gomudik_main_package.activity;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

import id.cnn.gomudik.R;
import id.cnn.gomudik.api.GoMudikAPI;
import id.cnn.gomudik.api.GoMudikInterface;
import id.cnn.gomudik.gomudik_main_package.adapter.GoMudikNewsAdapter;
import id.cnn.gomudik.gomudik_ads.model.GetAds;
import id.cnn.gomudik.gomudik_main_package.model.GetNews;
import id.cnn.gomudik.gomudik_ads.DialogAds;
import id.cnn.gomudik.gomudik_main_package.model.NewsActivityModel;
import retrofit2.Call;
import retrofit2.Response;

public class GoMudikNewsActivity extends AppCompatActivity {
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView cage;
    private getDataTask mGetDataTask = null;
    private RelativeLayout templateError;
    private RelativeLayout topAds, bottomAds;
    private ImageView topAdsImage, bottomAdsImage;
    private GoMudikNewsAdapter adapter;
    private ArrayList<GetNews.Data> data = new ArrayList<>();
    private LinearLayoutManager manager;
    private boolean isScroll = false;
    private static final String TAG = "GoMudikNewsActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gomudik_news);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("News");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        templateError = findViewById(R.id.template_error);
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!data.isEmpty()){
                    data.clear();
                    adapter.notifyDataSetChanged();
                }
                cageAddListener();
                init();
            }
        });

        topAds = findViewById(R.id.topAds);
        bottomAds = findViewById(R.id.bottomAds);
        topAdsImage = findViewById(R.id.top_ads_image);
        bottomAdsImage = findViewById(R.id.bottom_ads_image);
        Log.d(TAG, "onCreate: "+this.getClass().getSimpleName());

        cage = findViewById(R.id.cage);
        manager = new LinearLayoutManager(this);
        cage.setLayoutManager(manager);
        cage.setHasFixedSize(true);
        adapter = new GoMudikNewsAdapter(data,this);
        cage.setAdapter(adapter);
        cageAddListener();
        init();
    }

    public void init(){
        mGetDataTask = new getDataTask(data.size());
        mGetDataTask.execute((Void)null);
        cage.setVisibility(View.VISIBLE);
        templateError.setVisibility(View.GONE);
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
                if(isScroll && (manager.getChildCount() + manager.findFirstVisibleItemPosition() == manager.getItemCount())){
                    isScroll = false;
                    init();
                }
            }
        });
    }

    public void generatePlaylist(GetNews news){
        if(news.getTotal_data() > 0) {
            int limit = news.getTotal_data();
            int index = data.size();
            for (int i = 0; i < limit; i++) {
                data.add(news.getData().get(i));
            }
            adapter.notifyItemRangeInserted(index, data.size() - 1);
            if (data.size() >= news.getAll_data()) {
                cage.clearOnScrollListeners();
            }
            Log.d(TAG, "generatePlaylist: " + data.size());
        } else {
            cage.setVisibility(View.GONE);
            templateError.setVisibility(View.VISIBLE);
        }
    }

    private void generateAds(GetAds ads){
        if(ads.getTotal_data() == 0){
            topAds.setVisibility(View.GONE);
            bottomAds.setVisibility(View.GONE);
        } else {
            if(ads.getData().get(0).getId_active().equalsIgnoreCase("1")){
                topAds.setVisibility(View.VISIBLE);
                Picasso.get().load(Uri.parse("http:gomudik.id:81/".concat(ads.getData().get(0).getImage_link()))).into(topAdsImage);
            } else {
                topAds.setVisibility(View.GONE);
            }

            if(ads.getData().get(1).getId_active().equalsIgnoreCase("1")){
                bottomAds.setVisibility(View.VISIBLE);
                Picasso.get().load(Uri.parse("http:gomudik.id:81/".concat(ads.getData().get(1).getImage_link()))).into(bottomAdsImage);
            } else {
                bottomAds.setVisibility(View.GONE);
            }

            if(ads.getData().get(2).getId_active().equalsIgnoreCase("1")){
                new DialogAds(GoMudikNewsActivity.this,ads.getData().get(2).getImage_link());
            }
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

        Response<NewsActivityModel> response;
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
            try {
                // Simulate network access.
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                return false;
            }

            GoMudikInterface goMudikInterface = GoMudikAPI.getAPI().create(GoMudikInterface.class);
            Call<NewsActivityModel> call = goMudikInterface.getDataNewsActivity(GoMudikNewsActivity.class.getSimpleName(), index_at);
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
                if(response.isSuccessful()) {
                    if(response.body() != null) {
                        generateAds(response.body().getAds());
                        generatePlaylist(response.body().getNews());
                    } else {
                        Toast.makeText(GoMudikNewsActivity.this,"No data found",Toast.LENGTH_SHORT).show();
                        cage.setVisibility(View.GONE);
                        templateError.setVisibility(View.VISIBLE);
                    }
                } else {
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}