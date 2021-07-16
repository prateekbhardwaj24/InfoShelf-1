package com.card.infoshelf.sendPushNotification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.content.res.ResourcesCompat;

import com.card.infoshelf.InternalNotificationActivity;
import com.card.infoshelf.MainActivity;
import com.card.infoshelf.Messaging.MessagingActivity;
import com.card.infoshelf.R;
import com.card.infoshelf.postDetailsActivity;
import com.card.infoshelf.userProfileActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Random;

public class pushService extends FirebaseMessagingService {

    PendingIntent pendingIntent, pendingIntent1;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getData().size()>0){

            Map<String, String> map = remoteMessage.getData();
            String title = map.get("title");
            String message = map.get("body");
            String type = map.get("type");
            String pid = map.get("pid");
            String sender = map.get("sender");

            if (Build.VERSION.SDK_INT>Build.VERSION_CODES.O){
                createOreoNotification(title, message, pid, sender, remoteMessage, type);
            }
            else {
                createNormalNotification(title, message, pid, sender, remoteMessage, type);
            }
        }
    }

    private void createNormalNotification(String title, String message, String pid, String sender, RemoteMessage remoteMessage, String type){
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "1000");
//        int resourceImage = getResources().getIdentifier(remoteMessage.getNotification().getIcon(), "drawable", getPackageName());
        builder.setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.icon)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setColor(ResourcesCompat.getColor(getResources(), R.color.primary, null))
                .setSound(uri);


        if (type.equals("LikeC") || type.equals("ReplyC")){

            Intent intent = new Intent(this, InternalNotificationActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        }

        else if (type.equals("Request")){

            Intent intent = new Intent(this, userProfileActivity.class);
            intent.putExtra("userid", remoteMessage.getData().get("sender"));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        }

        else if (type.equals("chat")){

            String state = remoteMessage.getData().get("state");
            String receiverId = remoteMessage.getData().get("sender1");

            Intent intent = new Intent(this, MessagingActivity.class);
            intent.putExtra("userid", receiverId);
            intent.putExtra("state", state);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        }

        else if (type.equals("sharedPost")){

            String receiverId = remoteMessage.getData().get("sender");

            Intent intent = new Intent(this, MessagingActivity.class);
            intent.putExtra("userid", receiverId);
            intent.putExtra("state", "Saved");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

            if (!remoteMessage.getData().get("imageUrl").equals("none")) {
                Bitmap bitmap = getBitmapfromUrl(String.valueOf(remoteMessage.getData().get("imageUrl")));
                builder.setStyle(
                        new NotificationCompat.BigPictureStyle()
                                .bigPicture(bitmap)
                                .bigLargeIcon(null)
                );
            }

        }

        else {

            Intent intent = new Intent(this, postDetailsActivity.class);
            intent.putExtra("pId", remoteMessage.getData().get("pid"));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

            if (!remoteMessage.getData().get("imageUrl").equals("none")) {
                Bitmap bitmap = getBitmapfromUrl(String.valueOf(remoteMessage.getData().get("imageUrl")));
                builder.setStyle(
                        new NotificationCompat.BigPictureStyle()
                                .bigPicture(bitmap)
                                .bigLargeIcon(null)
                );
            }
        }

        builder.setContentIntent(pendingIntent);

        if (remoteMessage.getData().get("userProfile") != null){
            Bitmap imageBitmap = getImageBitmap(remoteMessage.getData().get("userProfile"));
            builder.setLargeIcon(imageBitmap);
        }

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(new Random().nextInt(85-65), builder.build());

    }

    private Bitmap getImageBitmap(String userProfile) {
        try {
            URL url = new URL(userProfile);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);

            final Bitmap output = Bitmap.createBitmap(myBitmap.getWidth(),
                    myBitmap.getHeight(), Bitmap.Config.ARGB_8888);
            final Canvas canvas = new Canvas(output);

            final int color = Color.RED;
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, myBitmap.getWidth(), myBitmap.getHeight());
            final RectF rectF = new RectF(rect);

            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawOval(rectF, paint);

            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(myBitmap, rect, rect, paint);

            myBitmap.recycle();

            return output;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void createOreoNotification(String title, String message, String pid, String sender, RemoteMessage remoteMessage, String type){

        if (Build.VERSION.SDK_INT>Build.VERSION_CODES.O){

            NotificationChannel channel = new NotificationChannel("1000", "postLike", NotificationManager.IMPORTANCE_HIGH);
            channel.setShowBadge(true);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setDescription("postLike Description");
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);


            Notification.Builder notification = new Notification.Builder(this, "1000");
            notification.setContentTitle(title)
                    .setContentText(message)
                    .setColor(ResourcesCompat.getColor(getResources(), R.color.primary, null))
                    .setSmallIcon(R.drawable.icon)
                    .setAutoCancel(true)
                    .setStyle(new Notification.BigTextStyle().bigText(message));
//                .build();


            if (type.equals("LikeC") || type.equals("ReplyC")){

                Intent intent = new Intent(this, InternalNotificationActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            }else if (type.equals("Request")){

                Intent intent = new Intent(this, userProfileActivity.class);
                intent.putExtra("userid", remoteMessage.getData().get("sender"));
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

            }else if (type.equals("chat")){

                String state = remoteMessage.getData().get("state");
                String receiverId = remoteMessage.getData().get("sender1");

                Intent intent = new Intent(this, MessagingActivity.class);
                intent.putExtra("userid", receiverId);
                intent.putExtra("state", state);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

            }

            else if (type.equals("sharedPost")){

                String receiverId = remoteMessage.getData().get("sender");

                Intent intent = new Intent(this, MessagingActivity.class);
                intent.putExtra("userid", receiverId);
                intent.putExtra("state", "Saved");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

                if (!remoteMessage.getData().get("imageUrl").equals("none")){
                    Bitmap bitmap = getBitmapfromUrl(String.valueOf(remoteMessage.getData().get("imageUrl")));
                    notification.setStyle(new Notification.BigPictureStyle().bigPicture(bitmap));
                }

            }

            else {
                Intent intent = new Intent(this, postDetailsActivity.class);
                intent.putExtra("pId", remoteMessage.getData().get("pid"));
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

                if (!remoteMessage.getData().get("imageUrl").equals("none")){
                    Bitmap bitmap = getBitmapfromUrl(String.valueOf(remoteMessage.getData().get("imageUrl")));
                    notification.setStyle(new Notification.BigPictureStyle().bigPicture(bitmap));
                }
            }

            notification.setContentIntent(pendingIntent);

            if (remoteMessage.getData().get("userProfile") != null){
                Bitmap imageBitmap = getImageBitmap(remoteMessage.getData().get("userProfile"));
                notification.setLargeIcon(imageBitmap);
            }


            manager.notify(new Random().nextInt(85-65), notification.build());

        }


    }

    public static Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
