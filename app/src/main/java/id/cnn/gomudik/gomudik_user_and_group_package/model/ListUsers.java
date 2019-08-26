package id.cnn.gomudik.gomudik_user_and_group_package.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ListUsers implements Serializable {
    @SerializedName("data")
    private List<Data> data;
    @SerializedName("total_data")
    private Integer total_data;

    public List<Data> getData() {
        return data;
    }
    public Integer getTotal_data() {
        return total_data;
    }

    public static class Data implements Serializable{
        @SerializedName("id")
        private String users_id;
        @SerializedName("name")
        private String users_name;
        @SerializedName("description")
        private String users_email;
        @SerializedName("image_link")
        private String users_image_link;
        public Data(String users_id, String users_name, String users_email, String users_image_link){
            this.users_id = users_id;
            this.users_email = users_email;
            this.users_name = users_name;
            this.users_image_link = users_image_link;
        }
        public String getUsers_id() { return users_id; }
        public String getUsers_email() { return users_email; }
        public String getUsers_name() {
            return users_name;
        }
        public String getUsers_image_link() { return users_image_link; }
    }
}