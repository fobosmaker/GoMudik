package id.cnn.gomudik;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;
import com.felipecsl.gifimageview.library.GifImageView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.apache.commons.io.IOUtils;
import java.io.IOException;
import java.io.InputStream;

import id.cnn.gomudik.gomudik_main_package.activity.MainActivity;
import id.cnn.gomudik.util.SharedPreferencesSlider;

public class SplashActivity extends AppCompatActivity {
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final String TAG = "SplashActivity";
    private GifImageView gifImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        gifImageView = findViewById(R.id.splashimg);
        if(isServiceOk()){
            splashShow();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    SharedPreferencesSlider slider = new SharedPreferencesSlider(SplashActivity.this);
                    //slider.resetSlider();
                    Log.d(TAG, "onCreate: slider: "+slider.isLoadSlider());
                    Intent main;
                    if(slider.isLoadSlider()){
                        main = new Intent(SplashActivity.this, MainActivity.class);
                    } else {
                        main = new Intent(SplashActivity.this, SliderActivity.class);
                    }
                    startActivity(main);
                    finish();
                }
            },3000);
        }
    }

    public boolean isServiceOk(){
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(SplashActivity.this);
        if(available == ConnectionResult.SUCCESS){ return true; }
        else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(SplashActivity.this, available,ERROR_DIALOG_REQUEST);
            dialog.show();
        } else{ Toast.makeText(this, "Can't Make Request", Toast.LENGTH_LONG).show(); }
        return false;
    }

    public void splashShow(){
        try{
            InputStream inputStream = getAssets().open("splashscreen.gif");
            byte[] bytes = IOUtils.toByteArray(inputStream);
            gifImageView.setBytes(bytes);
            gifImageView.startAnimation();
        } catch (IOException e) { e.printStackTrace(); }
    }
}