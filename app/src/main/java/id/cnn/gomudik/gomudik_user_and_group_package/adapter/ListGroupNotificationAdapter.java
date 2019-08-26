package id.cnn.gomudik.gomudik_user_and_group_package.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import id.cnn.gomudik.R;
import id.cnn.gomudik.gomudik_user_and_group_package.model.ListNotification;
import id.cnn.gomudik.util.DifferenceTime;

public class ListGroupNotificationAdapter extends RecyclerView.Adapter<ListGroupNotificationAdapter.ViewHolder> {
    private ArrayList<ListNotification.Data> list;

    public ListGroupNotificationAdapter(ArrayList<ListNotification.Data> list){
        this.list = list;
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
            Picasso.get().load(listItems.getRequester_image()).fit().centerInside().placeholder(R.drawable.no_photo).error(R.drawable.no_photo).into(holder.user_photo);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class  ViewHolder extends RecyclerView.ViewHolder{
        private ImageView user_photo;
        private TextView content, time;
        public ViewHolder(View itemView) {
            super(itemView);
            user_photo = itemView.findViewById(R.id.user_photo);
            content = itemView.findViewById(R.id.content);
            time = itemView.findViewById(R.id.time);
        }
    }
}