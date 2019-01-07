package com.ghd.ts.ddmusic.entity;

public class Music {

    private String musicName; //歌名

    private String singer; //歌手

    private String path; //歌曲的地址

    private int musicLength; //歌曲长度

    private String imageId; //歌曲图片

    private long size;

    public Music() {

    }


    public Music(String musicName, String singer) {
        this.musicName = musicName;
        this.singer = singer;
    }

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getMusicLength() {
        return musicLength;
    }

    public void setMusicLength(int musicLength) {
        this.musicLength = musicLength;
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
