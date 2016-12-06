package com.malloc.musics.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.malloc.musics.R;
import com.malloc.musics.services.PlayService;
import com.malloc.musics.utils.MediaUtils;
import com.malloc.musics.vo.Mp3Info;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlayActivity extends BaseActivity implements
        View.OnClickListener,SeekBar.OnSeekBarChangeListener {

    @BindView(R.id.albumImg)
    ImageView albumImg;
    @BindView(R.id.musicName)
    TextView musicName;
    @BindView(R.id.seekBar)
    SeekBar seekBar;
    @BindView(R.id.currentTime)
    TextView currentTime;
    @BindView(R.id.totalTime)
    TextView totalTime;
    @BindView(R.id.playMode)
    ImageView playMode;
    @BindView(R.id.prevMusic)
    ImageView prevMusic;
    @BindView(R.id.play_pauseMusic)
    ImageView playPauseMusic;
    @BindView(R.id.nextMusic)
    ImageView nextMusic;

    private ArrayList<Mp3Info> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        ButterKnife.bind(this);

        dataList = MediaUtils.getMp3Infos(this);   //获取音乐列表

        //绑定监听器
        playMode.setOnClickListener(this);
        prevMusic.setOnClickListener(this);
        playPauseMusic.setOnClickListener(this);
        nextMusic.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindPlayService();    //绑定服务
    }

    @Override
    public void publish(final int progress) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                currentTime.setText(MediaUtils.formatTime(progress));    //在主线程更新Ui
            }
        });
        seekBar.setProgress(progress);
    }

    @Override
    public void change(int position) {
        Mp3Info mp3Info = dataList.get(position);
        musicName.setText(mp3Info.getTitle());
        Bitmap artwork = MediaUtils.getArtwork(this, mp3Info.getId(),
                mp3Info.getAlbumId(), true, false);
        albumImg.setImageBitmap(artwork);
        totalTime.setText(MediaUtils.formatTime(mp3Info.getDuration()));
        seekBar.setProgress(0);
        seekBar.setMax((int) mp3Info.getDuration());

        if (playService.isPlaying()) {
            playPauseMusic.setImageResource(R.mipmap.nowplaying_pause_n);
        } else {
            playPauseMusic.setImageResource(R.mipmap.nowplaying_play_n);
        }

        switch (playService.getPlayMode()){
            case PlayService.PLAY_ORDER :
                playMode.setImageResource(R.mipmap.tool_repeat_all_n);
                playMode.setTag(PlayService.PLAY_ORDER);
                break;
            case PlayService.PLAY_SINGLE:
                playMode.setImageResource(R.mipmap.tool_repeat_current_n);
                playMode.setTag(PlayService.PLAY_SINGLE);
                break;
            case PlayService.PLAY_RANDOM:
                playMode.setImageResource(R.mipmap.tool_repeat_shuffle_n);
                playMode.setTag(PlayService.PLAY_RANDOM);
                break;
            default:break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play_pauseMusic:
                if (playService.isPlaying()) {
                    playPauseMusic.setImageResource(R.mipmap.nowplaying_play_n);
                    playService.pauseMusic();
                } else {
                    if (playService.isPause()) {
                        playPauseMusic.setImageResource(R.mipmap.nowplaying_pause_n);
                        this.playService.start();
                    } else {
                        this.playService.playMusic(playService.getCurrentPosition());
                    }
                }
                break;
            case R.id.prevMusic:
                playService.prevMusic();    //播放上一首ddd
                break;
            case R.id.nextMusic:
                playService.nextMusic();   //播放下一首
                break;
            case R.id.playMode:
                int mode = (int) playMode.getTag();
                switch (mode){
                    case PlayService.PLAY_ORDER:
                        playMode.setImageResource(R.mipmap.tool_repeat_current_n);
                        playMode.setTag(PlayService.PLAY_SINGLE);
                        playService.setPlayMode(PlayService.PLAY_SINGLE);
                        Toast.makeText(PlayActivity.this, "单曲循环", Toast.LENGTH_SHORT).show();
                        break;
                    case PlayService.PLAY_SINGLE:
                        playMode.setImageResource(R.mipmap.tool_repeat_shuffle_n);
                        playMode.setTag(PlayService.PLAY_RANDOM);
                        playService.setPlayMode(PlayService.PLAY_RANDOM);
                        Toast.makeText(PlayActivity.this, "随机播放", Toast.LENGTH_SHORT).show();
                        break;
                    case PlayService.PLAY_RANDOM:
                        playMode.setImageResource(R.mipmap.tool_repeat_all_n);
                        playMode.setTag(PlayService.PLAY_ORDER);
                        playService.setPlayMode(PlayService.PLAY_ORDER);
                        Toast.makeText(PlayActivity.this, "顺序播放", Toast.LENGTH_SHORT).show();
                        break;
                    default:break;
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindPlayService();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser){
            playService.seekTo(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
