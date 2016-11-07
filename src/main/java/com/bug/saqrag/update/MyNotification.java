package com.bug.saqrag.update;

/**
 * Created by saqrag on 3/23/2016.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.widget.RemoteViews;

/**
 * Notification类，既可用系统默认的通知布局，也可以用自定义的布局
 *
 * @author lz
 */
public class MyNotification {
    public final static int DOWNLOAD_COMPLETE = -2;
    public final static int DOWNLOAD_FAIL = -1;
    Context mContext;   //Activity或Service上下文
    Notification notification;  //notification
    NotificationManager nm;
    String titleStr;   //通知标题
    String contentStr; //通知内容
//    PendingIntent contentIntent; //点击通知后的动作
    int notificationID;   //通知的唯一标示ID
    int iconID;         //通知栏图标
    long when = System.currentTimeMillis();
    RemoteViews remoteView = null;  //自定义的通知栏视图

    /**
     * @param context       Activity或Service上下文
     * @param id            通知的唯一标示ID
     */
    public MyNotification(Context context, int id) {
        // TODO Auto-generated constructor stub
        mContext = context;
        notificationID = id;
//        this.contentIntent = contentIntent;
        this.nm = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    /**
     * 显示自定义通知
     *
     * @param icoId    自定义视图中的图片ID
     * @param titleStr 通知栏标题
     * @param layoutId 自定义布局文件ID
     */
    public void showCustomizeNotification(int icoId, String titleStr, int layoutId) {
        this.titleStr = titleStr;
        notification = new Notification(icoId, titleStr, when);
        notification.flags = Notification.FLAG_ONLY_ALERT_ONCE;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
//        notification.contentIntent = this.contentIntent;

        // 1、创建一个自定义的消息布局 view.xml
        // 2、在程序代码中使用RemoteViews的方法来定义image和text。然后把RemoteViews对象传到contentView字段
        if (remoteView == null) {
            remoteView = new RemoteViews(mContext.getPackageName(), layoutId);
            remoteView.setImageViewResource(R.id.ivNotification, icoId);
            remoteView.setTextViewText(R.id.tvTitle, titleStr);
            remoteView.setTextViewText(R.id.tvTip, "0%");
            remoteView.setProgressBar(R.id.pbNotification, 100, 0, false);
            notification.contentView = remoteView;
        }
        nm.notify(notificationID, notification);
    }

    /**
     * 更改自定义布局文件中的进度条的值
     *
     * @param p 进度值(0~100)
     */
    public void changeProgressStatus(int p) {
        if (notification.contentView != null) {
            if (p == DOWNLOAD_FAIL) {
                notification.contentView.setTextViewText(R.id.tvTitle, "下载失败！ ");
            } else if (p == DOWNLOAD_COMPLETE) {
                notification.contentView.setTextViewText(R.id.tvTitle, "下载完成");
                notification.contentView.setTextViewText(R.id.tvTip, "" + 100 + "%");
                notification.contentView.setProgressBar(R.id.pbNotification, 100, 100, false);
            } else {
                notification.contentView.setTextViewText(R.id.tvTip, "" + p + "%");
                notification.contentView.setTextViewText(R.id.tvTitle, "正在下载");
                notification.contentView.setProgressBar(R.id.pbNotification, 100, p, false);
            }
        }
        nm.notify(notificationID, notification);
    }

//    public void changeContentIntent(PendingIntent intent) {
////        this.contentIntent = intent;
////        notification.contentIntent = intent;
//    }

    /**
     * 移除通知
     */
    public void removeNotification() {
        // 取消的只是当前Context的Notification
        nm.cancel(notificationID);
    }

}
