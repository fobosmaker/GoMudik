package id.cnn.gomudik.gomudik_user_and_group_package.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import id.cnn.gomudik.R;
import id.cnn.gomudik.gomudik_user_and_group_package.model.Chat;


public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private List<Chat> list;
    private Context mContext;
    private String id_user;

    public ChatAdapter(List<Chat> list, String id_user){
        this.list = list;
        this.id_user = id_user;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_chat,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Chat chat = list.get(position);
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date;
        try {
            Date date1 = timeFormat.parse(chat.getDate());
            SimpleDateFormat getHours = new SimpleDateFormat("HH:mm");
            date = getHours.format(date1);
        } catch (ParseException e) {
            e.printStackTrace();
            date = e.toString();
        }
        if(chat.getUsers_id().equalsIgnoreCase(id_user)){
            holder.left_chat.setVisibility(View.GONE);
            holder.right_chat.setVisibility(View.VISIBLE);
            holder.your_username.setText("You");
            holder.your_date.setText(date);
            holder.your_message.setText(chat.getMessage());
        } else {
            holder.left_chat.setVisibility(View.VISIBLE);
            holder.right_chat.setVisibility(View.GONE);
            holder.username.setText(chat.getUsers_name());
            holder.date.setText(date);
            holder.message.setText(chat.getMessage());
        }
       /* if(chat.getImage_url() != null){
            Picasso.get().load(chat.getImage_url()).into(holder.chatImage);
            *//*Uri uri = Uri.parse(chat.getImage_url());
            holder.chatImage.setImageURI(uri);*//*
            holder.cageChatImage.setVisibility(View.VISIBLE);
        }*/
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class  ViewHolder extends RecyclerView.ViewHolder{
        private TextView username, date, message,your_username, your_date, your_message;
        private RelativeLayout left_chat, right_chat;
        public ViewHolder(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            date = itemView.findViewById(R.id.date_send);
            message = itemView.findViewById(R.id.chat_text);
            your_username = itemView.findViewById(R.id.your_username);
            your_date = itemView.findViewById(R.id.your_date_send);
            your_message = itemView.findViewById(R.id.your_chat_text);
            left_chat = itemView.findViewById(R.id.left_chat);
            right_chat = itemView.findViewById(R.id.right_chat);
        }
    }
}