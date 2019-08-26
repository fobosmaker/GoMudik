package id.cnn.gomudik.gomudik_main_package.adapter;

import android.content.Context;
import android.content.Intent;
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

import id.cnn.gomudik.R;
import id.cnn.gomudik.gomudik_main_package.activity.StatusCommentActivity;
import id.cnn.gomudik.gomudik_user_and_group_package.activity.user.ProfileUserActivity;
import id.cnn.gomudik.gomudik_user_and_group_package.model.ListStatus;
import id.cnn.gomudik.util.DifferenceTime;
import retrofit2.Response;


public class ListUsersStatusAdapter extends RecyclerView.Adapter<ListUsersStatusAdapter.ViewHolder> {
    private Response<ListStatus> list;
    private ProfileUserActivity mContext;
    private Integer last_row;

    public ListUsersStatusAdapter(Response<ListStatus> list, Context mContext, Integer limit){
        this.list = list;
        this.mContext = (ProfileUserActivity) mContext;
        this.last_row = limit-1;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_status,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ListStatus.Data listItems = list.body().getData().get(position);
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
        //holder.total_favorites.setText(listItems.getLove_count());
        holder.status.setText(listItems.getContent());
        if(listItems.getUsers_image_link() != null){
            Uri uri = Uri.parse("http://gomudik.id:81".concat(listItems.getUsers_image_link().substring(1)));
            Picasso.get().load(uri).fit().centerInside().placeholder(R.drawable.no_photo).error(R.drawable.no_photo).into(holder.user_photo);
        } else {
            Picasso.get().load(R.drawable.no_photo).fit().centerInside().placeholder(R.drawable.no_photo).error(R.drawable.no_photo).into(holder.user_photo);
        }
        if(listItems.getImage_link() != null){
            Uri uri = Uri.parse("http://gomudik.id:81".concat(listItems.getImage_link().substring(1)));
            Picasso.get().load(uri).fit().centerInside().placeholder(R.drawable.ex_thumbnail).error(R.drawable.ex_thumbnail).into(holder.thumbnail_status);
        } else {
            holder.thumbnail_status.setVisibility(View.GONE);
        }
        if(listItems.getAddress() != null){
            holder.text_location.setText(listItems.getAddress());
        } else {
            holder.user_location.setVisibility(View.GONE);
        }

        holder.buttonStatusAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.showDialogStatusAction(listItems);
            }
        });

        Integer totalComment = Integer.parseInt(listItems.getTotal_comment());
        String content;
        if(totalComment > 0){
            if(totalComment == 1){
                content = "View "+totalComment+" comment";
            } else {
                content = "View all "+totalComment+" comments";
            }
        } else {
            content = "Add comment";
        }
        holder.status_comment.setText(content);
        holder.status_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, StatusCommentActivity.class);
                intent.putExtra("data", listItems);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.body().getTotal_data();
    }

    public class  ViewHolder extends RecyclerView.ViewHolder{
        private ImageView user_photo, thumbnail_status;
        private TextView email,username,status,status_published,total_favorites, text_location, status_comment;
        private RelativeLayout bottom_status_description, cage_content, user_location;
        private ImageButton buttonStatusAction;
        public ViewHolder(View itemView) {
            super(itemView);
            user_photo = itemView.findViewById(R.id.user_photo);
            thumbnail_status = itemView.findViewById(R.id.thumbnail_status);
            username = itemView.findViewById(R.id.username);
            email = itemView.findViewById(R.id.email);
            status = itemView.findViewById(R.id.status_content);
            status_published = itemView.findViewById(R.id.status_published);
            //total_favorites = itemView.findViewById(R.id.total_favorites);
            //bottom_status_description = itemView.findViewById(R.id.status_description);
            cage_content = itemView.findViewById(R.id.contentCage);
            user_location = itemView.findViewById(R.id.user_location);
            text_location = itemView.findViewById(R.id.text_location);
            buttonStatusAction = itemView.findViewById(R.id.button_status_action);
            status_comment = itemView.findViewById(R.id.status_comment);
        }
    }
}