package id.cnn.gomudik.gomudik_user_and_group_package.activity.group_chat;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import id.cnn.gomudik.R;
import id.cnn.gomudik.gomudik_user_and_group_package.fragment.GroupNotificationFragment;
import id.cnn.gomudik.gomudik_user_and_group_package.fragment.GroupMemberFragment;
import id.cnn.gomudik.util.SharedPreferenceCheckerActivity;

public class GomudikGroupActivity extends SharedPreferenceCheckerActivity {
    private ViewPager mViewPager;
    private static final String TAG = "GomudikGroupActivity";
    private String id_group, group_image, group_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gomudik_group);
        Log.d(TAG, "onCreate: start");
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        // Create the adapter that will return a fragment for each of the three
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        if(getIntent().getStringExtra("id_group")!= null){
            id_group = getIntent().getStringExtra("id_group");
            group_image = getIntent().getStringExtra("group_image");
            group_name = getIntent().getStringExtra("group_name");
            CircleImageView profilImage = findViewById(R.id.profile_image);
            TextView groupName = findViewById(R.id.username);
            groupName.setText(group_name);
            if(group_image != null){
                Uri uri = Uri.parse("http://gomudik.id:81".concat(group_image.substring(1)));
                Picasso.get().load(uri).fit().centerInside().placeholder(R.drawable.no_photo).error(R.drawable.no_photo).into(profilImage);
            } else {
                Picasso.get().load(R.drawable.no_photo).fit().centerInside().placeholder(R.drawable.no_photo).error(R.drawable.no_photo).into(profilImage);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_group_default, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_edit:
                Intent intent = new Intent(GomudikGroupActivity.this, EditGroupActivity.class);
                intent.putExtra("id_group",id_group);
                intent.putExtra("group_image",group_image);
                intent.putExtra("group_name",group_name);
                startActivity(intent);
                break;
            case R.id.action_leave:
                Toast.makeText(GomudikGroupActivity.this,"Leave group",Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public String getUserToken(){
        return getCurrToken();
    }

    public String getGroupId(){ return id_group; }
    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position){
                case 0:
                    fragment = new GroupMemberFragment();
                    break;
                case 1:
                    fragment = new GroupNotificationFragment();
                    break;
            }
            return  fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: start");
    }
}