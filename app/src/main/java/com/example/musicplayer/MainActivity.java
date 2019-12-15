package com.example.musicplayer;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.ColorSpace;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity{

    private ListView listView;
    private ListDao listDao;
    private MyAdapter myAdapter;
    private MusicPlayDataBaseOpenHelper helper;
    private List<SongsList> lists;
    private FloatingActionButton add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        helper = new MusicPlayDataBaseOpenHelper(this);
        helper.getWritableDatabase();
        listView=findViewById(R.id.MusicList);
        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent intent=new Intent(MainActivity.this, ListActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt("list_id",i);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                }
        );
        add=findViewById(R.id.addlist);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddDialog();
            }
        });
        refreshData();
    }

    public void AddDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
        final View view= LayoutInflater.from(MainActivity.this).inflate(R.layout.add_list,null);
        Button button=view.findViewById(R.id.button);
        final EditText editText1=(EditText)view.findViewById(R.id.editText1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String listName=editText1.getText().toString();
                listDao.add(listName);
                Toast.makeText(MainActivity.this,"添加成功",Toast.LENGTH_SHORT).show();
                refreshData();
            }
        });
        builder.setView(view).show();

    }

    public void refreshData(){
        listDao = new ListDao(this);
        lists = listDao.findListAll();
        if(myAdapter==null){
            myAdapter=new MyAdapter();
            listView.setAdapter(myAdapter);
        }else {
            //通知数据适配器更新数据
            myAdapter.notifyDataSetChanged();
        }

    }
    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return lists.size();
        }

        @Override
        public Object getItem(int i) {
            return lists.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View v = null;
            if (view == null) {
                v = View.inflate(MainActivity.this, R.layout.list_item, null);
            } else {
                v = view;
            }
            TextView textView = v.findViewById(R.id.listName);
            textView.setText(lists.get(i).getListName());
            return v;
        }
    }





/*
    public void play(View view){
        try {
            mediaPlayer=new MediaPlayer();
            mediaPlayer.reset();
            mediaPlayer.setDataSource(musicPath.getText().toString().trim());
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                Toast.makeText(MainActivity.this,"播放失败",Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        mediaPlayer.start();
    }
    public void stop(View view){
        if(mediaPlayer!=null&&mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        }

    }
    public void loop(View view){

    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this,MusicService.class);
        switch (view.getId()){
            case R.id.bt_play_pause:
                MusicService.path = musicPath.getText().toString().trim();
                if(TextUtils.isEmpty(MusicService.path)){
                    Toast.makeText(this,"音乐路径不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                File file=new File(MusicService.path);
                if(file.exists()){
                    Toast.makeText(this,"音乐不存在请检查输入路径",Toast.LENGTH_SHORT).show();
                    return;
                }
                intent .putExtra("action",0);
                break;
            case R.id.bt_stop_next:
                intent.putExtra("action",1);
                break;
            case R.id.bt_pattern:
                intent.putExtra("action",2);
                break;
        }
        startService(intent);
    }



    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("是否要在后台继续播放音乐？");
        builder.setPositiveButton("继续播放", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.setNegativeButton("停止播放", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent=new Intent(MainActivity.this,MusicService.class);
                stopService(intent);
                finish();
            }
        });
        builder.show();
    }*/
}
