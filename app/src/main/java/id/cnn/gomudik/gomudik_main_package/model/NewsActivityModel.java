package id.cnn.gomudik.gomudik_main_package.model;

import id.cnn.gomudik.gomudik_ads.model.GetAds;
import id.cnn.gomudik.gomudik_user_and_group_package.model.ListStatus;

public class NewsActivityModel {
    private GetNews news;
    private GetAds ads;
    public NewsActivityModel(GetNews news, GetAds ads){
        this.news = news;
        this.ads = ads;
    }
    public GetNews getNews() { return news; }
    public GetAds getAds() {
        return ads;
    }
}
