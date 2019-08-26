package id.cnn.gomudik.notification.model;

import com.google.gson.annotations.SerializedName;

public class ResponseNotification {
    @SerializedName("multicast_id")
    private Float multicast_id;
    @SerializedName("success")
    private Integer success;
    @SerializedName("failure")
    private Integer failure;
    @SerializedName("canonical_ids")
    private Integer canonical_ids;
    @SerializedName("results")
    private Results results;

    public ResponseNotification(Float multicast_id, Integer success, Integer failure, Integer canonical_ids, Results results){
        this.multicast_id = multicast_id;
        this.success = success;
        this.failure = failure;
        this.canonical_ids = canonical_ids;
        this.results = results;
    }

    public class Results{ }
}
