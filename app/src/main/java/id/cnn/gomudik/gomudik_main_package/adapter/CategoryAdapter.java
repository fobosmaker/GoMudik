package id.cnn.gomudik.gomudik_main_package.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import id.cnn.gomudik.R;
import id.cnn.gomudik.gomudik_main_package.activity.CategoryActivity;
import id.cnn.gomudik.gomudik_main_package.model.GetCategories;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private List<GetCategories.Data> list;
    private CategoryActivity context;
    public CategoryAdapter(List<GetCategories.Data> list,Context context ){
        this.list = list;
        this.context = (CategoryActivity) context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_categories,parent,false);
        return new ViewHolder(v, context);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        GetCategories.Data listItems = list.get(position);
        holder.content.setText(listItems.getContent());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class  ViewHolder extends RecyclerView.ViewHolder{
        private TextView content;
        public ViewHolder(View itemView, final CategoryActivity context) {
            super(itemView);
            content = itemView.findViewById(R.id.content);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //context.selectedData(list.get(getAdapterPosition()).getId(),list.get(getAdapterPosition()).getContent());
                    context.selectedData(list.get(getAdapterPosition()).getId(),list.get(getAdapterPosition()).getContent(),list.get(getAdapterPosition()).getType(),list.get(getAdapterPosition()).getKeyword());
                }
            });
        }
    }
}