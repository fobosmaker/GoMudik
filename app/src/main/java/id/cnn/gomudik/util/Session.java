package id.cnn.gomudik.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class Session {
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    public static final String KEY_ID = "id";
    public static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_NAME = "name";
    public static final String KEY_TOKEN = "token";
    public static final String KEY_IMAGE_LINK = "image_link";
    private static final String is_login = "loginstatus";

    private Context _context;

    private final String SHARE_NAME = "loginsession";
    private final int MODE_PRIVATE = 0;

    public Session(Context context){
        this._context = context;
        sp = context.getSharedPreferences(SHARE_NAME, MODE_PRIVATE);
        editor = sp.edit();
    }

    public void storeLogin(String id, String username, String password, String email,String name, String token, String image_link){
        editor.putString(KEY_ID, id);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_PASSWORD, password);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_TOKEN, token);
        editor.putString(KEY_IMAGE_LINK,image_link);
        editor.putBoolean(is_login, true);
        editor.commit();
    }

    public HashMap getData() {
        HashMap<String, String> map = new HashMap<>();
        map.put(KEY_ID, sp.getString(KEY_ID,null));
        map.put(KEY_USERNAME, sp.getString(KEY_USERNAME,null));
        map.put(KEY_PASSWORD, sp.getString(KEY_PASSWORD,null));
        map.put(KEY_EMAIL, sp.getString(KEY_EMAIL,null));
        map.put(KEY_NAME, sp.getString(KEY_NAME,null));
        map.put(KEY_TOKEN, sp.getString(KEY_TOKEN,null));
        map.put(KEY_IMAGE_LINK,sp.getString(KEY_IMAGE_LINK,null));
        return map;
    }

    public void updateProfile(String name, String username){
        editor.putString(KEY_NAME,name);
        editor.putString(KEY_USERNAME,username);
        editor.commit();
    }

    public void updateProfilePicture(String image_link){
        editor.putString(KEY_IMAGE_LINK,image_link);
        editor.commit();
    }

    public Boolean login() {
        return sp.getBoolean(is_login,false);
    }

    public void logout() {
        editor.clear();
        editor.commit();
    }
}