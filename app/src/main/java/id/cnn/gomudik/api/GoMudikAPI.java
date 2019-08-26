package id.cnn.gomudik.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GoMudikAPI {
    public static final String BASE_URL = "http://gomudik.id:81/api/";
    public static Retrofit retrofit = null;
    public static Retrofit getAPI()
    {
        if(retrofit == null)
        {
            retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create()).build();
        }
        return retrofit;
    }
}
