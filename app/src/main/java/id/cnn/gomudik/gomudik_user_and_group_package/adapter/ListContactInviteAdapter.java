package id.cnn.gomudik.gomudik_user_and_group_package.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import id.cnn.gomudik.R;
import id.cnn.gomudik.gomudik_user_and_group_package.activity.group_chat.InviteActivity;
import id.cnn.gomudik.gomudik_user_and_group_package.model.ListUsers;

public class ListContactInviteAdapter extends RecyclerView.Adapter<ListContactInviteAdapter.ViewHolder> {
    private List<ListUsers.Data> list;
    private InviteActivity inviteActivity;

    public ListContactInviteAdapter(ArrayList<ListUsers.Data> list, Context mContext){
        this.list = list;
        this.inviteActivity = (InviteActivity) mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_listcontact,parent,false);
        return new ViewHolder(v, inviteActivity);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ListUsers.Data listItems = list.get(position);
        holder.username.setText(listItems.getUsers_name());
        holder.email.setText(listItems.getUsers_email());
        if(listItems.getUsers_image_link()==null) {
            Picasso.get().load(R.drawable.no_photo).fit().centerInside().placeholder(R.drawable.no_photo).error(R.drawable.no_photo).into(holder.user_photo);
        } else {
            Uri uri = Uri.parse("http://gomudik.id:81".concat(listItems.getUsers_image_link().substring(1)));
            Picasso.get().load(uri).fit().centerInside().placeholder(R.drawable.no_photo).error(R.drawable.no_photo).into(holder.user_photo);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView user_photo;
        private TextView username;
        private TextView email;
        private CheckBox checkBox;
        InviteActivity inviteActivity;
        public ViewHolder(View itemView, final InviteActivity inviteActivity) {
            super(itemView);
            user_photo = itemView.findViewById(R.id.user_photo);
            username = itemView.findViewById(R.id.username);
            email = itemView.findViewById(R.id.email);
            checkBox = itemView.findViewById(R.id.check_box);
            this.inviteActivity = inviteActivity;
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    inviteActivity.prepareSelection(view,getAdapterPosition());
                }
            });
        }
    }

    public void filterList(List<ListUsers.Data> newList){
        list = newList;
        notifyDataSetChanged();
    }
}