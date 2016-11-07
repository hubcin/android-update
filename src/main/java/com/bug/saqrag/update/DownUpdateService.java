package com.bug.saqrag.update;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownUpdateService extends IntentService {

    private MyNotification myNotification;
    private SharedPreferences sPre;
    private long downLength;
    private long fileLength;
    private String updatePath;
    private int down = 0;
    boolean isNotify = true;
    private boolean isDownloadBack;
    private String url;
    public static boolean isRunning = false;


    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public DownUpdateService() {
        super("DownUpdateService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        isRunning = true;
        sPre = getSharedPreferences(UpdateUtil.SHARE_NAME, MODE_PRIVATE);

        boolean isAutoInstall = intent.getBooleanExtra(UpdateUtil.UPDATE_AUTO_ISTALL, false);
        url = intent.getStringExtra(UpdateUtil.UPDATE_URI);
        int sourceIcon = intent.getIntExtra(UpdateUtil.UPDATE_ICON, R.drawable.launcher);
        updatePath = intent.getStringExtra(UpdateUtil.UPDATE_PATH);
        isDownloadBack = intent.getBooleanExtra(UpdateUtil.UPDATE_IS_BACKGROUND, false);

        downLength = sPre.getLong(UpdateUtil.SHARE_UPDATE_DOWNLENGTH, 0);
        fileLength = sPre.getLong(UpdateUtil.SHARE_UPDATE_FILELENGTH, 0);
        if (TextUtils.isEmpty(url))
            return;

        if (TextUtils.isEmpty(updatePath)) {
            sendBroadcast(new Intent(UpdateUtil.DOWNLOAD_ACTION_NO_SD_CARD));
            return;
        }

        //判断是否同一个版本
        //判断是否下载一半删除源文件
        if (!updatePath.equals(sPre.getString(UpdateUtil.SHARE_UPDATE_FILEPATH, ""))
                || !new File(updatePath).exists()) {

            downLength = 0;
            fileLength = 0;
            SharedPreferences.Editor edit = sPre.edit();
            edit.putString(UpdateUtil.SHARE_UPDATE_FILEPATH, updatePath);
            edit.putLong(UpdateUtil.SHARE_UPDATE_DOWNLENGTH, 0);
            edit.putLong(UpdateUtil.SHARE_UPDATE_FILELENGTH, 0);
            edit.apply();
        }

        download(url, sourceIcon, isAutoInstall);

    }

    private void download(String updateUrl, int resourceIcon, boolean isAutoIstall) {
//        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {


        startNotification(resourceIcon);


        InputStream input = null;
        RandomAccessFile output = null;

        try {
            URL downUrl = new URL(updateUrl);
            HttpURLConnection connection = (HttpURLConnection) downUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5 * 1000);

            //判断是否已经下载完成，但没有初始化
            if (downLength >= fileLength) {
                downLength = 0;
            }

            connection.setRequestProperty("Range", "bytes=" + downLength + "-");

            if (downLength == 0) {
                fileLength = connection.getContentLength();
                sPre.edit().putLong(UpdateUtil.SHARE_UPDATE_FILELENGTH, fileLength).apply();
            } else {
                fileLength = sPre.getLong(UpdateUtil.SHARE_UPDATE_FILELENGTH, 0);
            }

            // download the file
            input = connection.getInputStream();
            output = new RandomAccessFile(updatePath, "rwd");
            // 定位到pos位置
            output.seek(downLength);
            byte data[] = new byte[1024];
            int count;

            while ((count = input.read(data, 0, 1024)) != -1) {
                // publishing the progress....
                output.write(data, 0, count);
                downLength += count;
//                Log.e("download", "download:" + downLength);
            }

            output.close();
            input.close();
            isNotify = false;
            sPre.edit().putLong(UpdateUtil.SHARE_UPDATE_DOWNLENGTH, 0).apply();
            if (isAutoIstall) {
                UpdateUtil.install(this, url);
            } else {
                sendBroadcast(new Intent(UpdateUtil.DOWNLOAD_ACTION_COMPLETE));
            }

        } catch (IOException e) {
            e.printStackTrace();
            isNotify = false;
            sPre.edit().putLong(UpdateUtil.SHARE_UPDATE_DOWNLENGTH, downLength).apply();
            sendBroadcast(new Intent(UpdateUtil.DOWNLOAD_ACTION_FIAL));
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            isRunning = false;
        }
    }

    private void startNotification(int resourceIcon) {
        if (!isDownloadBack) {
            myNotification = new MyNotification(this, 0);
            myNotification.showCustomizeNotification(resourceIcon, "准备下载", R.layout.notify_download);
        }
        new Thread() {
            @Override
            public void run() {
                while (isNotify) {
                    SystemClock.sleep(1000);
                    if (fileLength != 0) {
                        down = (int) (downLength * 100 / fileLength);
                        if (myNotification != null) {
                            myNotification.changeProgressStatus(down);
                        }
                    }

                    if (down == 100) {
                        isNotify = false;
                    }
                    Log.e("DownUpdateService", "progress: " + down);
                    Intent progress = new Intent(UpdateUtil.DOWNLOAD_ACTION_PROGRESS);
                    progress.putExtra(UpdateUtil.DOWNLOAD_EXTRA_PROGRESS, down);
                    sendBroadcast(progress);
                }
                if (myNotification != null) {
                    myNotification.removeNotification();
                }
            }
        }.start();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //保存已下载长度
        isNotify = false;
        sPre.edit().putLong(UpdateUtil.SHARE_UPDATE_DOWNLENGTH, downLength).apply();
    }
}
