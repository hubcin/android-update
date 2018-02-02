package com.bug.saqrag.update;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * saqrag
 */
public class UpdateUtil {
    public static final String UPDATE_URI = "URI";
    public static final String UPDATE_ICON = "resourceIcon";
    public static final String UPDATE_AUTO_ISTALL = "autoInstall";
    public static final String UPDATE_PATH = "updatePath";
    public static final String UPDATE_SMALL_ICON = "JJJJJJKDLSLKFJSL";
    public static final String UPDATE_IS_BACKGROUND = "UPDATE_IS_BACKGROUND";
    public static final String UPDATE_NOTIFICATION_CHANNEL_ID = "UPDATE_NOTIFICATION_CHANNEL_ID";


    /**
     * 广播接收者的Action 代表广播状态
     */
    public static final String DOWNLOAD_ACTION_COMPLETE = "DOWNLOAD_ACTION_COMPLETE";
    public static final String DOWNLOAD_ACTION_FIAL = "DOWNLOAD_ACTION_FIAL";
    public static final String DOWNLOAD_ACTION_NO_SD_CARD = "NO_SD_CARD";
    public static final String DOWNLOAD_ACTION_PROGRESS = "DOWNLOAD_ACTION_PROGRESS";
    public static final String DOWNLOAD_EXTRA_PROGRESS = "DOWNLOAD_EXTRA_PROGRESS";


    public static final int BACKGROUND = -1;
    public static String filePath;

    public static final String SHARE_NAME = "UPDATE_SHARE";
    public static final String SHARE_UPDATE_DOWNLENGTH = "SHARE_UPDATE_DOWNLENGTH";
    public static final String SHARE_UPDATE_FILELENGTH = "SHARE_UPDATE_FILELENGTH";
    public static final String SHARE_UPDATE_FILEPATH = "SHARE_UPDATE_FILEPATH";

    /**
     * 网络连接状态
     */
    public static final int NET_STATUS_ERR = -1;
    public static final int NET_STATUS_WIFI = 0;
    public static final int NET_STATUS_FLOW = 1;
    public static final int NET_STATUS_UNCONCECTED = 2;


    /**
     * 返回网络连接状态
     *
     * @param context
     * @return NET_STATUS_ERR  (出错),
     * NET_STATUS_WIFI (wifi连接状态),
     * NET_STATUS_FLOW  (流量链接状态),
     * NET_STATUS_UNCONCECTED  (没有网络连接状态)
     */
    public static int checkNet(Context context) {
        if (context == null) {
            return NET_STATUS_ERR;
        }

        ConnectivityManager mConnectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        //检查网络连接
        NetworkInfo info = mConnectivity.getActiveNetworkInfo();
        if (info == null || !mConnectivity.getBackgroundDataSetting()) {
            return NET_STATUS_UNCONCECTED;
        } else {

            int netType = info.getType();
            int netSubtype = info.getSubtype();

            if (netType == ConnectivityManager.TYPE_WIFI) {  //WIFI
                return NET_STATUS_WIFI;
            } else if (netType == ConnectivityManager.TYPE_MOBILE) {   //MOBILE
                return NET_STATUS_FLOW;
            }
        }
        return NET_STATUS_ERR;
    }

    /**
     * 后台下载app,不自动安装app,要Update.install(Context c)方法来完成安装。当下载完成或失败时发出广播，广播Action为静态变量：DOWNLOAD_ACTION_COMPLETE = "DOWNLOAD_ACTION_COMPLETE"，DOWNLOAD_ACTION_FIAL = "DOWNLOAD_ACTION_FIAL"，DOWNLOAD_ACTION_NO_SD_CARD = "NO_SD_CARD";
     */
    public static void update(Context context, String downUrl, String channelId) {
        update(context, downUrl, true, BACKGROUND, BACKGROUND, false, channelId);
    }

    /**
     * 后台下载app。当下载完成或失败时发出广播，广播Action为静态变量：DOWNLOAD_ACTION_COMPLETE = "DOWNLOAD_ACTION_COMPLETE"，DOWNLOAD_ACTION_FIAL = "DOWNLOAD_ACTION_FIAL"，DOWNLOAD_ACTION_NO_SD_CARD = "NO_SD_CARD";
     *
     * @param downUrl       app下载的地址
     * @param isAutoInstall 下载完成是否要自动安装，如果是false,则需要Update.install(Context c)方法来完成安装
     */
    public static void update(Context context, String downUrl, boolean isAutoInstall, String channelId) {
        update(context, downUrl, true, BACKGROUND, BACKGROUND, isAutoInstall, channelId);
    }


    /**
     * 下载app,不自动安装app。当下载完成或失败时发出广播，广播Action为静态变量：DOWNLOAD_ACTION_COMPLETE = "DOWNLOAD_ACTION_COMPLETE"，DOWNLOAD_ACTION_FIAL = "DOWNLOAD_ACTION_FIAL"，DOWNLOAD_ACTION_NO_SD_CARD = "NO_SD_CARD";
     *
     * @param downUrl          app下载的地址
     * @param isDownBackground 是否后台下载，true后台下载
     * @param sourceIcon       如果在前台下载app，通知栏需要一张显示的图标
     */
    public static void update(Context context, String downUrl, boolean isDownBackground,
                              int sourceIcon, int smallIcon, String channelId) {
        update(context, downUrl, isDownBackground, sourceIcon, smallIcon, false, channelId);
    }

