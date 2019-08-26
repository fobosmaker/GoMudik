package id.cnn.gomudik.firebase.firebase_model;


import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Firebase_addSingleUserToGroup {
    private String users_id;
    private String id_chat_room;
    public Firebase_addSingleUserToGroup(){

    }
    public Firebase_addSingleUserToGroup(String users_id, String id_chat_room){
        this.users_id = users_id;
        this.id_chat_room = id_chat_room;
    }
    public String getUsers_id() { return users_id; }
    public String getId_chat_room() { return id_chat_room; }
}