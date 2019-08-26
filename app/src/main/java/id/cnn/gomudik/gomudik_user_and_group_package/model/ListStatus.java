package id.cnn.gomudik.gomudik_user_and_group_package.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ListStatus{
    @SerializedName("data")
    private List<Data> data;
    @SerializedName("total_data")
    private Integer total_data;
    @SerializedName("all_data")
    private Integer all_data;

    public List<Data> getData() {
        return data;
    }
    public Integer getTotal_data() {
        return total_data;
    }
    public Integer getAll_data() {
        return all_data;
    }
    public class Data implements Serializable {
        @SerializedName("id_status")
        private String id_status;
        @SerializedName("id_users")
        private String id_users;
        @SerializedName("users_name")
        private String users_name;
        @SerializedName("users_email")
        private String users_email;
        @SerializedName("created")
        private String created;
        @SerializedName("users_image_link")
        private String users_image_link;
        @SerializedName("address")
        private String address;
        @SerializedName("image_link")
        private String image_link;
        @SerializedName("content")
        private String content;
        @SerializedName("love_count")
        private String love_count;
        @SerializedName("latitude")
        private String latitude;
        @SerializedName("longitude")
        private String longitude;
        @SerializedName("total_comment")
        private String total_comment;
        @SerializedName("id_status_privacy")
        private String id_status_privacy;

        public Data(String id_status, String id_users, String users_name, String users_email, String created, String users_image_link, String address, String image_link, String content, String love_count, String latitude, String longitude, String total_comment, String id_status_privacy){
            this.id_status = id_status;
            this.id_users = id_users;
            this.users_name = users_name;
            this.users_email = users_email;
            this.created = created;
            this.users_image_link = users_image_link;
            this.address = address;
            this.image_link = image_link;
            this.content = content;
            this.love_count = love_count;
            this.latitude = latitude;
            this.longitude = longitude;
            this.total_comment = total_comment;
            this.id_status_privacy = id_status_privacy;
        }

        public String getId_status() {
            return id_status;
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

        public String getImage_link() {
            return image_link;
        }

        public String getUsers_image_link() {
            return users_image_link;
        }

        public String getAddress() {
            return address;
        }

        public String getContent() {
            return content;
        }

        public String getCreated() {
            return created;
        }

        public String getLove_count() {
            return love_count;
        }

        public String getLatitude() { return latitude; }

        public String getLongitude() { return longitude; }

        public String getTotal_comment() { return total_comment; }

        public String getId_status_privacy() { return id_status_privacy; }
    }
}