package id.cnn.gomudik.gomudik_user_and_group_package.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import id.cnn.gomudik.R;
import id.cnn.gomudik.gomudik_user_and_group_package.activity.user.ListContactActivity;
import id.cnn.gomudik.gomudik_user_and_group_package.activity.user.NotificationActivity;
import id.cnn.gomudik.gomudik_user_and_group_package.model.ListNotification;
import id.cnn.gomudik.util.DifferenceTime;

public class ListNotificationAdapter extends RecyclerView.Adapter<ListNotificationAdapter.ViewHolder> {
    private List<ListNotification.Data> list;
    private NotificationActivity notificationActivity;

    public ListNotificationAdapter(ArrayList<ListNotification.Data> list, Context mContext){
        this.list = list;
        this.notificationActivity = (NotificationActivity) mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_listnotification,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ListNotification.Data listItems = list.get(position);
        String content = listItems.getContent().replace(listItems.getRequested(),"you");
        holder.content.setText(content);
        DifferenceTime published = new DifferenceTime(listItems.getCreated());
        published.executeTimeChat();
        holder.time.setText(published.getResultDefault());
        if(listItems.getRequester_image()==null) {
            Picasso.get().load(R.drawable.no_photo).fit().centerInside().placeholder(R.drawable.no_photo).error(R.drawable.no_photo).into(holder.user_photo);
        } else {
            Uri uri = Uri.parse("http://gomudik.id:81".concat(listItems.getRequester_image().substring(1)));
            Picasso.get().load(uri).fit().centerInside().placeholder(R.drawable.no_photo).error(R.drawable.no_photo).into(holder.user_photo);
        }
        holder.cageContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!listItems.getType().equalsIgnoreCase("3")) {
                    notificationActivity.startActivity(new Intent(notificationActivity, ListContactActivity.class));
                } else {
                    notificationActivity.searchStatusById(listItems.getId_status());
                    //notificationActivity.startActivity(new Intent(notificationActivity, StatusActivity.class));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class  ViewHolder extends RecyclerView.ViewHolder{
        private ImageView user_photo;
        private TextView content, time;
        private RelativeLayout cageContact;
        public ViewHolder(View itemView) {
            super(itemView);
            user_photo = itemView.findViewById(R.id.user_photo);
            content = itemView.findViewById(R.id.content);
            time = itemView.findViewById(R.id.time);
            cageContact = itemView.findViewById(R.id.cage_contact);
        }
    }

    public void filterList(List<ListNotification.Data> newList){
        list = newList;
        notifyDataSetChanged();
    }
}