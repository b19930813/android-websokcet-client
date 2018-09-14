package com.example.youngtec.a20171201websocket;

import android.app.DatePickerDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import static android.provider.BaseColumns._ID;
import static com.example.youngtec.a20171201websocket.DBHelper.INFO;
import static com.example.youngtec.a20171201websocket.DBHelper.DATE;
import static com.example.youngtec.a20171201websocket.DBHelper.TABLE_NAME;
import static com.example.youngtec.a20171201websocket.DBHelper.CONFIRM;
import static com.example.youngtec.a20171201websocket.DBHelper.TABLE_NAME_SELECT_DATE;
import static com.example.youngtec.a20171201websocket.DBHelper.TEMP_INFO;
import static com.example.youngtec.a20171201websocket.DBHelper.TEMP_DATE;
import static com.example.youngtec.a20171201websocket.DBHelper.TEMP_CONDITION;
import java.util.Calendar;

//發出日期傳給Server後，接收到Server的歷史數據，存到一個暫存的資料表後顯示出來
public class Select_Activity extends AppCompatActivity {
    private DBHelper dbhelper = null;
    private ListView Listresult=null;
    private int Year, Month, Day;
private Button btnstartdate;
private Button btnenddate;
private Button btnsend_date;
private EditText txt_startdate;
private EditText txt_enddate;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        dbhelper = new DBHelper(this);//建資料庫
        dbhelper.close();
        //Service
        Intent bindIntent = new Intent(this, MainService.class);
        bindService(bindIntent, conn, BIND_AUTO_CREATE);
        btnstartdate=(Button)findViewById(R.id.btnstartdate);
        btnenddate=(Button)findViewById(R.id.btnenddate);
        btnsend_date=(Button)findViewById(R.id.btnsend_date);
        txt_enddate=(EditText)findViewById(R.id.txt_enddate);
        txt_startdate=(EditText)findViewById(R.id.txt_startdate);
        //show();
        //監控事件
        //日期開始時的功能
        btnsend_date.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectdate_require(txt_startdate.getText().toString().trim(),txt_enddate.getText().toString().trim());
            }
        });
        btnstartdate.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                Year = c.get(Calendar.YEAR);
                Month = c.get(Calendar.MONTH);
                Day = c.get(Calendar.DAY_OF_MONTH);
                new DatePickerDialog(Select_Activity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        String format =setDateFormat(year,month,day);
                        txt_startdate.getText().clear();
                        txt_startdate.setText(format);
                    }
                },Year,Month,Day).show();
                //按鈕處理事件

            }
        });
        btnenddate.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
final Calendar c=Calendar.getInstance();
Year=c.get(Calendar.YEAR);
Month=c.get(Calendar.MONTH);
Day=c.get(Calendar.DAY_OF_MONTH);
new DatePickerDialog(Select_Activity.this, new DatePickerDialog.OnDateSetListener() {
    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        String format=setDateFormat(year,month,day);
        txt_enddate.getText().clear();
        txt_enddate.setText(format);
    }
},Year,Month,Day).show();
//按鈕處理事件

            }
        });
    }
    private void show() {
        Cursor cursor = getCursor();
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_expandable_list_item_2,
                cursor,
                new String[] {"info", "date"},
                new int[] {android.R.id.text1, android.R.id.text2},
                0);
        Listresult.setAdapter(adapter);
    }
    private String setDateFormat(int year,int month,int day){
        return String.valueOf(year) + "-"
                + String.valueOf(month + 1) + "-"
                + String.valueOf(day);
    }
    private void selectdate_require(String startdate, String enddate) {
        String require="DateSelect"+","+startdate+","+enddate;
        mainBinder.send(require);
    }
    @SuppressWarnings("deprecation")
    public Cursor getCursor() {
        SQLiteDatabase db = dbhelper.getReadableDatabase();
        String[] columns = {_ID,INFO,DATE,CONFIRM};
        Cursor cursor = db.query(TABLE_NAME_SELECT_DATE, columns, null, null, null, null, null);
        startManagingCursor(cursor);
        return cursor;
    }
}
