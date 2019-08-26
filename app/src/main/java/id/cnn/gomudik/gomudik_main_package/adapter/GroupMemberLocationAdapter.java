package id.cnn.gomudik.gomudik_main_package.adapter;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import id.cnn.gomudik.R;
import id.cnn.gomudik.gomudik_main_package.activity.MapsActivity;
import id.cnn.gomudik.gomudik_main_package.model.GetLocationByGroup;
import id.cnn.gomudik.util.DifferenceTime;

public class GroupMemberLocationAdapter extends RecyclerView.Adapter<GroupMemberLocationAdapter.ViewHolder> {
    private List<GetLocationByGroup.Data> list;
    private MapsActivity context;
    public GroupMemberLocationAdapter(List<GetLocationByGroup.Data> list, MapsActivity context ){
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
        final GetLocationByGroup.Data listItems = list.get(position);
        holder.placeName.setText(listItems.getFullname());
        holder.placeAddress.setText(listItems.getEmail());
        DifferenceTime dt = new DifferenceTime(listItems.getCreated());
        dt.executeTimeChat();
        String lastUpdate = "Last update: "+dt.getResultDefault();
        holder.placeTelephone.setText(lastUpdate);
        DecimalFormat round = new DecimalFormat("#.##");
        String dist = round.format(Double.valueOf(listItems.getDistance()))+" km";
        holder.placeDistance.setText(dist);
        holder.cage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.zoomInGroupMember(Double.valueOf(listItems.getLatitude()),Double.valueOf(listItems.getLongitude()));
            }
        });
        holder.image.setVisibility(View.VISIBLE);
        if(listItems.getUsers_image_link() != null){
            Uri uri = Uri.parse("http://gomudik.id:81".concat(listItems.getUsers_image_link().substring(1)));
            Picasso.get().load(uri).fit().centerInside().placeholder(R.drawable.no_photo).error(R.drawable.no_photo).into(holder.image);
        } else {
            Picasso.get().load(R.drawable.no_photo).fit().centerInside().into(holder.image);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class  ViewHolder extends RecyclerView.ViewHolder{
        private TextView placeName, placeAddress, placeTelephone, placeDistance;
        private LinearLayout cage;
        private CircleImageView image;
        public ViewHolder(View itemView) {
            super(itemView);
            placeName = itemView.findViewById(R.id.placeName);
            placeAddress = itemView.findViewById(R.id.placeAddress);
            placeTelephone = itemView.findViewById(R.id.placeTelephone);
            placeDistance = itemView.findViewById(R.id.placeDistance);
            cage = itemView.findViewById(R.id.cage);
            image = itemView.findViewById(R.id.placePhoto);
        }
    }
}