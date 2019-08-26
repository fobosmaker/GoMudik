package id.cnn.gomudik.gomudik_main_package.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import id.cnn.gomudik.R;
import id.cnn.gomudik.gomudik_main_package.model.MenuJadwalSalatModel;

public class JadwalSalatAdapter extends RecyclerView.Adapter<JadwalSalatAdapter.ViewHolder> {
    private ArrayList<MenuJadwalSalatModel> list;
    public JadwalSalatAdapter(ArrayList<MenuJadwalSalatModel> list) {
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_jadwal_salat,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MenuJadwalSalatModel listItems = list.get(position);
        holder.title.setText(listItems.getTitle());
        holder.time.setText(listItems.getTime());
        Picasso.get().load(listItems.getImage()).fit().centerInside().into(holder.image_round);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public class  ViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView image_round;
        private TextView title, time;
        public ViewHolder(View itemView) {
            super(itemView);
            image_round= itemView.findViewById(R.id.image_round);
            title = itemView.findViewById(R.id.title);
            time = itemView.findViewById(R.id.time);
        }
    }
}