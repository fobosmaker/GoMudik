package id.cnn.gomudik.gomudik_main_package.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import id.cnn.gomudik.R;
import id.cnn.gomudik.gomudik_user_and_group_package.model.ListChatGroup;
import id.cnn.gomudik.gomudik_user_and_group_package.activity.group_chat.GroupListActivity;

public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.ViewHolder> {
    private List<ListChatGroup.Data> list;
    private GroupListActivity context;
    public GroupListAdapter(List<ListChatGroup.Data> list, Context context ){
        this.list = list;
        this.context = (GroupListActivity) context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_list_group,parent,false);
        return new ViewHolder(v, context);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ListChatGroup.Data listItems = list.get(position);
        holder.username.setText(listItems.getGroup_name());
        holder.email.setText(listItems.getGroup_member());
        if(listItems.getGroup_image() != null){
            Uri uri = Uri.parse("http://gomudik.id:81".concat(listItems.getGroup_image().substring(1)));
            Picasso.get().load(uri).fit().centerInside().placeholder(R.drawable.no_photo).error(R.drawable.no_photo).into(holder.user_photo);
        } else {
            Picasso.get().load(R.drawable.no_photo).fit().centerInside().placeholder(R.drawable.no_photo).error(R.drawable.no_photo).into(holder.user_photo);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class  ViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView user_photo;
        private TextView username, email;
        public ViewHolder(View itemView, final GroupListActivity context) {
            super(itemView);
            user_photo = itemView.findViewById(R.id.user_photo);
            username = itemView.findViewById(R.id.username);
            email = itemView.findViewById(R.id.email);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.selectedData(list.get(getAdapterPosition()).getId());
                }
            });
        }
    }
}