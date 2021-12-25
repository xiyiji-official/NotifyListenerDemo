package com.llw.notify;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

public class AppInfo {
    // 获取已安装的应用信息队列
    public static String getAppInfo(Context ctx, String packagename) {
        // 获得应用包管理器
        PackageManager pm = ctx.getPackageManager();
        // 获取系统中已经安装的应用列表

        ApplicationInfo installList = null;
        try {
            installList = pm.getApplicationInfo(
                    packagename, 0);
        } catch (PackageManager.NameNotFoundException e) {
            installList = null;
        }

        // 类型为0表示所有应用，为1表示只要联网应用

        String name = pm.getApplicationLabel(installList).toString();
        Log.i("name", name); // 获取应用的名称
        return name;  // 返回去重后的应用包队列
    }

}