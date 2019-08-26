package id.cnn.gomudik.gomudik_user_and_group_package.viewholder;

import android.net.Uri;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;

import de.hdodenhof.circleimageview.CircleImageView;
import id.cnn.gomudik.R;
import id.cnn.gomudik.gomudik_user_and_group_package.activity.user.ListContactActivity;

public class GroupContentViewHolder extends ChildViewHolder {
    private TextView username, email;
    private CircleImageView imageView;
    private RelativeLayout cage_button;
    private ImageButton buttonConfirm, buttonCancel;
    private ListContactActivity listContactActivity;
    public GroupContentViewHolder(View itemView, final ListContactActivity listContactActivity) {
        super(itemView);
        username = itemView.findViewById(R.id.username);
        imageView = itemView.findViewById(R.id.user_photo);
        email = itemView.findViewById(R.id.email);
        cage_button = itemView.findViewById(R.id.cage_button);
        buttonConfirm = itemView.findViewById(R.id.button_confirm);
        buttonCancel = itemView.findViewById(R.id.button_cancel);
        this.listContactActivity = listContactActivity;
    }
    public void setName(String string){
        username.setText(string);
    }
    public void setImageView(String url){
        if(url != null){
            Uri uri = Uri.parse("http://gomudik.id:81".concat(url.substring(1)));
            Picasso.get().load(uri).fit().centerInside().placeholder(R.drawable.no_photo).error(R.drawable.no_photo).into(imageView);
        } else {
            Picasso.get().load(R.drawable.no_photo).fit().centerInside().into(imageView);
        }
    }
    public void setEmail(String string){
        email.setText(string);
    }
    public void hideButton(){
        cage_button.setVisibility(View.GONE);
    }
    public void showButton(){ cage_button.setVisibility(View.VISIBLE); }
    public void setButtonOnClick(final Integer type_data, final String id, final  String code){
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listContactActivity.responseButton(type_data, id,true, code);
            }
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listContactActivity.responseButton(type_data, id,false, code);
            }
        });
    }
}