package id.cnn.gomudik.gomudik_user_and_group_package.model;

public class Chat {
    private String users_id;
    private String users_name;
    private String date;
    private String message;

    public Chat(){

    }

    public Chat(String date, String message, String users_id, String users_name){
        this.users_name = users_name;
        this.date = date;
        this.message = message;
        this.users_id = users_id;
    }

    public String getDate() {
        return date;
    }

    public String getMessage() {
        return message;
    }

    public String getUsers_name() { return users_name; }

    public String getUsers_id() { return users_id; }
}
