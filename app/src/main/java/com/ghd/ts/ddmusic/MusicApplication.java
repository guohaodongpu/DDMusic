package com.ghd.ts.ddmusic;

import android.app.Application;
import android.content.Intent;

import com.ghd.ts.ddmusic.entity.Music;
import com.ghd.ts.ddmusic.service.MusicService;

public class MusicApplication extends Application {


    private Music mMusic;

    public Music getmMusic() {
        return mMusic;
    }

    public void setmMusic(Music mMusic) {
        this.mMusic = mMusic;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Intent intent = new Intent(this, MusicService.class);
        startService(intent);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
