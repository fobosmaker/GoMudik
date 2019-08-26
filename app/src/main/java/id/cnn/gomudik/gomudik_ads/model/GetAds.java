package id.cnn.gomudik.gomudik_ads.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetAds {
    @SerializedName("data")
    private List<Data> data;
    @SerializedName("total_data")
    private Integer total_data;
    public List<Data> getData() { return data; }
    public Integer getTotal_data() {
        return total_data;
    }
    public class Data {
        @SerializedName("id_mobile_ads")
        private String id_mobile_ads;
        @SerializedName("id_mobile_activity")
        private String id_mobile_activity;
        @SerializedName("id_mobile_ads_position")
        private String id_mobile_ads_position;
        @SerializedName("image_link")
        private String image_link;
        @SerializedName("id_active")
        private String id_active;
        public Data(String id_mobile_ads, String id_mobile_activity, String id_mobile_ads_position, String image_link, String id_active) {
            this.id_mobile_ads = id_mobile_ads;
            this.id_mobile_activity = id_mobile_activity;
            this.id_mobile_ads_position = id_mobile_ads_position;
            this.image_link = image_link;
            this.id_active= id_active;
        }

        public String getId_mobile_ads() {
            return id_mobile_ads;
        }

        public String getId_mobile_activity() {
            return id_mobile_activity;
        }

        public String getId_mobile_ads_position() {
            return id_mobile_ads_position;
        }

        public String getImage_link() {
            return image_link;
        }

        public String getId_active() {
            return id_active;
        }
    }
}