    /**
     * 下载app,当下载完成或失败时发出广播，广播Action为静态变量：DOWNLOAD_ACTION_COMPLETE = "DOWNLOAD_ACTION_COMPLETE"，DOWNLOAD_ACTION_FIAL = "DOWNLOAD_ACTION_FIAL"，DOWNLOAD_ACTION_NO_SD_CARD = "NO_SD_CARD";
     *
     * @param downUrl          app下载的地址
     * @param isDownBackground 是否后台下载，true后台下载
     * @param sourceIcon       如果在前台下载app，通知栏需要一张显示的图标
     * @param isAutoInstall    下载完成是否要自动安装，如果是false,则需要Update.install(Context c)方法来完成安装
     */
    public static void update(Context context, String downUrl, boolean isDownBackground,
                              int sourceIcon, int smallIcon, boolean isAutoInstall, String channelId) {
        if (context == null) {
            return;
        }

//        String fileName = getFileName(downUrl);
//        if (TextUtils.isEmpty(fileName)) {
//            System.err.println("DownUrl error!");
//            return;
//        }

        filePath = getDownPath(downUrl);

//            //后台更新
//            Intent intent = new Intent(context, UpdateService.class);
//            intent.putExtra(UPDATE_URI, downUrl);
//            intent.putExtra(UPDATE_PATH, filePath);
//            intent.putExtra(UPDATE_AUTO_ISTALL, isAutoInstall);
//            context.startService(intent);


        Intent intent = new Intent(context, DownUpdateService.class);
        intent.putExtra(UPDATE_URI, downUrl);
        intent.putExtra(UPDATE_PATH, filePath);
        intent.putExtra(UPDATE_ICON, sourceIcon);
        intent.putExtra(UPDATE_SMALL_ICON, smallIcon);
        intent.putExtra(UPDATE_IS_BACKGROUND, isDownBackground);
        intent.putExtra(UPDATE_AUTO_ISTALL, isAutoInstall);
        intent.putExtra(UPDATE_NOTIFICATION_CHANNEL_ID, channelId);
        if (!DownUpdateService.isRunning) {
            context.startService(intent);
        }

    }

    public static boolean install(Context context, String downloadUrl) {
        if (context == null) {
            return false;
        }
        String downPath = getDownPath(downloadUrl);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setDataAndType(Uri.fromFile(new File(downPath)), "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 获取文件名
     */
    public static String getFileName(String downloadUrl) {
        if (TextUtils.isEmpty(downloadUrl))
            return "";
        return downloadUrl.substring(downloadUrl
                .lastIndexOf('/') + 1);
    }

    public static String getDownPath(String downloadUrl) {
        String path = "";
        String downPath = "";

        String fileName = getFileName(downloadUrl);
        if (TextUtils.isEmpty(fileName)) {
            return "";
        }

        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if (file.isDirectory() && file.canWrite()) {
            Log.e("storage1: ", file.getAbsolutePath());
            path = file.getAbsolutePath();
            String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
            File testWritable = new File(path, "test_" + timeStamp);

            if (testWritable.mkdirs()) {
                Log.e("storage2: ", file.getAbsolutePath());
                testWritable.delete();
            } else {
                Log.e("storage3: ", file.getAbsolutePath());
                path = "";
            }
        }
//        2851508870

        if (TextUtils.isEmpty(path)) {
            return path;
        }

        downPath = path + "/" + fileName;
        return downPath;
    }

//    /**
//     * 遍历 "system/etc/vold.fstab” 文件，获取全部的Android的挂载点信息
//     *
//     * @return
//     */
//    private static ArrayList<String> getDevMountList() {
//        String[] toSearch = FileUtils.readFile("/etc/vold.fstab").split(" ");
//        ArrayList<String> out = new ArrayList<String>();
//        for (int i = 0; i < toSearch.length; i++) {
//            if (toSearch[i].contains("dev_mount")) {
//                if (new File(toSearch[i + 2]).exists()) {
//                    out.add(toSearch[i + 2]);
//                }
//            }
//        }
//        return out;
//    }


    public static boolean isCompleteDownload(Context context, String downloadUrl) {
        SharedPreferences sPre = context.getSharedPreferences(UpdateUtil.SHARE_NAME, Context.MODE_PRIVATE);
        String downPath = getDownPath(downloadUrl);
        if (TextUtils.isEmpty(downPath)) {
            return false;
        }
        File fileDown = new File(downPath);
        String shareDownPath = sPre.getString(UpdateUtil.SHARE_UPDATE_FILEPATH, "");
        if (fileDown.exists() && shareDownPath.equals(downPath)) {
            long shareFilePath = sPre.getLong(UpdateUtil.SHARE_UPDATE_FILELENGTH, 0);
            if (fileDown.length() >= shareFilePath) {
                return true;
            }
        }
        return false;
    }

}


