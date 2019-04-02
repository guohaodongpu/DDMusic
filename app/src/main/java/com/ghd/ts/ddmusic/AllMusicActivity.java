package com.ghd.ts.ddmusic;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ghd.ts.ddmusic.adapter.MusicAdapter;
import com.ghd.ts.ddmusic.dao.MusicListDao;
import com.ghd.ts.ddmusic.entity.Music;
import com.ghd.ts.ddmusic.service.MusicService;
import com.ghd.ts.ddmusic.utils.MusicUtils;

import java.util.ArrayList;

public class AllMusicActivity extends SlideBackActivity {

    private ArrayList<Music> mList;
    private String mMusicName;
    private MusicService.MusicBinder mMusicControl;
    private ImageButton mOnOffButton;
    private ImageView mImageView;
    private MusicService mService = null;
    private boolean isBound = false;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private TextView mMusicNameTextView;
    private static final int UPDATE_PROGRESS = 0;
    private int mCurrenPostion;
    private SeekBar mSeekBar;
    private int mPosition;
    private MusicListDao mMusicListDao;

    /*
        private List<Fragment> mFragmentList = new ArrayList<Fragment>();
        private MainAdapter mFragmentAdapter;
        private ViewPager mPageVp;
        private TextView mTabMusicTv, mTabSingerTv;
        private AllMusicMusicFragment mMusicFg;
        private AllMusicSingerFragment mSingerFg;
        private int currentIndex;

        private void findById() {
            mTabMusicTv = this.findViewById(R.id.top_music);
            mTabSingerTv = this.findViewById(R.id.top_singer);
            mPageVp = this.findViewById(R.id.id_page_vp);
        }

        private void init() {
            mMusicFg = new AllMusicMusicFragment();
            mSingerFg = new AllMusicSingerFragment();
            mFragmentList.add(mMusicFg);
            mFragmentList.add(mSingerFg);

            mFragmentAdapter = new MainAdapter(
                    this.getSupportFragmentManager(), mFragmentList);
            mPageVp.setAdapter(mFragmentAdapter);
            mPageVp.setCurrentItem(0);

            mPageVp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {


                @Override
                public void onPageScrollStateChanged(int state) {

                }

                @Override
                public void onPageScrolled(int position, float offset,
                                           int offsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                    resetTextView();
                    switch (position) {
                        case 0:
                            mTabMusicTv.setTextColor(Color.WHITE);
                            break;
                        case 1:
                            mTabSingerTv.setTextColor(Color.WHITE);
                            break;
                        default:
                            break;
                    }
                    currentIndex = position;
                }
            });

        }

        private void resetTextView() {
            mTabMusicTv.setTextColor(Color.BLACK);
            mTabSingerTv.setTextColor(Color.BLACK);
        }
    */
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

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            isBound = true;
            mMusicControl = (MusicService.MusicBinder) binder;
            mService = mMusicControl.getMusicService();
            mPosition = MusicService.mPosition;
            mSeekBar.setMax(mMusicControl.getDuration());
            mSeekBar.setProgress(mMusicControl.getCurrenPostion());
            updatePlayText();
            updateTitle();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };


    private void initList() {
        if (ContextCompat.checkSelfPermission(AllMusicActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AllMusicActivity.this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            mList = mMusicListDao.select();
        }


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_music);
        mMusicListDao = new MusicListDao(this);
        initList();
        /*ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }*/
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

//        findById();
//        init();
        mOnOffButton = findViewById(R.id.on_off_button);
//        Intent MusicServiceIntent = new Intent(this, MusicService.class);
//        conn = new MyConnection();
//        startService(MusicServiceIntent);
//        bindService(MusicServiceIntent, conn, BIND_AUTO_CREATE);

        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, conn, BIND_AUTO_CREATE);

        MusicAdapter adapter = new MusicAdapter(AllMusicActivity.this, mList);
        mMusicNameTextView = findViewById(R.id.music_name_show);
        ListView listView = findViewById(R.id.all_music_list_item);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mMusicControl.play(position);
                updatePlayText();
                Music music = mList.get(position);
                mMusicName = music.getMusicName().replaceAll(".mp3", "");
                mMusicNameTextView.setText(music.getSinger() + "-" + mMusicName);
                mOnOffButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause));
                mSeekBar.setMax(mMusicControl.getDuration());
                mSeekBar.setProgress(mMusicControl.getCurrenPostion());
                updateMusicImageViewRotate();
                handler.sendEmptyMessage(UPDATE_PROGRESS);
            }
        });

        //进入播放页
        TextView textView = findViewById(R.id.music_name_show);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AllMusicActivity.this,
                        ListenMusicShowActivity.class);
                startActivity(intent);
            }
        });
/*
        ImageButton blackButton = findViewById(R.id.black_button);
        blackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });*/

        final ImageButton onOffButton = findViewById(R.id.on_off_button);
        onOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MusicService.mIsplaying) {
                    onOffButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_play));
                    mMusicControl.pause();
                } else {
                    onOffButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause));
                    mMusicControl.playContinue();

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

        mSeekBar = findViewById(R.id.bottom_seekBar);
        mImageView = findViewById(R.id.buttom_music_image);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mMusicControl != null) {
            handler.sendEmptyMessage(UPDATE_PROGRESS);
            updatePlayText();
            updateTitle();
        }
        updateMusicImageViewRotate();
    }


    private void updateTitle() {
        Music music = mList.get(MusicService.mPosition);
        mMusicName = music.getMusicName().replaceAll(".mp3", "");
        mMusicNameTextView.setText(music.getSinger() + "-" + mMusicName);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    initList();
                } else {
                    Toast.makeText(this, "权限不足", Toast.LENGTH_LONG).show();
//                    finish();
                    moveTaskToBack(true);
                }
                break;
            default:
        }
    }

    //更新下方的按钮
    public void updatePlayText() {
        if (MusicService.mIsplaying) {
            mOnOffButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause));
        } else {
            mOnOffButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_play));
        }
    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public static void verifyStoragePermissions(AllMusicActivity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }

    //更新进度条
    private void updateProgress() {
        mCurrenPostion = mMusicControl.getCurrenPostion();
        mSeekBar.setMax(mMusicControl.getDuration());
        mSeekBar.setProgress(mMusicControl.getCurrenPostion());
        mSeekBar.setProgress(mCurrenPostion);
        //使用Handler每500毫秒更新一次进度条
        handler.sendEmptyMessageDelayed(UPDATE_PROGRESS, 300);
    }

    public void allMusic(View view) {
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_all_music, menu);

        return super.onCreateOptionsMenu(menu);
    }



    public void updateMusicImageViewRotate() {
        if (!MusicService.mIsplaying) {
            mImageView.clearAnimation();
        } else {
            mImageView.startAnimation(AnimationUtils.loadAnimation(
                    AllMusicActivity.this, R.anim.imageview_rotate));
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.update_music:
                updateMusicList();
                Toast.makeText(this, "更新本地音乐成功！", Toast.LENGTH_SHORT).show();
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void updateMusicList() {
        ArrayList<Music> musicArrayList = new ArrayList<Music>();
        musicArrayList = MusicUtils.getMusicData(AllMusicActivity.this);
        Log.d("musicArrayList.size", String.valueOf(musicArrayList.size()));
        mMusicListDao.deleteAll();
        if (musicArrayList.size() != 0) {
            for (int i = 0; i < musicArrayList.size(); i++) {
                mMusicListDao.add(musicArrayList.get(i));
            }
        }
    }
}
