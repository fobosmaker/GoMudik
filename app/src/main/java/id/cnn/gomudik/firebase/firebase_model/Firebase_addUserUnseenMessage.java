package id.cnn.gomudik.firebase.firebase_model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Firebase_addUserUnseenMessage {
    private String users_id;
    private Integer total_notif;
    public Firebase_addUserUnseenMessage(){

    }
    public Firebase_addUserUnseenMessage(Integer total_notif, String users_id){
        this.users_id = users_id;
        this.total_notif = total_notif;
    }
    public String getUsers_id() { return users_id; }
    public Integer getTotal_notif() {
        return total_notif;
    }
}