package id.cnn.gomudik.gomudik_user_and_group_package.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import okhttp3.MultipartBody;

public class AddUserStatus {
    @SerializedName("id_status")
    @Expose
    private MultipartBody.Part id_status;
    @SerializedName("id_user")
    @Expose
    private MultipartBody.Part id_user;
    @SerializedName("id_status_privacy")
    @Expose
    private MultipartBody.Part id_status_privacy;
    @SerializedName("latitude")
    @Expose
    private MultipartBody.Part latitude;
    @SerializedName("longitude")
    @Expose
    private MultipartBody.Part longitude;
    @SerializedName("address")
    @Expose
    private MultipartBody.Part address;
    @SerializedName("image_file")
    @Expose
    private MultipartBody.Part image_file;
    @SerializedName("content")
    @Expose
    private MultipartBody.Part content;

    public AddUserStatus(MultipartBody.Part id_status, MultipartBody.Part id_user, MultipartBody.Part id_status_privacy, MultipartBody.Part latitude, MultipartBody.Part longitude, MultipartBody.Part address, MultipartBody.Part image_file,MultipartBody.Part content){
        this.id_status = id_status;
        this.id_user = id_user;
        this.id_status_privacy = id_status_privacy;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.image_file = image_file;
        this.content = content;
    }

    public MultipartBody.Part getId_status() {
        return id_status;
    }

    public MultipartBody.Part getId_user() {
        return id_user;
    }

    public MultipartBody.Part getId_status_privacy() {
        return id_status_privacy;
    }

    public MultipartBody.Part getLatitude() {
        return latitude;
    }

    public MultipartBody.Part getLongitude() {
        return longitude;
    }

    public MultipartBody.Part getAddress() {
        return address;
    }

    public MultipartBody.Part getImage_file() {
        return image_file;
    }

    public MultipartBody.Part getContent() {
        return content;
    }
}
