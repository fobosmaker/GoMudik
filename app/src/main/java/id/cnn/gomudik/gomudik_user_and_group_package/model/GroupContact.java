package id.cnn.gomudik.gomudik_user_and_group_package.model;
public class GroupContact{
    private String id,name,description,image_link,code;
    private Integer type_data, type_content;
    public GroupContact(String id,String name, String description ,String image_link,String code,Integer type_data, Integer type_content){
        this.id = id;
        this.name = name;
        this.image_link = image_link;
        this.description = description;
        this.type_data = type_data;
        this.type_content = type_content;
        this.code = code;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getId() {
        return id;
    }
    public String getDescription() {
        return description;
    }
    public String getImage_link() {
        return image_link;
    }
    public Integer getType_content() {
        return type_content;
    }
    public Integer getType_data() {
        return type_data;
    }
    public String getCode() {
        return code;
    }
}