package com.bug.saqrag.update;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

class UpdateNotification {

    private final Context mContext;
    private final int mId;
    private final NotificationManager mNm;
    private NotificationCompat.Builder builder;
    private final String channel = "update";

    UpdateNotification(Context context, int id) {
        mContext = context;
        mId = id;
        mNm = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
    }


    void showCustomizeNotification(int icoId, int smallIconId) {
        if (mContext == null) return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (mNm.getNotificationChannel(channel) == null) {
                setNotificationChannel(mContext);
            }
        }
        builder = new NotificationCompat.Builder(mContext, channel)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icoId < 1 ? R.drawable.launcher : icoId))
                .setSmallIcon(smallIconId < 1 ? R.drawable.launcher : smallIconId)
                .setContentTitle(mContext.getString(R.string.update_downloading))
                .setContentText(String.valueOf(0) + "%")
                .setWhen(System.currentTimeMillis())
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setSound(null)
                .setVibrate(new long[]{0})
                .setShowWhen(true)
                .setDefaults(0)
                .setProgress(100, 0, false);
        mNm.notify(mId, builder.build());
    }

    void changeProgressStatus(int p) {
        android.app.Notification not = builder.setProgress(100, p, false)
                .setContentText(String.valueOf(p) + "%")
                .build();
        not.flags = android.app.Notification.FLAG_NO_CLEAR;
        mNm.notify(mId, not);
    }

    void removeNotification() {
        mNm.cancel(mId);
    }


    private void setNotificationChannel(Context context) {
        if (android.os.Build.VERSION.SDK_INT < 26) {
            return;
        }
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        CharSequence name = context.getString(R.string.app_name);
        String description = context.getString(R.string.channel_description_update);
        int importance = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            importance = NotificationManager.IMPORTANCE_MAX;
        }
        NotificationChannel mChannel = new NotificationChannel(channel, name, importance);
        mChannel.setDescription(description);
        mChannel.enableLights(false);
        mChannel.enableVibration(false);
        mChannel.setSound(null, null);
        if (mNotificationManager != null) {
            mNotificationManager.createNotificationChannel(mChannel);
        }
    }
}
