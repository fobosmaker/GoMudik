package id.cnn.gomudik.gomudik_main_package.model;

public class MenuButtonModel {
    private String id;
    private String content;
    private Integer icon;

    public MenuButtonModel(String id, String content, Integer icon){
        this.id = id;
        this.content = content;
        this.icon = icon;
    }

    public String getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public Integer getIcon() {
        return icon;
    }
}
