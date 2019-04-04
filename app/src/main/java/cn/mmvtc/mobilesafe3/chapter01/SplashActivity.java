package cn.mmvtc.mobilesafe3.chapter01;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import cn.mmvtc.mobilesafe3.R;
import cn.mmvtc.mobilesafe3.chapter01.utils.MyUtils;
import cn.mmvtc.mobilesafe3.chapter01.utils.VersionUpdateUtils;

public class SplashActivity extends Activity {
    private TextView mVersionTV;    //版本号
    private String mVersion;        //本地版本号
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置该Activity没有标题,在加载布局之前调用
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        mVersion = MyUtils.getVersion(getApplicationContext());//本地版本号获取

        //初始化控件
        mVersionTV = (TextView) findViewById(R.id.tv_splash_version);
        mVersionTV.setText("版本号"+mVersion);

        final VersionUpdateUtils updateUtils = new VersionUpdateUtils
                (mVersion,SplashActivity.this);
        //服务器版本号获取
        new Thread(){
            @Override
            public void run() {
                //获取服务器版本号
                updateUtils.getCloudVersion();
            }
        }.start();
    }
}
