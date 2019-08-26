package id.cnn.gomudik.gomudik_user_and_group_package.adapter;

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

import java.util.List;

import id.cnn.gomudik.gomudik_user_and_group_package.activity.group_chat.ChatActivity;
import id.cnn.gomudik.R;
import id.cnn.gomudik.gomudik_user_and_group_package.activity.group_chat.ListChatActivity;
import id.cnn.gomudik.gomudik_user_and_group_package.model.ListChatGroup;
import id.cnn.gomudik.firebase.GoMudikFirebase;

public class ListChatAdapter extends RecyclerView.Adapter<ListChatAdapter.ViewHolder> {
    private List<ListChatGroup.Data> list;
    private ListChatActivity mContext;
    private String currId;
    private GoMudikFirebase gfb;

    public ListChatAdapter(List<ListChatGroup.Data> list, ListChatActivity mContext, String currId){
        this.list = list;
        this.mContext = mContext;
        this.currId = currId;
        gfb = new GoMudikFirebase();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_list_chat,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ListChatGroup.Data listItems = list.get(position);
        holder.username.setText(listItems.getGroup_name());
        gfb.getLastMessage(listItems.getCode(),holder.email,holder.chat_date);
        gfb.countBadge(holder.badge,holder.count_badge,listItems.getCode(),currId);
        if(listItems.getGroup_image() != null){
            Uri uri = Uri.parse("http://gomudik.id:81".concat(listItems.getGroup_image().substring(1)));
            Picasso.get().load(uri).fit().centerInside().placeholder(R.drawable.no_photo).error(R.drawable.no_photo).into(holder.user_photo);
        } else {
            Picasso.get().load(R.drawable.no_photo).fit().centerInside().placeholder(R.drawable.no_photo).error(R.drawable.no_photo).into(holder.user_photo);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext,ChatActivity.class);
                intent.putExtra("id_group",listItems.getId());
                intent.putExtra("id_chat_room",listItems.getCode());
                intent.putExtra("group_name",listItems.getGroup_name());
                intent.putExtra("group_members",listItems.getGroup_member());
                intent.putExtra("group_image",listItems.getGroup_image());
                intent.putExtra("group_created_by",listItems.getCreated_by());
                mContext.startActivity(intent);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mContext.showDialog(listItems);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class  ViewHolder extends RecyclerView.ViewHolder{
        private ImageView user_photo;
        private TextView username, email, chat_date, count_badge;
        private RelativeLayout badge;
        public ViewHolder(View itemView) {
            super(itemView);
            user_photo = itemView.findViewById(R.id.user_photo);
            username = itemView.findViewById(R.id.username);
            email = itemView.findViewById(R.id.email);
            chat_date = itemView.findViewById(R.id.chat_date);
            badge = itemView.findViewById(R.id.badge);
            count_badge = itemView.findViewById(R.id.count_badge);
        }
    }
}