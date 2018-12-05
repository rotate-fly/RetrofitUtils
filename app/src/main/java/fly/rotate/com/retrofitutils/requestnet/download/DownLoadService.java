package fly.rotate.com.retrofitutils.requestnet.download;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;


import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import fly.rotate.com.retrofitutils.R;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by hzs on 2018/12/05.
 *
 * @author hzs
 * app 在线更新功能
 * 调用方式：
 *     String url = "http://********.apk";
 *         Intent downIntent = new Intent(this, DownLoadService.class);
 *         downIntent.putExtra("url",url);
 *         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
 *             this.startForegroundService(downIntent);
 *         }else {
 *             this.startService(downIntent);
 *         }
 *
 */
public class DownLoadService extends Service {

    /**
     * 目标文件存储的文件夹路径
     */
    private String destFileDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "" + File
            .separator;

    /**
     * 目标文件存储的文件名
     */
    private String destFileName = "update.apk";

    private Context mContext;
    private int preProgress = 0;
    private int NOTIFY_ID = 1000;
    private NotificationCompat.Builder builder;
    private NotificationManager notificationManager;
    private Retrofit.Builder retrofit;
    private String url = "";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mContext = this;
        if (intent != null && intent.hasExtra("url")) {
            url = intent.getStringExtra("url");
        }
        loadFile(url);
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 下载文件
     */
    private void loadFile(String url) {
        initNotification();
        if (retrofit == null) {
            retrofit = new Retrofit.Builder();
        }
        retrofit.baseUrl("http://appstoreupload.daliandong.cn/")
                .client(initOkHttpClient())
                .build()
                .create(IFileLoad.class)
                .loadFile(url)
                .enqueue(new FileCallback(destFileDir, destFileName) {

                    @Override
                    public void onSuccess(File file) {
                        Log.e("zs", "请求成功");
                        // 安装软件
                        cancelNotification();
                        installApk(file);
                    }

                    @Override
                    public void onLoading(long progress, long total) {
                        Log.e("retrofit-down", progress + "----" + total);
                        updateNotification(progress * 100 / total);
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e("zs", "请求失败");
                        cancelNotification();
                    }
                });
    }

    public interface IFileLoad {
        @GET
        Call<ResponseBody> loadFile(@Url String url);
    }

    /**
     * 安装软件
     *
     * @param file
     */
    private void installApk(File file) {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(getApplicationContext(), getApplication().getPackageName() + ".provider", file);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }
        startActivity(intent);


    }

    /**
     * 初始化OkHttpClient
     *
     * @return
     */
    private OkHttpClient initOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(100000, TimeUnit.SECONDS);
        builder.networkInterceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response originalResponse = chain.proceed(chain.request());
                return originalResponse
                        .newBuilder()
                        .body(new FileResponseBody(originalResponse))
                        .build();
            }
        });
        return builder.build();
    }

    /**
     * 初始化Notification通知
     */
    public void initNotification() {
        String channelId = "1";
        notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannelId(channelId, notificationManager);
        }
        builder = new NotificationCompat.Builder(mContext, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText("0%")
                .setContentTitle(getApplicationContext().getString(R.string.app_name))
                .setProgress(100, 0, false);

        notificationManager.notify(NOTIFY_ID, builder.build());
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createChannelId(String channelId, NotificationManager notificationManager) {
//		String group_primary = "group_second";
//		NotificationChannelGroup ncp1 = new NotificationChannelGroup(group_primary, "DOWN");
//		getNotificationManager().createNotificationChannelGroup(ncp1);

        NotificationChannel channel = new NotificationChannel(channelId,
                getString(R.string.app_name),
                NotificationManager.IMPORTANCE_LOW);
//        channel.setGroup(group_primary);
        //锁屏的时候是否展示通知
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
//        NotificationChannel channel = new NotificationChannel(channelId, getString(R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT);
// 开启指示灯，如果设备有的话
        channel.enableLights(true);
// 设置指示灯颜色
        channel.setLightColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
// 是否在久按桌面图标时显示此渠道的通知
        channel.setShowBadge(true);
// 设置是否应在锁定屏幕上显示此频道的通知
        channel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PRIVATE);
// 设置绕过免打扰模式
        channel.setBypassDnd(false);
        notificationManager.createNotificationChannel(channel);
    }


    /**
     * 更新通知
     */
    public void updateNotification(long progress) {
        int currProgress = (int) progress;
        if (preProgress < currProgress) {
            builder.setContentText(progress + "%");
            builder.setProgress(100, (int) progress, false);
            notificationManager.notify(NOTIFY_ID, builder.build());
        }
        preProgress = (int) progress;
    }

    /**
     * 取消通知
     */
    public void cancelNotification() {
        notificationManager.cancel(NOTIFY_ID);
    }
}
