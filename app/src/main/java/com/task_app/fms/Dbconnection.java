package com.task_app.fms;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputEditText;

public class Dbconnection extends AppCompatActivity {

    Button connectdbbutton;
    TextInputEditText ServerIPText,SeverPortText,DBport,Pwd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dbconnection);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ServerIPText = (TextInputEditText) findViewById(R.id.ipaddress);
        SeverPortText = (TextInputEditText) findViewById(R.id.portno);
        Pwd = (TextInputEditText) findViewById(R.id.password);
        DBport = (TextInputEditText) findViewById(R.id.username);
        connectdbbutton  = findViewById(R.id.connectdbbutton);

        connectdbbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(Dbconnection.this,Login.class));
                String ServerIP = ServerIPText.getText().toString();
                String Username = SeverPortText.getText().toString();
                String DBPort = DBport.getText().toString();

                String Password = Pwd.getText().toString();

                SharedPreferences settings = getApplicationContext().getSharedPreferences("EmpDetails", 0);
                SharedPreferences.Editor editor = settings.edit();

                final SharedPreferences.Editor serverIp = editor.putString("serverip", ServerIP);
                serverIp.apply();

                final SharedPreferences.Editor userName = editor.putString("username", Username);
                userName.apply();

                final SharedPreferences.Editor PWD = editor.putString("password", Password);
                PWD.apply();

                final SharedPreferences.Editor dbPort = editor.putString("dbport", DBPort);
                dbPort.apply();

                Intent resultIntent = new Intent();
                resultIntent.putExtra("serverip", ServerIP);
                resultIntent.putExtra("username", Username);
                resultIntent.putExtra("password", Password);
                setResult(RESULT_OK, resultIntent);
                finish();

            }
        });
    }
}