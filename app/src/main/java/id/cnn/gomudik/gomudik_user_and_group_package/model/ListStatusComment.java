package id.cnn.gomudik.gomudik_user_and_group_package.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ListStatusComment {
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
    public static class Data implements Serializable {
        private String id_status_comment;
        private String id_users;
        private String users_name;
        private String users_email;
        private String created;
        private String users_image_link;
        private String content;

        public Data(String id_status_comment, String id_users, String users_name, String users_email, String created, String users_image_link, String content){
            this.id_status_comment = id_status_comment;
            this.id_users = id_users;
            this.users_name = users_name;
            this.users_email = users_email;
            this.created = created;
            this.users_image_link = users_image_link;
            this.content = content;
        }

        public String getId_status_comment() {
            return id_status_comment;
        }

        public String getId_users() {
            return id_users;
        }

        public String getUsers_name() {
            return users_name;
        }

        public String getUsers_email() {
            return users_email;
        }

        public String getUsers_image_link() {
            return users_image_link;
        }

        public String getContent() {
            return content;
        }

        public String getCreated() {
            return created;
        }
    }
}