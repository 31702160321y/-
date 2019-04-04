package com.example.administrator.mobliesafe.chapter01.utils;

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
    public static String getVersion(Context context){
        PackageManager manager=context.getPackageManager();
        try {
            PackageInfo packageInfo=manager.getPackageInfo(
            context.getPackageName(),0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }
    static void installApk(Activity activity){
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setDataAndType(Uri.fromFile(new File("mnt/sdcard/mobilesafe2.0.apk")),
        "application/vnd.android.package-archive");
        activity.startActivityForResult(intent,0);
    }
}
