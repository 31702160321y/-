package cn.mmvtc.mobilesafe3.chapter01.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Entity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import cn.mmvtc.mobilesafe3.HomeActivity;
import cn.mmvtc.mobilesafe3.R;

/**
 * Created by Administrator on 2019/3/6.
 * 1.本地版本号
 * 2.网络服务器的版本号,json数据的解析
 * 3.版本号对比不一致，弹出更新提示对话框
 * 4.进度条对话框初始化
 * 5.下载新版本
 */

public class VersionUpdateUtils {
    //相关信息处理的状态码
    private static final int MESSAGE_NET_EEOR = 101;
    private static final int MESSAGE_IO_EEOR = 102;
    private static final int MESSAGE_JSON_EEOR = 103;
    private static final int MESSAGE_SHOEW_DIALOG = 104;
    protected static final int MESSAGE_ENTERHOME = 105;

    //用于更新UI
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MESSAGE_IO_EEOR:
                    Toast.makeText(context, "IO异常",Toast.LENGTH_SHORT).show();
                    //进入主页面
                    enterhome();
                    break;
                case MESSAGE_JSON_EEOR:
                    Toast.makeText(context, "JSON异常",Toast.LENGTH_SHORT).show();
                    //进入主页面
                    enterhome();
                    break;
                case MESSAGE_NET_EEOR:
                    Toast.makeText(context, "网络异常",Toast.LENGTH_SHORT).show();
                    //进入主页面
                    enterhome();
                    break;
                case MESSAGE_SHOEW_DIALOG:
                    //弹出对话框的调用
                    showUpdateDialog(versionEntity);
                    break;
                case MESSAGE_ENTERHOME:
                    //进入主页面
                    Intent intent = new Intent(context, HomeActivity.class);
                    context.startActivity(intent);
                    context.finish();
                    break;
            }
        }
    };
    private String mVersion;
    private Activity context;
    private VersionEntity versionEntity;
    private ProgressDialog mProgressDialog;
    //构造函数，（快捷键：alt+insert/第一个进行构造函数的创建)
    //1.本地版本号
    public VersionUpdateUtils(String Version, Activity activity) {
        this.mVersion = Version;
        this.context = activity;
    }

    //2.网络服务器的版本号
    public void getCloudVersion(){
        try {
            HttpClient client = new DefaultHttpClient();
            //超时设置
            HttpConnectionParams.setConnectionTimeout(client.getParams(),5000);
            HttpConnectionParams.setSoTimeout(client.getParams(),5000);
            //get请求方式创建
            HttpGet httpGet = new HttpGet("http://172.26.42.36/updateinfo.html");

            //发送请求，得到响应
            HttpResponse response = client.execute(httpGet);

            //判断响应的数据是否成功
            if (response.getStatusLine().getStatusCode()==200){
                /**
                 * HttpClient访问成功后得到的数据转换过程：
                 * 1.得到实体。response.getEntity()。
                 * 2.将实体转换为实际的内容：
                 * EntityUtils.toString(entity,"gbk"）。
                 */

                HttpEntity entity = response.getEntity();
                String result = EntityUtils.toString(entity,"gbk");
                versionEntity = new VersionEntity();
                //JSON解析
                JSONObject jsonObject = new JSONObject(result);
                String code = jsonObject.getString("code");
                String des = jsonObject.getString("des");
                String apkurl = jsonObject.getString("apkurl");
                //存放数据到versionEntity实体类对象里
                versionEntity.versioncode = code;
                versionEntity.description = des;
                versionEntity.apkurl = apkurl;
                //版本号做对比，不一致，就会弹出下载对话框
                if (!mVersion.equals(versionEntity.versioncode)){
                    //发送信息给ui
                    handler.sendEmptyMessage(MESSAGE_SHOEW_DIALOG);//状态码what=MESSAGE_SHOEW_DIALOG
                }
            }
        } catch (IOException e) {
            handler.sendEmptyMessage(MESSAGE_IO_EEOR);
            e.printStackTrace();
        } catch (JSONException e) {
            handler.sendEmptyMessage(MESSAGE_JSON_EEOR);
            e.printStackTrace();
        }
    }
    /**
     * 弹出更新提示对话框
     */
    private void showUpdateDialog(final VersionEntity versionEntity){
        //创建对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("检查到新版本"+versionEntity.versioncode);//设置标题
        builder.setMessage(versionEntity.description);//设置对话框的信息
        builder.setIcon(R.drawable.ic_launcher);//设置对话框图标
        builder.setCancelable(false);//设置不能点击手机返回按键隐藏对话框
        builder.setPositiveButton("立即升级", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //下载进度条的调用
                initProgressDialog();
                //下载新版本apk的调用
                downloadNewApk(versionEntity.apkurl);
            }
        });
        //设置不升级按钮点击事件
        builder.setNegativeButton("暂不升级", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                enterhome();
            }
        });
        builder.show();//要展示对话框
    }
    //初始化进度条对话框
    private void initProgressDialog(){
        mProgressDialog = new ProgressDialog(context);//进度条对象
        mProgressDialog.setMessage("准备下载...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.show();
    }
    //下载新版本apk
    protected void downloadNewApk(String apkurl){
        DownLoadUtils downLoadUtils = new DownLoadUtils();
        downLoadUtils.downapk(apkurl, "mnt/sdcard/mobliesafe.apk", new MyCallBack() {
            @Override
            public void onSuccess(ResponseInfo<File> arg0) {
                mProgressDialog.dismiss();//进度条消失
                MyUtils.installApk(context);//安装新版本的apk
            }

            @Override
            public void onLoading(long total, long curren, boolean isUploading) {
                mProgressDialog.setMax((int) total);
                mProgressDialog.setProgress((int) curren);
                mProgressDialog.setMessage("正在下载...");
            }

            @Override
            public void onFailure(HttpException arg0, String arg1) {
                mProgressDialog.setMessage("下载失败");
                mProgressDialog.dismiss();
                enterhome();
            }
        });
    }

    private void enterhome(){
        handler.sendEmptyMessageDelayed(MESSAGE_ENTERHOME,2000);//延迟2秒
    }
}
