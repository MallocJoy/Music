package com.malloc.musics.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentActivity;

import com.malloc.musics.services.PlayService;
import com.malloc.musics.utils.Contant;

/**
 * 作者： 86563
 * 时间： 2016/12/2
 */
public abstract class BaseActivity extends FragmentActivity{

    public PlayService playService;
    private boolean isBind = false;


    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

    }

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PlayService.PlayBinder playBinder = (PlayService.PlayBinder) service;
            playService = playBinder.getPlayService();
            playService.setMusicUpdateListener(musicUpdateListener);
            musicUpdateListener.onChange(playService.getCurrentPosition());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            playService = null;
            isBind = false;
        }
    };

    //回调接口的实现
    private PlayService.MusicUpdateListener  musicUpdateListener = new PlayService.MusicUpdateListener() {
        @Override
        public void onPublish(int progress) {
            publish(progress);
        }

        @Override
        public void onChange(int position) {
            change(position);
        }
    };

    public abstract void publish(int progress);
    public abstract void change(int position);

    //绑定服务
    public void bindPlayService(){
        if (!isBind){
            Intent intent = new Intent(this,PlayService.class);
            bindService(intent,conn, Context.BIND_AUTO_CREATE);
            isBind = true;
        }
    }

    //解绑服务
    public void unbindPlayService(){
        if (isBind){
            unbindService(conn);
            isBind = false;
        }
    }
}
