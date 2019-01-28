package com.ghd.ts.ddmusic;

import android.app.ActionBar;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ghd.ts.ddmusic.entity.Music;
import com.ghd.ts.ddmusic.utils.MusicUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ListenMusicShowActivity extends AppCompatActivity {

    private MediaPlayer mplayer=new MediaPlayer();
    private ArrayList<Music> list;
    private int mPosition;
    private TextView musicNameTextView;
    private TextView musicDurationTextView;
    private SeekBar mSeekBar;
    private ImageView mImageView;
    private Thread thread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listen_music_show);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        musicNameTextView = findViewById(R.id.top_music_name);
        musicDurationTextView = findViewById(R.id.music_duration);
        Intent intent = getIntent();
        mPosition = intent.getIntExtra("position",mPosition);
        list =  (ArrayList<Music>)intent.getSerializableExtra("musicList");
        Music music = list.get(mPosition);
        musicNameTextView.setText(music.getMusicName().replaceAll(".mp3",""));
        musicDurationTextView.setText(MusicUtils.formatTime(music.getDuration()));

        ImageButton onOffButton = findViewById(R.id.music_list_on_off);
        onOffButton.setOnClickListener(new View.OnClickListener() {
            ImageButton onOffButton = findViewById(R.id.music_list_on_off);
            @Override
            public  void onClick(View v){
                if (mplayer.isPlaying()) {
                    mplayer.pause();
                    onOffButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_play));
                } else {
                    mplayer.start();
                    thread = new Thread(new SeekBarThread());
                    thread.start();
                    onOffButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_stop));
                }
            }
        });

        ImageButton blackButton = findViewById(R.id.black_button);
        blackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ImageButton lastMusicButton = findViewById(R.id.last_music);
        lastMusicButton.setOnClickListener(new View.OnClickListener() {
            public  void onClick(View v){
                frontMusic();

            }
        });
        ImageButton nextMusicButton = findViewById(R.id.next_music);
        nextMusicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public  void onClick(View v){
                nextMusic();
            }
        });
    }

/*    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_listen_music_show, menu);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.to_down:
                finish();
                break;
            case R.id.more:

                Toast.makeText(ListenMusicShowActivity.this, "" + "更改", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }


        return super.onOptionsItemSelected(item);
    }




    // 上一曲
    private void frontMusic() {
        mPosition--;
        if (mPosition < 0) {
            mPosition = list.size() - 1;
        }
        musicplay(mPosition);
        Music music = list.get(mPosition);
        musicNameTextView.setText(music.getMusicName().replaceAll(".mp3",""));
        musicDurationTextView.setText(MusicUtils.formatTime(music.getDuration()));

    }

    // 下一曲
    private void nextMusic() {
        mPosition++;
        if (mPosition > list.size() - 1) {
            mPosition = 0;
        }
        musicplay(mPosition);
        Music music = list.get(mPosition);
        musicNameTextView.setText(music.getMusicName().replaceAll(".mp3",""));
        musicDurationTextView.setText(MusicUtils.formatTime(music.getDuration()));
    }

    //播放音乐
    private void musicplay(int position) {
        mSeekBar = findViewById(R.id.seekBar);
        mImageView = findViewById(R.id.music_image);
        mSeekBar.setMax(list.get(position).getDuration());
        mImageView.startAnimation(AnimationUtils.loadAnimation(
                ListenMusicShowActivity.this, R.anim.imageview_rotate));
        try {
            List<Music> list = MusicUtils.getMusicData(ListenMusicShowActivity.this);
            mplayer.reset();
            mplayer.setDataSource(list.get(position).getPath());
            mplayer.prepare();
            mplayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        thread = new Thread(new SeekBarThread());
        thread.start();
    }


    class SeekBarThread implements Runnable {

        @Override
        public void run() {
            while (mplayer.isPlaying()) {
                mSeekBar = findViewById(R.id.seekBar);
                // 将SeekBar位置设置到当前播放位置
                mSeekBar.setProgress(mplayer.getCurrentPosition());

                try {
                    // 每500毫秒更新一次位置
                    Thread.sleep(500);
                    // 播放进度

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }







}
