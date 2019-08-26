package id.cnn.gomudik.gomudik_user_and_group_package.model;

import com.google.gson.annotations.SerializedName;

public class Login {
    @SerializedName("data")
    private Data data;
    @SerializedName("message")
    private String message;
    @SerializedName("token")
    private String token;
    @SerializedName("total_data")
    private Integer total_data;
    public Login(Data data, String message, String token, Integer total_data){
        this.data = data;
        this.message = message;
        this.token = token;
        this.total_data = total_data;
    }
    public Data getData() { return data; }
    public String getMessage() { return message; }
    public Integer getTotal_data() { return total_data; }
    public String getToken() { return token; }
    public class Data{
        @SerializedName("users_id")
        private String users_id;
        @SerializedName("users_username")
        private String users_username;
        @SerializedName("users_password")
        private String users_password;
        @SerializedName("users_private_key")
        private String users_private_key;
        @SerializedName("users_name")
        private String users_name;
        @SerializedName("users_email")
        private String users_email;
        @SerializedName("users_level_id")
        private String users_level_id;
        @SerializedName("users_ip_address")
        private String users_ip_address;
        @SerializedName("users_last_login")
        private String users_last_login;
        @SerializedName("users_active_id")
        private String users_active_id;
        @SerializedName("users_lastModifiedBy")
        private String users_lastModifiedBy;
        @SerializedName("users_lastModifiedOn")
        private String users_lastModifiedOn;
        @SerializedName("users_image_link")
        private String users_image_link;
        public Data(String users_id, String users_username, String users_password, String users_private_key, String users_name, String users_email, String users_level_id, String users_ip_address, String users_last_login, String users_active_id, String users_lastModifiedBy, String users_lastModifiedOn, String users_image_link ){
            this.users_id = users_id;
            this.users_username = users_username;
            this.users_password = users_password;
            this.users_private_key = users_private_key;
            this.users_name = users_name;
            this.users_email = users_email;
            this.users_level_id = users_level_id;
            this.users_ip_address = users_ip_address;
            this.users_last_login = users_last_login;
            this.users_active_id = users_active_id;
            this.users_lastModifiedBy = users_lastModifiedBy;
            this.users_lastModifiedOn = users_lastModifiedOn;
            this.users_image_link = users_image_link;
        }
        public String getUsers_id() { return users_id; }
        public String getUsers_username() { return users_username; }
        public String getUsers_password() { return users_password; }
        public String getUsers_private_key() { return users_private_key; }
        public String getUsers_name() { return users_name; }
        public String getUsers_email() { return users_email; }
        public String getUsers_level_id() { return users_level_id; }
        public String getUsers_active_id() { return users_active_id; }
        public String getUsers_ip_address() { return users_ip_address; }
        public String getUsers_last_login() { return users_last_login; }
        public String getUsers_lastModifiedBy() { return users_lastModifiedBy; }
        public String getUsers_lastModifiedOn() { return users_lastModifiedOn; }
        public String getUsers_image_link() { return users_image_link; }
    }
}