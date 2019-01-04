package com.ghd.ts.ddmusic;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class DDMusicMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ddmusic_main);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.hide();
        }

    }



    public void listenMusic_show(View v){

        Intent intent=new Intent(DDMusicMainActivity.this,ListenMusicShowActivity.class);
        startActivity(intent);
    }

}
