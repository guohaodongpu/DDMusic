package com.ghd.ts.ddmusic;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ghd.ts.ddmusic.adapter.MusicAdapter;
import com.ghd.ts.ddmusic.entity.Music;
import com.ghd.ts.ddmusic.service.MusicService;
import com.ghd.ts.ddmusic.utils.MusicUtils;

import java.util.ArrayList;

public class AllMusicActivity extends AppCompatActivity {

    private ArrayList<Music> list;
    private int mPosition;
    private String mMusicName;
//    private ServiceConnection conn;
    private MusicService.MusicBinder musicControl;
    private ImageButton onOffButton;
    private MusicService service  = null;
    private boolean isBound = false;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

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

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            isBound = true;
            musicControl = (MusicService.MusicBinder)binder;
            service = musicControl.getMusicService();
            updatePlayText();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };



    private void initList(){
        list = MusicUtils.getMusicData(AllMusicActivity.this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_music);

        if(ContextCompat.checkSelfPermission(AllMusicActivity.this ,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(AllMusicActivity.this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        } else {
            initList();
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
//        findById();
//        init();
        onOffButton = findViewById(R.id.on_off_button);
//        Intent MusicServiceIntent = new Intent(this, MusicService.class);
//        conn = new MyConnection();
//        startService(MusicServiceIntent);
//        bindService(MusicServiceIntent, conn, BIND_AUTO_CREATE);

        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, conn, BIND_AUTO_CREATE);

        MusicAdapter adapter = new MusicAdapter(AllMusicActivity.this, list);
        ListView listView = findViewById(R.id.all_music_list_item);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mPosition = position;
                service.setmPosition(position);
                musicControl.play(list.get(mPosition).getPath());
                updatePlayText();
                TextView textView = findViewById(R.id.music_name_show);
                Music music = list.get(position);
                mMusicName = music.getMusicName().replaceAll(".mp3","");
                textView.setText(music.getSinger()+" "+mMusicName);
                onOffButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause));
            }
        });

        //进入播放页
       TextView textView = findViewById(R.id.music_name_show);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AllMusicActivity.this, ListenMusicShowActivity.class);
                intent.putExtra("musicList",list);
                intent.putExtra("position",mPosition);
                startActivity(intent);
            }
        });

        ImageButton blackButton = findViewById(R.id.black_button);
        blackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final ImageButton onOffButton = findViewById(R.id.on_off_button);
        onOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (musicControl.isPlaying()) {
                    onOffButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_play));
                    musicControl.pause();
                } else {
                    onOffButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause));
                    musicControl.play(list.get(mPosition).getPath());

                }
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    initList();
                }else {
                    Toast.makeText(this,"权限不足",Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
            default:
        }
    }

    //更新下方的按钮
    public void updatePlayText() {
        if (musicControl.isPlaying()) {
            onOffButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause));
        } else {
            onOffButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_play));
        }
    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE };

    public static void verifyStoragePermissions(AllMusicActivity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //退出应用后与service解除绑定
    }

}
