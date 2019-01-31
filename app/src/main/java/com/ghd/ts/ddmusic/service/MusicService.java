package com.ghd.ts.ddmusic.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.ghd.ts.ddmusic.AllMusicActivity;
import com.ghd.ts.ddmusic.entity.Music;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public class MusicService extends Service {

    private MediaPlayer mPlayer;
    public static int mPosition;
    public static int mPlayStyle = 0; // 默认单曲循环
    private List<Music> mMusicList;
    private Random mRandom;
    public static boolean mIsplaying;
    private static Music mMusic;


    public MusicService() {

    }

    public int getPosition() {
        return this.mPosition;
    }

    public void setPosition(int position) {
        this.mPosition = position;
    }

    public void setPlayStyle(int playStyle) {
        this.mPlayStyle = playStyle;
    }

    public int getPlayStyle() {
        return mPlayStyle;
    }

    public void setmIsplaying(boolean mIsplaying) {
        MusicService.mIsplaying = mIsplaying;
    }

    public void onCreate() {
        super.onCreate();
        mPlayer = new MediaPlayer();
        mMusicList = AllMusicActivity.mList;
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                switch (mPlayStyle) {
                    case 0://单曲循环
                        new MusicBinder().cil_nextMusic();
                        break;
                    case 1://随机播放
                        new MusicBinder().random_nextMusic();
                        break;
                    case 2://顺序播放
                        new MusicBinder().list_cil_nextMusic();
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

        public MusicService getMusicService() {
            return MusicService.this;
        }

        //判断是否处于播放状态
        public boolean isPlaying() {
            return mPlayer.isPlaying();
        }

        //暂停歌曲
        public void pause() {
            if (mPlayer.isPlaying()) {
                mPlayer.pause();
                setmIsplaying(false);
            }
        }

        //继续播放
        public void playContinue() {

            if (!mPlayer.isPlaying()) {
                mPlayer.start();
                setmIsplaying(true);
            }
        }

        //播放歌曲
        public void play(int position) {
            setPosition(position);
            mMusic = mMusicList.get(position);
            setmIsplaying(true);
            try {
                mPlayer.reset();
                mPlayer.setDataSource(mMusicList.get(mPosition).getPath());
                mPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mPlayer.start();
                }
            });
            //Log.e("服务", "播放音乐");

        }

        //停止播放
        public void stop() {
            mPlayer.stop();
            setmIsplaying(false);
        }

        //返回歌曲的长度，单位为毫秒
        public int getDuration() {
            return mPlayer.getDuration();
        }

        //返回歌曲目前的进度，单位为毫秒
        public int getCurrenPostion() {
            return mPlayer.getCurrentPosition();
        }

        //设置歌曲播放的进度，单位为毫秒
        public void seekTo(int mesc) {
            mPlayer.seekTo(mesc);
        }

        //单曲循环
        private void cil_nextMusic() {
            play(mPosition);
        }

        // 随机播放
        private void random_nextMusic() {
            mRandom = new Random();
            mPosition = mPosition + mRandom.nextInt(mMusicList.size() - 1);
            mPosition %= mMusicList.size();
            setPosition(mPosition);
            play(mPosition);
        }

        // 顺序播放
        private void list_cil_nextMusic() {
            nextMusic();
        }

        // 上一曲
        public void lastMusic() {
            setPosition(--mPosition);
            if (mPosition < 0) {
                mPosition = mMusicList.size() - 1;
            }
            play(mPosition);
        }

        // 下一曲
        public void nextMusic() {
            setPosition(++mPosition);
            if (mPosition > mMusicList.size() - 1) {
                mPosition = 0;
            }
            play(mPosition);
        }

    }


}
