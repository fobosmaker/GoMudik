package id.cnn.gomudik.gomudik_main_package.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import id.cnn.gomudik.R;
import id.cnn.gomudik.gomudik_main_package.activity.GoMudikNewsVideoActivity;
import id.cnn.gomudik.gomudik_main_package.model.GetNews;
import id.cnn.gomudik.util.DifferenceTime;

public class MenuGoMudikNewsAdapter extends RecyclerView.Adapter<MenuGoMudikNewsAdapter.ViewHolder> {
    private List<GetNews.Data> list;
    private Context mContext;

    public MenuGoMudikNewsAdapter(List<GetNews.Data> list, Context mContext){
        this.list = list;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_menu_gomudiknews,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final GetNews.Data listItems = list.get(position);
        if (listItems.getTitle() != null) {
            holder.title.setText(listItems.getTitle());
        }
        String text = "";
        if (listItems.getPublished_at() != null && listItems.getChannel_title() != null) {
            DifferenceTime published = new DifferenceTime(listItems.getPublished_at());
            published.executeDateTimeDifference();
            text = listItems.getChannel_title() + " • " + published.getResultDefault();
        }
        holder.desc.setText(text);
        if (listItems.getUrl() != null) {
            String url = listItems.getUrl();
            Picasso.get().load(url).fit().centerInside().placeholder(R.drawable.ex_thumbnail).error(R.drawable.ex_thumbnail).into(holder.thumbnail);
        } else {
            Picasso.get().load(R.drawable.ex_thumbnail).fit().centerInside().into(holder.thumbnail);
        }
        if (listItems.getVideo_id() != null) {
            final String id = listItems.getVideo_id();
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, GoMudikNewsVideoActivity.class);
                    intent.putExtra("videoId", id);
                    mContext.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class  ViewHolder extends RecyclerView.ViewHolder{
        private ImageView thumbnail;
        private TextView title;
        private TextView desc;
        public ViewHolder(View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.image_thumbnail);
            title = itemView.findViewById(R.id.video_title);
            desc = itemView.findViewById(R.id.video_detail);
        }
    }
}