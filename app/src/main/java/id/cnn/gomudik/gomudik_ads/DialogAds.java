package id.cnn.gomudik.gomudik_ads;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;

import id.cnn.gomudik.R;

public class DialogAds {
    private Context mContext;
    private Dialog dialog;
    private String image_link;
    public DialogAds(Context mContext, String image_link){
        this.mContext = mContext;
        this.image_link = image_link;
        showPopUpDialog();
    }

    private void showPopUpDialog(){
        dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.ads_popup);
        ImageView img = dialog.findViewById(R.id.popup_image);
        Picasso.get().load(Uri.parse("http:gomudik.id:81/".concat(image_link))).into(img);
        RelativeLayout closeButton = dialog.findViewById(R.id.cage_button_ads);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        if(!((Activity)mContext).isFinishing()) {
            //show dialog
            dialog.show();
        }
    }
}
