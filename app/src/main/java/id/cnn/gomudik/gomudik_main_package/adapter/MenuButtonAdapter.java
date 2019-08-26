package id.cnn.gomudik.gomudik_main_package.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import id.cnn.gomudik.gomudik_main_package.activity.MainActivity;
import id.cnn.gomudik.R;
import id.cnn.gomudik.gomudik_main_package.model.MenuButtonModel;

public class MenuButtonAdapter extends RecyclerView.Adapter<MenuButtonAdapter.ViewHolder> {
    private ArrayList<MenuButtonModel> list;
    private MainActivity context;
    public MenuButtonAdapter(ArrayList<MenuButtonModel> list, MainActivity context){
        this.list = list;
        this.context = context;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_menu_button,parent,false);
        return new ViewHolder(v, context);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MenuButtonModel listItems = list.get(position);
        holder.content.setText(listItems.getContent());
        holder.imageView.setImageResource(listItems.getIcon());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class  ViewHolder extends RecyclerView.ViewHolder{
        private TextView content;
        private ImageView imageView;
        public ViewHolder(final View itemView, final MainActivity context) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_icon);
            content = itemView.findViewById(R.id.content);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.menuButtonClick(getAdapterPosition());
                }
            });
        }
    }
}