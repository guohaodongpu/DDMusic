package com.ghd.ts.ddmusic;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ghd.ts.ddmusic.adapter.MainAdapter;
import com.ghd.ts.ddmusic.adapter.MusicListAdapter;
import com.ghd.ts.ddmusic.entity.MusicList;
import com.ghd.ts.ddmusic.fragment.MainFindFragment;
import com.ghd.ts.ddmusic.fragment.MainMineFragment;
import com.ghd.ts.ddmusic.fragment.MainMusicFragment;
import com.ghd.ts.ddmusic.utils.MusicUtils;

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
                        mTabMineTv.setTextColor(Color.WHITE);
                        break;
                    case 1:
                        initViews();
                        if(!mCanExcute){
                            initLibrary();
                            mCanExcute = true;
                        }
                        mTabMusicTv.setTextColor(Color.WHITE);
                        break;
                    case 2:
                        mTabFindTv.setTextColor(Color.WHITE);
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
        mTabMineTv.setTextColor(Color.BLACK);
        mTabMusicTv.setTextColor(Color.BLACK);
        mTabFindTv.setTextColor(Color.BLACK);
    }

    private List<MusicList> mMusicListList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
                mTabMineTv.setTextColor(Color.WHITE);
                mPageVp.setCurrentItem(0);
            }
        });
        TextView topMusicLibrary = findViewById(R.id.top_music_library);
        topMusicLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTextView();
                initViews();
                if(!mCanExcute){
                    initLibrary();
                    mCanExcute = true;
                }
                mTabMineTv.setTextColor(Color.WHITE);
                mPageVp.setCurrentItem(1);
            }
        });
        TextView topFind = findViewById(R.id.top_find);
        topFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTextView();
                mTabMineTv.setTextColor(Color.WHITE);
                mPageVp.setCurrentItem(2);
            }
        });

        ImageButton showMenu = findViewById(R.id.show_menu);
        showMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawer.openDrawer(Gravity.LEFT);
            }
        });

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

        if (id == R.id.nav_camera) {
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

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
            layoutParams = new LinearLayout.LayoutParams(10, 10);
            if (i==0){
                imageView.setBackgroundResource(R.drawable.a);
            }else{
                imageView.setEnabled(false);
                layoutParams.leftMargin=12;
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isRunning = false;
    }

}
