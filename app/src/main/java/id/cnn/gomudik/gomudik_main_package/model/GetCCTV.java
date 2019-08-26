package id.cnn.gomudik.gomudik_main_package.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class GetCCTV implements Serializable{
    @SerializedName("data")
    private List<Data> data;
    @SerializedName("total_data")
    private Integer total_data;
    public List<Data> getData() { return data; }
    public Integer getTotal_data() {
        return total_data;
    }
    public static class Data implements Serializable{
        @SerializedName("content")
        private String content;
        @SerializedName("image_link")
        private String image_link;
        @SerializedName("source")
        private String source;
        public Data(String content, String image_link, String source) {
            this.content = content;
            this.image_link = image_link;
            this.source = source;
        }

        public String getContent() {
            return content;
        }

        public String getImage_link() {
            return image_link;
        }

        public String getSource() { return source; }

    }
}