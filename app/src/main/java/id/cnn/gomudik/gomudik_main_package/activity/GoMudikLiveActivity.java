package id.cnn.gomudik.gomudik_main_package.activity;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.ext.rtmp.RtmpDataSourceFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import id.cnn.gomudik.R;
import id.cnn.gomudik.api.GoMudikAPI;
import id.cnn.gomudik.api.GoMudikInterface;
import id.cnn.gomudik.gomudik_ads.DialogAds;
import id.cnn.gomudik.gomudik_ads.model.GetAds;
import id.cnn.gomudik.util.BaseGoMudikActivity;
import retrofit2.Call;
import retrofit2.Response;

public class GoMudikLiveActivity extends BaseGoMudikActivity {
    private ProgressBar progressBar;
    private static final String TAG = "GoMudikLiveFragment";
    PlayerView exoPlayerView;
    SimpleExoPlayer exoPlayer;
    private getDataTask mGetDataTask;
    private RelativeLayout topAds, bottomAds;
    private ImageView topAdsImage, bottomAdsImage;
    boolean playWhenReady;
    int currentWindow;
    long playbackPosition;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gomudik_live);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Live Streaming");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        progressBar = findViewById(R.id.progressBar);
        exoPlayerView = findViewById(R.id.exoPlayer);

        topAds = findViewById(R.id.topAds);
        bottomAds = findViewById(R.id.bottomAds);
        topAdsImage = findViewById(R.id.top_ads_image);
        bottomAdsImage = findViewById(R.id.bottom_ads_image);

        mGetDataTask = new getDataTask();
        mGetDataTask.execute((Void)null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    protected void onStop() {
        super.onStop();
        releaseExoPlayer();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void generateExoPlayer(){
        String videoURL = "rtmp://c-stream.cnn.id/live/gomudik.sdp";
        //String videoURL = "rtmp://stream1.livestreamingservices.com:1935/tvmlive/tvmlive";

        //initialize player & data source
        exoPlayer = ExoPlayerFactory.newSimpleInstance(GoMudikLiveActivity.this, new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(new DefaultBandwidthMeter())));
        exoPlayerView.setPlayer(exoPlayer);
        exoPlayer.prepare(new ExtractorMediaSource.Factory(new RtmpDataSourceFactory()).createMediaSource(Uri.parse(videoURL)));
        exoPlayer.setPlayWhenReady(playWhenReady);
        exoPlayer.seekTo(currentWindow,playbackPosition);

        exoPlayer.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {
                Log.d(TAG, "onTimelineChanged: start");
            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                Log.d(TAG, "onTracksChanged: ");
            }

            @Override
            public void onLoadingChanged(boolean isLoading) {
                Log.d(TAG, "onLoadingChanged: ");
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if(playbackState == 3){
                    progressBar.setVisibility(View.GONE);
                }
                Log.d(TAG, "onPlayerStateChanged: state:"+playbackState);
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {
                Log.d(TAG, "onRepeatModeChanged: ");
            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) { Log.d(TAG, "onShuffleModeEnabledChanged: "); }

            @Override
            public void onPlayerError(ExoPlaybackException error) { Log.d(TAG, "onPlayerError: "+error.toString()); }

            @Override
            public void onPositionDiscontinuity(int reason) { Log.d(TAG, "onPositionDiscontinuity: "); }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) { Log.d(TAG, "onPlaybackParametersChanged: "); }

            @Override
            public void onSeekProcessed() {
                Log.d(TAG, "onSeekProcessed: start");
            }
        });
    }

    private void releaseExoPlayer(){
        if (exoPlayer != null) {
            playbackPosition = exoPlayer.getCurrentPosition();
            currentWindow = exoPlayer.getCurrentWindowIndex();
            playWhenReady = exoPlayer.getPlayWhenReady();
            exoPlayer.release();
            exoPlayer = null;
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
                new DialogAds(GoMudikLiveActivity.this,ads.getData().get(2).getImage_link());
            }
        }
    }

    public class getDataTask extends AsyncTask<Void,Void,Boolean> {

        Response<GetAds> responseAds;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                // Simulate network access.
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                return false;
            }
            GoMudikInterface goMudikInterface2 = GoMudikAPI.getAPI().create(GoMudikInterface.class);
            Call<GetAds> callAds = goMudikInterface2.adsOnlyActivity(GoMudikLiveActivity.class.getSimpleName());
            try {
                responseAds = callAds.execute();
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
            progressBar.setVisibility(View.GONE);
            if(success){
                if(responseAds.isSuccessful()){
                    if(responseAds.body() != null) {
                        generateAds(responseAds.body());
                        generateExoPlayer();
                    } else {
                        Toast.makeText(GoMudikLiveActivity.this,""+responseAds.message(),Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onPostExecute: No body found");
                    }
                } else {
                    Toast.makeText(GoMudikLiveActivity.this,""+getResources().getString(R.string.error_loading),Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onPostExecute: failure response to get data ads");
                }
            } else {
                Log.d(TAG, "onPostExecute: failure to get data ads");
                Toast.makeText(GoMudikLiveActivity.this, ""+getResources().getString(R.string.error_loading), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            mGetDataTask = null;
            progressBar.setVisibility(View.GONE);
        }
    }
}
