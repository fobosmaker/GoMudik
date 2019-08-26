package id.cnn.gomudik.gomudik_main_package.adapter;

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

import id.cnn.gomudik.gomudik_main_package.activity.MainActivity;
import id.cnn.gomudik.R;
import id.cnn.gomudik.gomudik_user_and_group_package.model.ListStatus;
import id.cnn.gomudik.util.DifferenceTime;

public class MenuStatusAdapter extends RecyclerView.Adapter<MenuStatusAdapter.ViewHolder> {
    private ArrayList<ListStatus.Data> list;
    private MainActivity mContext;

    public MenuStatusAdapter(ArrayList<ListStatus.Data> list, MainActivity mContext){
        this.list = list;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_menu_status,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ListStatus.Data listItems = list.get(position);
        DifferenceTime published = new DifferenceTime(listItems.getCreated());
        published.executeDateTimeDifference();
        holder.email.setText(listItems.getUsers_email());
        holder.username.setText(listItems.getUsers_name());
        holder.status_published.setText(published.getResultDefault());
        //holder.total_favorites.setText(listItems.getLove_count());
        holder.status.setText(listItems.getContent());
        if(listItems.getUsers_image_link() != null){
            String link = listItems.getUsers_image_link().substring(1);
            Uri uri = Uri.parse("http://gomudik.id:81".concat(link));
            Picasso.get().load(uri).fit().centerInside().placeholder(R.drawable.no_photo).error(R.drawable.no_photo).into(holder.user_photo);
        } else {
            Picasso.get().load(R.drawable.no_photo).fit().centerInside().placeholder(R.drawable.no_photo).error(R.drawable.no_photo).into(holder.user_photo);
        }
        if(listItems.getImage_link() != null){
            final Uri uri = Uri.parse("http://gomudik.id:81".concat(listItems.getImage_link().substring(1)));
            Picasso.get().load(uri).fit().centerInside().placeholder(R.drawable.ex_thumbnail).error(R.drawable.ex_thumbnail).into(holder.thumbnail_status);
            holder.buttonZoom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mContext.onZoom(uri);
                }
            });
        } else {
            holder.thumbnail_status.setVisibility(View.GONE);
            holder.buttonZoom.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class  ViewHolder extends RecyclerView.ViewHolder{
        private ImageView user_photo, thumbnail_status;
        private TextView email,username,status,status_published,total_favorites;
        private RelativeLayout buttonZoom;
        public ViewHolder(View itemView) {
            super(itemView);
            user_photo = itemView.findViewById(R.id.user_photo);
            thumbnail_status = itemView.findViewById(R.id.thumbnail_status);
            username = itemView.findViewById(R.id.username);
            email = itemView.findViewById(R.id.email);
            status = itemView.findViewById(R.id.status_content);
            status_published = itemView.findViewById(R.id.status_published);
            //total_favorites = itemView.findViewById(R.id.total_favorites);
            buttonZoom = itemView.findViewById(R.id.button_zoom);
        }
    }
}