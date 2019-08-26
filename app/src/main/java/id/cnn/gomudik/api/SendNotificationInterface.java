package id.cnn.gomudik.api;

import id.cnn.gomudik.notification.model.RequestNotification;
import id.cnn.gomudik.notification.model.ResponseNotification;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface SendNotificationInterface {
    @Headers({
            "Content-Type: application/json",
            "Authorization: key=AAAAaZuZJi0:APA91bG6XJ4FmIQS47lDnX9hmbfzdM6gAfdtHelg802LPJgiG_O7eFtL8E0iu1Vx54xWUDsgkWPy7n1SU641CNChvR0kizoVUB3YERM1_IHhX4OvGVo1mGWStK2pZo-hWJhnyfdtW3eH"
    })
    @POST("fcm/send")
    Call<ResponseNotification> send(@Body RequestNotification body);
}
