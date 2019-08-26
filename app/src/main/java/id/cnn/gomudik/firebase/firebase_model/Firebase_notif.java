package id.cnn.gomudik.firebase.firebase_model;


import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Firebase_notif {
    public String users_id;
    public Integer total_notif;

    public Firebase_notif() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Firebase_notif(Integer total_notif, String users_id){
        this.users_id = users_id;
        this.total_notif = total_notif;
    }

    public String getUsers_id() {
        return users_id;
    }

    public Integer getTotal_notif() {
        return total_notif;
    }
}