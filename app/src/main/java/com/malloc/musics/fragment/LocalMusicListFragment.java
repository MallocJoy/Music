package com.malloc.musics.fragment;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.malloc.musics.R;
import com.malloc.musics.activity.MainActivity;
import com.malloc.musics.activity.PlayActivity;
import com.malloc.musics.adapter.LocalMusicAdapter;
import com.malloc.musics.utils.MediaUtils;
import com.malloc.musics.views.CircleTransform;
import com.malloc.musics.vo.Mp3Info;

import java.util.ArrayList;
import java.util.Random;

/**
 * 本地音乐页面
 * 作者： 86563
 * 时间： 2016/12/2
 */
public class LocalMusicListFragment extends Fragment implements
        ListView.OnItemClickListener, View.OnClickListener {

    private ListView localMusicList;
    private TextView tv_musicName;
    private TextView tv_singer;
    private ImageView iv_palyMusic;
    private ImageView iv_nextMusic;
    private ImageView iv_album;

    private ArrayList<Mp3Info> dataList;
    private MainActivity mainActivity;
    private LocalMusicAdapter adapter;
    private LinearLayout musicInfo;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }

    //初始化页面
    public static LocalMusicListFragment newInstance() {
        return new LocalMusicListFragment();
    }

    @Nullable
    @Override //加载页面布局
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View localMusicVIew = inflater.inflate(R.layout.local_music_layout, container, false);

        localMusicList = ((ListView) localMusicVIew.findViewById(R.id.localMusicList));
        tv_musicName = ((TextView) localMusicVIew.findViewById(R.id.musicName));
        tv_singer = ((TextView) localMusicVIew.findViewById(R.id.singer));
        iv_palyMusic = ((ImageView) localMusicVIew.findViewById(R.id.playMusic));
        iv_nextMusic = ((ImageView) localMusicVIew.findViewById(R.id.nextMusic));
        iv_album = ((ImageView) localMusicVIew.findViewById(R.id.img));
        musicInfo = (LinearLayout) localMusicVIew.findViewById(R.id.musicInfo);

        initData();

        //组件的监听事件的注册
        localMusicList.setOnItemClickListener(this);
        iv_palyMusic.setOnClickListener(this);
        iv_nextMusic.setOnClickListener(this);
        iv_album.setOnClickListener(this);
        musicInfo.setOnClickListener(this);

        return localMusicVIew;
    }

    //查询本地音乐数据
    private void initData() {
        dataList = MediaUtils.getMp3Infos(mainActivity);
        adapter = new LocalMusicAdapter(mainActivity, dataList);
        localMusicList.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        mainActivity.bindPlayService();   //绑定播放服务
    }

    @Override
    public void onPause() {
        super.onPause();
        mainActivity.unbindPlayService();    //解绑播放服务
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mainActivity.playService.playMusic(position);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.playMusic:
                if (mainActivity.playService.isPlaying()) {
                    iv_palyMusic.setImageResource(R.mipmap.nowplaying_play_n);
                    mainActivity.playService.pauseMusic();
                } else {
                    if (mainActivity.playService.isPause()) {
                        iv_palyMusic.setImageResource(R.mipmap.nowplaying_pause_n);
                        mainActivity.playService.start();
                    } else {
                        mainActivity.playService.playMusic(mainActivity.playService.getCurrentPosition());
                    }
                }
                break;
            case R.id.nextMusic:
                mainActivity.playService.nextMusic();
                break;
            case R.id.img:
            case R.id.musicInfo:
                Intent intent = new Intent(mainActivity, PlayActivity.class);
                startActivity(intent);
                break;
        }
    }

    //回调更新Ui操作
    public void changeUiState(int position) {
        if (position >= 0 && position < dataList.size()) {
            Mp3Info mp3Info = dataList.get(position);
            tv_musicName.setText(mp3Info.getTitle());
            tv_singer.setText(mp3Info.getArtist());

            if (mainActivity.playService.isPlaying()) {
                iv_palyMusic.setImageResource(R.mipmap.nowplaying_pause_n);
            }else {
                iv_palyMusic.setImageResource(R.mipmap.nowplaying_play_n);
            }
            /*Bitmap artwork = MediaUtils.getArtwork(mainActivity, mp3Info.getId(),
                    mp3Info.getAlbumId(), true, true);*/
            //加载圆角图片
            Glide.with(mainActivity)
                    .load(ContentUris.withAppendedId(MediaUtils.albumArtUri, mp3Info.getAlbumId()))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transform(new CircleTransform(mainActivity))
                    .placeholder(R.mipmap.app_logo2)
                    .error(R.mipmap.app_logo2)
                    .crossFade()
                    .into(iv_album);
        }
    }
}
