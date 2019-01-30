package com.ghd.ts.ddmusic.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.ghd.ts.ddmusic.entity.Music;
import com.ghd.ts.ddmusic.utils.MusicUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Random;

public class MusicService extends Service {


    private MediaPlayer player;
    private int mPosition;
    private String path;
    private int play_style = 0;//默认单曲循环
    private Music music;

    public MusicService() {

    }

    public int getmPosition() {
        return mPosition;
    }

    public MediaPlayer getPlayer() {
        return player;
    }

    public void setPlayer(MediaPlayer player) {
        this.player = player;
    }

    public void setmPosition(int mPosition) {
        this.mPosition = mPosition;
    }

    public void setPlay_style(int play_style){
        this.play_style = play_style;
    }

    public void onCreate() {
        super.onCreate();
        player = new MediaPlayer();
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Intent intent = new Intent();
                intent.setAction("com.ghd.ddmusic.play_style");
                switch (play_style) {
                    case 0://单曲循环
                        intent.putExtra("play_style", 0);
                        sendBroadcast(intent);
                        break;
                    case 1://顺序播放
                        intent.putExtra("play_style", 1);
                        sendBroadcast(intent);
                        break;
                    case 2://随机播放
                        intent.putExtra("play_style", 2);
                        sendBroadcast(intent);
                        break;
                    default:
                        break;
                }
            }
        });


    }


    @Override
    public IBinder onBind(Intent intent) {
        return new MusicBinder();
    }

    public class MusicBinder extends Binder {

        public MusicService getMusicService(){
            return MusicService.this;
        }

        private ArrayList<Music> list;
        //判断是否处于播放状态
        public boolean isPlaying() {
            return player.isPlaying();
        }

        //setMuisicList

        public void setMuisicList(ArrayList<Music> list){
            this.list = list;
        }

        //暂停歌曲
        public void pause() {
            if (player.isPlaying()) {
                player.pause();
            }
        }

        //播放歌曲
        public void play(String playPath) {
                try {
                    player.reset();
                    player.setDataSource(playPath);
                    player.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        player.start();
                    }
                });
                Log.e("服务", "播放音乐");

        }

        //停止播放
        public void stop(){
            player.stop();
        }

        //返回歌曲的长度，单位为毫秒
        public int getDuration() {
            return player.getDuration();
        }

        //返回歌曲目前的进度，单位为毫秒
        public int getCurrenPostion() {
            return player.getCurrentPosition();
        }

        //设置歌曲播放的进度，单位为毫秒
        public void seekTo(int mesc) {
            player.seekTo(mesc);
        }

    }



}
