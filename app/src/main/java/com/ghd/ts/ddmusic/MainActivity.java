package com.ghd.ts.ddmusic;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
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
        implements NavigationView.OnNavigationItemSelectedListener{

    private List<Fragment> mFragmentList = new ArrayList<Fragment>();
    private MainAdapter mFragmentAdapter;

    private ViewPager mPageVp;
    private TextView mTabMineTv, mTabFindTv, mTabMusicTv;
    private ImageView mTabLineIv;

    private MainMineFragment mMineFg;
    private MainFindFragment mFindFg;
    private MainMusicFragment mMusicFg;

    private int currentIndex;

    private int screenWidth;

    private void findById() {
        mTabMineTv = this.findViewById(R.id.top_mine);
        mTabFindTv = this.findViewById(R.id.top_find);
        mTabMusicTv = this.findViewById(R.id.top_music_library);
//        mTabLineIv = this.findViewById(R.id.id_tab_line_iv);
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
                /*LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mTabLineIv
                        .getLayoutParams();*/

//                Log.e("offset:", offset + "");
                /**
                 * 利用currentIndex(当前所在页面)和position(下一个页面)以及offset来
                 * 设置mTabLineIv的左边距 滑动场景：
                 * 记3个页面,
                 * 从左到右分别为0,1,2
                 * 0->1; 1->2; 2->1; 1->0
                 */
/*
                if (currentIndex == 0 && position == 0)// 0->1
                {
                    lp.leftMargin = (int) (offset * (screenWidth * 1.0 / 3) + currentIndex
                            * (screenWidth / 3));

                } else if (currentIndex == 1 && position == 0) // 1->0
                {
                    lp.leftMargin = (int) (-(1 - offset)
                            * (screenWidth * 1.0 / 3) + currentIndex
                            * (screenWidth / 3));

                } else if (currentIndex == 1 && position == 1) // 1->2
                {
                    lp.leftMargin = (int) (offset * (screenWidth * 1.0 / 3) + currentIndex
                            * (screenWidth / 3));
                } else if (currentIndex == 2 && position == 1) // 2->1
                {
                    lp.leftMargin = (int) (-(1 - offset)
                            * (screenWidth * 1.0 / 3) + currentIndex
                            * (screenWidth / 3));
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
                        mTabMusicTv.setTextColor(Color.WHITE);
                        break;
                    case 2:
                        mTabFindTv.setTextColor(Color.WHITE);
                        break;
                }
                currentIndex = position;
            }
        });

    }

/*
    private void initTabLineWidth() {
        DisplayMetrics dpMetrics = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay()
                .getMetrics(dpMetrics);
        screenWidth = dpMetrics.widthPixels;
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mTabLineIv
                .getLayoutParams();
        lp.width = screenWidth / 4;
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
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
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


        initMusicList();
        View view=View.inflate(getApplicationContext(),R.layout.layout_activity_main_tab_mine, null);
        MusicListAdapter adapter = new MusicListAdapter(
                MainActivity.this, R.layout.layout_music_list_list, mMusicListList);
        ListView listView = view.findViewById(R.id.music_list_list_item);
        listView.setAdapter(adapter);

        /*LayoutInflater factory = LayoutInflater.from(MainActivity.this);
        // LayoutInflater.from(当前类.this).inflate(R.layout.XML, null).findViewById(R.id.控件ID);
        View layoutMine = factory.inflate(R.layout.layout_activity_main_tab_mine, null);
        ImageButton allMusicBtn = layoutMine.findViewById(R.id.center_all_music);
        allMusicBtn.setOnClickListener(this);
        ImageButton downloadMusicBtn = layoutMine.findViewById(R.id.center_download_music);
        downloadMusicBtn.setOnClickListener(this);
        ImageButton RecentlyPlayedBtn = layoutMine.findViewById(R.id.center_recently_played);
        RecentlyPlayedBtn.setOnClickListener(this);
        ImageButton MyFavoriteBtn = layoutMine.findViewById(R.id.center_my_favorite);
        MyFavoriteBtn.setOnClickListener(this);
        ImageButton AlreadyBoughtMusicBtn = layoutMine.findViewById(R.id.center_already_bought_music);
        AlreadyBoughtMusicBtn.setOnClickListener(this);
        ImageButton RunningRadioBtn = layoutMine.findViewById(R.id.center_running_radio);
        RunningRadioBtn.setOnClickListener(this);
*/

        TextView textView = findViewById(R.id.music_name_show);
        textView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, ListenMusicShowActivity.class);
                intent.putExtra("musicList",MusicUtils.getMusicData(MainActivity.this));
                intent.putExtra("position",0);
                startActivity(intent);
            }
        });

    }

  /*  @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.center_all_music : {
                Intent intent = new Intent(MainActivity.this, AllMusicActivity.class);
                startActivity(intent);
            }break;
            case R.id.center_download_music : {
                Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                startActivity(intent);
            }break;
            case R.id.center_recently_played : {

            }break;
            case R.id.center_my_favorite : {

            }break;
            case R.id.center_already_bought_music : {

            }break;
            case R.id.center_running_radio : {

            }break;
            default: break;

        }
    }*/

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

        }
    }

    public void allMusic(View v){
        Intent intent = new Intent(MainActivity.this, AllMusicActivity.class);
        startActivity(intent);
    }
    public void downloadMusic(View v){
        Intent intent = new Intent(MainActivity.this, AllMusicActivity.class);
        startActivity(intent);
    }
}
