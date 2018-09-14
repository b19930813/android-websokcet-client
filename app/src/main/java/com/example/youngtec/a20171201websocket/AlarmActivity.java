package com.example.youngtec.a20171201websocket;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import static android.provider.BaseColumns._ID;
import static com.example.youngtec.a20171201websocket.DBHelper.INFO;
import static com.example.youngtec.a20171201websocket.DBHelper.DATE;
import static com.example.youngtec.a20171201websocket.DBHelper.TABLE_NAME;
import static com.example.youngtec.a20171201websocket.DBHelper.CONFIRM;
//測試用


import android.os.Build;

public class AlarmActivity extends AppCompatActivity  {
       private DBHelper dbhelper = null;
    private TextView result = null;
    private ListView Listresult=null;
//呼叫Service是因為要傳送確認訊息到Server 端，採用Service的Send功能
    private MainService.MainBinder mainBinder;
    private ServiceConnection conn=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            //連線時執行的動作
            mainBinder=(MainService.MainBinder)iBinder;
            //執行主程序,push加後面的IP
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            //服務關閉時所產生的動作，不知道要加什麼先用finish暫定
            finish();
        }
    };
    //Run Service
    private SwipeRefreshLayout mSwipeRefreshLayout;
    //下拉刷新

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        //Service
        Intent bindIntent = new Intent(this, MainService.class);
        bindService(bindIntent, conn, BIND_AUTO_CREATE);

        //資料刷新
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                },3000);
                mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_red_light,
                        android.R.color.holo_blue_light,
                        android.R.color.holo_green_light,
                        android.R.color.holo_orange_light,
                        android.R.color.holo_purple);

            }
        });
        Listresult= (ListView) findViewById(R.id.list);
        dbhelper = new DBHelper(this);//建資料庫
        dbhelper.close();
        //add(); //每次應用程式執行時,會新增

        //result = (TextView) findViewById(R.id.textView2);

        show1(); //顯示資料內容



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //dbhelper = new DBHelper(this);//this service,db name ,standard,version1
        Listresult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(
                        AlarmActivity.this);
                dialog.setTitle("確認訊息");
                dialog.setMessage(" 是否確認完這筆資料，按下確認會回傳通知");
                dialog.setPositiveButton("確認", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Send to all
                        String device_name=Build.DEVICE;
                        mainBinder.send("事件id");
                    }
                })
                        .setNegativeButton("保留", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Do Nothing
                            }
                        });
                dialog.show();
            }
        });

    }
    private void show1() {
        Cursor cursor = getCursor();
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_expandable_list_item_2,
                cursor,
                new String[] {"info", "date"},
                new int[] {android.R.id.text1, android.R.id.text2},
                0);
        Listresult.setAdapter(adapter);
    }
    //已經用不到了
/*
    private void show() {
        Cursor cursor = getCursor();

        StringBuilder resultData = new StringBuilder("RESULT: \n");

        while(cursor.moveToNext()){

            int id = cursor.getInt(0);

            String info = cursor.getString(1);

            String data = cursor.getString(2);

            String confirm = cursor.getString(3);

            resultData.append(id).append(": ");

            resultData.append(info).append(", ");

            resultData.append(data).append(", ");

            resultData.append(confirm).append(", ");

            resultData.append("\n");

        }

        result.setText(resultData);
    }
*/

    @SuppressWarnings("deprecation")
    public Cursor getCursor() {
        SQLiteDatabase db = dbhelper.getReadableDatabase();

        String[] columns = {_ID, INFO, DATE, CONFIRM};

        Cursor cursor = db.query(TABLE_NAME, columns, null, null, null, null, null);
        startManagingCursor(cursor);
        return cursor;
    }
}
