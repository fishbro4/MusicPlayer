package com.example.musicplayer;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class ListActivity extends AppCompatActivity implements View.OnClickListener{

    private Button bt_prev,bt_next,bt_pause,bt_loop;
    private ListView listView;
    private SongDao songDao;
    private List<Song> songs;
    private MyAdapter myAdapter;
    private MusicPlayDataBaseOpenHelper helper;
    private TextView playingSong;
    private static String list_id;
    private MediaPlayer mediaPlayer;
    private int position;
    private static SeekBar seekBar;
    private int playStyle=0;

    public static Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
        //处理消息
        Bundle bundle=msg.getData();
        //获取歌曲长度和当前播放位置，并设置到进度条上
        int duration=bundle.getInt("duration");
        int currentposition=bundle.getInt("currentposition");
        seekBar.setMax(duration);
        seekBar.setProgress(currentposition);
    }
};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.songs_list);
        Bundle bundle=getIntent().getExtras();
        //Toast.makeText(ListActivity.this,bundle.getString("list_id"),Toast.LENGTH_LONG).show();
        list_id=String.valueOf(bundle.getInt("list_id")+1);
        helper = new MusicPlayDataBaseOpenHelper(this);
        helper.getWritableDatabase();
        playingSong=findViewById(R.id.playingSong);
        listView=findViewById(R.id.songs_list);
        songDao =new SongDao(this);
        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        position=i;
                        TextView textView=view.findViewById(R.id.songName);
                        String song_name=textView.getText().toString();
                        String path = songDao.findPath(list_id,song_name);
                        if(mediaPlayer==null){
                            mediaPlayer=new MediaPlayer();//避免了每次点击重叠播放
                        }
                        mediaPlayer.reset();
                        try {
                            mediaPlayer.setDataSource(path);
                            mediaPlayer.prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mediaPlayer.start();
                        playingSong.setText(song_name);
                        seekBarPlayProgress();
                    }
                }
        );

        seekBar=findViewById(R.id.seekBar);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
