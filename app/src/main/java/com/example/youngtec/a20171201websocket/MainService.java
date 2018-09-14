package com.example.youngtec.a20171201websocket;


import android.app.Notification;

import android.app.Service;
import android.content.ContentValues;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import android.support.v4.app.NotificationManagerCompat;

import android.util.Log;

import android.widget.ArrayAdapter;

import android.widget.Toast;
import static android.provider.BaseColumns._ID;
import static android.widget.Toast.LENGTH_LONG;
import static com.example.youngtec.a20171201websocket.DBHelper.INFO;
import static com.example.youngtec.a20171201websocket.DBHelper.ALARM_ID;
import static com.example.youngtec.a20171201websocket.DBHelper.DATE;
import static com.example.youngtec.a20171201websocket.DBHelper.TABLE_NAME;
import static com.example.youngtec.a20171201websocket.DBHelper.TABLE_NAME_USER;
import static com.example.youngtec.a20171201websocket.DBHelper.CONFIRM;
import static com.example.youngtec.a20171201websocket.DBHelper.USER_NAME;
import static com.example.youngtec.a20171201websocket.DBHelper.USER_DEVICE;
import static com.example.youngtec.a20171201websocket.DBHelper.USER_PHONE;
import static com.example.youngtec.a20171201websocket.DBHelper.USER_MAIL;
import static com.example.youngtec.a20171201websocket.DBHelper.USER_DEPARTMENT;
import static com.example.youngtec.a20171201websocket.DBHelper.USER_IDENTIFIER;
import com.example.youngtec.a20171201websocket.MainActivity;
//該Import的都進來
import org.java_websocket.WebSocketImpl;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_10;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.drafts.Draft_75;
import org.java_websocket.drafts.Draft_76;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.Calendar;
import java.util.Date;

import android.os.Build;


/**
 * Created by Youngtec on 2017/12/18.
 */

public class MainService extends Service {

  //using database
    private DBHelper helper;
    public static final String TAG = "Youngtec Android Service";
    private DBHelper dbhelper = null;
    private MainBinder mBinder = new MainBinder();
    private WebSocketClient client;// Connect To Client
    private MainActivity.DraftInfo selectDraft;

    @Override
    public void onCreate() {
        super.onCreate();
//helper=new DBHelper(this,"AlarmData.db",null,1);//this service,db name ,standard,version1
        //Websocket 通訊
        MainActivity.DraftInfo[] draftInfos = {new MainActivity.DraftInfo( "WebSocket:Draft_17", new Draft_17() ), new MainActivity.DraftInfo
                ( "WebSocket:Draft_10", new Draft_10() ), new MainActivity.DraftInfo( "WebSocket:Draft_76", new Draft_76() ), new
                MainActivity.DraftInfo( "WebSocket:Draft_75", new Draft_75() )};// 取得所有連接的協定
         selectDraft = draftInfos[0];// 預設使用第一個
        ArrayAdapter<MainActivity.DraftInfo> draftAdapter = new ArrayAdapter<MainActivity.DraftInfo>( this, android.R.layout
                .simple_spinner_item, draftInfos );
        dbhelper = new DBHelper(this);//建資料庫
    }

