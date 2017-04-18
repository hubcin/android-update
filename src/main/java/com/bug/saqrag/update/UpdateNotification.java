package com.bug.saqrag.update;

import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

class UpdateNotification {

    private final Context mContext;
    private final int mId;
    private final NotificationManager mNm;
    private NotificationCompat.Builder builder;

    UpdateNotification(Context context, int id) {
        mContext = context;
        mId = id;
        mNm = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
    }


    void showCustomizeNotification(int icoId, int smallIconId) {
        if (mContext == null) return;
        builder = new NotificationCompat.Builder(mContext)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icoId < 1 ? R.drawable.launcher : icoId))
                .setSmallIcon(smallIconId < 1 ? R.drawable.launcher : smallIconId)
                .setContentTitle(mContext.getString(R.string.download_now))
                .setContentText(String.valueOf(0) + "%")
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


}