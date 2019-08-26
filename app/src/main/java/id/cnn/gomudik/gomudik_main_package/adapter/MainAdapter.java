package id.cnn.gomudik.gomudik_main_package.adapter;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import id.cnn.gomudik.gomudik_ads.model.GetAds;
import id.cnn.gomudik.gomudik_main_package.model.GetNews;
import id.cnn.gomudik.gomudik_main_package.model.YoutubeGetPlaylistError;
import id.cnn.gomudik.gomudik_user_and_group_package.activity.user.LoginActivity;
import id.cnn.gomudik.gomudik_main_package.activity.MainActivity;
import id.cnn.gomudik.R;
import id.cnn.gomudik.firebase.GoMudikFirebase;
import id.cnn.gomudik.gomudik_user_and_group_package.activity.user.ProfileUserActivity;
import id.cnn.gomudik.gomudik_user_and_group_package.model.ListStatus;
import id.cnn.gomudik.gomudik_main_package.model.MenuAds;
import id.cnn.gomudik.gomudik_main_package.model.MenuJadwalSalatModel;
import id.cnn.gomudik.gomudik_main_package.model.MenuButtonModel;
import id.cnn.gomudik.gomudik_main_package.model.MenuProfile;
import retrofit2.Response;

public class MainAdapter extends RecyclerView.Adapter {
    private MainActivity context;
    private ArrayList<Object> list;
    private final int menu_button = 1;
    private final int menu_jadwal_imsak = 2;
    private final int menu_gomudik_news = 3;
    private final int menu_ads = 4;
    private final int menu_status = 5;
    private final int menu_profile = 6;
    private final int menu_gomudik_news_error = 7;
    private final int menu_status_error = 8;
    private static final String TAG = "MainAdapter";
    private boolean isScrollStatus = false;
    private boolean isScrollNews = false;

