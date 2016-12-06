package com.malloc.musics.application;

import android.app.Application;
import android.content.SharedPreferences;

import com.malloc.musics.utils.Contant;

/**全局类
 * 作者： 86563
 * 时间： 2016/12/6
 */

public class BaseApplication extends Application {
    public  SharedPreferences sp;

    @Override
    public void onCreate() {
        super.onCreate();
        sp = getSharedPreferences(Contant.SP_NAME,MODE_PRIVATE);
    }
}
