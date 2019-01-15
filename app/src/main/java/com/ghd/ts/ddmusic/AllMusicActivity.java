package com.ghd.ts.ddmusic;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ghd.ts.ddmusic.adapter.MusicAdapter;
import com.ghd.ts.ddmusic.entity.Music;
import com.ghd.ts.ddmusic.utils.MusicUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AllMusicActivity extends AppCompatActivity {

    private MediaPlayer mplayer=new MediaPlayer();

    private ArrayList<Music> list;

    private int mPosition;

    private String mMusicName;

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

        MusicAdapter adapter = new MusicAdapter(AllMusicActivity.this, list);
        ListView listView = findViewById(R.id.all_music_list_item);
        ImageButton onOffButton = findViewById(R.id.on_off_button);
        onOffButton.setOnClickListener(new View.OnClickListener() {
            ImageButton onOffButton = findViewById(R.id.on_off_button);
            @Override
                 public  void onClick(View v){
                if (mplayer.isPlaying()) {
                    mplayer.pause();
                    onOffButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_play));
                } else {
                    mplayer.start();
                    // thread = new Thread(new SeekBarThread());
                    // thread.start();
                    onOffButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_stop));
                }
            }
        });

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                mPosition = position;
                musicplay(position);
                ImageButton onOffButton = findViewById(R.id.on_off_button);
                TextView textView = findViewById(R.id.music_name_show);
                onOffButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_stop));
                Music music = list.get(position);
                mMusicName = music.getMusicName().replaceAll(".mp3","");
                textView.setText(music.getSinger()+" "+mMusicName);


            }
        });


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

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_all_music, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.back:
                finish();
                break;
            case R.id.more:

                Toast.makeText(AllMusicActivity.this,
                        "" + "更多", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }


        return super.onOptionsItemSelected(item);
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



    //播放音乐
    private void musicplay(int position) {
        try {
            List<Music> list = MusicUtils.getMusicData(AllMusicActivity.this);
            mplayer.reset();
            mplayer.setDataSource(list.get(position).getPath());
            mplayer.prepare();
            mplayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE };

    public static void verifyStoragePermissions(AllMusicActivity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the u
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }

}
