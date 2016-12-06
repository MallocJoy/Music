package com.malloc.musics.services;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;

import com.malloc.musics.application.BaseApplication;
import com.malloc.musics.utils.MediaUtils;
import com.malloc.musics.vo.Mp3Info;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 音乐播放的服务组件
 * 实现的功能有：
 * 1.播放
 * 2.暂停
 * 3.上一首
 * 4.下一首
 * 5.获取当前的播放进度
 */
public class PlayService extends Service implements
        MediaPlayer.OnCompletionListener,MediaPlayer.OnErrorListener{

    private MediaPlayer mediaPlayer;
    private int currentPosition;   //表示当前正在播放的歌曲的位置
    private ArrayList<Mp3Info> dataList;
    private MusicUpdateListener musicUpdateListener;

    private ExecutorService es = Executors.newSingleThreadExecutor();
    private boolean isPause = false;

    //Use for play mode flag
    public static final int PLAY_ORDER = 1;        //顺序播放
    public static final int PLAY_SINGLE = 2;        //单曲循环
    public static final int PLAY_RANDOM = 3;   //随机播放
    private int playMode = PLAY_ORDER;
    private Random random = new Random();    //产生随机数，用于随机播放

    public int getPlayMode() {
        return playMode;
    }

    public void setPlayMode(int playMode) {
        this.playMode = playMode;
    }

    public boolean isPause(){
        return isPause;
    }

    public PlayService() {
    }

    Runnable updateStateRunnable = new Runnable() {
        @Override
        public void run() {
            while (true) {
                if (musicUpdateListener != null && mediaPlayer != null && mediaPlayer.isPlaying()) {
                    musicUpdateListener.onPublish(getCurrentProgress());
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        dataList = MediaUtils.getMp3Infos(this);
        es.execute(updateStateRunnable);

        BaseApplication app = (BaseApplication) getApplication();
         currentPosition = app.sp.getInt("currentPosition", 0);
         playMode = app.sp.getInt("playMode", PlayService.PLAY_ORDER);

        //注册监听器
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
//        throw new UnsupportedOperationException("Not yet implemented");
        return new PlayBinder();
    }

   //播放音乐
    public void playMusic(int position) {
        if (position >= 0 && position < dataList.size()) {
            Mp3Info mp3Info = dataList.get(position);
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(this, Uri.parse(mp3Info.getUrl()));
                mediaPlayer.prepare();
                mediaPlayer.start();
                currentPosition = position;
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (musicUpdateListener != null) {
                musicUpdateListener.onChange(currentPosition);
            }
        }
    }

    //暂停音乐
    public void pauseMusic() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPause = true;
        }
    }

    //播放下一首
    public void nextMusic() {
        if (currentPosition + 1 >= dataList.size() - 1) {
            currentPosition = 0;
        } else {
            currentPosition++;
        }
        playMusic(currentPosition);
    }

    //播放上一首
    public void prevMusic() {
        if (currentPosition - 1 < 0) {
            currentPosition = dataList.size() - 1;
        } else {
            currentPosition--;
        }
        playMusic(currentPosition);
    }

    //继续播放音乐
    public void start() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    //获取音乐时长
    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    //跳转到指定位置播放
    public void seekTo(int msec) {
        mediaPlayer.seekTo(msec);
    }

    //获取当前的进度值
    public int getCurrentProgress() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    //获取当前播放位置
    public int  getCurrentPosition(){
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            return currentPosition;
        }
        return 0;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        switch (playMode){
            case PLAY_ORDER:
                nextMusic();
                break;
            case PLAY_SINGLE:
                playMusic(currentPosition);
                break;
            case PLAY_RANDOM:
                playMusic(random.nextInt(dataList.size()));
                break;
            default:break;
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mediaPlayer.reset();
        return false;
    }

    //更新状态的接口（观察者设计模式）
    public interface MusicUpdateListener {
        void onPublish(int progress);
        void onChange(int position);
    }

    //判断音乐是否正在播放
    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    public void setMusicUpdateListener(MusicUpdateListener musicUpdateListener) {
        this.musicUpdateListener = musicUpdateListener;
    }

    public class PlayBinder extends Binder {
        public PlayService getPlayService() {
            return PlayService.this;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (es != null && !es.isShutdown()){
            es.shutdown();
            es = null;
        }
    }
}