package id.cnn.gomudik.gomudik_main_package.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetNews {
    @SerializedName("data")
    private List<Data> data;
    @SerializedName("total_data")
    private Integer total_data;
    @SerializedName("all_data")
    private Integer all_data;
    public List<Data> getData() { return data; }
    public Integer getTotal_data() {
        return total_data;
    }
    public Integer getAll_data() { return all_data; }
    public class Data {
        @SerializedName("id_news")
        private String id_news;
        @SerializedName("created")
        private String created;
        @SerializedName("channel_title")
        private String channel_title;
        @SerializedName("video_id")
        private String video_id;
        @SerializedName("title")
        private String title;
        @SerializedName("url")
        private String url;
        @SerializedName("published_at")
        private String published_at;
        public Data(String id_news, String created, String channel_title, String video_id, String title, String url, String published_at) {
            this.id_news = id_news;
            this.created = created;
            this.channel_title = channel_title;
            this.video_id = video_id;
            this.title = title;
            this.url = url;
            this.published_at = published_at;
        }

        public String getId_news() {
            return id_news;
        }

        public String getCreated() {
            return created;
        }

        public String getChannel_title() {
            return channel_title;
        }

        public String getVideo_id() {
            return video_id;
        }

        public String getTitle() {
            return title;
        }

        public String getUrl() {
            return url;
        }

        public String getPublished_at() {
            return published_at;
        }
    }
}