package com.ghd.ts.ddmusic.entity;

public class Music {

    private String musicName; //歌名

    private String singer; //歌手

    private String path; //歌曲的地址

    private int musicLength; //歌曲长度

    private int imageId; //歌曲图片


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

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }
}