    //Start Service crteate WebSocket Server
    //Service OPen

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//Notify("Service運行","Service已經被背景程式運行，如果要停止，請在裝置管理員關閉或按下Disconnect");
        return super.onStartCommand(intent, flags, startId);
    }
    //Service Stop
    @Override
    public void onDestroy() {
        //Log.d(TAG, "onDestroy() executed");
        //服務關掉之後，關閉連線(OK)
        if (client != null) {
            client.close();
        }

        //Toast.makeText( this, "Service stop", Toast.LENGTH_LONG ).show();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    class MainBinder extends Binder {
        //do something
        public void starttag() {
            Log.d("Tag", "Start Receive");
        }

        public void send(String text) {
            try {
                if (client != null) {
                    client.send(text);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void push(final String WebsocketIP) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (selectDraft == null) {
                            return;
                        }

                        String address = WebsocketIP;
                        client = new WebSocketClient(new URI(address), selectDraft.draft) {

//使用固定的WebsocletIP，這暫時不考慮
                            @Override
                            public void onOpen(final ServerHandshake serverHandshakeData) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        String title = "連線至Server通知";
                                        String text = "當前連線的網址為" + getURI();
                                        Log.e("wlf", "已經連接到Server【" + getURI() + "】");
                                        Notify(title, text);
                                    }
                                });
                                send("connect"+","+code());
                            }

                            @Override
                            public void onMessage(final String message) {
//抓取Time
                                Calendar calendar = Calendar.getInstance();
                                final int year = calendar.get(Calendar.YEAR);
                                final int month = calendar.get(Calendar.MONTH);
                                final int day = calendar.get(Calendar.DAY_OF_MONTH);
                                final int hour = calendar.get(Calendar.HOUR_OF_DAY);
                                final int minute = calendar.get(Calendar.MINUTE);
                                final int second = calendar.get(Calendar.SECOND);
                                final String date="日期"+year+"/"+month+"/"+day+"/"+"時間"+hour+":"+minute+":"+second;
                                //Run 執行續
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.e("wlf", "接收到Server的訊息【" + message + "】" +date);
                                        //通知顯示
                                    }
                                });
                                //判斷送來的是什麼訊息
                                String[] Array=message.split(",");
                                String condition=Array[0];

                                switch (condition){

                                    case "alarm":
                                        String Alarm_message=Array[1];
                                        String Date=Array[2];
                                        String Title_Alarm = "警報訊息";
                                        String Alarm_Message = message + "日期:" +date;
                                        //Using Class
                                        Notify(Title_Alarm, Alarm_Message);
                                        dbhelper.close();
                                        add(Alarm_message,Date,false);
                                        break;
                                    case "user":
                                        //會收到認證碼後存到手機裡面
                                         String User_Name=Array[1];
                                        String User_Device=Array[2];
                                        String User_Phone=Array[3];
                                        String User_Mail=Array[4];
                                        String User_Department=Array[5];
                                        String User_Identifier=Array[6];

                                        dbhelper.close();
                                        add_user(User_Name,User_Device,User_Phone,User_Mail,User_Department,User_Identifier);
                                        //存到資料庫後完成驗證動作
                                        break;

                                    case "connect_check":
                                        String Android_Code=Array[1].trim();//自己的code
                                        break;
                                    case "StringData":
                                        //進行字串拆解
                                        String[] Data=message.split(";");
                                        //解析Data資料後

                                        break;
                                    case "dataselect":
                                        //顯示全部的資料 格視為 資料名稱&&日期l
                                        String all_data=Array[1].trim();
                                        String[] data=all_data.split(";");
                                        String[] one_data=data[1].split("&&");
                                        String AE_info=one_data[0];
                                        String AE_date=one_data[1];
                                        add_temp(AE_info,AE_date);
                                        default:
                                            break;
                                }
                            }

                            @Override
                            public void onClose(final int code, final String reason, final boolean remote) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.e("wlf", "中斷Server連線【" + getURI() + "，state code： " + code + "，reason：" + reason + "】");
                                        Notify("警告","伺服器發生斷線!將會收不到報警資訊");
                                    }
                                });
                            }

                            @Override
                            public void onError(final Exception ex) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.e("wlf", "發生連線異常【Reason：" + ex + "】");
                                        Notify("警告","伺服器發生斷線!將會收不到報警資訊");
                                    }
                                });
                            }
                        };
                        client.connect();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        Notify("錯誤","找不到Server，請確認Server狀態");
                    }
                }

            }).start();
        }
    }
        //通知
        public void Notify(String title,String text){
    android.support.v7.app.NotificationCompat.Builder notificationBuilder = (android.support.v7.app.NotificationCompat.Builder) new android.support.v7.app.NotificationCompat.Builder( MainService.this )
            .setSmallIcon( android.R.drawable.ic_menu_view )
            .setContentTitle(title)
            .setContentText(text );
    notificationBuilder.setDefaults(
            Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE
    );
    NotificationManagerCompat notificationManager = NotificationManagerCompat.from( MainService.this );
    notificationManager.notify( 1, notificationBuilder.build() );
}
// insert to database
    public void add(String info, String date,Boolean confirm){
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(INFO, info);
        values.put(DATE, date);
        values.put(CONFIRM, confirm);

        db.insert(TABLE_NAME, null, values);
    }
    //將資料寫進User資料表，以方面日後的修正，引繼等等事項
    public void add_user(String Name,String Device,String Phone,String Mail,String Department,String Identifier){
        SQLiteDatabase db=dbhelper.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(USER_NAME,Name);
        values.put(USER_DEVICE,Device);
        values.put(USER_PHONE,Phone);
        values.put(USER_MAIL,Mail);
        values.put(USER_DEPARTMENT,Department);
        values.put(USER_IDENTIFIER,Identifier);
        db.insert(TABLE_NAME_USER,null,values);
    }
    public void add_temp(String info,String date){
        SQLiteDatabase db=dbhelper.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(INFO,info);
        values.put(DATE,date);

    }
    //識別碼
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
       // startManagingCursor(cursor);
        return cursor;
    }

}

