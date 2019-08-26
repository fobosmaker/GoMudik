package id.cnn.gomudik.gomudik_main_package.model;

public class MenuProfile {
    private String id;
    private String icon;
    private String username;
    private String email;
    private Boolean isLogin;
    public MenuProfile(String id, String username, String email, String icon, Boolean isLogin){
        this.icon = icon;
        this.username = username;
        this.email = email;
        this.isLogin = isLogin;
        this.id = id;
    }

    public String getId() { return id; }

    public String getIcon() {
        return icon;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public Boolean getLogin() {
        return isLogin;
    }
}
