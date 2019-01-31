package com.ghd.ts.ddmusic.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ghd.ts.ddmusic.AllMusicActivity;
import com.ghd.ts.ddmusic.R;
import com.ghd.ts.ddmusic.entity.Music;
import com.ghd.ts.ddmusic.utils.MusicUtils;

import java.util.List;

public class MusicAdapter extends BaseAdapter {
    private Context mContext;
    private List<Music> mList;
    private int mPositionFlag = 0;

    public MusicAdapter(AllMusicActivity allMusicActivity, List<Music> list) {
        this.mContext = allMusicActivity;
        this.mList = list;
    }

    @Override
    public int getCount() {
        if (mList == null) {
            return 0;
        } else {
            return mList.size();
        }
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder holder = null;
        if (view == null) {
            holder = new ViewHolder();
            // 引入布局
            view = View.inflate(mContext, R.layout.layout_all_music_list, null);
            // 实例化对象
            holder.song = view.findViewById(R.id.music_name);
            holder.singer = view.findViewById(R.id.music_singer);
            holder.duration = view.findViewById(R.id.music_duration);
            //holder.position = view.findViewById(R.id.music_postion);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // 给控件赋值
        String string_song = mList.get(i).getMusicName();
        if (string_song.length() >= 5
                && string_song.substring(string_song.length() - 4,
                string_song.length()).equals(".mp3")) {
            holder.song.setText(string_song.substring(0,
                    string_song.length() - 4).trim());
        } else {
            holder.song.setText(string_song.trim());
        }
        holder.singer.setText(mList.get(i).getSinger().trim());
        // 时间转换为时分秒
        int duration = mList.get(i).getDuration();
        String time = MusicUtils.formatTime(duration);
        holder.duration.setText(time);
        return view;
    }

    class ViewHolder {
        TextView song;// 歌曲名
        TextView singer;// 歌手
        TextView duration;// 时长
        //TextView position;// 序号获取
    }

}
