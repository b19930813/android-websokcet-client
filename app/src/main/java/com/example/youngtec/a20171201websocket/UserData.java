package com.example.youngtec.a20171201websocket;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class UserData extends AppCompatActivity {
    private MainService.MainBinder mainBinder;
    private Button btnSubmit;
    private EditText etUserName;
    private EditText etPhone;
    private EditText etEmail;
    private EditText etDepartment;

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
        setContentView(R.layout.activity_user_data);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);  Intent bindIntent = new Intent(this, MainService.class);
        bindService(bindIntent, conn, BIND_AUTO_CREATE);
        btnSubmit=(Button)findViewById(R.id.btnsubmit);
        etUserName = (EditText) findViewById(R.id.etUserName);
        etPhone=(EditText)findViewById(R.id.etPhone);
        etEmail=(EditText)findViewById(R.id.etEmail);
        etDepartment=(EditText)findViewById(R.id.etDepartment);

        btnSubmit.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                //Send Data to Server
                String device_name= Build.DEVICE;
                String User_name=etUserName.getText().toString().trim();
                String User_phone=etPhone.getText().toString().trim();
                String User_Email=etEmail.getText().toString().trim();
                String User_Department=etDepartment.getText().toString().trim();
                //傳送使用者資料到Server
                String user_data="require"+","+User_name+","+device_name+","+User_phone+","+User_Email+","+User_Department;
                mainBinder.send(user_data);
            }
        });

    }
}
