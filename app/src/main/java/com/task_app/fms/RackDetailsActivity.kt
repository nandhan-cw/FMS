package com.task_app.fms

import android.app.Dialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode

private const val CAMERA_REQUEST_CODE=101

class RackDetailsActivity : AppCompatActivity() {
    private lateinit var codeScanner: CodeScanner
    private lateinit var tv_textView: TextView
    private lateinit var scannerView: CodeScannerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rack_details_form)

        scannerView = findViewById<CodeScannerView>(R.id.scanner_emp)
//        tv_textView = findViewById<TextView>(R.id.textScanned) as TextView
        setUpPermission()
        codeScanner()

    }

    private fun codeScanner(){
        codeScanner = CodeScanner(this,scannerView)
        codeScanner.apply {
            camera = CodeScanner.CAMERA_BACK
            formats  = CodeScanner.ALL_FORMATS
            autoFocusMode= AutoFocusMode.CONTINUOUS
            scanMode= ScanMode.CONTINUOUS
            isAutoFocusEnabled=true
            isFlashEnabled=false

            decodeCallback = DecodeCallback {
                runOnUiThread{
//                    tv_textView.text=it.text
                    tv_textView.text = it.text
                    Log.e("output","Camera initialization error : ${it.text}")
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
                if(grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this,"Camera permission ??", Toast.LENGTH_SHORT).show()
                }
                else{
                    //success
                }
            }
        }
    }


}