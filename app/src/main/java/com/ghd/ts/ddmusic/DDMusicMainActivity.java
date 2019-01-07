package com.ghd.ts.ddmusic;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import com.ghd.ts.ddmusic.adapter.MusicListAdapter;
import com.ghd.ts.ddmusic.entity.MusicList;

import java.util.ArrayList;
import java.util.List;


public class DDMusicMainActivity extends AppCompatActivity {
    private List<MusicList> mMusicListList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ddmusic_main);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        initMusicList();
        MusicListAdapter adapter = new MusicListAdapter(
                DDMusicMainActivity.this, R.layout.layout_music_list_list, mMusicListList);
        ListView listView = findViewById(R.id.music_list_list_item);
        listView.setAdapter(adapter);

    }

    public void listenMusic_show(View v) {
        Intent intent = new Intent(DDMusicMainActivity.this, ListenMusicShowActivity.class);
        startActivity(intent);
    }
    public void allMusic_show(View v) {
        Intent intent = new Intent(DDMusicMainActivity.this, AllMusicActivity.class);
        startActivity(intent);
    }

    private void initMusicList() {
        for (int i = 0; i < 2; i++) {
            MusicList zhoujielun = new MusicList("周杰伦", "这是周杰伦的专辑", R.drawable.music);
            mMusicListList.add(zhoujielun);
            MusicList zhoujielun1 = new MusicList("周杰伦", "这是周杰伦的专辑", R.drawable.music);
            mMusicListList.add(zhoujielun1);
            MusicList zhoujielun2 = new MusicList("周杰伦", "这是周杰伦的专辑", R.drawable.music);
            mMusicListList.add(zhoujielun2);
            MusicList zhoujielun3 = new MusicList("周杰伦", "这是周杰伦的专辑", R.drawable.music);
            mMusicListList.add(zhoujielun3);
            MusicList zhoujielun4 = new MusicList("周杰伦", "这是周杰伦的专辑", R.drawable.music);
            mMusicListList.add(zhoujielun4);
            MusicList zhoujielun5 = new MusicList("周杰伦", "这是周杰伦的专辑", R.drawable.music);
            mMusicListList.add(zhoujielun5);
            MusicList zhoujielun6 = new MusicList("周杰伦", "这是周杰伦的专辑", R.drawable.music);
            mMusicListList.add(zhoujielun6);
            MusicList zhoujielun7 = new MusicList("周杰伦", "这是周杰伦的专辑", R.drawable.music);
            mMusicListList.add(zhoujielun7);
            MusicList zhoujielun8 = new MusicList("周杰伦", "这是周杰伦的专辑", R.drawable.music);
            mMusicListList.add(zhoujielun8);
            MusicList zhoujielun9 = new MusicList("周杰伦", "这是周杰伦的专辑", R.drawable.music);
            mMusicListList.add(zhoujielun9);
            MusicList zhoujielun10 = new MusicList("周杰伦", "这是周杰伦的专辑", R.drawable.music);
            mMusicListList.add(zhoujielun10);
        }
    }
}
