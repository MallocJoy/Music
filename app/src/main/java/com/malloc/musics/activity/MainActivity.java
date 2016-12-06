package com.malloc.musics.activity;

import android.app.Application;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.astuetz.PagerSlidingTabStrip;
import com.malloc.musics.R;
import com.malloc.musics.application.BaseApplication;
import com.malloc.musics.fragment.LocalMusicListFragment;
import com.malloc.musics.fragment.NetMusicListFragment;

import java.util.Random;

public class MainActivity extends BaseActivity {

	private PagerSlidingTabStrip tabs;
	private ViewPager pager;

	LocalMusicListFragment localMusicListFragment;
	NetMusicListFragment netMusicListFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		pager = (ViewPager) findViewById(R.id.pager);
		MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager());

		pager.setAdapter(adapter);
		tabs.setViewPager(pager);

		//绑定服务
		bindPlayService();
	}

	@Override
	public void publish(int progress) {
		//更新进度条
	}

	@Override
	public void change(int position) {
		//切换播放状态
		if (pager.getCurrentItem() == 0){
			localMusicListFragment.changeUiState(position);
			Random random = new Random();
			tabs.setIndicatorColor(Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
		}else if (pager.getCurrentItem() == 1){
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		//保存当前的一些状态值
        BaseApplication app = (BaseApplication) getApplication();
        SharedPreferences.Editor edit = app.sp.edit();
        edit.putInt("currentPosition",playService.getCurrentPosition());
        edit.putInt("playMode",playService.getPlayMode());
        edit.apply();
    }

	public class MyPagerAdapter extends FragmentPagerAdapter {

		private final String[] TITLES = { "本地音乐","网络推荐"};


		MyPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return TITLES[position];
		}

		@Override
		public int getCount() {
			return TITLES.length;
		}

		@Override
		public Fragment getItem(int position) {
			if (position == 0){
				if (localMusicListFragment == null){
					localMusicListFragment = LocalMusicListFragment.newInstance();
				}
				return localMusicListFragment;
			}else if (position ==1){
				if (netMusicListFragment == null){
					netMusicListFragment = NetMusicListFragment.newInstance();
				}
				return netMusicListFragment;
			}
			return null;
		}
	}
}