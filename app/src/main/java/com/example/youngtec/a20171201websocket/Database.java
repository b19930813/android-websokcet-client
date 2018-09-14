package com.example.youngtec.a20171201websocket;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
//Import database value

import static com.example.youngtec.a20171201websocket.DBHelper.TABLE_NAME_USER;
import static com.example.youngtec.a20171201websocket.DBHelper.USER_NAME;
import static com.example.youngtec.a20171201websocket.DBHelper.USER_DEVICE;
import static com.example.youngtec.a20171201websocket.DBHelper.USER_PHONE;
import static com.example.youngtec.a20171201websocket.DBHelper.USER_MAIL;
import static com.example.youngtec.a20171201websocket.DBHelper.USER_DEPARTMENT;
import static com.example.youngtec.a20171201websocket.DBHelper.USER_IDENTIFIER;

public class Database extends AppCompatActivity {
    private DBHelper dbhelper = null;
    private TextView result = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbhelper = new DBHelper(this);//建資料庫
        dbhelper.close();
        result = (TextView) findViewById(R.id.textView2);
        show();
    }


    private void show() {
        try {
            Cursor cursor = getCursor();

            StringBuilder resultData = new StringBuilder("RESULT: \n");

            while (cursor.moveToNext()) {
                String user_name = cursor.getString(0);
                String user_device = cursor.getString(1);
                String user_phone = cursor.getString(2);
                String user_mail = cursor.getString(3);
                String user_department = cursor.getString(4);
                String user_identifier = cursor.getString(5);

                resultData.append(user_name).append(", ");
                resultData.append(user_device).append(", ");
                resultData.append(user_phone).append(", ");
                resultData.append(user_mail).append(", ");
                resultData.append(user_department).append(", ");
                resultData.append(user_identifier).append(", ");
                resultData.append("\n");
            }
            result.setText(resultData);
        } catch (Exception ex) {
            Log.d("Database", "Show Error");
        }
    }

    public Cursor getCursor() {
          SQLiteDatabase db = dbhelper.getReadableDatabase();
          String[] columns = {USER_NAME, USER_DEVICE, USER_PHONE, USER_MAIL, USER_DEPARTMENT, USER_IDENTIFIER};
          Cursor cursor = db.query(TABLE_NAME_USER, columns, null, null, null, null, null);
          startManagingCursor(cursor);

          return cursor;
    }
}
