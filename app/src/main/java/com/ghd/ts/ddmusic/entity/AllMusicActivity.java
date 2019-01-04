package com.ghd.ts.ddmusic.entity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ghd.ts.ddmusic.R;
import com.ghd.ts.ddmusic.entity.Music;
import java.util.ArrayList;
import java.util.List;

public class AllMusicActivity extends AppCompatActivity {

    private List<Music> muisicList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_music);

    }

}
