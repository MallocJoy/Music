package com.malloc.musics.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.malloc.musics.R;
import com.malloc.musics.utils.MediaUtils;
import com.malloc.musics.vo.Mp3Info;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 本地音乐适配器
 * 作者： 86563
 * 时间： 2016/12/2
 */

public class LocalMusicAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Mp3Info> dataList;

    public LocalMusicAdapter(Context context, ArrayList<Mp3Info> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    //设置音乐信息
    public void setDataList(ArrayList<Mp3Info> dataList) {
        this.dataList = dataList;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int i) {
        return dataList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder holder = null;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.music_item, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        holder = (ViewHolder) view.getTag();

        Mp3Info mp3Info = dataList.get(i);
        holder.musicName.setText(mp3Info.getTitle());
        holder.musicSinger.setText(mp3Info.getArtist());
        holder.musicTime.setText(MediaUtils.formatTime(mp3Info.getDuration()));

        return view;
    }

    static class ViewHolder {
        @BindView(R.id.musicName)
        TextView musicName;
        @BindView(R.id.musicTime)
        TextView musicTime;
        @BindView(R.id.singer)
        TextView musicSinger;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}









