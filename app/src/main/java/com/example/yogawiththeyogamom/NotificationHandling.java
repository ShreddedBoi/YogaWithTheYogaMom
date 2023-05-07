package com.example.yogawiththeyogamom;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class NotificationHandling {
    private static final String CHANNEL_ID = "shop_notification_channel";
    private final int NOTIFICATION_ID = 0;
    private NotificationManager mHandler;
    private Context mcontext;
    public NotificationHandling(Context context){
        this.mcontext =context;
        this.mHandler = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        createChannel();
    }
    private void createChannel(){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            return;

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Shop Notification", NotificationManager.IMPORTANCE_DEFAULT);
        channel.enableLights(true);
        channel.enableVibration(true);
        channel.setLightColor(Color.WHITE);
        channel.setDescription("Notification from Yoga app");
        this.mHandler.createNotificationChannel(channel);
    }

    public void send(String message){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mcontext, CHANNEL_ID)
                .setContentTitle("Yoga app")
                .setContentText(message)
                .setSmallIcon(R.drawable.basket);

        this.mHandler.notify(NOTIFICATION_ID, builder.build());
    }
}
