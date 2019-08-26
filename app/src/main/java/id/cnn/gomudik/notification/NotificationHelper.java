package id.cnn.gomudik.notification;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
//import android.content.res.Resources;
import android.graphics.Bitmap;
/*
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
*/
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
//import android.util.DisplayMetrics;

import com.squareup.picasso.Picasso;
//import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

/*import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;*/

import id.cnn.gomudik.R;
import id.cnn.gomudik.gomudik_user_and_group_package.activity.group_chat.ChatActivity;
import id.cnn.gomudik.notification.model.Data;
import id.cnn.gomudik.notification.model.Notification;


public class NotificationHelper {
    private static final String CHANNEL_ID ="GoMudik";
    public static void displayNotification(final Context context, Notification notif, Data data){
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("id_group",data.getGroup_id());
        intent.putExtra("id_chat_room",data.getGroup_chat_room());
        intent.putExtra("group_name",data.getGroup_name());
        intent.putExtra("group_members",data.getGroup_member());
        intent.putExtra("group_image",data.getGroup_image_link());

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                100,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT
                //PendingIntent.FLAG_UPDATE_CURRENT
        );

        //Bitmap bitmap = getBitmapFromURL("http://gomudik.id:81".concat(data.getGroup_image_link().substring(1)),context);
        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications)
                .setContentTitle(notif.getTitle())
                .setContentText(notif.getBody())
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setDefaults(android.app.Notification.DEFAULT_VIBRATE | android.app.Notification.DEFAULT_LIGHTS)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        /*Load image group with picasso from url group
        run in ui handler cause picasso cant run in main thread*/
        if(data.getGroup_image_link() != null) {
            final Uri uri = Uri.parse("http://gomudik.id:81".concat(data.getGroup_image_link().substring(1)));
            final Handler uiHandler = new Handler(Looper.getMainLooper());
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    Picasso.get().load(uri).resize(250,250).transform(new CircleImage()).centerInside().into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            mBuilder.setLargeIcon(bitmap);
                            buildNotification(mBuilder,context);
                        }

                        @Override
                        public void onBitmapFailed(Exception e, Drawable errorDrawable) { }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) { }
                    });
                }
            });
        } else {
            final Handler uiHandler = new Handler(Looper.getMainLooper());
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    Picasso.get().load(R.drawable.no_photo).resize(250,250).transform(new CircleImage()).centerInside().into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            mBuilder.setLargeIcon(bitmap);
                            buildNotification(mBuilder,context);
                        }

                        @Override
                        public void onBitmapFailed(Exception e, Drawable errorDrawable) { }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) { }
                    });
                }
            });
        }

    }

    public static void buildNotification(NotificationCompat.Builder builder, Context context){
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(1, builder.build());
    }

    /*private static Bitmap getBitmapFromURL(String strURL,Context context) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap circleImage = getCircleBitmap(BitmapFactory.decodeStream(input));
            float multiplier = getImageFactor(context.getResources());
            return Bitmap.createScaledBitmap(circleImage,(int)(circleImage.getWidth()*multiplier),(int)(circleImage.getWidth()*multiplier),false);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Bitmap getCircleBitmap(Bitmap bitmap) {
        int squareBitmap = Math.min(bitmap.getWidth(),
                bitmap.getHeight());
        final Bitmap output = Bitmap.createBitmap(squareBitmap,
                squareBitmap, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, squareBitmap, squareBitmap);
        final RectF rectF = new RectF(rect);
        float left = (float)(squareBitmap-bitmap.getWidth())/2;
        float top = (float) (squareBitmap-bitmap.getHeight())/2;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawOval(rectF, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, left, top, paint);
        bitmap.recycle();

        return output;
    }

    private static float getImageFactor(Resources r){
        DisplayMetrics metrics = r.getDisplayMetrics();
        return metrics.density/3f;
    }*/
}
