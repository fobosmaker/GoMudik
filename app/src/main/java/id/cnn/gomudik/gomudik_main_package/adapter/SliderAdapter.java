package id.cnn.gomudik.gomudik_main_package.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;

import java.util.List;

import id.cnn.gomudik.R;
import id.cnn.gomudik.gomudik_main_package.model.ListSlider;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.ViewHolder> {
    private List<ListSlider> list;
    private Context mContext;

    public SliderAdapter(List<ListSlider> list, Context mContext){
        this.list = list;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_slider,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ListSlider data = list.get(position);
        //holder.image.setImageResource(data.getImage());
        Picasso.get().load(data.getImage()).into(holder.image);
        holder.cage_image.setBackgroundResource(data.getColor_background());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class  ViewHolder extends RecyclerView.ViewHolder{
        private ImageView image;
        private RelativeLayout cage_image;
        public ViewHolder(View itemView) {
            super(itemView);
            image= itemView.findViewById(R.id.image);
            cage_image = itemView.findViewById(R.id.cage_image);
        }
    }
}