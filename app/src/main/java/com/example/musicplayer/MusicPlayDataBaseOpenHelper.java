package com.example.musicplayer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MusicPlayDataBaseOpenHelper extends SQLiteOpenHelper {

    public MusicPlayDataBaseOpenHelper(Context context) {
        super(context, "music_play.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table songlist(list_id integer primary key autoincrement,list_name varchar(50))");
        sqLiteDatabase.execSQL("create table songs(song_id integer primary key autoincrement,list_id integer,song_name varchar(50),path varchar(100),FOREIGN KEY(list_id) REFERENCES LinkMan_table(list_id))");
        sqLiteDatabase.execSQL("insert into songlist(list_name) values('TestList')");
        sqLiteDatabase.execSQL("insert into songs(list_id,song_name,path) values(1,'清晨','/mnt/sdcard/Music/清晨.mp3')");
        sqLiteDatabase.execSQL("insert into songs(list_id,song_name,path) values(1,'追梦人','/mnt/sdcard/Music/追梦人.mp3')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("");
        onCreate(sqLiteDatabase);
    }


}
