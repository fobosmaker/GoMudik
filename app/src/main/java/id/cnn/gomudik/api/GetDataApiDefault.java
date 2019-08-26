package id.cnn.gomudik.api;

import com.google.gson.annotations.SerializedName;

public class GetDataApiDefault {
    @SerializedName("status")
    private String status;
    @SerializedName("message")
    private String message;
    @SerializedName("is_success")
    private Boolean is_success;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Boolean getIs_success () { return is_success; }
}
