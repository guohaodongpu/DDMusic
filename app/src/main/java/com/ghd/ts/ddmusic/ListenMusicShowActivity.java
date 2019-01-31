package com.ghd.ts.ddmusic;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
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

public class ListenMusicShowActivity extends AppCompatActivity {

    private int mPosition;
    private TextView musicNameTextView;
    private TextView musicDurationTextView;
    private TextView musicNowDurationTextView;
    private ImageView mImageView;
    private ImageButton changePlayStyle;
    private ImageButton onOffButton;
    private MusicService.MusicBinder musicControl;
    private SeekBar mSeekBar;
    private boolean isBound = false;
    private MusicService service = null;
    private static final int UPDATE_PROGRESS = 0;
    private int mCurrenPostion;

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            isBound = true;
            musicControl = (MusicService.MusicBinder) binder;
            service = musicControl.getMusicService();
            mPosition = MusicService.mPosition;
            mSeekBar.setMax(musicControl.getDuration());
            mSeekBar.setProgress(musicControl.getCurrenPostion());
            updatePlayBtn();
            updateTitle();
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

        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, conn, BIND_AUTO_CREATE);

        musicDurationTextView.setText(MusicUtils.formatTime(AllMusicActivity
                .list.get(MusicService.mPosition).getDuration()));

        onOffButton = findViewById(R.id.music_list_on_off);
        onOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (musicControl.isPlaying()) {
                    onOffButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_play));
                    musicControl.pause();
                } else {
                    musicControl.play(MusicService.mPosition);
                    onOffButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause));
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
                musicControl.lastMusic();
                updateTitle();
            }
        });

        ImageButton nextMusicButton = findViewById(R.id.next_music);
        nextMusicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicControl.nextMusic();
                updateTitle();
            }
        });

        mSeekBar = findViewById(R.id.seekBar);

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //进度条改变
                if (fromUser) {
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

        changePlayStyle = findViewById(R.id.play_style);
        changePlayStyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (service.getPlayStyle() == 0) {
                    changePlayStyle.setImageDrawable(getResources().getDrawable(R.drawable.ic_random));
                    service.setPlayStyle(1);
                } else if (service.getPlayStyle() == 1) {
                    changePlayStyle.setImageDrawable(getResources().getDrawable(R.drawable.ic_list_cir));
                    service.setPlayStyle(2);
                } else if (service.getPlayStyle() == 2) {
                    changePlayStyle.setImageDrawable(getResources().getDrawable(R.drawable.ic_cir));
                    service.setPlayStyle(0);
                }
            }
        });

        mImageView = findViewById(R.id.music_image);

    }

    //下面按钮
    private void updatePlayBtn() {
        if (MusicService.mIsplaying) {
            onOffButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause));
        } else {
            onOffButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_play));
        }

    }
    //更新文字
    private void updateTitle(){
        Music music = AllMusicActivity.list.get(MusicService.mPosition);
        musicNameTextView.setText(music.getMusicName().replaceAll(".mp3", ""));
        musicDurationTextView.setText(MusicUtils.formatTime(music.getDuration()));
    }



    @Override
    protected void onResume() {
        super.onResume();
        //进入到界面后开始更新进度条
        if (musicControl != null) {
            handler.sendEmptyMessage(UPDATE_PROGRESS);
        }
    }

    //设置播放顺序
    public void setPlayStyle(int playStyle) {
        service.setPlayStyle(playStyle);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //停止更新进度条的进度
        handler.removeCallbacksAndMessages(null);
        unbindService(conn);
    }

    //更新进度条
    private void updateProgress() {
        mCurrenPostion = musicControl.getCurrenPostion();
        mSeekBar.setProgress(mCurrenPostion);
        musicNowDurationTextView.setText(MusicUtils.formatTime(mCurrenPostion));
        //使用Handler每500毫秒更新一次进度条
        handler.sendEmptyMessageDelayed(UPDATE_PROGRESS, 300);
    }

    public void allMusic(View view){
        finish();
    }

}
