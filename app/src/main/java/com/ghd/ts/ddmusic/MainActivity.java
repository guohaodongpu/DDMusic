package com.ghd.ts.ddmusic;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.text.TextPaint;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ghd.ts.ddmusic.adapter.GlideImageLoader;
import com.ghd.ts.ddmusic.adapter.MainAdapter;
import com.ghd.ts.ddmusic.adapter.MusicListAdapter;
import com.ghd.ts.ddmusic.dao.MusicListDao;
import com.ghd.ts.ddmusic.entity.Music;
import com.ghd.ts.ddmusic.entity.SongSheet;
import com.ghd.ts.ddmusic.fragment.MainFindFragment;
import com.ghd.ts.ddmusic.fragment.MainMineFragment;
import com.ghd.ts.ddmusic.fragment.MainMusicFragment;
import com.ghd.ts.ddmusic.service.MusicService;
import com.ghd.ts.ddmusic.utils.MusicUtils;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private List<Fragment> mFragmentList = new ArrayList<Fragment>();
    private MainAdapter mFragmentAdapter;
    private DrawerLayout mDrawer;
    private ViewPager mPageVp;//页面
    private TextView mTabMineTv, mTabFindTv, mTabMusicTv;
    private ImageView mTabLineIv;
    private MainMineFragment mMineFg;
    private MainFindFragment mFindFg;
    private MainMusicFragment mMusicFg;
    private int mCurrentIndex;
    private int mScreenWidth;
    private ImageButton mOnOffButton;
    private TextView mMusicNameTextView;

    private ViewPager mLibraryViewPager;
    private int[] mImageResIds;
    private ArrayList<ImageView> mImageViewList;
    private LinearLayout mLlPointContainer;
    private String[] mContentDescs;
    private TextView mTvDesc;
    private int mPreviousSelectedPosition = 0;
    boolean isRunning = false;
    private boolean mCanExcute = false;

    private MusicService.MusicBinder mMusicControl;
    private MusicService mService = null;
    private static final int UPDATE_PROGRESS = 0;
    private int mCurrenPostion;
    private SeekBar mSeekBar;
    private ImageView mImageView;
    private boolean isBound = false;
    private MusicListDao mMusicListDao;
    private String mMusicName;

    private Banner banner;

    private void findById() {
        mTabMineTv = this.findViewById(R.id.top_mine);
        mTabFindTv = this.findViewById(R.id.top_find);
        mTabMusicTv = this.findViewById(R.id.top_music_library);
        //mTabLineIv = this.findViewById(R.id.id_tab_line_iv);
        mPageVp = this.findViewById(R.id.id_page_vp);
    }

    private void init() {
        mMineFg = new MainMineFragment();
        mFindFg = new MainFindFragment();
        mMusicFg = new MainMusicFragment();
        mFragmentList.add(mMineFg);
        mFragmentList.add(mMusicFg);
        mFragmentList.add(mFindFg);

        mFragmentAdapter = new MainAdapter(
                this.getSupportFragmentManager(), mFragmentList);
        mPageVp.setAdapter(mFragmentAdapter);
        mPageVp.setCurrentItem(0);
        mPageVp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            /**
             * state滑动中的状态 有三种状态（0，1，2） 1：正在滑动 2：滑动完毕 0：什么都没做。
             */
            @Override
            public void onPageScrollStateChanged(int state) {
            }

            /**
             * position :当前页面，及你点击滑动的页面 offset:当前页面偏移的百分比
             * offsetPixels:当前页面偏移的像素位置
             */
            @Override
            public void onPageScrolled(int position, float offset,
                                       int offsetPixels) {
               /* LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mTabLineIv
                        .getLayoutParams();*/
                /**
                 * 利用currentIndex(当前所在页面)和position(下一个页面)以及offset来
                 * 设置mTabLineIv的左边距 滑动场景：
                 * 记3个页面,
                 * 从左到右分别为0,1,2
                 * 0->1; 1->2; 2->1; 1->0
                 */
                /*if (mCurrentIndex == 0 && position == 0)// 0->1
                {
                    lp.leftMargin = (int) (offset * (mScreenWidth * 1.0 / 3) + mCurrentIndex
                            * (mScreenWidth / 3));
                } else if (mCurrentIndex == 1 && position == 0) // 1->0
                {
                    lp.leftMargin = (int) (-(1 - offset)
                            * (mScreenWidth * 1.0 / 3) + mCurrentIndex
                            * (mScreenWidth / 3));
                } else if (mCurrentIndex == 1 && position == 1) // 1->2
                {
                    lp.leftMargin = (int) (offset * (mScreenWidth * 1.0 / 3) + mCurrentIndex
                            * (mScreenWidth / 3));
                } else if (mCurrentIndex == 2 && position == 1) // 2->1
                {
                    lp.leftMargin = (int) (-(1 - offset)
                            * (mScreenWidth * 1.0 / 3) + mCurrentIndex
                            * (mScreenWidth / 3));
                }
                mTabLineIv.setLayoutParams(lp);*/
            }

            @Override
            public void onPageSelected(int position) {
                resetTextView();
                switch (position) {
                    case 0:
                        resetTextView();
                        mTabMineTv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                        mTabMineTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                        break;
                    case 1:
                        initViews();
                        if (!mCanExcute) {
                            initLibrary();
                            mCanExcute = true;
                        }
                        resetTextView();
                        mTabMusicTv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                        mTabMusicTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                        break;
                    case 2:
                        initBanner();
                        resetTextView();
                        mTabFindTv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                        mTabFindTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                        break;
                }
                mCurrentIndex = position;
            }
        });

    }

    /*private void initTabLineWidth() {
        DisplayMetrics dpMetrics = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay()
                .getMetrics(dpMetrics);
        mScreenWidth = dpMetrics.widthPixels;
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mTabLineIv
                .getLayoutParams();
        lp.width = mScreenWidth / 10;
        mTabLineIv.setLayoutParams(lp);
    }*/

    private void resetTextView() {
        mTabMineTv.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        mTabMineTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 19);
        mTabMusicTv.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        mTabMusicTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 19);
        mTabFindTv.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        mTabFindTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 19);
    }

    private List<SongSheet> mMusicListList = new ArrayList<>();

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            isBound = true;
            mMusicControl = (MusicService.MusicBinder) binder;
            mService = mMusicControl.getMusicService();
            updatePlayText();
            if(MusicService.mPosition != -1){
                updateTitle();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

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
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mMusicListDao = new MusicListDao(this);
        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, conn, BIND_AUTO_CREATE);
        mDrawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        findById();
        init();
        //initTabLineWidth();
        //initMusicList();
        View view = View.inflate(getApplicationContext(),
                R.layout.layout_activity_main_tab_mine, null);
        MusicListAdapter adapter = new MusicListAdapter(
                MainActivity.this, R.layout.layout_music_list_list, mMusicListList);
        ListView listView = view.findViewById(R.id.music_list_list_item);
        listView.setAdapter(adapter);

        TextView textView = findViewById(R.id.music_name_show);
        textView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this,
                        ListenMusicShowActivity.class);
                intent.putExtra("musicList",
                        MusicUtils.getMusicData(MainActivity.this));
                intent.putExtra("position", 0);
                startActivity(intent);
            }
        });

        TextView topMine = findViewById(R.id.top_mine);
        topMine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTextView();
                mTabMineTv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                mTabMineTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                mPageVp.setCurrentItem(0);
            }
        });
        TextView topMusicLibrary = findViewById(R.id.top_music_library);
        topMusicLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTextView();
                initViews();
                if (!mCanExcute) {
                    initLibrary();
                    mCanExcute = true;
                }
                mTabMusicTv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                mTabMusicTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                mPageVp.setCurrentItem(1);
            }
        });
        TextView topFind = findViewById(R.id.top_find);
        topFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTextView();
                mTabFindTv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                mTabFindTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                mPageVp.setCurrentItem(2);
                initBanner();
            }
        });

        ImageButton showMenu = findViewById(R.id.show_menu);
        showMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawer.openDrawer(Gravity.LEFT);
            }
        });

        mOnOffButton = findViewById(R.id.on_off_button);
        mOnOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MusicService.mIsplaying) {
                    mOnOffButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_play));
                    mMusicControl.pause();
                } else {
                    mOnOffButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause));
                    mMusicControl.playContinue();

                }
                updateMusicImageViewRotate();
            }
        });

        mMusicNameTextView = findViewById(R.id.music_name_show);
        mSeekBar = findViewById(R.id.bottom_seekBar);
        mImageView = findViewById(R.id.buttom_music_image);
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

        int[] data = load();
        if (data[2] != 0) {
            mSeekBar.setMax(data[2]);
            mSeekBar.setProgress(data[1]);
            MusicService.mPosition = data[0];
        }

