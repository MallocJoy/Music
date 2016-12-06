package com.malloc.musics.activity;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;

import com.malloc.musics.R;
import com.malloc.musics.application.BaseApplication;
import com.malloc.musics.services.PlayService;


//启动页面的实现以及Service的启动
public class SplashActivity extends Activity {

    private static final int START_FLAG = 0x01;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case START_FLAG:
                    //页面跳转
                    startActivity(new Intent(SplashActivity.this,MainActivity.class));
                    finish();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //启动服务
        Intent service = new Intent(this, PlayService.class);
        startService(service);

        //发送延时消息
        handler.sendEmptyMessageDelayed(START_FLAG,3000);
    }
}
