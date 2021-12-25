package com.llw.notify;

import android.service.notification.StatusBarNotification;

/**
 * @author llw
 * @description NotifyListener
 * @date 2021/8/12 10:30
 */
public interface NotifyListener {

    /**
     * 接收到通知栏消息
     * @param sbn
     */
    void onNotificationPosted(StatusBarNotification sbn);
}
