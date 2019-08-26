package id.cnn.gomudik.api;
import id.cnn.gomudik.gomudik_main_package.model.MapsGetNearby;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;
public interface MapsInterface {
    @GET
    Call<MapsGetNearby> getNearby(@Url String url);
}
