package com.ghd.ts.ddmusic.entity;

import android.widget.CursorAdapter;

import java.io.Serializable;

public class Music implements Serializable {

    private String mMusicName; //歌名

    private String mSinger; //歌手

    private String mPath; //歌曲的地址

    private int mDuration; //歌曲长度

    private String imageId; //歌曲图片

    private long size;

    //CursorAdapter

    public Music() {

    }


    public Music(String musicName, String singer) {
        this.mMusicName = musicName;
        this.mSinger = singer;
    }

    public String getMusicName() {
        return mMusicName;
    }

    public void setMusicName(String musicName) {
        this.mMusicName = musicName;
    }

    public String getSinger() {
        return mSinger;
    }

    public void setSinger(String singer) {
        this.mSinger = singer;
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        this.mPath = path;
    }

    public int getDuration() {
        return mDuration;
    }

    public void setDuration(int duration) {
        this.mDuration = duration;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
