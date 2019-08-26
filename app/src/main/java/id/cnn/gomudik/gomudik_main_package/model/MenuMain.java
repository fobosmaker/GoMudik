package id.cnn.gomudik.gomudik_main_package.model;

public class MenuMain {
    private Integer image;
    private String title;
    private String time;
    private Boolean isGPSOn;
    public MenuMain(Integer image, String title, String time, Boolean isGPSOn){
        this.image = image;
        this.title = title;
        this.time = time;
        this.isGPSOn = isGPSOn;
    }
    public Integer getImage() {
        return image;
    }
    public String getTime() {
        return time;
    }
    public String getTitle() {
        return title;
    }
    public Boolean getGPSOn() { return isGPSOn; }
}
