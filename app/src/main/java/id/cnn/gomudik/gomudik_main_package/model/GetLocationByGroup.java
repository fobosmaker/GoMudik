package id.cnn.gomudik.gomudik_main_package.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class GetLocationByGroup implements Serializable{
    @SerializedName("data")
    private List<Data> data;
    @SerializedName("total_data")
    private Integer total_data;
    public List<Data> getData() { return data; }
    public Integer getTotal_data() {
        return total_data;
    }
    public static class Data implements Serializable{
        @SerializedName("id")
        private String id;
        @SerializedName("fullname")
        private String fullname;
        @SerializedName("email")
        private String email;
        @SerializedName("users_image_link")
        private String users_image_link;
        @SerializedName("latitude")
        private String latitude;
        @SerializedName("longitude")
        private String longitude;
        @SerializedName("distance")
        private String distance;
        @SerializedName("created")
        private String created;
        public Data(String id, String fullname, String email, String users_image_link, String latitude, String longitude, String distance, String created) {
            this.id = id;
            this.fullname = fullname;
            this.email = email;
            this.users_image_link = users_image_link;
            this.latitude = latitude;
            this.longitude = longitude;
            this.distance = distance;
            this.created = created;
        }
        public String getId() {
            return id;
        }

        public String getFullname() {
            return fullname;
        }

        public String getEmail() {
            return email;
        }

        public String getUsers_image_link() {
            return users_image_link;
        }

        public String getLatitude() { return latitude; }

        public String getLongitude() { return longitude; }

        public String getDistance() { return distance; }

        public String getCreated() { return created; }
    }
}