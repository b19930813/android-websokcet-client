package com.example.youngtec.a20171201websocket;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static android.provider.BaseColumns._ID;

/**
 * Created by Youngtec on 2017/12/27.
 */
//呼叫此class可以得到2種方法getReadableDatabase() 和getWritableDatabase()
public class DBHelper extends SQLiteOpenHelper {
    //儲存報警訊息的資料表名稱
    public static final String TABLE_NAME = "Alarm";
    //儲存使用者的資料表名稱
    //使用者的欄位名稱

    public static final String TABLE_NAME_USER="user";
    public static final String TABLE_NAME_SELECT_DATE="temp_alarm";
    public static final String TEMP_INFO="info";
    public static final String TEMP_DATE="date";
    public static final String TEMP_CONDITION="condition";
    public static final String USER_NAME="name";
    public static final String USER_DEVICE="device";
    public static final String USER_PHONE="phone";
    public static final String USER_MAIL="mail";
    public static final String USER_DEPARTMENT="department";
    public static final String USER_IDENTIFIER="indentifier";
    //報警資訊的欄位名稱
    public static final String INFO = "info";
    public static final String ALARM_ID="alarm_id";
    public static final String DATE = "date";

    public static final String CONFIRM = "confirm";
    private final static String DATABASE_NAME = "youngtec.db";  //資料庫名稱

    private final static int DATABASE_VERSION = 1;  //資料庫版本

    private static SQLiteDatabase database;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        //創建資料表，名稱為AlarmData,欄位依序為編號(ID)報警訊息(info)日期(date)是否查收該項報警資訊(confirm)
        final String INIT_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +ALARM_ID +" INTEGER,"+INFO + " CHAR, " + DATE + " CHAR, " + CONFIRM + " BOOLEAN);";
        db.execSQL(INIT_TABLE);
        //創建資料表，User
        final String USER_TABLE="CREATE TABLE "+ TABLE_NAME_USER +"("+USER_NAME +" CHAR,"+USER_DEVICE +" CHAR,"+USER_PHONE +" CHAR,"+USER_MAIL +" CHAR,"+USER_DEPARTMENT +" CHAR,"+USER_IDENTIFIER +" CHAR);";
        db.execSQL(USER_TABLE);
        final String TEMP_TABLE="CREATE TABLE "+ TABLE_NAME_SELECT_DATE +" (" +_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +ALARM_ID +" INTEGER,"+INFO + " CHAR, " + DATE + " CHAR, " + CONFIRM + " BOOLEAN);";
        db.execSQL(TEMP_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
//如果資料庫是舊的，可以利用這個method，將資料庫update(刪除)
        final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(DROP_TABLE);
        final String DROP_TABLE_USER="DROP TABLE IF EXISTS "+TABLE_NAME_USER;
        db.execSQL(DROP_TABLE_USER);
        final String DROP_TABLE_TEMP="DROP TABLE IF EXISTS "+TABLE_NAME_SELECT_DATE;
        db.execSQL(DROP_TABLE_TEMP);

        onCreate(db);
    }
}
