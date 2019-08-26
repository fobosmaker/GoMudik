package id.cnn.gomudik.util;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.HashMap;

import id.cnn.gomudik.gomudik_user_and_group_package.activity.user.LoginActivity;

public class SharedPreferenceCheckerActivity extends BaseGoMudikActivity {
    private static final String TAG = "SharedPreferenceChecker";
    private Session session;
    private HashMap<String, String> map;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new Session(SharedPreferenceCheckerActivity.this);
        if(!session.login()){
            Intent login = new Intent(SharedPreferenceCheckerActivity.this, LoginActivity.class);
            startActivity(login);
            finish();
        }
        Log.d(TAG, "onCreate: start "+session.login());
        map = session.getData();
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart: start "+session.login());
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: start "+session.login());
    }

    public void userLogOut(){
        Log.d(TAG, "userLogOut: true");
        session.logout();
    }

    public void userUpdateProfile(String name, String username){
        session.updateProfile(name, username);
    }

    public void userUpdateProfilePicture(String image_link){
        session.updateProfilePicture(image_link);
    }
    protected HashMap<String, String> getBundle(){
        return map;
    }

    protected String getCurrId(){
        return map.get(session.KEY_ID);
    }

    protected String getCurrToken(){ return map.get(session.KEY_TOKEN); }

    protected String getCurrEmail(){
        return map.get(session.KEY_EMAIL);
    }

    protected String getCurrName(){ return map.get(session.KEY_NAME); }

    protected String getCurrUsername(){ return map.get(session.KEY_USERNAME);}

    protected String getCurrImageLink(){ return map.get(session.KEY_IMAGE_LINK);}
}