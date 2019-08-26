package id.cnn.gomudik.gomudik_user_and_group_package.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import id.cnn.gomudik.R;
import id.cnn.gomudik.gomudik_user_and_group_package.model.ListUsers;

public class GroupMemberAdapter extends RecyclerView.Adapter<GroupMemberAdapter.ViewHolder> {
    private List<ListUsers.Data> list;
    private Context mContext;

    public GroupMemberAdapter(List<ListUsers.Data> list, Context mContext){
        this.list = list;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_addgroup_step2,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.button_close.setVisibility(View.GONE);
        ListUsers.Data listItems = list.get(position);
        holder.username.setText(listItems.getUsers_name());
        if(listItems.getUsers_image_link()==null) {
            Picasso.get().load(R.drawable.no_photo).fit().centerInside().placeholder(R.drawable.no_photo).error(R.drawable.no_photo).into(holder.member_photo);
        } else {
            Uri uri = Uri.parse("http://gomudik.id:81".concat(listItems.getUsers_image_link().substring(1)));
            Picasso.get().load(uri).fit().centerInside().placeholder(R.drawable.no_photo).error(R.drawable.no_photo).into(holder.member_photo);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class  ViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView member_photo;
        private RelativeLayout button_close;
        private TextView username;
        public ViewHolder(View itemView) {
            super(itemView);
            member_photo = itemView.findViewById(R.id.member_photo);
            username = itemView.findViewById(R.id.username);
            button_close = itemView.findViewById(R.id.button_close);
        }
    }
}