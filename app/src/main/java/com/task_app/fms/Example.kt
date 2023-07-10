package com.task_app.fms

import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.*
import org.w3c.dom.Text

private const val CAMERA_REQUEST_CODE=101

class Example : AppCompatActivity() {

    private lateinit var codeScanner: CodeScanner
    private lateinit var tv_textView: TextView
    private lateinit var scannerView: CodeScannerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_example)
        scannerView = findViewById<CodeScannerView>(R.id.codescanner)
        tv_textView = findViewById<TextView>(R.id.textScanned) as TextView
        setUpPermission()
        codeScanner()

    }

    private fun codeScanner(){
        codeScanner = CodeScanner(this,scannerView)
        codeScanner.apply {
            camera = CodeScanner.CAMERA_BACK
            formats  = CodeScanner.ALL_FORMATS
            autoFocusMode=AutoFocusMode.CONTINUOUS
            scanMode=ScanMode.CONTINUOUS
            isAutoFocusEnabled=true
            isFlashEnabled=false

            decodeCallback = DecodeCallback {
                runOnUiThread{
                    tv_textView.text=it.text
                }
            }
            errorCallback = ErrorCallback {
                runOnUiThread{
                    Log.e("Main","Camera initialization error : ${it.message}")
                }
            }
        }

        scannerView.setOnClickListener{
            codeScanner.startPreview()
        }
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    private fun setUpPermission(){
        val permission = ContextCompat.checkSelfPermission(this,android.Manifest.permission.CAMERA)
        if(permission != PackageManager.PERMISSION_GRANTED){
            makeRequest()
        }
    }

    private fun makeRequest(){
        ActivityCompat.requestPermissions(this,arrayOf(android.Manifest.permission.CAMERA),CAMERA_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            CAMERA_REQUEST_CODE -> {
                if(grantResults.isEmpty() || grantResults[0] !=PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this,"Camera permission ??",Toast.LENGTH_SHORT).show()
                }
                else{
                    //success
                }
            }
        }
    }

}