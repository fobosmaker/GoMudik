package id.cnn.gomudik.gomudik_user_and_group_package.model;

import java.io.Serializable;

public class ListChatContent implements Serializable {
    private String name;
    private String email;
    private String photo;
    public ListChatContent(String name, String email, String photo){
        this.name = name;
        this.email = email;
        this.photo = photo;
    }
    public String getName() {
        return name;
    }
    public String getEmail() {
        return email;
    }
    public String getPhoto() {
        return photo;
    }
}