    public MainAdapter(ArrayList<Object> list, MainActivity context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        switch (viewType){
            case menu_button:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.menu_button,parent,false);
                return new menuButtonViewHolder(v);
            case menu_jadwal_imsak:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.menu_jadwal_imsak,parent,false);
                return new jadwalImsakViewHolder(v);
            case menu_gomudik_news:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.menu_gomudik_news,parent,false);
                return new goMudikNewsViewholder(v);
            case menu_ads:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.menu_ads,parent,false);
                return new adsViewholder(v);
            case menu_status:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.menu_status,parent,false);
                return new statusViewholder(v);
            case menu_profile:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.menu_profile,parent,false);
                return new profilViewholder(v);
            case menu_gomudik_news_error:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.menu_gomudik_news,parent,false);
                return new goMudikNewsViewholder(v);
            case -1:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.template_error,parent,false);
                return new errorViewholder(v);
        }
        return  null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        switch (holder.getItemViewType()){
            case menu_button:
                menuButtonView((menuButtonViewHolder)holder);
                break;
            case menu_jadwal_imsak:
                jadwalImsakView((jadwalImsakViewHolder)holder);
                break;
            case menu_gomudik_news:
                goMudikNewsView((goMudikNewsViewholder)holder);
                break;
            case menu_ads:
                adsView((adsViewholder)holder, position);
                break;
            case menu_status:
                statusView((statusViewholder)holder);
                break;
            case menu_profile:
                profilView((profilViewholder) holder,position);
        }
    }

    private void menuButtonView(menuButtonViewHolder holder){
        RecyclerView.LayoutManager grid = new GridLayoutManager(context,3);
        MenuButtonAdapter adapter = new MenuButtonAdapter(context.getMenuButtonData(),context);
        holder.recyclerView.setLayoutManager(grid);
        holder.recyclerView.setHasFixedSize(true);
        holder.recyclerView.setAdapter(adapter);
    }

    private void jadwalImsakView(jadwalImsakViewHolder holder){
        RecyclerView.LayoutManager grid = new GridLayoutManager(context,3);
        grid.setAutoMeasureEnabled(true);
        ArrayList<MenuJadwalSalatModel> data = context.getMenuJadwalSalatData();
        SimpleDateFormat format = new SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH);
        holder.date.setText(format.format(new Date()));
        if(data.get(0).getGPSOn()) {
            holder.templateNoGPS.setVisibility(View.GONE);
            holder.recyclerView.setVisibility(View.VISIBLE);
            JadwalSalatAdapter adapter = new JadwalSalatAdapter(context.getMenuJadwalSalatData());
            holder.recyclerView.setLayoutManager(grid);
            holder.recyclerView.setHasFixedSize(true);
            holder.recyclerView.setAdapter(adapter);
        } else {
            holder.templateNoGPS.setVisibility(View.VISIBLE);
            holder.recyclerView.setVisibility(View.GONE);
            holder.textNoGPS.setText(data.get(0).getTitle());
            holder.detailNoGPS.setText(data.get(0).getTime());
            if(data.get(0).getTitle().contains("Enable")) {
                holder.buttonNoGPS.setVisibility(View.VISIBLE);
                holder.buttonNoGPS.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d(TAG, "onClick: button is clicked");
                        context.settingGPS();
                    }
                });
            } else {
                holder.buttonNoGPS.setVisibility(View.GONE);
            }
        }
    }

    private void goMudikNewsView(final goMudikNewsViewholder holder){
        ArrayList<GetNews.Data> data = context.getMenuNews();
        if(data.size() > 0){
            holder.cageGoMudikNewsContent.setVisibility(View.VISIBLE);
            holder.cageTemplateError.setVisibility(View.GONE);
            final LinearLayoutManager manager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            holder.recyclerView.setLayoutManager(manager);
            holder.recyclerView.setHasFixedSize(true);
            SnapHelper snapHelper = new PagerSnapHelper();
            holder.recyclerView.setOnFlingListener(null);
            snapHelper.attachToRecyclerView(holder.recyclerView);
            holder.arrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.onMore("gomudikNews");
                }
            });
            MenuGoMudikNewsAdapter adapter = new MenuGoMudikNewsAdapter(data, context);
            holder.recyclerView.setAdapter(adapter);
            createDotsNews(0, holder.dotsLayout, 5);
            holder.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (isScrollNews && manager.findLastCompletelyVisibleItemPosition() >= 0) {
                        Log.d(TAG, "onScrolled: position " + manager.findLastCompletelyVisibleItemPosition());
                        createDotsNews(manager.findLastCompletelyVisibleItemPosition(), holder.dotsLayout, 5);
                    }
                }

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                        isScrollNews = true;
                    }
                }
            });
        } else {
            holder.cageGoMudikNewsContent.setVisibility(View.GONE);
            holder.cageTemplateError.setVisibility(View.VISIBLE);
            Log.d(TAG, "goMudikNewsView: data is null / empty / 0");
        }
    }

    private void adsView(adsViewholder holder,int position){
        MenuAds ads = (MenuAds) list.get(position);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        if(position == 0){
            params.setMargins(0,0,0,10);
            holder.cage_ads.setLayoutParams(params);
        }
        if(position == list.size()-1){
            params.setMargins(0,10,0,0);
            holder.cage_ads.setLayoutParams(params);
        }
        if(ads.getId_active().equalsIgnoreCase("1")) {
            Picasso.get().load(Uri.parse("http://gomudik.id:81/".concat(ads.getImage_link()))).into(holder.image_ads);
        }
    }

    private void statusView(final statusViewholder holder){
        ArrayList<ListStatus.Data> data = context.getMenuStatus();
        if(data.size() > 0) {
            final LinearLayoutManager manager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            holder.recyclerView.setLayoutManager(manager);
            holder.recyclerView.setHasFixedSize(true);
            SnapHelper snapHelper = new PagerSnapHelper();
            holder.recyclerView.setOnFlingListener(null);
            snapHelper.attachToRecyclerView(holder.recyclerView);
            holder.arrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.onMore("status");
                }
            });
            MenuStatusAdapter listStatusAdapter = new MenuStatusAdapter(data, context);
            holder.recyclerView.setAdapter(listStatusAdapter);
            final int limit = data.size();
            createDotsStatus(0, holder.dotsLayout, limit);
            holder.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (isScrollStatus && manager.findLastCompletelyVisibleItemPosition() >= 0) {
                        Log.d(TAG, "onScrolled: position " + manager.findLastCompletelyVisibleItemPosition());
                        createDotsStatus(manager.findLastCompletelyVisibleItemPosition(), holder.dotsLayout, limit);
                    }
                }

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                        isScrollStatus = true;
                    }
                }

            });
        }
    }

    private void profilView(profilViewholder holder, int position){
        MenuProfile data = (MenuProfile) list.get(position);
        holder.username.setText(data.getUsername());
        holder.email.setText(data.getEmail());
        if(data.getIcon() != null){
            Uri uri = Uri.parse("http://gomudik.id:81".concat(data.getIcon().substring(1)));
            Picasso.get().load(uri).fit().centerInside().placeholder(R.drawable.no_photo).error(R.drawable.no_photo).into(holder.userPhoto);
        } else {
            Picasso.get().load(R.drawable.no_photo).into(holder.userPhoto);
        }
        if(data.getLogin()){
            holder.button.setVisibility(View.VISIBLE);
            holder.cageMenuProfil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.startActivity(new Intent(context, ProfileUserActivity.class));
                }
            });
            holder.buttonMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.moveToChat();
                }
            });
            holder.buttonNotif.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.moveToNotif();
                }
            });
            GoMudikFirebase gfb = new GoMudikFirebase();
            gfb.getTotalNotif(data.getId(),holder.countBadgeNotification, holder.badgeNotification);
            gfb.getTotalMessage(data.getId(), holder.countBadgeMessage, holder.badgeMessage);
        } else {
            holder.button.setVisibility(View.GONE);
            holder.cageMenuProfil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.startActivity(new Intent(context, LoginActivity.class));
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if( list.get(position) instanceof MenuButtonModel)
            return menu_button;
        if( list.get(position) instanceof MenuJadwalSalatModel)
            return menu_jadwal_imsak;
        if( list.get(position) instanceof GetNews.Data)
            return menu_gomudik_news;
        if( list.get(position) instanceof YoutubeGetPlaylistError)
            return menu_gomudik_news;
        if( list.get(position) instanceof MenuAds)
            return menu_ads;
        if( list.get(position) instanceof ListStatus.Data)
            return menu_status;
        if( list.get(position) instanceof MenuProfile)
            return menu_profile;
        return -1;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private class menuButtonViewHolder extends RecyclerView.ViewHolder{
        private RecyclerView recyclerView;
        private menuButtonViewHolder(View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.cage);
        }
    }
    private class jadwalImsakViewHolder extends RecyclerView.ViewHolder{
        private RecyclerView recyclerView;
        private ImageButton arrow;
        private String string = "jadwalImsak";
        private TextView date, textNoGPS, detailNoGPS;
        private RelativeLayout templateNoGPS;
        private Button buttonNoGPS;
        private jadwalImsakViewHolder(View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.cage);
            arrow = itemView.findViewById(R.id.arrow);
            date = itemView.findViewById(R.id.date);
            templateNoGPS = itemView.findViewById(R.id.template_no_gps);
            buttonNoGPS =itemView.findViewById(R.id.button_noGPS);
            textNoGPS = itemView.findViewById(R.id.text_noGPS);
            detailNoGPS = itemView.findViewById(R.id.detail_noGPS);
            arrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.onMore(string);
                }
            });
        }
    }

   private class goMudikNewsViewholder extends RecyclerView.ViewHolder{
        private RecyclerView recyclerView;
        private ImageButton arrow;
        private LinearLayout dotsLayout;
        private RelativeLayout cageGoMudikNewsContent, cageTemplateError;
        private goMudikNewsViewholder(View itemView){
            super(itemView);
            recyclerView = itemView.findViewById(R.id.cage);
            arrow = itemView.findViewById(R.id.arrow);
            dotsLayout = itemView.findViewById(R.id.dotsLayout);
            cageGoMudikNewsContent = itemView.findViewById(R.id.cage_gomudik_news_content);
            cageTemplateError = itemView.findViewById(R.id.cage_template_error);
        }
   }

   private class adsViewholder extends RecyclerView.ViewHolder{
        private ImageView image_ads;
        private RelativeLayout cage_ads;
        private adsViewholder(View itemView){
            super(itemView);
            image_ads = itemView.findViewById(R.id.image_ads);
            cage_ads = itemView.findViewById(R.id.cage_ads);
        }
   }

   private class statusViewholder extends RecyclerView.ViewHolder{
        private RecyclerView recyclerView;
        private ImageButton arrow;
        private LinearLayout dotsLayout;
        private statusViewholder(View itemView){
            super(itemView);
            recyclerView = itemView.findViewById(R.id.cage);
            arrow = itemView.findViewById(R.id.arrow);
            dotsLayout = itemView.findViewById(R.id.dotsLayout);
        }
   }

    private class profilViewholder extends RecyclerView.ViewHolder{
        private RelativeLayout button, buttonMessage, buttonNotif, cageMenuProfil, badgeMessage, badgeNotification;
        private CircleImageView userPhoto;
        private TextView username, email, countBadgeMessage, countBadgeNotification;

        private profilViewholder(View itemView){
            super(itemView);
            button = itemView.findViewById(R.id.button);
            buttonMessage = itemView.findViewById(R.id.button_message);
            buttonNotif = itemView.findViewById(R.id.button_notif);
            userPhoto = itemView.findViewById(R.id.user_photo);
            username = itemView.findViewById(R.id.username);
            email = itemView.findViewById(R.id.email);
            cageMenuProfil = itemView.findViewById(R.id.cage_menu_profile);
            badgeMessage = itemView.findViewById(R.id.badge_message);
            badgeNotification = itemView.findViewById(R.id.badge_notification);
            countBadgeMessage = itemView.findViewById(R.id.count_badge_message);
            countBadgeNotification = itemView.findViewById(R.id.count_badge_notification);
        }
    }

   private class errorViewholder extends RecyclerView.ViewHolder{
        private errorViewholder(View itemView){
            super(itemView);
        }
   }

   public void createDotsStatus(int position, LinearLayout dotsLayout, int limit){
       if(dotsLayout != null){
           dotsLayout.removeAllViews();
       }
       ImageView[] dots = new ImageView[limit];
       for (int i = 0; i < limit; i++) {
           dots[i] = new ImageView(context);
           if (i == position) {
               dots[i].setImageDrawable(ContextCompat.getDrawable(context, R.drawable.dots_active));
           } else {
               dots[i].setImageDrawable(ContextCompat.getDrawable(context, R.drawable.dots_default));
           }

           LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
           params.setMargins(4, 0, 4, 0);
           dotsLayout.addView(dots[i], params);
       }
   }

    public void createDotsNews(int position, LinearLayout dotsLayout, int limit){
        if(dotsLayout != null){
            dotsLayout.removeAllViews();
        }
        ImageView[] dots = new ImageView[limit];
        for (int i = 0; i < limit; i++) {
            dots[i] = new ImageView(context);
            if (i == position) {
                dots[i].setImageDrawable(ContextCompat.getDrawable(context, R.drawable.dots_active));
            } else {
                dots[i].setImageDrawable(ContextCompat.getDrawable(context, R.drawable.dots_default));
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(4, 0, 4, 0);

            dotsLayout.addView(dots[i], params);
        }
    }
}