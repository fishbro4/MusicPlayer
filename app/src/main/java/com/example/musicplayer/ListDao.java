package com.example.musicplayer;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class ListDao {

    private MusicPlayDataBaseOpenHelper helper;
    public ListDao(Context context){
        helper = new MusicPlayDataBaseOpenHelper(context);
    }

    /**
     * 增
     */
    public void add(String listName){
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("insert into songlist(list_name) values(?)",new Object[]{listName});
        db.close();
    }

    /**
     * 删
     */
    public void delete(String list){
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("delete from words where list=?",new Object[]{list});
        db.close();
    }

    public List<SongsList> findListAll(){
        List<SongsList> lists = new ArrayList<SongsList>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select list_name from songlist",null);
        while (cursor.moveToNext()){
            String list_name=cursor.getString(0);
            SongsList list=new SongsList();
            list.setListName(list_name);
            lists.add(list);
        }
        cursor.close();
        db.close();
        return  lists;
    }
}
