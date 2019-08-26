package id.cnn.gomudik.gomudik_main_package.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class YoutubeGetPlaylist {
    @SerializedName("kind")
    private String kind;
    @SerializedName("etag")
    private String etag;
    @SerializedName("nextPageToken")
    private String nextPageToken;
    @SerializedName("pageInfo")
    private PageInfo pageInfo;
    @SerializedName("items")
    private List<Items> items;
    public List<Items> getItems() {
        return items;
    }
    public void setKind(String kind) {
        this.kind = kind;
    }
    public void setEtag(String etag) {
        this.etag = etag;
    }
    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }
    public void setItems(List<Items> items) {
        this.items = items;
    }
    public void setPageInfo(PageInfo pageInfo) { this.pageInfo = pageInfo; }
    public String getKind() {
        return kind;
    }
    public String getEtag() { return  etag; }
    public String getNextPageToken() { return  nextPageToken; }
    public PageInfo getPageInfo() {
        return pageInfo;
    }
    public class PageInfo {
        @SerializedName("totalResults")
        private int totalResults;
        @SerializedName("resultsPerPage")
        private int resultsPerPage;
        public PageInfo(Integer totalResults, Integer resultsPerPage){
            this.totalResults = totalResults;
            this.resultsPerPage = resultsPerPage;
        }
        public Integer getTotalResults() { return totalResults; }
        public Integer getResultsPerPage() {
            return resultsPerPage;
        }
        public void setResultsPerPage(Integer resultsPerPage) {
            this.resultsPerPage = resultsPerPage;
        }
        public void setTotalResults(Integer totalResults) {
            this.totalResults = totalResults;
        }
    }
    public static class Items {
        @SerializedName("kind")
        private String kind;
        @SerializedName("etag")
        private String etag;
        @SerializedName("id")
        private String id;
        @SerializedName("snippet")
        private Snippet snippet;
        @SerializedName("status")
        private Status status;
        public Items(String kind, String etag,String id, Snippet snippet,Status status){
            this.kind = kind;
            this.etag = etag;
            this.id = id;
            this.snippet = snippet;
            this.status = status;
        }
        public String getKind() { return kind; }
        public String getEtag() { return etag; }
        public String getId() { return id; }
        public Snippet getSnippet() { return snippet; }
        public Status getStatus() { return status; }
    }
    public class Snippet {
        @SerializedName("publishedAt")
        private String publishedAt;
        @SerializedName("channelId")
        private String channelId;
        @SerializedName("title")
        private String title;
        @SerializedName("description")
        private String description;
        @SerializedName("thumbnails")
        private Thumbnails thumbnails;
        @SerializedName("channelTitle")
        private String channelTitle;
        @SerializedName("pplaylistId")
        private String playlistId;
        @SerializedName("position")
        private Integer position;
        @SerializedName("resourceId")
        private ResourceId resourceId;
        public Snippet(String publishedAt, String channelId, String title, String description, Thumbnails thumbnails, String channelTitle, String playlistId, Integer position, ResourceId resourceId){
            this.publishedAt = publishedAt;
            this.channelId = channelId;
            this.title = title;
            this.description = description;
            this.thumbnails = thumbnails;
            this.channelTitle = channelTitle;
            this.playlistId = playlistId;
            this.position = position;
            this.resourceId = resourceId;
        }
        public String getPublishedAt() {
            return publishedAt;
        }
        public String getChannelId() {
            return channelId;
        }
        public String getTitle() {
            return title;
        }
        public String getDescription() {
            return description;
        }
        public Thumbnails getThumbnails() {
            return thumbnails;
        }
        public String getChannelTitle() {
            return channelTitle;
        }
        public String getPlaylistId() {
            return playlistId;
        }
        public Integer getPosition() {
            return position;
        }
        public ResourceId getResourceId() {
            return resourceId;
        }
    }
    public class Status{
        @SerializedName("privacyStatus")
        private String privacyStatus;
        public Status(String privacyStatus){
            this.privacyStatus = privacyStatus;
        }
        public String getPrivacyStatus() {
            return privacyStatus;
        }
    }
    public class ResourceId {
        @SerializedName("kind")
        private String kind;
        @SerializedName("videoId")
        private String videoId;
        public ResourceId(String kind, String videoId){
            this.kind = kind;
            this.videoId = videoId;
        }
        public String getKind() {
            return kind;
        }
        public String getVideoId() {
            return videoId;
        }
    }
    public class Thumbnails {
        @SerializedName("default")
        Default mdefault;
        @SerializedName("medium")
        Medium medium;
        @SerializedName("high")
        High high;
        public Thumbnails(Default xdefault, Medium xmedium, High xhigh){
            this.mdefault = xdefault;
            this.medium = xmedium;
            this.high = xhigh;
        }
        public Default getMdefault() {
            return mdefault;
        }
        public Medium getMedium() {
            return medium;
        }
        public High getHigh() {
            return high;
        }
    }
    public class Default {
        private String url;
        private Integer width;
        private Integer height;
        public Default(String url, Integer width, Integer height){
            this.url = url;
            this.width = width;
            this.height = height;
        }
        public String getUrl() {
            return url;
        }
        public Integer getHeight() {
            return height;
        }
        public Integer getWidth() {
            return width;
        }
    }
    public class Medium {
        private String url;
        private Integer width;
        private Integer height;
        public Medium(String url, Integer width, Integer height){
            this.url = url;
            this.width = width;
            this.height = height;
        }
        public String getUrl() {
            return url;
        }
        public Integer getHeight() {
            return height;
        }
        public Integer getWidth() {
            return width;
        }
    }
    public class High {
        private String url;
        private Integer width;
        private Integer height;
        public High(String url, Integer width, Integer height){
            this.url = url;
            this.width = width;
            this.height = height;
        }
        public String getUrl() {
            return url;
        }
        public Integer getHeight() {
            return height;
        }
        public Integer getWidth() {
            return width;
        }
    }
}
