package com.ghd.ts.ddmusic.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class MydatabaseHelper extends SQLiteOpenHelper {

    public static final String CREATE_MUSIC_LIST = "create table MusicList ("
            +"id integer primary key autoincrement, "

            +"music_name text, "
            +"singer text, "
            +"path text, "
            +"duration integer)";


    private Context mContext;
    public MydatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                            int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_MUSIC_LIST);
        Toast.makeText(mContext, "创建成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldversion, int newVersion) {
        db.execSQL("drop table if exists MusicList");
        onCreate(db);
    }
}
