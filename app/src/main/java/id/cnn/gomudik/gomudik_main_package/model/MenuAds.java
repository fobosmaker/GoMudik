package id.cnn.gomudik.gomudik_main_package.model;

public class MenuAds {
   private String image_link;
   private String id_active;
    public MenuAds(String image_link, String id_active){
        this.image_link = image_link;
        this.id_active = id_active;
    }
    public String getImage_link() {
        return image_link;
    }

    public String getId_active() {
        return id_active;
    }
}
