package com.llw.notify;

import android.content.ComponentName;
import android.os.Build;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

/**
 * @author llw
 * @description NotifyService
 * @date 2021/8/5 19:14
 */
public class NotifyService extends NotificationListenerService {

    /**
     * 发布通知
     *
     * @param sbn 状态栏通知
     */
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        NotifyHelper.getInstance().onNotificationPosted(sbn);
    }

    /**
     * 监听断开
     */
    @Override
    public void onListenerDisconnected() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // 通知侦听器断开连接 - 请求重新绑定
            requestRebind(new ComponentName(this, NotificationListenerService.class));
        }
    }
}
