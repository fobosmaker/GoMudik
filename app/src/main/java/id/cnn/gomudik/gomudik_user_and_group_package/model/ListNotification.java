package id.cnn.gomudik.gomudik_user_and_group_package.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ListNotification implements Serializable {
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
    public Integer getAll_data() { return all_data; }

    public static class Data implements Serializable{
        private String id;
        private String created;
        private String requester;
        private String requester_image;
        private String requested;
        private String content;
        private String type;
        private String id_status;
        public Data(String id, String created,String requester, String requester_image, String requested, String content, String type, String id_status){
            this.id = id;
            this.requester = requester;
            this.requester_image = requester_image;
            this.requested = requested;
            this.content = content;
            this.created = created;
            this.type = type;
            this.id_status = id_status;
        }

        public String getId() {
            return id;
        }

        public String getRequested() {
            return requested;
        }

        public String getRequester() {
            return requester;
        }

        public String getRequester_image() {
            return requester_image;
        }

        public String getContent() {
            return content;
        }

        public String getCreated() {
            return created;
        }

        public String getType() { return type; }

        public String getId_status() { return id_status; }
    }
}