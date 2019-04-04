package com.example.administrator.mobliesafe.chapter01.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.job.JobInfo;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.example.administrator.mobliesafe.R;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

/**
 * Created by Administrator on 2019/3/6.
 */

public class VersionUpdateUtils {
    private static final int MESSAGE_NET_EEOR = 101;
    private static final int MESSAGE_IO_EEOR = 102;
    private static final int MESSAGE_JSON_EEOR = 103;
    private static final int MESSAGE_SHOEW_DIALOG = 104;
    protected static final int MESSAGE_ENTERHOME = 105;

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        switch (msg.what){
            case MESSAGE_IO_EEOR:
                Toast.makeText(context, "IO异常", Toast.LENGTH_SHORT).show();
            break;
            case MESSAGE_JSON_EEOR:
                Toast.makeText(context, "JSON解析异常", Toast.LENGTH_SHORT).show();
                break;
            case MESSAGE_NET_EEOR:
                Toast.makeText(context, "网络异常", Toast.LENGTH_SHORT).show();
                break;
        }
        }
    };
    private String mVersion;
    private Activity context;
    private ProgressDialog mProgressDialog;


    public VersionUpdateUtils(String Version, Activity activity) {
        mVersion = Version;
        context = activity;
    }

    public void getCloudVersion() throws IOException, JSONException {
        try {
            HttpClient client = new DefaultHttpClient();
          /*连接超时*/
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 5000);
        /*请求超时*/
            HttpConnectionParams.setSoTimeout(client.getParams(), 5000);
            HttpGet httpGet = new HttpGet(
                    "http://192.168.100.9:8080/updateinfo.html");
            HttpResponse response = client.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == 200) {
                // 请求和响应都成功了
                HttpEntity entity = response.getEntity();
                String result = EntityUtils.toString(entity, "gbk");
                // 创建jsonObject对象
                JSONObject jsonObject = new JSONObject(result);
                VersionEntity versionEntity = new VersionEntity();
                String code = jsonObject.getString("code");
                versionEntity.versioncode = code;
                String des = jsonObject.getString("des");
                versionEntity.description = des;
                String apkurl = jsonObject.getString("apkurl");
                versionEntity.apkurl = apkurl;
                if (!mVersion.equals(versionEntity.versioncode)) {
                    // 版本号不一致
                    Handler handler = null;
                    handler.sendEmptyMessage(MESSAGE_SHOEW_DIALOG);
                }
            }
        } catch (ClientProtocolException e) {
            handler.sendEmptyMessage(MESSAGE_NET_EEOR);
            e.printStackTrace();
        } catch (IOException e) {
            handler.sendEmptyMessage(MESSAGE_IO_EEOR);
            e.printStackTrace();
        } catch (JSONException e) {
            handler.sendEmptyMessage(MESSAGE_JSON_EEOR);
            e.printStackTrace();
        }
    }
    private void initProgressDialog() {
        mProgressDialog.setMessage("准备下载...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.show();
    }


    private void showUpdateDialog(final VersionEntity versionEntity) {
        // 创建dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("检查到新版本：" + versionEntity.versioncode);// 设置标题
        builder.setMessage(versionEntity.description);// 根据服务器返回描述,设置升级描述信息
        builder.setCancelable(false);// 设置不能点击手机返回按钮隐藏对话框
        builder.setIcon(R.drawable.ic_launcher);// 设置对话框图标
        // 设置立即升级按钮点击事件
        builder.setPositiveButton("立即升级",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        initProgressDialog();
                        downloadNewApk(versionEntity.apkurl);
                    }


                });
        // 设置暂不升级按钮点击事件
        builder.setNegativeButton("暂不升级",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        enterHome();
                    }

                    private void enterHome() {
                    }
                });
        // 对话框必须调用show方法 否则不显示
        builder.show();
    }
    protected void downloadNewApk(String apkurl) {
        DownLoadUtils downLoadUtils = new DownLoadUtils();
        downLoadUtils.downapk(apkurl, "/mnt/sdcard/mobilesafe2.0.apk", new MyCallBack() {

            @Override
            public void onSuccess(ResponseInfo<File> arg0) {
                // TODO Auto-generated method stub
                mProgressDialog.dismiss();
                MyUtils.installApk(context);
            }

            @Override
            public void onLoadding(long total, long current, boolean isUploading) {
                // TODO Auto-generated method stub
                mProgressDialog.setMax((int) total);
                mProgressDialog.setMessage("正在下载...");
                mProgressDialog.setProgress((int) current);
            }

            @Override
            public void onFailure(HttpException arg0, String arg1) {
                // TODO Auto-generated method stub
                mProgressDialog.setMessage("下载失败");
                mProgressDialog.dismiss();
                enterHome();
            }
        });
    }
    private void enterHome() {
        handler.sendEmptyMessageDelayed(MESSAGE_ENTERHOME, 2000);
    }


    private class MyCallBack implements DownLoadUtils.MyCallBack {
        @Override
        public void onSuccess(ResponseInfo<File> arg0) {

        }

        @Override
        public void onFailure(HttpException arg0, String arg1) {

        }

        @Override
        public void onLoadding(long total, long current, boolean isUploading) {

        }


    }
}
