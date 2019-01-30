package com.ghd.ts.ddmusic;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ghd.ts.ddmusic.entity.Music;
import com.ghd.ts.ddmusic.service.MusicService;
import com.ghd.ts.ddmusic.utils.MusicUtils;

import java.util.ArrayList;
import java.util.Random;

public class ListenMusicShowActivity extends AppCompatActivity {

    private ArrayList<Music> list;
    private int mPosition;
    private TextView musicNameTextView;
    private TextView musicDurationTextView;
    private TextView  musicNowDurationTextView;
    private ImageView mImageView;
    private ImageButton changePlayStyle;
    private MusicService.MusicBinder musicControl;
    private SeekBar mSeekBar;
    private boolean isBound = false;
    private MusicService service = null;
    private int mCurrentposition;
    private Random random;
    private static final int UPDATE_PROGRESS = 0;
    private MyReceiver receiver;
    private int play_style;

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            isBound = true;
            musicControl = (MusicService.MusicBinder)binder;
            service = musicControl.getMusicService();
            mPosition = service.getmPosition();
            mSeekBar.setMax(musicControl.getDuration());
            mSeekBar.setProgress(musicControl.getCurrenPostion());
            resetMusicNameTitle(mPosition);

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

    //使用handler定时更新进度条
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_PROGRESS:
                    updateProgress();
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listen_music_show);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        musicNameTextView = findViewById(R.id.top_music_name);
        musicDurationTextView = findViewById(R.id.music_duration);
        musicNowDurationTextView = findViewById(R.id.now_music_duration);



        Intent intent2 = new Intent(this, MusicService.class);
        bindService(intent2, conn, BIND_AUTO_CREATE);

        Intent intent = getIntent();
        list = (ArrayList<Music>) intent.getSerializableExtra("musicList");
        Music music = list.get(mPosition);
        musicNameTextView.setText(music.getMusicName().replaceAll(".mp3", ""));
        musicDurationTextView.setText(MusicUtils.formatTime(music.getDuration()));

        final ImageButton onOffButton = findViewById(R.id.music_list_on_off);
        onOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (musicControl.isPlaying()) {
                    onOffButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause));
                    musicControl.pause();
                } else {
                    musicControl.play(list.get(mPosition).getPath());
                    onOffButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_play));
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
            public void onClick(View v) {
                frontMusic(mPosition);
                resetMusicNameTitle(mPosition);
            }
        });

        ImageButton nextMusicButton = findViewById(R.id.next_music);
        nextMusicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextMusic(mPosition);
                resetMusicNameTitle(mPosition);
            }
        });

        mSeekBar = findViewById(R.id.seekBar);

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //进度条改变
                if (fromUser){
                    musicControl.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //开始触摸进度条
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //停止触摸进度条
            }
        });

        receiver=new MyReceiver();
        IntentFilter filter=new IntentFilter();
        filter.addAction("com.ghd.ddmusic.play_style");
        ListenMusicShowActivity.this.registerReceiver(receiver,filter);

        changePlayStyle = findViewById(R.id.on_off_button);
        changePlayStyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(play_style == 0){
                    changePlayStyle.setImageDrawable(getResources().getDrawable(R.drawable.ic_random));
                    service.setPlay_style(1);
                }
                if (play_style == 1){
                    changePlayStyle.setImageDrawable(getResources().getDrawable(R.drawable.ic_list_cir));
                    service.setPlay_style(2);
                }
                if (play_style == 2){
                    changePlayStyle.setImageDrawable(getResources().getDrawable(R.drawable.ic_cir));
                    service.setPlay_style(0);
                }
            }
        });



        mImageView = findViewById(R.id.music_image);

    }


    //更换头部音乐名
    private void resetMusicNameTitle(int position){
        Music music = list.get(position);
        musicNameTextView.setText(music.getMusicName().replaceAll(".mp3", ""));
        musicDurationTextView.setText(MusicUtils.formatTime(music.getDuration()));
    }

    //更新上方的文字和下面按钮
    public void updatePlayText() {
        ImageButton onOffButton = findViewById(R.id.on_off_button);
        if (musicControl.isPlaying()) {
            onOffButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause));
        } else {
            onOffButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_play));
        }
    }



    // 上一曲
    public void frontMusic(int position) {
        mPosition = position;
        mPosition--;
        if (mPosition < 0) {
            mPosition = list.size() - 1;
        }
        musicControl.play(list.get(mPosition).getPath());
    }

    // 下一曲
    public int nextMusic(int position) {
        mPosition = position;
        mPosition++;
        if (mPosition > list.size() - 1) {
            mPosition = 0;
        }
        musicControl.play(list.get(mPosition).getPath());
        return mPosition;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //进入到界面后开始更新进度条
        if (musicControl != null){
            handler.sendEmptyMessage(UPDATE_PROGRESS);
        }
    }




    //设置播放顺序
    public void setPlayStyle(int play_style_0){
        service.setPlay_style(play_style_0);
    }

    //单曲循环
    private void cil_nextMusic(){
        musicControl.play(list.get(mPosition).getPath());
    }

    // 随机播放
    private void random_nextMusic(){
        mCurrentposition = mPosition + random.nextInt(list.size() - 1);
        mCurrentposition %= list.size();
        musicControl.play(list.get(mCurrentposition).getPath());
    }

    // 顺序播放
    private void list_cil_nextMusic(){
        nextMusic(mPosition);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //停止更新进度条的进度
        handler.removeCallbacksAndMessages(null);
    }

    //更新进度条
    private void updateProgress() {
        int currenPostion = musicControl.getCurrenPostion();
        mSeekBar.setProgress(currenPostion);
        musicNowDurationTextView.setText(MusicUtils.formatTime(currenPostion));
        //使用Handler每500毫秒更新一次进度条
        handler.sendEmptyMessageDelayed(UPDATE_PROGRESS, 500);
    }


    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle=intent.getExtras();
            play_style=bundle.getInt("play_style");
            switch (play_style) {
                case 0 : cil_nextMusic();
                case 1 : random_nextMusic();
                case 2 : list_cil_nextMusic();
            }
        }
    }


}
