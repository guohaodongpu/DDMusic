package com.ghd.ts.ddmusic.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import com.ghd.ts.ddmusic.AllMusicActivity;
import com.ghd.ts.ddmusic.entity.Music;
import com.ghd.ts.ddmusic.utils.MusicUtils;

import java.util.List;

public class PlatMusicService extends Service {

    private MediaPlayer mplayer=new MediaPlayer();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("ServiceTest","  ----->  onCreate");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("ServiceTest","  ----->  onStartCommand");

        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d("ServiceTest","  ----->  onDestroy");

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    //播放音乐
    private void musicplay(int position) {
        try {
//            List<Music> list = MusicUtils.getMusicData(AllMusicActivity.this);
            mplayer.reset();
//            mplayer.setDataSource(list.get(position).getPath());
            mplayer.prepare();
            mplayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
