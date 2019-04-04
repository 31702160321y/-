package com.example.administrator.mobliesafe.chapter01.utils;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import java.io.File;

/**
 * Created by Administrator on 2019/2/27.
 * 下载apk
 */

public class DownLoadUtils {

    public void downapk(String url,String targerFile,final MyCallBack myCallBack){
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.download(url, targerFile, new RequestCallBack<File>() {
            @Override
            public void onSuccess(ResponseInfo<File> arg0) {
                myCallBack.onSuccess(arg0);
            }

            @Override
            public void onFailure(HttpException arg0, String arg1) {
                myCallBack.onFailure(arg0,arg1);
            }

            @Override
            public void onLoading(long total, long current, boolean isUploading) {
                super.onLoading(total, current, isUploading);
                myCallBack.onLoadding(total,current,isUploading);
            }
            
        });
    }
    interface MyCallBack{
        void onSuccess(ResponseInfo<File> arg0);
        void onFailure(HttpException arg0, String arg1);
        void onLoadding(long total, long current, boolean isUploading);
    }
}
