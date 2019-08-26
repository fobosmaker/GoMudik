package id.cnn.gomudik.notification.model;

import java.util.List;

public class RequestNotification {
    private List<String> registration_ids;
    private Notification notification;
    private Data data;

    public RequestNotification(List<String> registration_ids, Notification notification, Data data){
        this.registration_ids = registration_ids;
        this.notification = notification;
        this.data = data;
    }

    public Data getData() {
        return data;
    }

    public List<String> getRegistration_ids() {
        return registration_ids;
    }

    public Notification getNotification() {
        return notification;
    }

    public static class Notification{
        private String title;
        private String body;

        public Notification(String title, String body){
            this.body = body;
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        public String getBody() {
            return body;
        }
    }

    public static class Data{
        private String group_id;
        private String group_name;
        private String group_image_link;
        private String group_member;
        private String group_chat_room;

        public Data(String group_id, String group_name, String group_image_link, String group_member, String group_chat_room){
            this.group_id = group_id;
            this.group_name = group_name;
            this.group_image_link = group_image_link;
            this.group_member = group_member;
            this.group_chat_room = group_chat_room;
        }

        public String getGroup_id() {
            return group_id;
        }

        public String getGroup_name() {
            return group_name;
        }

        public String getGroup_member() {
            return group_member;
        }

        public String getGroup_image_link() {
            return group_image_link;
        }

        public String getGroup_chat_room() {
            return group_chat_room;
        }
    }
}
