package id.cnn.gomudik.gomudik_user_and_group_package.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

import id.cnn.gomudik.R;
import id.cnn.gomudik.gomudik_user_and_group_package.activity.user.ListContactActivity;
import id.cnn.gomudik.gomudik_user_and_group_package.model.GroupContact;
import id.cnn.gomudik.gomudik_user_and_group_package.model.ListUsers;
import id.cnn.gomudik.gomudik_user_and_group_package.viewholder.GroupContentViewHolder;
import id.cnn.gomudik.gomudik_user_and_group_package.viewholder.GroupTitleViewHolder;

public class GroupTitleAdapter extends ExpandableRecyclerViewAdapter<GroupTitleViewHolder, GroupContentViewHolder> {
    private Context mContext;
    List<? extends ExpandableGroup> groups;
    ListContactActivity listContactActivity;
    public GroupTitleAdapter(List<? extends ExpandableGroup> groups, Context context) {
        super(groups);
        this.mContext = context;
        this.groups = groups;
        this.listContactActivity = (ListContactActivity) mContext;
        notifyDataSetChanged();
    }

    @Override
    public GroupTitleViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_group_title,parent,false);
        return new GroupTitleViewHolder(v);
    }

    @Override
    public GroupContentViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_group_contact,parent,false);
        return new GroupContentViewHolder(v,listContactActivity);
    }

    @Override
    public void onBindChildViewHolder(GroupContentViewHolder holder, int flatPosition, ExpandableGroup group, int childIndex) {
        GroupContact contact = (GroupContact) group.getItems().get(childIndex);
        //ListContact.Data contact = (ListContact.Data) group.getItems().get(childIndex);
        holder.setName(contact.getName());
        holder.setImageView(contact.getImage_link());
        holder.setEmail(contact.getDescription());
        if(contact.getType_content() == 1){
            holder.hideButton();
        } else {
            holder.showButton();
            holder.setButtonOnClick(contact.getType_data(),contact.getId(),contact.getCode());
        }
    }

    @Override
    public void onBindGroupViewHolder(final GroupTitleViewHolder holder, int flatPosition, ExpandableGroup group) {
        holder.setGroupTitle(group.getTitle());
    }
}
