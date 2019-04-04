package cn.mmvtc.mobilesafe3.chapter01.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

import java.io.File;

/**
 * Created by Administrator on 2019/3/6.
 */

public class MyUtils {
    /**
     * 1.获取本地版本号
     * 2.安装新版本APK
     */
    public static String getVersion(Context context){
        //1、PackageManager用于获得清单文件中的所有信息
        PackageManager manager = context.getPackageManager();
        try {
            //getPackgeName获取当前程序的包名
           PackageInfo packageInfo = manager.getPackageInfo(context.getPackageName(),0);
           return packageInfo.versionName;//返回版本号
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }
    public static void installApk(Activity activity){
        //使用隐式意图去安装apk
        Intent intent = new Intent("android.intent.action.VIEW");
        //添加默认分类，属性action和category是成对出现,action不可少,否则即使匹配,也不发生。
        intent.addCategory("android.intent.category.DEFAULT");
        //设置数据和类型
        intent.setDataAndType(Uri.fromFile(new File("mnt/sdcard/mobliesafe.apk")),
                "application/vnd.android.package-archive");
        //开启Activity,安装apk
        activity.startActivityForResult(intent,0);
    }
}
