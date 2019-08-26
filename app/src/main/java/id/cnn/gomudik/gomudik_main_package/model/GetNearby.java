package id.cnn.gomudik.gomudik_main_package.model;
import com.google.gson.annotations.SerializedName;
import java.util.List;
public class GetNearby {
    @SerializedName("data")
    private List<Data> data;
    @SerializedName("total_data")
    private Integer total_data;
    public List<Data> getData() { return data; }
    public Integer getTotal_data() {
        return total_data;
    }
    public class Data {
        @SerializedName("id")
        private String id;
        @SerializedName("name")
        private String name;
        @SerializedName("address")
        private String address;
        @SerializedName("telephone")
        private String telephone;
        @SerializedName("latitude")
        private Double latitude;
        @SerializedName("longitude")
        private Double longitude;
        @SerializedName("distance")
        private Double distance;
        public Data(String id, String name, String address, String telephone, Double latitude, Double longitude,Double distance) {
            this.id = id;
            this.name = name;
            this.address = address;
            this.telephone = telephone;
            this.latitude = latitude;
            this.longitude = longitude;
            this.distance = distance;
        }
        public String getId() {
            return id;
        }
        public String getName() {
            return name;
        }
        public String getAddress() {
            return address;
        }
        public String getTelephone() {
            return telephone;
        }
        public Double getLatitude() {
            return latitude;
        }
        public Double getLongitude() {
            return longitude;
        }
        public Double getDistance() {
            return distance;
        }
    }
}