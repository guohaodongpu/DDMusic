package com.ghd.ts.ddmusic;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ghd.ts.ddmusic.dao.MusicListDao;
import com.ghd.ts.ddmusic.view.LrcView;
import com.ghd.ts.ddmusic.entity.Music;
import com.ghd.ts.ddmusic.service.MusicService;
import com.ghd.ts.ddmusic.utils.MusicUtils;

import java.util.ArrayList;
import java.util.List;

public class ListenMusicShowActivity extends AppCompatActivity {
    
    private int mPosition;
    private TextView mMusicNameTextView;
    private TextView mMusicSingerNameTextView;
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
    private View mMusicImageView, mMusicLyricView;
    private ViewPager mViewPager;
    private List<View> mViewList;
    private LrcView mLrcView;
    private List<String> mLrcList;
    private MusicListDao mMusicListDao;

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
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mMusicListDao = new MusicListDao(this);
        mMusicNameTextView = findViewById(R.id.top_music_name);
        mMusicSingerNameTextView = findViewById(R.id.top_music_singer_name);
        mMusicDurationTextView = findViewById(R.id.music_duration);
        mMusicNowDurationTextView = findViewById(R.id.now_music_duration);

        mViewPager = findViewById(R.id.music_show_viewpager);
        LayoutInflater inflater = getLayoutInflater();
        mMusicImageView = inflater.inflate(R.layout.layout_music_show_image, null);
        mMusicLyricView = inflater.inflate(R.layout.layout_music_show_lyric, null);
        mViewList = new ArrayList<View>();
        mViewList.add(mMusicImageView);
        mViewList.add(mMusicLyricView);
        PagerAdapter pagerAdapter = new PagerAdapter() {

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                // TODO Auto-generated method stub
                return arg0 == arg1;
            }

            @Override
            public int getCount() {
                // TODO Auto-generated method stub
                return mViewList.size();
            }

