package id.cnn.gomudik.gomudik_main_package.activity;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import id.cnn.gomudik.R;

public class GoMudikNewsVideoActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {
    private String videoId;
    private YouTubePlayerView playerView;
    private YouTubePlayer player;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gomudik_news_video);
        playerView = findViewById(R.id.player);
        Bundle b = getIntent().getExtras();
        if(b != null){
            videoId = b.getString("videoId");
            playerView.initialize(getResources().getString(R.string.DEV_KEY),this);
        } else {
            Toast.makeText(this, "No video id found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        if (player != null){
            player.release();
        }
        super.onDestroy();
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        player = youTubePlayer;
        if (!b) {
            player.cueVideo(videoId);
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        Toast.makeText(this, ""+youTubeInitializationResult.toString(), Toast.LENGTH_SHORT).show();
    }
}
