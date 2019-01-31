package com.ghd.ts.ddmusic.utils;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

import com.ghd.ts.ddmusic.entity.Music;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;


public class MusicUtils {
    /**
     * 扫描系统里面的音乐，返回一个list集合
     */
    public static ArrayList<Music> getMusicData(Context context) {
        ArrayList<Music> list = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,
                null, null, MediaStore.Audio.AudioColumns.IS_MUSIC);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Music Music = new Music();
                Music.setMusicName(cursor.getString(
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME))
                );
                Music.setSinger(cursor.getString(
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))
                );
                Music.setPath(cursor.getString(
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
                );
                Music.setDuration(cursor.getInt(
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
                );
                Music.setSize(cursor.getLong(
                        cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE))
                );
                if (Music.getSize() > 1000 * 800) {//过滤掉短音频
                    // 分离出歌曲名和歌手
                    if (Music.getMusicName().contains("-")) {
                        String[] str = Music.getMusicName().split("-");
                        Music.setSinger(str[0]);
                        Music.setMusicName(str[1]);
                    }
                    list.add(Music);
                }
            }
            // 释放资源
            cursor.close();
        }
        return list;
    }

    //格式化时间
    public static String formatTime(int time) {
        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        return formatter.format(time);
    }

}
