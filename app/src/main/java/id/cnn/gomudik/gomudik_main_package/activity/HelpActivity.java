package id.cnn.gomudik.gomudik_main_package.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import id.cnn.gomudik.R;
import id.cnn.gomudik.util.BaseGoMudikActivity;
import id.cnn.gomudik.util.LoadingProgress;

public class HelpActivity extends BaseGoMudikActivity{
    PDFView pdfView;
    private static final String TAG = "HelpActivity";
    private android.support.v4.app.DialogFragment loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Help");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        loadingDialog = new LoadingProgress();
        String url = "http://gomudik.id:81/user_guide/gomudik_user_guide.pdf";
        pdfView = findViewById(R.id.pdf_viewer);
        new getPDFStream().execute(url);
    }

    class getPDFStream extends AsyncTask<String, Void, InputStream>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog.show(getSupportFragmentManager(),"HelpActivity");
        }

        @Override
        protected InputStream doInBackground(String... strings) {
            InputStream inputStream = null;
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                if(connection.getResponseCode() == 200){
                    inputStream = new BufferedInputStream(connection.getInputStream());
                }
            } catch (IOException e){
                Log.d(TAG, "doInBackground: error, "+e.toString());
            }
            return inputStream;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            super.onPostExecute(inputStream);
            pdfView.fromStream(inputStream).load();
            loadingDialog.dismiss();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
