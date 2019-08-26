package id.cnn.gomudik.gomudik_main_package.activity;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import id.cnn.gomudik.R;
import id.cnn.gomudik.api.GoMudikAPI;
import id.cnn.gomudik.api.GoMudikInterface;
import id.cnn.gomudik.gomudik_ads.model.GetAds;
import id.cnn.gomudik.gomudik_main_package.model.CCTVActivityModel;
import id.cnn.gomudik.gomudik_main_package.model.GetCCTV;
import id.cnn.gomudik.util.BaseGoMudikActivity;
import id.cnn.gomudik.gomudik_ads.DialogAds;
import retrofit2.Call;
import retrofit2.Response;

public class CCTVActivity extends BaseGoMudikActivity{
    ImageView imageCCTV;
    EditText cctvContent;
    String[] array;
    String[] link;
    String[] source;
    Handler mHandler;
    private getDataTask mGetDataTask;
    private RelativeLayout topAds, bottomAds;
    private ImageView topAdsImage, bottomAdsImage;
    private TextView sourceCCTV;
    private DownloadImageTask mDownload;
    private boolean isDownloading = false;
    private Button btnSelectPlace;
    private static final String TAG = "CCTVActivity";
    private SwipeRefreshLayout swipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cctv);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("CCTV");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        imageCCTV = findViewById(R.id.image_cctv);
        cctvContent = findViewById(R.id.cctv_content);
        mHandler = new Handler();
        sourceCCTV = findViewById(R.id.cctv_source);
        btnSelectPlace = findViewById(R.id.button_select);
        btnSelectPlace.setEnabled(false);
        btnSelectPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogSelectCCTV();
            }
        });
        topAds = findViewById(R.id.topAds);
        bottomAds = findViewById(R.id.bottomAds);
        topAdsImage = findViewById(R.id.top_ads_image);
        bottomAdsImage = findViewById(R.id.bottom_ads_image);
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                init();
            }
        });
        init();
    }

    public void init(){
        mGetDataTask = new getDataTask();
        mGetDataTask.execute((Void)null);
    }

    public void executeDownloadImage(String x){
        mDownload = new DownloadImageTask(imageCCTV);
        mDownload.execute(x);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void showDialogSelectCCTV(){
        AlertDialog.Builder builder = new AlertDialog.Builder(CCTVActivity.this);
        builder.setTitle("Select place")
                .setItems(array, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        cctvContent.setText(array[which]);
                        String content = "Source: "+source[which];
                        sourceCCTV.setText(content);
                        imageCCTV.setImageResource(R.drawable.ex_thumbnail);
                        executeDownloadImage(link[which]);
                        mHandler.removeCallbacksAndMessages(null);
                        if(isDownloading){
                            mDownload.cancel(true);
                        }
                        mHandler.post(createRunnable(link[which]));
                    }
                });
        builder.show();
    }

    public void generateAds(GetAds ads){
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
                new DialogAds(CCTVActivity.this,ads.getData().get(2).getImage_link());
            }
        }
    }

    private void generateCCTV(GetCCTV data){
        int limit = data.getTotal_data();
        array = new String[limit];
        link = new String[limit];
        source = new String[limit];
        //insert into array place
        for (int i = 0; i < limit; i++) {
            array[i] = data.getData().get(i).getContent();
            link[i] = data.getData().get(i).getImage_link();
            source[i] = data.getData().get(i).getSource();
        }
    }

    public void showProgress(Boolean show){
        if(show){
            swipeRefreshLayout.setRefreshing(true);
        } else {
            swipeRefreshLayout.setRefreshing(false);
        }
    }


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            Log.d(TAG, "doInBackground: start");
            isDownloading = true;
            try {
                Log.d(TAG, "doInBackground: process "+urldisplay);
                URL url = new URL(urldisplay);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "doInBackground: failed, "+e.getMessage());
                return null;
            }
        }

        protected void onPostExecute(Bitmap result) {
            isDownloading = false;
            mDownload = null;
            if (result != null) {
                Log.d(TAG, "onPostExecute: set to bmImage");
                bmImage.setImageBitmap(result);
            } else {
                Log.d(TAG, "onPostExecute: NULL value");
                imageCCTV.setImageResource(R.drawable.ex_thumbnail);
                Toast.makeText(CCTVActivity.this, "Failed to get resource!", Toast.LENGTH_SHORT).show();
            }
        }
        @Override
        protected void onCancelled() {
            super.onCancelled();
            isDownloading = false;
            mDownload = null;
            Log.d(TAG, "onCancelled: cancel task");
        }
    }

    public class getDataTask extends AsyncTask<Void,Void,Boolean> {

        Response<CCTVActivityModel> response;

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
            Call<CCTVActivityModel> call = goMudikInterface.getDataCCTVActivity(CCTVActivity.class.getSimpleName());
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
                showProgress(false);
                if(response.isSuccessful()) {
                    if (response.body() != null) {
                        generateAds(response.body().getAds());
                        if (response.body().getCCTV().getTotal_data() > 0) {
                            btnSelectPlace.setEnabled(true);
                            generateCCTV(response.body().getCCTV());
                        } else {
                            btnSelectPlace.setEnabled(false);
                            Toast.makeText(CCTVActivity.this, "There is no CCTV available ", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        btnSelectPlace.setEnabled(false);
                        Toast.makeText(CCTVActivity.this, "No CCTV available", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    btnSelectPlace.setEnabled(false);
                    Toast.makeText(CCTVActivity.this, "Response from server is failure", Toast.LENGTH_SHORT).show();

                }
            } else {
                showProgress(false);
                btnSelectPlace.setEnabled(false);
                Toast.makeText(CCTVActivity.this, "Failed to get data, try again", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            mGetDataTask = null;
            showProgress(false);
        }
    }

    private Runnable createRunnable(final String paramStr){
        Runnable aRunnable = new Runnable(){
            public void run(){
                if(!isDownloading && mDownload == null){
                    executeDownloadImage(paramStr);
                }
                mHandler.postDelayed(createRunnable(paramStr),3000);
            }
        };
        return aRunnable;
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
