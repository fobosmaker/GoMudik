package id.cnn.gomudik.gomudik_main_package.model;

import id.cnn.gomudik.gomudik_ads.model.GetAds;
import id.cnn.gomudik.gomudik_user_and_group_package.model.ListStatus;

public class CCTVActivityModel {
    private GetCCTV cctv;
    private GetAds ads;
    public CCTVActivityModel(GetCCTV cctv, GetAds ads){
        this.cctv = cctv;
        this.ads = ads;
    }
    public GetCCTV getCCTV() {
        return cctv;
    }
    public GetAds getAds() {
        return ads;
    }
}
