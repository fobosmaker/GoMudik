package id.cnn.gomudik.gomudik_user_and_group_package.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import id.cnn.gomudik.gomudik_user_and_group_package.activity.user.AddFriendActivity;
import id.cnn.gomudik.R;
import id.cnn.gomudik.gomudik_user_and_group_package.model.ListUsers;

public class ListAddFriendAdapter extends RecyclerView.Adapter<ListAddFriendAdapter.ViewHolder> {
    private List<ListUsers.Data> list;
    private AddFriendActivity addFriendActivity;

    public ListAddFriendAdapter(ArrayList<ListUsers.Data> list, Context mContext){
        this.list = list;
        this.addFriendActivity = (AddFriendActivity) mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_listsaddcontact,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ListUsers.Data listItems = list.get(position);
        holder.username.setText(listItems.getUsers_name());
        holder.email.setText(listItems.getUsers_email());
        if(listItems.getUsers_image_link()==null) {
            Picasso.get().load(R.drawable.no_photo).fit().centerInside().placeholder(R.drawable.no_photo).error(R.drawable.no_photo).into(holder.user_photo);
        } else {
            Picasso.get().load(listItems.getUsers_image_link()).fit().centerInside().placeholder(R.drawable.no_photo).error(R.drawable.no_photo).into(holder.user_photo);
        }
        holder.addContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFriendActivity.dataSelection(listItems.getUsers_id(), listItems.getUsers_name());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class  ViewHolder extends RecyclerView.ViewHolder{
        private ImageView user_photo;
        private TextView username;
        private TextView email;
        private ImageButton addContact;
        public ViewHolder(View itemView) {
            super(itemView);
            user_photo = itemView.findViewById(R.id.user_photo);
            username = itemView.findViewById(R.id.username);
            email = itemView.findViewById(R.id.email);
            addContact = itemView.findViewById(R.id.add_contact);
        }
    }

    public void filterList(List<ListUsers.Data> newList){
        list = newList;
        notifyDataSetChanged();
    }
}