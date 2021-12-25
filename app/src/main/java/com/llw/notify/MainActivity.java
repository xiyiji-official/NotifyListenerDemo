package com.llw.notify;


import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements NotifyListener {
    String msgTitle;
    String msgText;
    String appName = "";
    String data = "";
    String url = "";
    private static final int REQUEST_CODE = 9527;
    private TextView textView;

    @Override
    @SuppressLint({"NewApi", "SetTextI18n"})
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);
        TextView tips = findViewById(R.id.tipview);
        tips.setText("使用方法：\n" +
                "            1、请求权限，授予软件获得通知的权限。\n" +
                "            2、请正确输入电脑端的IPv4地址和端口（电脑端显示的）\n" +
                "            3、正常使用啦~");
        NotifyHelper.getInstance().setNotifyListener(this);
    }

    public void requestPermission(View view) {
        if (!isNLServiceEnabled()) {
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            startActivityForResult(intent, REQUEST_CODE);
        } else {
            showMsg("通知服务已开启");
            toggleNotificationListenerService();
        }
    }

    /**
     * 是否启用通知监听服务
     */
    public boolean isNLServiceEnabled() {
        Set<String> packageNames = NotificationManagerCompat.getEnabledListenerPackages(this);
        return packageNames.contains(getPackageName());
    }

    /**
     * 切换通知监听器服务
     */
    public void toggleNotificationListenerService() {
        PackageManager pm = getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName(getApplicationContext(), NotifyService.class),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

        pm.setComponentEnabledSetting(new ComponentName(getApplicationContext(), NotifyService.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (isNLServiceEnabled()) {
                showMsg("通知服务已开启");
                toggleNotificationListenerService();
            } else {
                showMsg("通知服务未开启");
            }
        }
    }


    private void showMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }


    /**
     * 收到通知
     *
     * @param sbn 状态栏通知
     */
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        final EditText editIP = findViewById(R.id.input_ip);
        final EditText editPort = findViewById(R.id.port);
        url = editIP.getText().toString() + ":" + editPort.getText().toString();
        boolean same;
        if (sbn.getNotification() == null) return;
        try {
            same = msgText.equals(sbn.getNotification().extras.getString(Notification.EXTRA_TEXT)) &&
                    msgTitle.equals(sbn.getNotification().extras.getString(Notification.EXTRA_TITLE));
        } catch (Exception e) {
            same = false;
        }
        if (same) {
            Log.i("same", "相同");
        } else {
            msgTitle = sbn.getNotification().extras.getString(Notification.EXTRA_TITLE);
            msgText = sbn.getNotification().extras.getString(Notification.EXTRA_TEXT);
            if (msgTitle == null) return;
            appName = AppInfo.getAppInfo(this, sbn.getPackageName());

            //消息时间
            String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE)
                    .format(new Date(sbn.getPostTime()));
            data = String.format(Locale.getDefault(),
                    "{'PackageName':'%s','AppName':'%s','Title':'%s','Text':'%s','time':'%s'}",
                    sbn.getPackageName(), appName, msgTitle, msgText, time);
            try {
                HttpHelper.post(url, data);
            } catch (Exception e) {
                showMsg("请检查并输入正确的IP地址和端口");
            }
            textView.setText(String.format(Locale.getDefault(),
                    "应用包名：%s\n应用名：%s\n消息标题：%s\n消息内容：%s\n消息时间：%s\n",
                    sbn.getPackageName(), appName, msgTitle, msgText, time));
        }

    }
}