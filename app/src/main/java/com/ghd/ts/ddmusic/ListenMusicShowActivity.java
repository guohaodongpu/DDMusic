package com.ghd.ts.ddmusic;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ghd.ts.ddmusic.entity.Music;
import com.ghd.ts.ddmusic.service.MusicService;
import com.ghd.ts.ddmusic.utils.MusicUtils;

public class ListenMusicShowActivity extends AppCompatActivity {

    private int mPosition;
    private TextView mMusicNameTextView;
    private TextView mMusicDurationTextView;
    private TextView mMusicNowDurationTextView;
    private ImageView mImageView;
    private ImageButton mChangePlayStyle;
    private ImageButton mOnOffButton;
    private MusicService.MusicBinder mMusicControl;
    private SeekBar mSeekBar;
    private boolean mIsBound = false;
    private MusicService mService = null;
    private static final int UPDATE_PROGRESS = 0;
    private int mCurrenPostion;

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            mIsBound = true;
            mMusicControl = (MusicService.MusicBinder) binder;
            mService = mMusicControl.getMusicService();
            mPosition = MusicService.mPosition;
            mSeekBar.setMax(mMusicControl.getDuration());
            mSeekBar.setProgress(mMusicControl.getCurrenPostion());
            updatePlayBtn();
            updateTitle();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mIsBound = false;
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
        mMusicNameTextView = findViewById(R.id.top_music_name);
        mMusicDurationTextView = findViewById(R.id.music_duration);
        mMusicNowDurationTextView = findViewById(R.id.now_music_duration);

        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, conn, BIND_AUTO_CREATE);

        mMusicDurationTextView.setText(MusicUtils.formatTime(AllMusicActivity
                .mList.get(MusicService.mPosition).getDuration()));

        mOnOffButton = findViewById(R.id.music_list_on_off);
        mOnOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMusicControl.isPlaying()) {
                    mOnOffButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_play));
                    mMusicControl.pause();
                } else {
                    mMusicControl.playContinue();
                    mOnOffButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause));
                }
                updateMusicImageViewRotate();
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
                mMusicControl.lastMusic();
                updateTitle();
            }
        });

        ImageButton nextMusicButton = findViewById(R.id.next_music);
        nextMusicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMusicControl.nextMusic();
                updateTitle();
            }
        });

        mSeekBar = findViewById(R.id.seekBar);

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //进度条改变
                if (fromUser) {
                    mMusicControl.seekTo(progress);
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

        mChangePlayStyle = findViewById(R.id.play_style);
        mChangePlayStyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mService.getPlayStyle() == 0) {
                    mChangePlayStyle.setImageDrawable(getResources()
                            .getDrawable(R.drawable.ic_random));
                    mService.setPlayStyle(1);
                } else if (mService.getPlayStyle() == 1) {
                    mChangePlayStyle.setImageDrawable(getResources()
                            .getDrawable(R.drawable.ic_list_cir));
                    mService.setPlayStyle(2);
                } else if (mService.getPlayStyle() == 2) {
                    mChangePlayStyle.setImageDrawable(getResources()
                            .getDrawable(R.drawable.ic_cir));
                    mService.setPlayStyle(0);
                }
            }
        });

        mImageView = findViewById(R.id.music_image);

        handler.sendEmptyMessage(UPDATE_PROGRESS);
    }

    //下面按钮
    private void updatePlayBtn() {
        if (MusicService.mIsplaying) {
            mOnOffButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause));
        } else {
            mOnOffButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_play));
        }

    }

    //更新文字
    private void updateTitle() {
        Music music = AllMusicActivity.mList.get(MusicService.mPosition);
        mMusicNameTextView.setText(music.getSinger() + "-" +
                music.getMusicName().replaceAll(".mp3", ""));
        mMusicDurationTextView.setText(MusicUtils.formatTime(music.getDuration()));
    }


    @Override
    protected void onResume() {
        super.onResume();
        //进入到界面后开始更新进度条
        if (mMusicControl != null) {
            handler.sendEmptyMessage(UPDATE_PROGRESS);
        }
    }

    //设置播放顺序
    public void setPlayStyle(int playStyle) {
        mService.setPlayStyle(playStyle);
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
        mCurrenPostion = mMusicControl.getCurrenPostion();
        mSeekBar.setProgress(mCurrenPostion);
        mMusicNowDurationTextView.setText(MusicUtils.formatTime(mCurrenPostion));
        updateTitle();
        mSeekBar.setMax(mMusicControl.getDuration());
        mSeekBar.setProgress(mMusicControl.getCurrenPostion());
        //使用Handler每500毫秒更新一次进度条
        handler.sendEmptyMessageDelayed(UPDATE_PROGRESS, 300);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mMusicControl != null) {
            handler.sendEmptyMessage(UPDATE_PROGRESS);
        }
        if (MusicService.mPlayStyle == 0) {
            mChangePlayStyle.setImageDrawable(getResources().getDrawable(R.drawable.ic_cir));
        } else if (MusicService.mPlayStyle == 1) {
            mChangePlayStyle.setImageDrawable(getResources().getDrawable(R.drawable.ic_random));
        } else if (MusicService.mPlayStyle == 2) {
            mChangePlayStyle.setImageDrawable(getResources().getDrawable(R.drawable.ic_list_cir));
        }
        updateMusicImageViewRotate();

    }

    public void updateMusicImageViewRotate() {
        if (!MusicService.mIsplaying) {
            mImageView.clearAnimation();
        } else {
            mImageView.startAnimation(AnimationUtils.loadAnimation(
                    ListenMusicShowActivity.this, R.anim.imageview_rotate));
        }
    }

    public void allMusic(View view) {
        finish();
    }

}
