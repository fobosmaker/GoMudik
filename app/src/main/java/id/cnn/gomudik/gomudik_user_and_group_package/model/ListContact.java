package id.cnn.gomudik.gomudik_user_and_group_package.model;

import java.util.List;

public class ListContact {
    private List<Data> data_friend;
    private Integer total_data_friend;
    private List<Data> data_invited;
    private Integer total_data_invited;
    private List<Data> data_group;
    private Integer total_data_group;

    public ListContact(List<Data> data_friend, Integer total_data_friend, List<Data> data_invited, Integer total_data_invited,List<Data> data_group, Integer total_data_group){
        this.data_friend = data_friend;
        this.total_data_friend = total_data_friend;
        this.data_group = data_group;
        this.total_data_group = total_data_group;
        this.data_invited = data_invited;
        this.total_data_invited = total_data_invited;
    }

    public List<Data> getData_friend() {
        return data_friend;
    }

    public Integer getTotal_data_friend() {
        return total_data_friend;
    }

    public List<Data> getData_group() {
        return data_group;
    }

    public Integer getTotal_data_group() {
        return total_data_group;
    }

    public List<Data> getData_invited() { return data_invited; }

    public Integer getTotal_data_invited() { return total_data_invited; }

    public class Data{
        private String id;
        private String name;
        private String description;
        private String image_link;
        private String code;

        public Data(String id, String name, String description, String image_link, String code){
            this.id = id;
            this.name = name;
            this.description = description;
            this.image_link = image_link;
            this.code = code;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public String getImage_link() {
            return image_link;
        }

        public String getCode() { return code; }
    }
}