//                if(b){
//                    mediaPlayer.seekTo(i);
//                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //seekBar.getProgress();
                seekPlayProgress(seekBar.getProgress());
            }
        });

        bt_prev=findViewById(R.id.prev);
        bt_next=findViewById(R.id.next);
        bt_pause=findViewById(R.id.pause);
        bt_loop=findViewById(R.id.loop);
        bt_prev.setOnClickListener(this);
        bt_next.setOnClickListener(this);
        bt_pause.setOnClickListener(this);
        bt_loop.setOnClickListener(this);

        refreshData(list_id);
    }


    private void refreshData(String list_id) {
        songDao = new SongDao(this);
        songs = songDao.findSongsAll(list_id);
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
            return songs.size();
        }

        @Override
        public Object getItem(int i) {
            return songs.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View v = null;
            if (view == null) {
                v = View.inflate(ListActivity.this, R.layout.song_item, null);
            } else {
                v = view;
            }
            TextView textView1 = v.findViewById(R.id.songNum);
            TextView textView2 = v.findViewById(R.id.songName);
            textView1.setText(String.valueOf(i+1));
            textView2.setText(songs.get(i).getSong());
            return v;
        }
    }

    @Override
    public void onClick(View view) {
        //Intent intent = new Intent(ListActivity.this,MusicService.class);
        switch (view.getId()) {
            case R.id.prev:
                //intent.putExtra("action", 0);
                try {
                    findPrev();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.next:
                //intent.putExtra("action", 1);
                try {
                    findNext();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.pause:
                Button button = view.findViewById(R.id.pause);
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    button.setText("继续");
                    //intent.putExtra("action", 2);
                    mediaPlayer.pause();
                } else if (!mediaPlayer.isPlaying()) {
                    button.setText("暂停");
                    mediaPlayer.start();
                    //intent.putExtra("action",3);
                }
                break;
            case R.id.loop:
                //intent.putExtra("action", 3);
                playStyle++;
                if(playStyle>2){
                    playStyle=0;
                }
                switch (playStyle){
                    case 0:
                        bt_loop.setText("顺序播放");
                        break;
                    case 1:
                        bt_loop.setText("随机播放");
                        break;
                    case 2:
                        bt_loop.setText("单曲循环");
                        break;
                }
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        switch (playStyle){
                            case 0:
                                try {
                                    findNext();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case 1:
                                Random random=new Random();
                                position=position+random.nextInt(songs.size()-1);
                                position %=songs.size();
                                mediaPlayer.reset();
                                try {
                                    mediaPlayer.setDataSource(songs.get(position).getPath());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    mediaPlayer.prepare();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                mediaPlayer.start();
                                break;
                            case 2:
                                mediaPlayer.reset();
                                try {
                                    mediaPlayer.setDataSource(songs.get(position).getPath());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    mediaPlayer.prepare();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                mediaPlayer.start();
                                break;
                            default:
                                break;
                        }
                    }
                });
                break;
//            case R.id.seekBar:
//                intent.putExtra("action", 4);
//                break;
        }
        //startService(intent);
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.add:
                AddDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void findPrev() throws IOException {
        position--;
        if(position<0){
            position=songs.size()-1;
        }
        mediaPlayer.reset();
        mediaPlayer.setDataSource(songs.get(position).getPath());
        mediaPlayer.prepare();
        mediaPlayer.start();
        playingSong.setText(songs.get(position).getSong());
    }

    public void findNext() throws IOException {
        position++;
        if (position>songs.size()-1){
            position=0;
        }
        mediaPlayer.reset();
        mediaPlayer.setDataSource(songs.get(position).getPath());
        mediaPlayer.prepare();
        mediaPlayer.start();
        playingSong.setText(songs.get(position).getSong());
    }

    public void AddDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(ListActivity.this);
        final View view= LayoutInflater.from(ListActivity.this).inflate(R.layout.add,null);
        Button button=view.findViewById(R.id.button);
        final EditText editText1=(EditText)view.findViewById(R.id.editText1);
        final EditText editText2=(EditText)view.findViewById(R.id.editText2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String songName=editText1.getText().toString();
                String songPath=editText2.getText().toString();
                songDao.add("1",songName,songPath);
                Toast.makeText(ListActivity.this,"添加成功",Toast.LENGTH_SHORT).show();
                refreshData("1");
            }
        });
        builder.setView(view).show();
    }



    public void play(String path) throws IOException {
        mediaPlayer=new MediaPlayer();
        mediaPlayer.reset();
        mediaPlayer.setDataSource(path);
        mediaPlayer.setLooping(true);
//        File file=new File(path);
//        if(file.exists()){
//            Toast.makeText(this,"音乐不存在请检查输入路径",Toast.LENGTH_SHORT).show();
//            return;
//        }else{
            mediaPlayer.prepare();
            mediaPlayer.start();
            seekBarPlayProgress();
//        }
    }

    public void seekBarPlayProgress(){
        final int duration=mediaPlayer.getDuration();
        //计时器对象
        final Timer timer=new Timer();
        final TimerTask task=new TimerTask() {
            @Override
            public void run() {
                //开启线程定时获取当前播放进度
                int currentposition=mediaPlayer.getCurrentPosition();
                //利用message给主线程发消息更新seekbar进度
                Message ms=Message.obtain();
                Bundle bundle=new Bundle();
                bundle.putInt("duration",duration);
                bundle.putInt("currentposition",currentposition);
                ms.setData(bundle);
                ListActivity.handler.sendMessage(ms);
            }
        };
        timer.schedule(task,0,500);
        //当播放结束时停止播放
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {;
                timer.cancel();
                task.cancel();
            }
        });
    }

    public void seekPlayPositiom(int positont){
        mediaPlayer.seekTo(positont);
    }

    public void seekPlayProgress(int position){
        seekPlayPositiom(position);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        helper.close();
    }


}
