package cn.mmvtc.mobilesafe3.chapter01.utils;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import java.io.File;

/**
 * Created by Administrator on 2019/2/27.
 *下载文件的工具类
 */

public class DownLoadUtils {

    public void downapk(String url,String targetFile,
                        final MyCallBack myCallBack){
        //创建HttpUtils对象
        HttpUtils httpUtils = new HttpUtils();
        //调用HttpUtils下载方法去下载指定文件
        httpUtils.download(url, targetFile, new RequestCallBack<File>() {
            @Override
            public void onSuccess(ResponseInfo<File> arg0) {
                myCallBack.onSuccess(arg0);
            }

            @Override
            public void onLoading(long total, long current, boolean isUploading) {
                super.onLoading(total, current, isUploading);
                myCallBack.onLoading(total,current,isUploading);
            }

            @Override
            public void onFailure(HttpException arg0, String arg1) {
                myCallBack.onFailure(arg0,arg1);
            }
        });
    }
}

/**接口，用于监听下载状态的接口*/
interface MyCallBack{
    //下载成功时调用
    void onSuccess(ResponseInfo<File> arg0);

    //下载中时调用
    void onLoading(long total,long curren,boolean isUploading);

    //下载失败时调用
    void onFailure(HttpException arg0,String arg1);

}