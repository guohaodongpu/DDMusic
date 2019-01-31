package com.ghd.ts.ddmusic.entity;

public class MusicList {

    private String mName;

    private String mDescription;

    private int mImageId;

    public MusicList(String name, String description, int imageId) {
        this.mName = name;
        this.mDescription = description;
        this.mImageId = imageId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public int getImageId() {
        return mImageId;
    }

    public void setImageId(int mImageId) {
        this.mImageId = mImageId;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        this.mDescription = description;
    }
}
