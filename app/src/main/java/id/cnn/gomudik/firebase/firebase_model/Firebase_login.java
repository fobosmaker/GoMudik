package id.cnn.gomudik.firebase.firebase_model;

public class Firebase_login {
    private String users_id;
    private String token;
    public  Firebase_login(){

    }
    public Firebase_login(String users_id, String token ){
        this.users_id = users_id;
        this.token = token;
    }
    public String getUsers_id() { return users_id; }
    public String getToken() { return token; }
}