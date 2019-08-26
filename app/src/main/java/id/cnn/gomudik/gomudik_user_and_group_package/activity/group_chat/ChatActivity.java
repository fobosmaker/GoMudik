package id.cnn.gomudik.gomudik_user_and_group_package.activity.group_chat;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.annotations.Nullable;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import id.cnn.gomudik.api.GetDataApiDefault;
import id.cnn.gomudik.api.GoMudikAPI;
import id.cnn.gomudik.api.GoMudikInterface;
import id.cnn.gomudik.api.SendNotificationAPI;
import id.cnn.gomudik.api.SendNotificationInterface;
import id.cnn.gomudik.firebase.firebase_model.Firebase_login;
import id.cnn.gomudik.gomudik_user_and_group_package.adapter.ChatAdapter;
import id.cnn.gomudik.gomudik_user_and_group_package.model.Chat;
import id.cnn.gomudik.R;
import id.cnn.gomudik.firebase.GoMudikFirebase;
import id.cnn.gomudik.notification.model.RequestNotification;
import id.cnn.gomudik.notification.model.ResponseNotification;
import id.cnn.gomudik.util.LoadingProgress;
import id.cnn.gomudik.util.SharedPreferenceCheckerActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends SharedPreferenceCheckerActivity {
    private RecyclerView cage;
    private List<Firebase_login> member = new ArrayList<>();
    private String id_chat_room, id_group, group_image, group_name, group_member, group_created_by;
    private ArrayList<Chat> data = new ArrayList<>();
    private ChatAdapter adapter;
    private GoMudikFirebase gfb = new GoMudikFirebase();
    private static final String TAG = "ChatActivity";
    private DialogFragment loadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        invalidateOptionsMenu();
        loadingDialog = new LoadingProgress();
        if(getIntent().getStringExtra("group_name") != null && getIntent().getStringExtra("group_members") != null && getIntent().getStringExtra("group_created_by") != null ){
            getSupportActionBar().setTitle("");
            CircleImageView groupLogo = findViewById(R.id.group_image);
            TextView groupName = findViewById(R.id.group_name);
            TextView groupDetail = findViewById(R.id.group_member);
            group_name = getIntent().getStringExtra("group_name");
            group_member = getIntent().getStringExtra("group_members");
            groupName.setText(group_name);
            groupDetail.setText(group_member);
            group_image = getIntent().getStringExtra("group_image");
            group_created_by = getIntent().getStringExtra("group_created_by");
            if(group_image == null){
                Picasso.get().load(R.drawable.no_photo).fit().centerInside().placeholder(R.drawable.no_photo).error(R.drawable.no_photo).into(groupLogo);
            } else {
                Uri uri = Uri.parse("http://gomudik.id:81".concat(group_image.substring(1)));
                Picasso.get().load(uri).fit().centerInside().placeholder(R.drawable.no_photo).error(R.drawable.no_photo).into(groupLogo);
            }
            id_chat_room = getIntent().getStringExtra("id_chat_room");
            id_group = getIntent().getStringExtra("id_group");
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        cage = findViewById(R.id.cage);
        cage.setLayoutManager(linearLayoutManager);
        cage.setHasFixedSize(true);
        adapter = new ChatAdapter(data,getCurrId());
        cage.setAdapter(adapter);

        //get member
        member = gfb.getGroupMember(id_chat_room,getCurrId());

        //update seen message by user
        gfb.updateUserSeenGroupMessage(getCurrId(),id_chat_room);

        //get list chat
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("group_chat").child(id_chat_room);
        Query q = db.limitToLast(10);
        q.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Chat x = dataSnapshot.getValue(Chat.class);
                data.add(x);
                adapter.notifyDataSetChanged();
                cage.smoothScrollToPosition(adapter.getItemCount()-1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final EditText chat = findViewById(R.id.input_chat);
        final ImageButton send = findViewById(R.id.send_button);
        send.setEnabled(false);
        chat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable str) {
                if(str.toString().trim().length()>0){
                    send.setEnabled(true);
                }else{
                    send.setEnabled(false);
                }
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String dateNowString = dateFormat.format(Calendar.getInstance().getTime());
                Chat addChat = new Chat(dateNowString, chat.getText().toString(),getCurrId(),getCurrName());
                FirebaseDatabase.getInstance().getReference("group_chat").child(id_chat_room).push().setValue(addChat);
                updateUnseen();
                sendNotifications(getCurrName(),chat.getText().toString());
                chat.getText().clear();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_group_chat,menu);
        MenuItem menuItem = menu.findItem(R.id.action_invite);
        if(!getCurrId().equalsIgnoreCase(group_created_by)){
            menuItem.setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        startActivity(new Intent(ChatActivity.this,ListChatActivity.class));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_invite:
                Intent intentInvite = new Intent(ChatActivity.this, InviteActivity.class);
                intentInvite.putExtra("group_name", group_name);
                intentInvite.putExtra("group_id",id_group);
                startActivityForResult(intentInvite,300);
                break;
            case R.id.action_group_detail:
                Intent intent = new Intent(ChatActivity.this, GomudikGroupActivity.class);
                intent.putExtra("id_group",id_group);
                intent.putExtra("group_image",group_image);
                intent.putExtra("group_name",group_name);
                startActivity(intent);
                break;
            case R.id.action_leave:
                final AlertDialog.Builder dialog = new AlertDialog.Builder(ChatActivity.this);
                dialog.setTitle("Leave group")
                        .setMessage("Are you sure to leave this group?")
                        .setPositiveButton("Leave", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                //Toast.makeText(ChatActivity.this, "Leaving group...", Toast.LENGTH_SHORT).show();
                                leaveGroup();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                            }
                        });
                dialog.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == 300) {
            //Toast.makeText(ChatActivity.this,"Invite Success",Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onActivityResult: invite success");
        }
    }

    public void leaveGroup(){
        loadingDialog.show(getSupportFragmentManager(),"Leave group");
        GoMudikInterface goMudikInterface = GoMudikAPI.getAPI().create(GoMudikInterface.class);
        Call<GetDataApiDefault> call = goMudikInterface.leaveGroup(getCurrToken(),id_group,getCurrId());
        call.enqueue(new Callback<GetDataApiDefault>() {
            @Override
            public void onResponse(Call<GetDataApiDefault> call, Response<GetDataApiDefault> response) {
                if(response.isSuccessful()){
                    if(response.body().getIs_success()){
                        new GoMudikFirebase().userLeaveGroup(getCurrId(),id_chat_room);
                        loadingDialog.dismiss();
                        Toast.makeText(ChatActivity.this,""+response.body().getMessage(),Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(new Intent(ChatActivity.this,ListChatActivity.class));

                    } else {
                        loadingDialog.dismiss();
                        Toast.makeText(ChatActivity.this,"Leave group failed!",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    loadingDialog.dismiss();
                    Toast.makeText(ChatActivity.this,"Failed: to operate leave group",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GetDataApiDefault> call, Throwable t) {
                loadingDialog.dismiss();
                Toast.makeText(ChatActivity.this,"Failed: "+t.toString(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateUnseen(){
        int limit = member.size();
        if(limit > 0){
            List<String> user_id = new ArrayList<>();
            for(int i = 0; i < limit; i++){
                user_id.add(member.get(i).getUsers_id());
            }
            Log.d(TAG, "updateUnseen: "+user_id.size());
            gfb.updateUserUnseenGroupMessage(user_id,id_chat_room);
        }
    }

    public void sendNotifications(String name, String chat){
        int limit = member.size();
        if(limit > 0){
            List<String> user_token = new ArrayList<>();
            for(int i = 0; i < limit; i++){
                user_token.add(member.get(i).getToken());
            }
            Log.d(TAG, "sendNotifications: "+user_token.size());
            String detail = name+": "+chat;
            RequestNotification.Notification notif = new RequestNotification.Notification(group_name,detail);
            RequestNotification.Data data = new RequestNotification.Data(id_group,group_name,group_image,group_member,id_chat_room);
            RequestNotification req = new RequestNotification(user_token,notif,data);

            SendNotificationInterface intr = SendNotificationAPI.getNotificationAPI().create(SendNotificationInterface.class);
            Call<ResponseNotification> call = intr.send(req);
            call.enqueue(new Callback<ResponseNotification>() {
                @Override
                public void onResponse(Call<ResponseNotification> call, Response<ResponseNotification> response) {
                    Log.d(TAG, "onResponse: success send");
                }

                @Override
                public void onFailure(Call<ResponseNotification> call, Throwable t) {
                    Log.d(TAG, "onFailure: failure caused: "+t.toString());
                }
            });
        }
    }
}