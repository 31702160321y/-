package com.example.administrator.mobliesafe.utils;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import java.io.File;

/**
 * Created by Administrator on 2019/2/27.
 */

public class DownLoadUtils {
    public  void  downapk(String url,String targetFile,final MycallBack mycallBack){
        HttpUtils httpUtils=new HttpUtils();
        httpUtils.download(url,targetFile, new RequestCallBack<File>() {
            @Override
            public void onSuccess(ResponseInfo<File> arg0) {
                mycallBack.onSuccess(arg0);
            }

            @Override
            public void onFailure(HttpException arg0, String arg1) {
            mycallBack.onFailure(arg0,arg1);
            }

            @Override
            public void onLoading(long total, long current, boolean isUploading) {
                super.onLoading(total, current, isUploading);
                mycallBack.onLoadding(total,current,isUploading);
            }
        });
    }
}
interface MycallBack{
    void onSuccess(ResponseInfo<File>arg0);
    void onFailure(HttpException arg0,String arg1);
    void  onLoadding (long total,long curren,boolean isUploading);
}
