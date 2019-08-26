package id.cnn.gomudik.gomudik_main_package.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import id.cnn.gomudik.R;
import id.cnn.gomudik.gomudik_main_package.activity.MapsActivity;
import id.cnn.gomudik.gomudik_main_package.model.MapsGetNearby;

public class ListNearbyByGoogleAdapter extends RecyclerView.Adapter<ListNearbyByGoogleAdapter.ViewHolder> {
    private List<MapsGetNearby.Results> list;
    private MapsActivity context;
    public ListNearbyByGoogleAdapter(List<MapsGetNearby.Results> list, MapsActivity context ){
        this.list = list;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_list_nearby,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final MapsGetNearby.Results listItems = list.get(position);
        holder.placeName.setText(listItems.getName());
        holder.placeAddress.setText(listItems.getVicinity());
        holder.placeTelephone.setVisibility(View.GONE);
        //holder.placeTelephone.setText(listItems.getTelephone());
        DecimalFormat round = new DecimalFormat("#.##");
        String dist = round.format(context.countDistance(listItems.getGeometry().getLocation().getLat(),listItems.getGeometry().getLocation().getLng()))+" km";
        holder.placeDistance.setText(dist);
        holder.cage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //context.zoomInNearby(listItems.getLatitude(),listItems.getLongitude());
                context.zoomInNearbyByGoogle(listItems);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class  ViewHolder extends RecyclerView.ViewHolder{
        private TextView placeName, placeAddress, placeTelephone, placeDistance;
        private LinearLayout cage;
        public ViewHolder(View itemView) {
            super(itemView);
            placeName = itemView.findViewById(R.id.placeName);
            placeAddress = itemView.findViewById(R.id.placeAddress);
            placeTelephone = itemView.findViewById(R.id.placeTelephone);
            placeDistance = itemView.findViewById(R.id.placeDistance);
            cage = itemView.findViewById(R.id.cage);
        }
    }
}