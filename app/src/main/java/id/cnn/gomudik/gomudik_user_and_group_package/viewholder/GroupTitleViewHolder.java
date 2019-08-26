package id.cnn.gomudik.gomudik_user_and_group_package.viewholder;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import id.cnn.gomudik.R;

public class GroupTitleViewHolder extends GroupViewHolder {

    private TextView groupTitle;
    private ImageView icon;
    private FrameLayout frame;
    private RelativeLayout badge;
    private TextView countBadge;
    public GroupTitleViewHolder(View itemView) {
        super(itemView);
        groupTitle = itemView.findViewById(R.id.listGroupTitle);
        icon = itemView.findViewById(R.id.icon);
        frame = itemView.findViewById(R.id.frame);
        badge = itemView.findViewById(R.id.badge);
        countBadge = itemView.findViewById(R.id.count_badge);
    }

    public void setGroupTitle(String string){
        groupTitle.setText(string);
    }

    public void hideIcon(){
        icon.setVisibility(View.GONE);
    }

    public void hideBadge(){ badge.setVisibility(View.GONE); }

    public void setCountBadge(String string){
        countBadge.setText(string);
    }

    @Override
    public void expand() {
        super.expand();
        icon.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
    }

    @Override
    public void collapse() {
        super.collapse();
        icon.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
    }
}
