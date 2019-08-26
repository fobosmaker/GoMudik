package id.cnn.gomudik.gomudik_main_package.model;

public class YoutubeGetPlaylistError {
    private String id_error;
    private String content;

    public YoutubeGetPlaylistError(String id_error, String content){
        this.id_error = id_error;
        this.content = content;
    }
}
