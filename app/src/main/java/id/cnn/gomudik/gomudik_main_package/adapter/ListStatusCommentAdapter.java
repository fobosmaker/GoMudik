package id.cnn.gomudik.gomudik_main_package.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import id.cnn.gomudik.R;
import id.cnn.gomudik.gomudik_main_package.activity.StatusCommentActivity;
import id.cnn.gomudik.gomudik_user_and_group_package.model.ListStatusComment;
import id.cnn.gomudik.util.DifferenceTime;


public class ListStatusCommentAdapter extends RecyclerView.Adapter<ListStatusCommentAdapter.ViewHolder> {
   // private Response<ListStatusComment> list;
    private ArrayList<ListStatusComment.Data> list;
    private StatusCommentActivity mContext;
    private Integer last_row;

    public ListStatusCommentAdapter(ArrayList<ListStatusComment.Data> list, Context mContext, Integer limit){
        this.list = list;
        this.mContext = (StatusCommentActivity) mContext;
        this.last_row = limit-1;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_status_comment,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //final ListStatusComment.Data listItems = list.body().getData().get(position);
        ListStatusComment.Data listItems = list.get(position);
        if(holder.getAdapterPosition() == last_row){
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0,0,0,0);
            holder.cage_content.setLayoutParams(params);
        }
        DifferenceTime published = new DifferenceTime(listItems.getCreated());
        published.executeDateTimeDifference();
        holder.email.setText(listItems.getUsers_email());
        holder.username.setText(listItems.getUsers_name());
        holder.status_published.setText(published.getResultDefault());
        holder.status.setText(listItems.getContent());
        if(listItems.getUsers_image_link() != null){
            Uri uri = Uri.parse("http://gomudik.id:81".concat(listItems.getUsers_image_link().substring(1)));
            Picasso.get().load(uri).fit().centerInside().placeholder(R.drawable.no_photo).error(R.drawable.no_photo).into(holder.user_photo);
        } else {
            Picasso.get().load(R.drawable.no_photo).fit().centerInside().placeholder(R.drawable.no_photo).error(R.drawable.no_photo).into(holder.user_photo);
        }

        holder.user_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // mContext.showProfile(listItems);
                //Toast.makeText(mContext, "Image clicked", Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class  ViewHolder extends RecyclerView.ViewHolder{
        private ImageView user_photo;
        private TextView email,username,status,status_published;
        private RelativeLayout cage_content;
        private ImageButton buttonStatusAction;
        public ViewHolder(View itemView) {
            super(itemView);
            user_photo = itemView.findViewById(R.id.user_photo);
            username = itemView.findViewById(R.id.username);
            email = itemView.findViewById(R.id.email);
            status = itemView.findViewById(R.id.status_content);
            status_published = itemView.findViewById(R.id.status_published);
            cage_content = itemView.findViewById(R.id.contentCage);
            buttonStatusAction = itemView.findViewById(R.id.button_status_action);
        }
    }
}