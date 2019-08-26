package id.cnn.gomudik.gomudik_ads.model;

public class DialogAdsModel {
    private Integer imageAds;
    private String urlAds;

    public DialogAdsModel(Integer imageAds, String urlAds){
        this.imageAds = imageAds;
        this.urlAds = urlAds;
    }

    public Integer getImageAds() {
        return imageAds;
    }

    public String getUrlAds() {
        return urlAds;
    }
}
