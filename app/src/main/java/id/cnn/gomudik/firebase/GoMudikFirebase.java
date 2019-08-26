package id.cnn.gomudik.firebase;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import id.cnn.gomudik.firebase.firebase_model.Firebase_addUserUnseenMessage;
import id.cnn.gomudik.firebase.firebase_model.Firebase_login;
import id.cnn.gomudik.firebase.firebase_model.Firebase_notif;
import id.cnn.gomudik.gomudik_user_and_group_package.model.Chat;
import id.cnn.gomudik.gomudik_user_and_group_package.model.Login;
import id.cnn.gomudik.util.DifferenceTime;
import retrofit2.Response;

public class GoMudikFirebase {
    private static final String TAG = "GoMudikFirebase";

    public GoMudikFirebase() { }

    public void loginAddUser(final Response<Login> response, final String token) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("user").child(response.body().getData().getUsers_id());
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Firebase_login addUser = new Firebase_login(response.body().getData().getUsers_id(), token);
                FirebaseDatabase.getInstance().getReference("user").child(response.body().getData().getUsers_id()).setValue(addUser);

/*
                if (!dataSnapshot.exists()) {
                    Firebase_login addUser = new Firebase_login(response.body().getData().getUsers_id(),token);
                    FirebaseDatabase.getInstance().getReference("user").child(response.body().getData().getUsers_id()).setValue(addUser);
                }
*/
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: " + databaseError.getMessage());
            }
        });
        db.onDisconnect();
    }

    public void loginAddNotif(final String id) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("user_notification").child(id);
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    Firebase_notif notif = new Firebase_notif(0, id);
                    FirebaseDatabase.getInstance().getReference("user_notification").child(id).setValue(notif);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: " + databaseError.getMessage());
            }
        });
        db.onDisconnect();
    }

    public void getTotalNotif(final String id, final TextView textView, final RelativeLayout badge) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("user_notification").child(id).child("total_notif");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    int notif = dataSnapshot.getValue(Integer.class);
                    if(notif == 0){
                        badge.setVisibility(View.INVISIBLE);
                    } else {
                        badge.setVisibility(View.VISIBLE);
                        if(notif >= 100) {
                            String string = String.valueOf(99)+"+";
                            textView.setText(string);
                        } else {
                            textView.setText(String.valueOf(notif));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: "+databaseError.getMessage());
            }
        });
    }

    public void getTotalMessage(final String id, final TextView textView, final RelativeLayout badge) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("group_chat_unseen_message");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    int notif = 0;
                    for (DataSnapshot x : dataSnapshot.getChildren()) {
                        //where user not in group
                        if (x.child(id).child("total_notif").getValue() != null) {
                            notif = notif + x.child(id).child("total_notif").getValue(Integer.class);
                            Log.d(TAG, "onDataChange: group" + x.getKey());
                        }
                    }
                    Log.d(TAG, "onDataChange: " + notif);
                    if (notif == 0) {
                        badge.setVisibility(View.INVISIBLE);
                    } else {
                        badge.setVisibility(View.VISIBLE);
                        if (notif >= 100) {
                            String string = String.valueOf(99) + "+";
                            textView.setText(string);
                        } else {
                            textView.setText(String.valueOf(notif));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: "+databaseError.getMessage());
            }
        });
    }

    public void updateNotif(final String id) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("user_notification").child(id);
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    Firebase_notif notif = new Firebase_notif(1, id);
                    FirebaseDatabase.getInstance().getReference("user_notification").child(id).setValue(notif);
                } else {
                    Firebase_notif x = dataSnapshot.getValue(Firebase_notif.class);
                    int newCount = x.getTotal_notif()+1;
                    FirebaseDatabase.getInstance().getReference("user_notification").child(id).child("total_notif").setValue(newCount);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: " + databaseError.getMessage());
            }
        });
        db.onDisconnect();
    }

    public void addUserUnseenGroupMessage(String id, String id_chat_room) {
        Firebase_addUserUnseenMessage add = new Firebase_addUserUnseenMessage(0, id);
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("group_chat_unseen_message");
        db.child(id_chat_room).child(id).setValue(add);
        db.onDisconnect();
    }

    public List<Firebase_login> getGroupMember(String id_chat_room, final String id) {
        final List<Firebase_login> member = new ArrayList<>();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("group_chat_unseen_message").child(id_chat_room);
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dt : dataSnapshot.getChildren()) {
                        if (!id.equalsIgnoreCase(dt.getKey())){
                            final String member_id = dt.getKey();
                            DatabaseReference db2 = FirebaseDatabase.getInstance().getReference("user").child(member_id).child("token");
                            db2.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                                    member.add(new Firebase_login(member_id,dataSnapshot2.getValue(String.class)));
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError2) {
                                    Log.d(TAG, "onCancelled: "+databaseError2.getMessage());
                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: " + databaseError.getMessage());
            }
        });
        return member;
    }

    public void resetUserNotification(final String id){
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("user_notification").child(id).child("total_notif");
        db.setValue(0);
        db.onDisconnect();
    }

    public void updateUserSeenGroupMessage(final String id, final String id_chat_room) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("group_chat_unseen_message").child(id_chat_room).child(id).child("total_notif");
        db.setValue(0);
        db.onDisconnect();
    }

    public void updateUserUnseenGroupMessage(final List<String> member, final String id_chat_room) {
        final int limit = member.size();
        if (limit > 0) {
            for (int i = 0; i < limit; i++){
                final int i_ = i;
                DatabaseReference db = FirebaseDatabase.getInstance().getReference("group_chat_unseen_message").child(id_chat_room).child(member.get(i)).child("total_notif");
                db.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            int x = dataSnapshot.getValue(Integer.class) + 1;
                            FirebaseDatabase.getInstance().getReference("group_chat_unseen_message").child(id_chat_room).child(member.get(i_)).child("total_notif").setValue(x);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d(TAG, "onCancelled: "+databaseError.getMessage());
                    }
                });
            }
        }
    }

    public void getLastMessage(String group, final TextView textView, final TextView chatDate) {
        Query q2 = FirebaseDatabase.getInstance().getReference("group_chat").child(group).orderByKey().limitToLast(1);
        q2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String mLastMessage = "default";
                String mMessageDate = "";
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Chat chat = ds.getValue(Chat.class);
                        mLastMessage = chat.getMessage();
                        DifferenceTime dt = new DifferenceTime(chat.getDate());
                        dt.executeTimeChat();
                        mMessageDate = dt.getResultDefault();
                    }
                }
                switch (mLastMessage) {
                    case "default":
                        textView.setText("Start chat with your group");
                        chatDate.setVisibility(View.INVISIBLE);
                        break;
                    default:
                        textView.setText(mLastMessage);
                        chatDate.setVisibility(View.VISIBLE);
                        chatDate.setText(mMessageDate);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: " + databaseError.getMessage());
            }
        });
    }

    public void countBadge(final RelativeLayout badge, final TextView countBadge, final String group, final String id){
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("group_chat_unseen_message").child(group).child(id).child("total_notif");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    int x = dataSnapshot.getValue(Integer.class);
                    if(x == 0){
                        badge.setVisibility(View.INVISIBLE);
                    } else {
                        badge.setVisibility(View.VISIBLE);
                        if(x >= 100) {
                            String string = String.valueOf(99)+"+";
                            countBadge.setText(string);
                        } else {
                            countBadge.setText(String.valueOf(x));
                        }
                    }
                } else {
                    badge.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: "+databaseError.getMessage());
            }
        });
    }

    public void userLeaveGroup(String id, String id_chat_room){
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("group_chat_unseen_message").child(id_chat_room).child(id);
        db.removeValue();
        db.onDisconnect();
    }
}