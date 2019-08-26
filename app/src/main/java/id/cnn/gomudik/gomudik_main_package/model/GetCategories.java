package id.cnn.gomudik.gomudik_main_package.model;
import com.google.gson.annotations.SerializedName;
import java.util.List;
public class GetCategories {
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
        @SerializedName("content")
        private String content;
        @SerializedName("type")
        private String type;
        @SerializedName("keyword")
        private String keyword;
        public Data(String id, String content, String type, String keyword) {
            this.id = id;
            this.content = content;
            this.type = type;
            this.keyword = keyword;
        }
        public String getId() {
            return id;
        }
        public String getContent() { return content; }
        public String getType() { return type; }
        public String getKeyword() { return keyword; }
    }
}