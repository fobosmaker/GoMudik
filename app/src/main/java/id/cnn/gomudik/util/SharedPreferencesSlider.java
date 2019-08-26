package id.cnn.gomudik.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesSlider {
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private static final String is_load = "sliderstatus";
    private Context _context;
    private final String SHARE_NAME = "Slider";
    private final int MODE_PRIVATE = 0;
    private static final String TAG = "SharedPreferencesSlider";
    public SharedPreferencesSlider(Context context){
        this._context = context;
        sp = context.getSharedPreferences(SHARE_NAME, MODE_PRIVATE);
        editor = sp.edit();
    }

    public void storeSlider(){
        editor.putBoolean(is_load, true);
        editor.commit();
    }

    public Boolean isLoadSlider() {
        return sp.getBoolean(is_load,false);
    }

    public void resetSlider() {
        editor.clear();
        editor.commit();
    }
}