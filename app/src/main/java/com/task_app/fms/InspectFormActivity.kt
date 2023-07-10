package com.task_app.fms

import android.app.Dialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.AudioManager
import android.media.ToneGenerator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
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
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import java.lang.reflect.Array.get
import java.lang.reflect.Array.set
import java.util.*

private const val CAMERA_REQUEST_CODE=101

class InspectFormActivity : AppCompatActivity() {
    lateinit var QRResult: String
    var PreQrResult: String = ""

    private lateinit var codeScanner: CodeScanner
    private lateinit var tv_textView: TextView
    private lateinit var scannerView: CodeScannerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inspect_form2)

        scannerView = findViewById<CodeScannerView>(R.id.scanner)
        tv_textView = findViewById<TextView>(R.id.textScanned) as TextView
        setUpPermission()
        codeScanner()



        val fabButton : FloatingActionButton = findViewById(R.id.fabButton)
        fabButton.setOnClickListener {
            showCustomDialogBox();
        }
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
                    Toast.makeText(this,"Camera permission ??",Toast.LENGTH_SHORT).show()
                }
                else{
                    //success
                }
            }
        }
    }

    private fun showCustomDialogBox() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.addfrontredpoint)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        var defectArrayAdapter: ArrayAdapter<String> =
                ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.DefectsList))
        var defectSpinner: Spinner = dialog.findViewById<Spinner>(R.id.defectspinner) as Spinner
        defectSpinner!!.adapter = defectArrayAdapter


        var pointArrayAdapter: ArrayAdapter<String> =
                ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.PointList))
        var pointSpinner: Spinner = dialog.findViewById<Spinner>(R.id.pointSpinner) as Spinner
        pointSpinner!!.adapter = pointArrayAdapter

        var pointText: TextView = dialog.findViewById<Spinner>(R.id.pointText) as TextView

        pointSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(pointSpinner.selectedItem == "Upto 3 Inch"){
                    pointText.text="1"
                }else if(pointSpinner.selectedItem == "3 to 6 Inch"){
                    pointText.text="2"
                }else if(pointSpinner.selectedItem == "6 to 9 Inch"){
                    pointText.text="3"
                }else{
                    pointText.text="4"
                }
            }
        }

        var item = pointSpinner.selectedItem
        Toast.makeText(this,""+item,Toast.LENGTH_SHORT).show()

        dialog.show()

    }
}