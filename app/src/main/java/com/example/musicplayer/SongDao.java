package com.example.musicplayer;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class SongDao {

    private MusicPlayDataBaseOpenHelper helper;
    public SongDao(Context context){
        helper = new MusicPlayDataBaseOpenHelper(context);
    }

    /**
     * 增
     */
    public void add(String listId,String song,String path){
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("insert into songlist(list_id,song_name,singer,path) values(?,?,?)",new Object[]{listId,song,path});
        db.close();
    }

    /**
     * 删
     */
    public void delete(String song){
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("delete from words where song=?",new Object[]{song});
        db.close();
    }

    public List<Song> findSongsAll(String list_id){
        SQLiteDatabase db = helper.getReadableDatabase();
        List<Song> songs=new ArrayList<Song>();
        String song_name=null;
        String path=null;
        Cursor cursor = db.rawQuery("select song_name,path from songs where list_id=?",new String[]{list_id});
        while (cursor.moveToNext()){
            song_name=cursor.getString(0);
            path=cursor.getString(1);
            Song song=new Song();
            song.setSong(song_name);
            song.setPath(path);
            songs.add(song);
        }
        cursor.close();
        db.close();
        return  songs;
    }

    public String findPath(String list_id,String song_name){
        SQLiteDatabase db=helper.getReadableDatabase();
        String path = null;
        Cursor cursor = db.rawQuery("select path from songs where list_id=? and song_name=?",new String[]{list_id,song_name});
        boolean result = cursor.moveToNext();
        if(result){
            path = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return path;

    }
}
