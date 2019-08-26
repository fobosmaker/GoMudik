package id.cnn.gomudik;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import id.cnn.gomudik.gomudik_main_package.activity.MainActivity;
import id.cnn.gomudik.gomudik_main_package.adapter.SliderAdapter;
import id.cnn.gomudik.gomudik_main_package.model.ListSlider;
import id.cnn.gomudik.util.SharedPreferencesSlider;

public class SliderActivity extends AppCompatActivity {
    private static final String TAG = "SliderActivity";
    private LinearLayout dotsLayout;
    private ImageView[] dots;
    private Button buttonNext, buttonBack;
    private int position = 0;
    private int limit = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider);
        final RecyclerView cage = findViewById(R.id.cage);
        dotsLayout = findViewById(R.id.dotsLayout);
        buttonNext = findViewById(R.id.btn_next);
        buttonBack = findViewById(R.id.btn_back);
        List<ListSlider> data = new ArrayList<>();
        data.add(new ListSlider(R.drawable.slider_maps, R.color.orangePastel));
        data.add(new ListSlider(R.drawable.slider_cctv, R.color.bluePastel));
        data.add(new ListSlider(R.drawable.slider_jadwal_salat, R.color.redPastel));
        data.add(new ListSlider(R.drawable.slider_channel, R.color.greenPastel));
        limit = data.size();
        final LinearLayoutManager manager = new LinearLayoutManager(SliderActivity.this, LinearLayoutManager.HORIZONTAL, false);
        cage.setLayoutManager(manager);
        cage.setHasFixedSize(true);
        SnapHelper snapHelper = new PagerSnapHelper();
        cage.setOnFlingListener(null);
        snapHelper.attachToRecyclerView(cage);
        final SliderAdapter adapter = new SliderAdapter(data, SliderActivity.this);
        cage.setAdapter(adapter);
        createDot(position);
        cage.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                position = manager.findLastCompletelyVisibleItemPosition();
                Log.d(TAG, "onScrollStateChanged: position: "+
                        manager.findLastCompletelyVisibleItemPosition());
                cage.setBackgroundColor(getResources().getColor(R.color.bluePastel));
                createDot( position );
                buttonConfig( position );
            }
        });
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(position == (limit-1)){
                    SharedPreferencesSlider slider = new SharedPreferencesSlider(SliderActivity.this);
                    slider.storeSlider();
                    startActivity(new Intent(SliderActivity.this, MainActivity.class));
                    finish();
                } else {
                    cage.smoothScrollToPosition(position+1);
                }
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cage.smoothScrollToPosition(position-1);
            }
        });
    }

    private void createDot(int position){
        if(dotsLayout != null){
            dotsLayout.removeAllViews();
        }
        dots = new ImageView[limit];
        for (int i = 0; i < limit; i++) {
            dots[i] = new ImageView(this);
            if (i == position) {
                dots[i].setImageDrawable(ContextCompat.getDrawable(SliderActivity.this, R.drawable.dots_active));
            } else {
                dots[i].setImageDrawable(ContextCompat.getDrawable(SliderActivity.this, R.drawable.dots_default));
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(4, 0, 4, 0);
            dotsLayout.addView(dots[i], params);
        }
    }

    private void buttonConfig(int position){
        if(position == 0){
            buttonBack.setVisibility(View.INVISIBLE);
        } else {
            buttonBack.setVisibility(View.VISIBLE);
        }

        if(position == (limit-1)){
            buttonNext.setText("START");
        } else {
            buttonNext.setText("NEXT");
        }
    }
}
