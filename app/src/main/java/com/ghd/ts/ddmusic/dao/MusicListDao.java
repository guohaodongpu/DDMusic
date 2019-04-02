package com.ghd.ts.ddmusic.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ghd.ts.ddmusic.db.MydatabaseHelper;
import com.ghd.ts.ddmusic.entity.Music;

import java.util.ArrayList;
import java.util.List;

public class MusicListDao {
    private MydatabaseHelper sqliteHelper;
    private SQLiteDatabase db;

    public MusicListDao(Context context) {
        sqliteHelper = new MydatabaseHelper(context, "Music.db", null, 2);
        db = sqliteHelper.getReadableDatabase();
    }

    //增加的方法
    public void add(Music music) {
        ContentValues values = new ContentValues();
        values.put("music_name", music.getMusicName());
        values.put("singer", music.getSinger());
        values.put("path", music.getPath());
        values.put("duration", music.getDuration());
        db.insert("MusicList", null, values);
    }

    //查询
    public ArrayList<Music> select() {
        Cursor rawQuery = db.rawQuery("select * from MusicList", null);
        ArrayList<Music> list = new ArrayList<>();
        Music music ;
        while (rawQuery.moveToNext()) {
            music = new Music();
            String music_name = rawQuery.getString(rawQuery.getColumnIndex("music_name"));
            String singer = rawQuery.getString(rawQuery.getColumnIndex("singer"));
            String path = rawQuery.getString(rawQuery.getColumnIndex("path"));
            int duration = rawQuery.getInt(rawQuery.getColumnIndex("duration"));
            music.setMusicName(music_name);
            music.setSinger(singer);
            music.setPath(path);
            music.setDuration(duration);
            list.add(music);
        }
        return list;
    }
/*
    public void delete(String name) {
        db.delete("MusicList", "name = ?", new String[]{name});
    }

    public void update(String tj, String name, String sex) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        db.update("MusicList", values, "name = ?", new String[]{tj});
    }*/
    public void deleteAll() {
        db.execSQL("DELETE FROM MusicList");
    }
}
