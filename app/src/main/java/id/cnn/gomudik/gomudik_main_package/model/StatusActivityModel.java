package id.cnn.gomudik.gomudik_main_package.model;

import id.cnn.gomudik.gomudik_ads.model.GetAds;
import id.cnn.gomudik.gomudik_user_and_group_package.model.ListStatus;

public class StatusActivityModel {
    private ListStatus status;
    private GetAds ads;
    public StatusActivityModel(ListStatus status, GetAds ads){
        this.status = status;
        this.ads = ads;
    }
    public ListStatus getStatus() {
        return status;
    }
    public GetAds getAds() {
        return ads;
    }
}