            @Override
            public void destroyItem(ViewGroup container, int position,
                                    Object object) {
                container.removeView(mViewList.get(position));
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(mViewList.get(position));
                return mViewList.get(position);
            }
        };
        mViewPager.setAdapter(pagerAdapter);

        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, conn, BIND_AUTO_CREATE);

        mMusicDurationTextView.setText(MusicUtils.formatTime(mMusicListDao.select().get(MusicService.mPosition).getDuration()));

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

        mImageView = mMusicImageView.findViewById(R.id.music_image);
        mLrcView = mMusicLyricView.findViewById(R.id.lrc_view);
        handler.sendEmptyMessage(UPDATE_PROGRESS);

        mLrcView.init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_listen_music_show, menu);

        return super.onCreateOptionsMenu(menu);
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
        Music music = mMusicListDao.select().get(MusicService.mPosition);
        mMusicNameTextView.setText(music.getMusicName().replaceAll(".mp3", ""));
        mMusicSingerNameTextView.setText("—"+music.getSinger()+"—");
        mMusicDurationTextView.setText(MusicUtils.formatTime(music.getDuration()));
    }

    //updatelyric
    private void updateLyric() {
        /*mLrcView.setLrc("[ti:想哭][ar:陈奕迅][al:special thanks to][by:昨夜星辰昨夜风]\n" +
                "[00:03.11]陈奕迅： 想哭\n" +
                "[00:05.07]作词：林夕　作曲：徐伟贤　编曲：Jim Lee\n" +
                "[00:06.16]\n" +
                "[00:06.63]想约在一个适合聊天的下午\n" +
                "[00:14.60]分开很多年满以为没有包袱\n" +
                "[00:22.18]我还打算回顾我们为何结束\n" +
                "[00:30.04]还想问你是不是一个人住\n" +
                "[00:37.48]\n" +
                "[00:38.63]当你的笑容给我礼貌的招呼\n" +
                "[00:46.46]当我想诉说这些年来的感触\n" +
                "[00:54.03]你却点了满桌我最爱的食物\n" +
                "[01:02.04]介绍我看一本天文学的书\n" +
                "[01:07.81]\n" +
                "[01:09.84]我想哭　不敢哭\n" +
                "[01:12.31]难道这种相处\n" +
                "[01:14.40]不像我们梦寐以求的幸福\n" +
                "[01:18.28]走下去　这一步\n" +
                "[01:20.59]是宽容　还是痛苦\n" +
                "[01:24.35]\n" +
                "[01:26.13]我想哭　怎么哭\n" +
                "[01:28.02]完成爱情旅途\n" +
                "[01:30.33]谈天说地是最理想的出路\n" +
                "[01:34.06]谈音乐　谈时事　不说爱\n" +
                "[01:38.13]若无其事　原来是　最狠的报复\n" +
                "[02:31.24]\n" +
                "[02:32.04]我想哭　不敢哭\n" +
                "[02:33.89]难道这种相处\n" +
                "[02:36.20]不像我们梦寐以求的幸福\n" +
                "[02:39.95]走下去　这一步\n" +
                "[02:41.76]是宽容　还是痛苦\n" +
                "[02:46.16]\n" +
                "[02:47.96]我想哭　怎么哭\n" +
                "[02:49.73]完成爱情旅途\n" +
                "[02:52.08]谈天说地是最理想的出路\n" +
                "[02:55.88]谈音乐　谈时事　不说爱\n" +
                "[03:44.33][03:35.98][02:59.85][01:38.13]若无其事　原来是　最狠的报复\n" +
                "[03:06.95][01:46.48]\n" +
                "[03:08.17][02:32.04][01:09.84]我想哭　不敢哭\n" +
                "[03:09.98][02:33.89][01:12.31]难道这种相处\n" +
                "[03:12.27][02:36.20][01:14.40]不像我们梦寐以求的幸福\n" +
                "[03:16.05][02:39.95][01:18.28]走下去　这一步\n" +
                "[03:18.13][02:41.76][01:20.59]是宽容　还是痛苦\n" +
                "[03:22.03][02:46.16][01:24.35]\n" +
                "[03:23.91][02:47.96][01:26.13]我想哭　怎么哭\n" +
                "[03:25.91][02:49.73][01:28.02]完成爱情旅途\n" +
                "[03:28.30][02:52.08][01:30.33]谈天说地是最理想的出路\n" +
                "[03:32.07][02:55.88][01:34.06]谈音乐　谈时事　不说爱\n" +
                "[03:44.33][03:35.98][02:59.85][01:38.13]若无其事　原来是　最狠的报复\n" +
                "[03:43.40][03:06.95][01:46.48]\n" +
                "[02:00.84]当我想坦白我们的乐多于苦\n" +
                "[02:08.52]你说水星它没有卫星　好孤独\n" +
                "[02:16.02]我才明白时间较分手还　残酷\n" +
                "[02:23.89]老朋友了　再没资格不满足");*/
        mLrcView.setLrc("[00:02.03]十年\n" +
                "[00:04.77]演唱：陈奕迅\n" +
                "[00:06.18]\n" +
                "[00:15.42]如果那两个字没有颤抖\n" +
                "[00:19.68]我不会发现 我难受\n" +
                "[00:23.09]怎么说出口\n" +
                "[00:26.58]也不过是分手\n" +
                "[00:31.18]如果对于明天没有要求\n" +
                "[00:35.24]牵牵手就像旅游\n" +
                "[00:38.30]成千上万个门口\n" +
                "[00:42.22]总有一个人要先走\n" +
                "[00:47.81]怀抱既然不能逗留\n" +
                "[00:51.23]何不在离开的时候\n" +
                "[00:54.11]一边享受 一边泪流\n" +
                "[01:01.34]十年之前\n" +
                "[01:03.35]我不认识你 你不属于我\n" +
                "[01:07.01]我们还是一样\n" +
                "[01:09.54]陪在一个陌生人左右\n" +
                "[01:13.48]走过渐渐熟悉的街头\n" +
                "[01:16.81]十年之后\n" +
                "[01:18.82]我们是朋友 还可以问候\n" +
                "[01:22.54]只是那种温柔\n" +
                "[01:25.08]再也找不到拥抱的理由\n" +
                "[01:28.89]情人最后难免沦为朋友\n" +
                "[01:35.50]\n" +
                "[01:57.73]怀抱既然不能逗留\n" +
                "[02:00.87]何不在离开的时候\n" +
                "[02:03.81]一边享受 一边泪流\n" +
                "[02:11.03]十年之前\n" +
                "[02:12.91]我不认识你 你不属于我\n" +
                "[02:16.73]我们还是一样\n" +
                "[02:19.30]陪在一个陌生人左右\n" +
                "[02:23.08]走过渐渐熟悉的街头\n" +
                "[02:26.39]十年之后\n" +
                "[02:28.50]我们是朋友 还可以问候\n" +
                "[02:32.13]只是那种温柔\n" +
                "[02:34.67]再也找不到拥抱的理由\n" +
                "[02:38.51]情人最后难免沦为朋友\n" +
                "[02:48.59]直到和你做了多年朋友\n" +
                "[02:52.80]才明白我的眼泪\n" +
                "[02:55.65]不是为你而流\n" +
                "[02:59.46]也为别人而流\n" +
                "[03:03.39]");
        mMusicControl.setPlayer(mLrcView);

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
    }

    //更新进度条
    private void updateProgress() {
        mCurrenPostion = mMusicControl.getCurrenPostion();
        mSeekBar.setProgress(mCurrenPostion);
        mMusicNowDurationTextView.setText(MusicUtils.formatTime(mCurrenPostion));
        updateTitle();
        mSeekBar.setMax(mMusicControl.getDuration());
        mSeekBar.setProgress(mMusicControl.getCurrenPostion());
        updateLyric();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
