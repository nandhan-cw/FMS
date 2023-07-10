package com.task_app.fms;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class Dbconnection extends AppCompatActivity {

    Button connectdbbutton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dbconnection);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        connectdbbutton  = findViewById(R.id.connectdbbutton);

        connectdbbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Dbconnection.this,Login.class));
            }
        });
    }
}