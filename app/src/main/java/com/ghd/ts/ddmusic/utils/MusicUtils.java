package com.ghd.ts.ddmusic.utils;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.ghd.ts.ddmusic.entity.Music;

import java.util.ArrayList;
import java.util.List;


public class MusicUtils {
    /**
     * 扫描系统里面的音乐，返回一个list集合
     */
    public static List<Music> getMusicData(Context context) {
        List<Music> list = new ArrayList<>();
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
                Music.setMusicLength(cursor.getInt(
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
        if (time / 1000 % 60 < 10) {
            return time / 1000 / 60 + ":0" + time / 1000 % 60;
        } else {
            return time / 1000 / 60 + ":" + time / 1000 % 60;
        }
    }
}
