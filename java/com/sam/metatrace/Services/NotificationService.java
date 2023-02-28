package com.sam.metatrace.Services;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.sam.metatrace.Fragments.ChatWithOneFriendFragment;
import com.sam.metatrace.MainPage;
import com.sam.metatrace.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

// 注意在创建新的服务的时候需要到android manifest文件中注册服务
// 用来创建常驻通知栏的运行图标服务
public class NotificationService extends IntentService {

    private NotificationCompat.Builder notification;
    private Notification.Builder notificationCompat;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public NotificationService(){
        super("notificationIntent");
    }
    public static NotificationService context;

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        // 自动开启新的线程进行操作
        while (true) {
            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            updateNotificationMessage("MetaTrace", "您有"+HttpClient.unreadMessagesCount+"条未读消息");

        }
    }

    @SuppressLint("WrongConstant")
    public static void makeOnePopUpNotification(String title, String content) {
        if(context != null){
            NotificationCompat.Builder notification;
            Notification.Builder notificationCompat;
            Intent intent = new Intent(context, MainPage.class);
            int NOTIFICATION_ID = new Random().nextInt(255)+1;
            PendingIntent pi = PendingIntent.getActivity(context, NOTIFICATION_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            String CHANNEL_ID = "Pop Up Msg";
            String CHANNEL_NAME = "pop_up_message";
            NotificationChannel notificationChannel;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationChannel = new NotificationChannel(CHANNEL_ID,
                        CHANNEL_NAME, NotificationManager.IMPORTANCE_MAX);
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.RED);
                notificationChannel.setShowBadge(true);
                notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
                manager.createNotificationChannel(notificationChannel);

                notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setContentTitle(title)
                        .setContentText(content)
                        .setWhen(System.currentTimeMillis())
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(content)) // 221113 new
                        .setSmallIcon(R.drawable.ic_notification)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_notification))
                        .setContentIntent(pi);
                /*
                 * 设置notification在前台展示
                 */
                if(title.equals("server")) return;
                ChatWithOneFriendFragment.intentUsername = title;
                System.out.println("NOTIFICATION_ID:" + NOTIFICATION_ID);
                context.startForeground(NOTIFICATION_ID, notification.build());

            } else {
                notificationCompat = new Notification.Builder(context)
                        .setContentTitle(title)
                        .setContentText(content)
                        .setWhen(System.currentTimeMillis())
                        .setStyle(new Notification.BigTextStyle().bigText(content))
                        .setSmallIcon(R.drawable.ic_notification)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_notification))
                        .setContentIntent(pi);
                /*
                 * 设置notification在前台展示
                 */
                if(title.equals("server")) return;
                ChatWithOneFriendFragment.intentUsername = title;
                NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
                manager.notify(NOTIFICATION_ID, notificationCompat.build());
            }
        }

    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        /*
         * 创建Notification
         */
        Intent intent = new Intent(this, MainPage.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
        String CHANNEL_ID = "channel_id_01";
        String CHANNEL_NAME = "channel_name_test";
        int NOTIFICATION_ID = 1;
        NotificationChannel notificationChannel;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(CHANNEL_ID,
                    CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setShowBadge(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(notificationChannel);

            notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("MetaTrace")
                    .setContentText("🙂")
                    .setSmallIcon(R.drawable.ic_notification)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_notification))
                    .setContentIntent(pi);
            /*
             * 设置notification在前台展示
             */
            startForeground(NOTIFICATION_ID, notification.build());
        } else {
            notificationCompat = new Notification.Builder(this)
                    .setContentTitle("MetaTrace")
                    .setContentText("🙂")
                    .setSmallIcon(R.drawable.ic_notification)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_notification))
                    .setContentIntent(pi);
            /*
             * 设置notification在前台展示
             */
            startForeground(NOTIFICATION_ID, notificationCompat.build());
        }
    }

    public void updateNotificationMessage(String title, String context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            notification.setContentTitle(title);
            notification.setContentText(context).setWhen(System.currentTimeMillis());
            startForeground(1, notification.build());
        }else{
            notificationCompat.setContentTitle(title);
            notificationCompat.setContentText(context).setWhen(System.currentTimeMillis());
            startForeground(1, notificationCompat.build());
        }

    }
}
