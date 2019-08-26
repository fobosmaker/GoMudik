package id.cnn.gomudik.gomudik_user_and_group_package.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ListChatGroup implements Serializable {
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
        private String id;
        @SerializedName("name")
        private String group_name;
        @SerializedName("description")
        private String group_member;
        @SerializedName("image_link")
        private String group_image;
        @SerializedName("code")
        private String code;
        @SerializedName("created_by")
        private String created_by;
        public Data(String id, String group_name, String group_member, String group_image, String code, String created_by){
            this.id = id;
            this.group_name = group_name;
            this.group_image = group_image;
            this.group_member = group_member;
            this.code = code;
            this.created_by = created_by;
        }

        public String getId() {
            return id;
        }

        public String getGroup_name() {
            return group_name;
        }

        public String getGroup_image() {
            return group_image;
        }

        public String getGroup_member() {
            return group_member;
        }

        public String getCode() { return code; }

        public String getCreated_by() { return created_by; }
    }
}