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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.ghd.ts.ddmusic.adapter.MusicAdapter;
import com.ghd.ts.ddmusic.entity.Music;
import com.ghd.ts.ddmusic.utils.MusicUtils;

import java.util.List;

public class AllMusicActivity extends AppCompatActivity {

    MediaPlayer mplayer=new MediaPlayer();

    List<Music> list;

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
        listView.setAdapter(adapter);


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
                        "" + "更过", Toast.LENGTH_SHORT).show();
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



    public void listenMusic_show(View v) {
        Intent intent = new Intent(AllMusicActivity.this, ListenMusicShowActivity.class);
        startActivity(intent);
    }

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


}
