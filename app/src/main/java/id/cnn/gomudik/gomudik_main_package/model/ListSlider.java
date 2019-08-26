package id.cnn.gomudik.gomudik_main_package.model;

public class ListSlider {
    private Integer image;
    private Integer color_background;

    public ListSlider(Integer image, Integer color_background){
        this.image = image;
        this.color_background = color_background;
    }

    public Integer getImage() {
        return image;
    }

    public Integer getColor_background() { return color_background; }
}
