package com.example.youngtec.a20171201websocket;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import android.os.Build;

import static com.example.youngtec.a20171201websocket.DBHelper.TABLE_NAME_USER;
import static com.example.youngtec.a20171201websocket.DBHelper.USER_DEPARTMENT;
import static com.example.youngtec.a20171201websocket.DBHelper.USER_DEVICE;
import static com.example.youngtec.a20171201websocket.DBHelper.USER_IDENTIFIER;
import static com.example.youngtec.a20171201websocket.DBHelper.USER_MAIL;
import static com.example.youngtec.a20171201websocket.DBHelper.USER_NAME;
import static com.example.youngtec.a20171201websocket.DBHelper.USER_PHONE;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DBHelper dbhelper = null;
    //宣告要用到的控制項
    private Button btnConnect;
    //Service部分
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
    public void startService() {
        startService(new Intent(getBaseContext(), MainService.class));
    }
    //stop
    public void stopService(){
        stopService(new Intent(getBaseContext(),MainService.class));
    }
//選項Item設定(之後在寫)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.action_settings){
            return true;
        }
     return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  //隱藏狀態列
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        //Start Service
        startService();
        //Start Bind Service
        Intent bindIntent = new Intent(this, MainService.class);
        bindService(bindIntent, conn, BIND_AUTO_CREATE);
       // etDetails.append("初次使用請先按下啟動按鈕，來進行App後台程序" + "\n");
        dbhelper = new DBHelper(this);//建資料庫
        dbhelper.close();

        //UpdateINFO("test");
//定義控制項

        btnConnect = (Button) findViewById(R.id.btnConnect);
//Websocket 通訊
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    //Connect to Websocket Server
    private void Server_Close() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("確定要中斷嗎(連服務也會一起關閉)?"); //設定dialog 的title顯示內容
        dialog.setIcon(android.R.drawable.ic_dialog_alert);//設定dialog 的ICON
        dialog.setCancelable(false); //關閉 Android 系統的主要功能鍵(menu,home等...)
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //if (client != null) {
                //取消關閉client，改寫在service方面
                // client.close();
                //***這裡做兩個動作，unbind銷毀Service後再將Service停止
                if(isServiceRunning()==true) {
                    stopService();
                    finish();
                }
                else
                {
                    finish();
                }
            }
        })
                .setNegativeButton("No",new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog,int which){
                        //Do nothing...
                    }
                });
        dialog.show();
    }

    private void Server_Connect() {
        try {
            if(isServiceRunning()==true) {
                String URL = "ws://192.168.0.204:8181/";
                mainBinder.push(URL);
                //按下連線後，導入到Alarm監控的畫面(2018/01/18取消)
            }
            else//當Service為關閉的時候才會跳這錯誤，原則上市不可能
            {
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle("發生錯誤!!!Service並未正常啟動!"); //設定dialog 的title顯示內容
                dialog.setIcon(android.R.drawable.ic_dialog_alert);//設定dialog 的ICON
                dialog.setCancelable(false); //關閉 Android 系統的主要功能鍵(menu,home等...)
                dialog.setPositiveButton("確認", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //Do nothing
                    }
                });
                dialog.show();
            }
        }
        catch (Exception ex){
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("無法找到Server，請確認Server URL是否正確"); //設定dialog 的title顯示內容
            dialog.setIcon(android.R.drawable.ic_dialog_alert);//設定dialog 的ICON
            dialog.setCancelable(false); //關閉 Android 系統的主要功能鍵(menu,home等...)
            dialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Log.e("Error","Con't connect to server");
                }
            });
            dialog.show();
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //關閉unbind，節省後台記憶體
        unbindService(conn);
    }

//檢查Service是否有在Run
    public boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if(MainService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private String code() {
        try {
            Cursor cursor = getCursor();
            StringBuilder resultData = new StringBuilder();
            while (cursor.moveToNext()) {
                String code = cursor.getString(0);
                resultData.append(code).append("");
                resultData.append("\n");
            }
           String idetify=resultData.toString();
            return idetify;
        } catch (Exception ex) {
            Log.d("Database", "Show Error");
            String Error_Message="讀取資料庫的識別碼時發生錯誤";
            return Error_Message;
        }
    }
    public Cursor getCursor() {
        SQLiteDatabase db = dbhelper.getReadableDatabase();
        String[] columns = {USER_IDENTIFIER};
        Cursor cursor = db.query(TABLE_NAME_USER, columns, null, null, null, null, null);
        startManagingCursor(cursor);
        return cursor;
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_alarm) {
            Intent PagetoAlarmlist=new Intent();
            PagetoAlarmlist.setClass(MainActivity.this,AlarmActivity.class);
            startActivity(PagetoAlarmlist);
        } else if (id == R.id.nav_alarm_log) {
            Intent PagetoNetDatabase=new Intent();
            PagetoNetDatabase.setClass(MainActivity.this,Select_Activity.class);
            startActivity(PagetoNetDatabase);
            WebSocketImpl.DEBUG = true;
            System.setProperty("java.net.preferIPv6Addresses", "false");
            System.setProperty("java.net.preferIPv4Stack", "true");
        } else if (id == R.id.nav_database) {
            Intent PagetoDatabase=new Intent();
            PagetoDatabase.setClass(MainActivity.this,Database.class);
            startActivity(PagetoDatabase);
        } else if (id == R.id.nav_uesr) {
            Intent PateToUserData=new Intent();
            PateToUserData.setClass(MainActivity.this,UserData.class);
            startActivity(PateToUserData);
        } else if (id == R.id.nav_setting) {
           //還沒放好，之後再補
        } else if (id == R.id.nav_exit) {
            Server_Close();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public static class DraftInfo {
        private final String draftName;
        final Draft draft;
        public DraftInfo(String draftName, Draft draft) {
            this.draftName = draftName;
            this.draft = draft;
        }
        @Override
        public String toString() {
            return draftName;
        }
    }
}