//        mMusicControl.seekTo(data[1]);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mMusicControl != null) {
            mService = mMusicControl.getMusicService();
            handler.sendEmptyMessage(UPDATE_PROGRESS);
            updatePlayText();
            if(MusicService.mPosition != -1){
                updateTitle();
            }
        }
        updateMusicImageViewRotate();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_share) {

        } else if (id == R.id.nav_close) {
            moveTaskToBack(true);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void allMusic(View v) {
        Intent intent = new Intent(MainActivity.this, AllMusicActivity.class);
        startActivity(intent);
    }

    public void downloadMusic(View v) {
        Intent intent = new Intent(MainActivity.this, AllMusicActivity.class);
        startActivity(intent);
    }


    private void initLibrary() {
        initData();
        initAdapter();
        mLibraryViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                int newPosition = position % mImageViewList.size();

                mTvDesc.setText(mContentDescs[newPosition]);
                mLlPointContainer.getChildAt(mPreviousSelectedPosition).setEnabled(false);
                mLlPointContainer.getChildAt(mPreviousSelectedPosition).
                        setBackgroundResource(R.drawable.gray_dot);
                mLlPointContainer.getChildAt(newPosition).setEnabled(true);
                mLlPointContainer.getChildAt(newPosition).
                        setBackgroundResource(R.drawable.green_dot);
                mPreviousSelectedPosition = newPosition;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        new Thread() {
            public void run() {
                isRunning = true;
                while (isRunning) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // 往下跳一位
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            mLibraryViewPager.setCurrentItem(mLibraryViewPager.getCurrentItem() + 1);
                        }
                    });
                }
            }


        }.start();

    }


    private void initBanner() {

        List<Integer> images = new ArrayList<>();
        images.add(R.drawable.a);
        images.add(R.drawable.b);
        images.add(R.drawable.c);

        banner = findViewById(R.id.banner);
        //设置banner样式
        banner.setBannerStyle(BannerConfig.NUM_INDICATOR);
        //设置图片加载器
        banner.setImageLoader(new GlideImageLoader());
        //设置图片集合
        banner.setImages(images);
        //设置轮播时间
        banner.setDelayTime(2000);
        //banner设置方法全部调用完毕时最后调用
        banner.start();

    }


    private void initViews() {
        mLibraryViewPager = findViewById(R.id.top_music_library_viewpager);
        //mLibraryViewPager.setOnPageChangeListener(MainActivity.this);// 设置页面更新监听
        mLlPointContainer = findViewById(R.id.ll_point_container);

        mTvDesc = findViewById(R.id.tv_desc);

    }

    private void initData() {
        mImageResIds = new int[]{R.drawable.a, R.drawable.b, R.drawable.c, R.drawable.a, R.drawable.b};

        //下方描述
        mContentDescs = new String[]{
                "QQ音乐绿钻优惠",
                "周杰伦新曲发布",
                "揭秘北京电影",
                "莫扎特音乐试听",
                "测试测试测试"
        };

        mImageViewList = new ArrayList<ImageView>();

        ImageView imageView;
        View pointView;
        LinearLayout.LayoutParams layoutParams;
        for (int i = 0; i < mImageResIds.length; i++) {
            // 初始化要显示的图片对象
            imageView = new ImageView(this);
            imageView.setBackgroundResource(mImageResIds[i]);
            mImageViewList.add(imageView);

            // 加小白点, 指示器
            pointView = new View(this);
            pointView.setBackgroundResource(R.drawable.gray_dot);
            layoutParams = new LinearLayout.LayoutParams(15, 15);
            if (i == 0) {
                imageView.setBackgroundResource(R.drawable.a);
            } else {
                imageView.setEnabled(false);
                layoutParams.leftMargin = 20;
            }
            mLlPointContainer.addView(pointView, layoutParams);
        }
    }

    private void initAdapter() {
        mLlPointContainer.getChildAt(0).setEnabled(true);
        mTvDesc.setText(mContentDescs[0]);
        mPreviousSelectedPosition = 0;
        mLibraryViewPager.setAdapter(new MyAdapter());
        int pos = Integer.MAX_VALUE / 2 - (Integer.MAX_VALUE / 2 % mImageViewList.size());
        mLibraryViewPager.setCurrentItem(5000000); // 设置到某个位置
    }

    class MyAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            // container: 容器: ViewPager
            // position: 当前要显示条目的位置 0 -> 4
            int newPosition = position % mImageViewList.size();

            ImageView imageView = mImageViewList.get(newPosition);
            //把View对象添加到container中
            container.addView(imageView);
            //把View对象返回给框架, 适配器
            return imageView; // 必须重写, 否则报异常
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
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

    //更新进度条
    private void updateProgress() {
        mCurrenPostion = mMusicControl.getCurrenPostion();
        mSeekBar.setMax(mMusicControl.getDuration());
        mSeekBar.setProgress(mMusicControl.getCurrenPostion());
        mSeekBar.setProgress(mCurrenPostion);
        handler.sendEmptyMessageDelayed(UPDATE_PROGRESS, 300);
    }

    //图片旋转
    public void updateMusicImageViewRotate() {
        if (!MusicService.mIsplaying) {
            mImageView.clearAnimation();
        } else {
            mImageView.startAnimation(AnimationUtils.loadAnimation(
                    MainActivity.this, R.anim.imageview_rotate));
        }
    }

    //更新文字
    private void updateTitle() {
        Music music = mMusicListDao.select().get(MusicService.mPosition);
        mMusicName = music.getMusicName().replaceAll(".mp3", "");
        Log.d("mMusicName", mMusicName);
        mMusicNameTextView.setText(music.getSinger() + "-" + mMusicName);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isRunning = false;
        unbindService(conn);
    }

    private int[] load() {
        int[] data = {0, 0, 0};
        FileInputStream in = null;
        BufferedReader reader = null;
        StringBuilder content = new StringBuilder();
        try {
            in = openFileInput("data");
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (!content.equals("")) {
            String[] datas = content.toString().split(",");
            for (int i = 0; i < datas.length; i++) {
                if (datas[i].equals("")) {
                    data[i] = 0;
                } else {
                    data[i] = Integer.valueOf(datas[i]);
                }
            }
        }


        return data;
    }


}
