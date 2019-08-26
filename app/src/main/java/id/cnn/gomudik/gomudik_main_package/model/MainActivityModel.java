package id.cnn.gomudik.gomudik_main_package.model;

import id.cnn.gomudik.gomudik_ads.model.GetAds;
import id.cnn.gomudik.gomudik_user_and_group_package.model.ListStatus;

public class MainActivityModel {
    private GetNews news;
    private ListStatus status;
    private GetAds ads;
    public MainActivityModel(GetNews news, ListStatus status, GetAds ads){
        this.news = news;
        this.status = status;
        this.ads = ads;
    }

    public GetNews getNews() {
        return news;
    }

    public ListStatus getStatus() {
        return status;
    }

    public GetAds getAds() {
        return ads;
    }
}
