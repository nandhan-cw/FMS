package com.task_app.fms;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Login extends AppCompatActivity {
    Button loginbutton;
    TextInputEditText username,password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        loginbutton = findViewById(R.id.loginbutton);
        password = findViewById(R.id.password);
        username = findViewById(R.id.username);

        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(Login.this,MainActivity.class));
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                RTSGlobal.ServerIP = data.getStringExtra("serverip");
                RTSGlobal.UserName = data.getStringExtra("username");
                RTSGlobal.Password = data.getStringExtra("password");
                LoginChk();
            }
            if (resultCode == RESULT_CANCELED) {
                finish();
            }
        }
    }

    private void LoginChk(){
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
                //Toast.makeText(getApplicationContext(), "Wifi Connection",Toast.LENGTH_SHORT).show();
                if (DBConnChk(RTSGlobal.ServerIP, RTSGlobal.UserName, RTSGlobal.Password)) {
                    loginbutton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(username.getText().toString().equals("bss") && password.getText().toString().equals("profit")){
                                RequestCameraPermission();
                                Toast.makeText(getApplicationContext(), "Logging in...",Toast.LENGTH_SHORT).show();
                            }else{
                                finish();
                            }
                        }
                    });
                }
            } else {
                // not connected to the internet
                Toast.makeText(getApplicationContext(), "Please turn on WiFi and try again", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private void RequestCameraPermission() {

        Dexter.withContext(this).withPermission(Manifest.permission.CAMERA).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Toast CameraPermissionToast = Toast.makeText(getApplicationContext(), "Camera Permission is Required", Toast.LENGTH_SHORT);
                CameraPermissionToast.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        CameraPermissionToast.cancel();
                    }
                }, 1000);
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }

    public static Connection createConnection(String driver, String url, String username, String password) throws ClassNotFoundException, SQLException {
        Class.forName(driver);
        return DriverManager.getConnection(url, username, password);
    }

    public static Connection createConnection() throws ClassNotFoundException, SQLException {
        return createConnection(RTSGlobal.DEFAULT_DRIVER, RTSGlobal.DEFAULT_URL, RTSGlobal.DEFAULT_USERNAME, RTSGlobal.DEFAULT_PASSWORD);
    }

    public Boolean DBConnChk(String DBServerIP, String DBUsername, String DBPwd) {
        RTSGlobal.DEFAULT_URL = "jdbc:oracle:thin:@"+DBServerIP+":"+RTSGlobal.DBPort+":XE";
        RTSGlobal.DEFAULT_USERNAME = DBUsername;
        RTSGlobal.DEFAULT_PASSWORD = DBPwd;
        try {
            RTSGlobal.connection = createConnection();
            //Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_SHORT).show();
            Toast.makeText(getApplicationContext(), "DB Connected",Toast.LENGTH_SHORT).show();
            RTSGlobal.connection.close();
            return(true);
        } catch (Exception e) {
//            Toast.makeText(MainActivity.this, "Connection Error: " + e, Toast.LENGTH_SHORT).show();
            return(false);
        }
    }

    public String readUsrPwd(final String userName){
        String pwd = "";
        try{
            RTSGlobal.connection = createConnection();
            Statement stmt = RTSGlobal.connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT PASSWORD FROM GARMENT.AXUSERS WHERE USERNAME = '"+userName+"'");
            while(rs.next()){
                pwd = rs.getString(1);
            }
            rs = stmt.executeQuery("SELECT B.DESIG FROM EMPMAS A, DESIGMAS B WHERE A.EMPNAME = '" +
                    userName.toUpperCase() + "' AND A.DESIG = B.DESIGMASID");
//            if(rs.next()) resp = rs.getString(1);
            return pwd;
        }catch (Exception e) {
            Toast.makeText(getApplicationContext(), "" + e, Toast.LENGTH_SHORT).show();
            return "failed";
        }
    }
    public static String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}