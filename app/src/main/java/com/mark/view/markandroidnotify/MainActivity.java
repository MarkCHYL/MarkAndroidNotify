package com.mark.view.markandroidnotify;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RemoteViews;


public class MainActivity extends AppCompatActivity {

    private NotificationManager manager;
    private NotificationManagerCompat managerCompat;
    private boolean isOpended = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boolean isOpened = getManagerCompat().areNotificationsEnabled();
        if (!isOpened) {
            // 根据isOpened结果，判断是否需要提醒用户跳转AppInfo页面，去打开App通知权限
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", this.getApplication().getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
        }

    }

    public NotificationManagerCompat getManagerCompat() {
        if (managerCompat == null) {
            managerCompat = NotificationManagerCompat.from(this);
        }
        return managerCompat;
    }

    public NotificationManager getManager() {
        if (manager == null) {
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }

    /**
     * 发送消息通知
     *
     * @param view
     */
    public void sendNotifyMessage(View view) {
        Notification.Builder builder = getNotificationBuilder();

        getManager().notify(1, builder.build());

    } //发送一个普通的通知，新增一个通知

    private Notification.Builder getNotificationBuilder() {

        /**
         * NotificationChannel只是安卓8.0才出现的新的API
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("channel_id",
                    "channel_name", NotificationManager.IMPORTANCE_DEFAULT);
            channel.canBypassDnd();//可绕过请勿打扰模式
            channel.setLockscreenVisibility(RECEIVER_VISIBLE_TO_INSTANT_APPS);//锁屏可见
            channel.canShowBadge();//显示角标
            channel.setShowBadge(true);//Android系统默认也是会显示角标的
            channel.setLightColor(Color.argb(100, 100, 100, 100));//闪光事的灯光颜色
            channel.enableLights(true);//是否闪光
            channel.enableVibration(true);//是否震动
            channel.getGroup();//通知渠道组
            channel.shouldShowLights();//是否应该有闪光
            channel.setVibrationPattern(new long[]{100, 100, 200});//震动模式
            channel.getAudioAttributes();//获取系统通知的响铃声音配置

            getManager().createNotificationChannel(channel);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return new Notification.Builder(this)
                        .setAutoCancel(true)
                        .setChannelId("channel_id")
                        .setContentText("周四的世界杯开幕，好开心！！！")
                        .setContentTitle("2018年世界杯")
                        .setSmallIcon(R.mipmap.notifyicon)
                        .setNumber(3)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.largeicon))
                        .setWhen(System.currentTimeMillis());
            } else {
                return new Notification.Builder(this)
                        .setAutoCancel(true)
                        .setContentText("周四的世界杯开幕，好开心！！！")
                        .setContentTitle("2018年世界杯")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setNumber(2)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.largeicon))
                        .setWhen(System.currentTimeMillis());
            }

        }
        return null;
    }


    /**
     * 带进度条的
     *
     * @param view
     */
    public void sendNotifyProgressBar(View view) {
        final Notification.Builder builder = getNotificationBuilder();
        builder.setNumber(Notification.FLAG_ONLY_ALERT_ONCE);
        getManager().notify(2, builder.build());

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 100; i++) {
                    try {
                        Thread.sleep(100);
                        builder.setDefaults(Notification.FLAG_ONLY_ALERT_ONCE);
                        builder.setProgress(100, i, false);
                        getManager().notify(2, builder.build());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    } //发送一个带进度条的通知，对通知进行更新

    /**
     * 自定义布局的样式的通知栏
     *
     * @param view
     */
    public void customNotify(View view) {
        Notification.Builder builder = getNotificationBuilder();

        RemoteViews remoteViews = new RemoteViews(getPackageName(),R.layout.notify_layout_item);
        remoteViews.setTextViewText(R.id.title,"世界杯来了");
        remoteViews.setTextViewText(R.id.content,"嗨起来！兄弟们！！");

        Intent intent = new Intent(this,CustomActivity.class);
        /**
         * PendingIntent 表示将要发生的意图，他是可以被取消的
         */
        PendingIntent pendingIntent = PendingIntent.getActivity(this,-1,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.turn_layout,pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            builder.setCustomBigContentView(remoteViews);
            getManager().notify(3,builder.build());
        }
    }

    /**
     * 删除一个通知
     * @param view
     */
    public void deletemNotification(View view){
        getManager().cancel(3);
    }
}
