package id.cnn.gomudik.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SendNotificationAPI {
    public static final String BASE_URL = "https://fcm.googleapis.com/";
    public static Retrofit retrofit = null;

    public static Retrofit getNotificationAPI()
    {
        if(retrofit == null)
        {
            retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create()).build();
        }
        return retrofit;
    }
}
