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
import android.text.Editable
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import kotlin.collections.ArrayList
import kotlin.math.absoluteValue
import kotlin.math.round
import kotlin.math.roundToInt

private const val CAMERA_REQUEST_CODE=101

class InspectFormActivity : AppCompatActivity() {
    lateinit var QRResult: String
    var PreQrResult: String = ""

    private lateinit var codeScanner: CodeScanner
    private lateinit var tv_textView: TextView
    private lateinit var scannerView: CodeScannerView
    private lateinit var recyclerview: RecyclerView
    private lateinit var newarraylist: ArrayList<Points>
    private lateinit var result: TextView
    private lateinit var comMtr: TextInputEditText
    private lateinit var width: TextInputEditText
    val defectList = ArrayList<String>()
    val defectpointList = ArrayList<String>()
    val pointList = ArrayList<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inspect_form2)

        scannerView = findViewById<CodeScannerView>(R.id.scanner)
        tv_textView = findViewById<TextView>(R.id.textScanned) as TextView
        recyclerview = findViewById<RecyclerView>(R.id.recyclerview) as RecyclerView
        result = findViewById<TextView>(R.id.result) as TextView
        comMtr = findViewById<TextInputEditText>(R.id.comMtr) as TextInputEditText
        width = findViewById<TextInputEditText>(R.id.width) as TextInputEditText

        setUpPermission()
        codeScanner()

        val fabButton : FloatingActionButton = findViewById(R.id.fabButton)
        fabButton.setOnClickListener {
            showCustomDialogBox();
        }
        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.setHasFixedSize(true)
        newarraylist = arrayListOf<Points>()
        getUserData()
        findtotalpoint()
    }

    private fun getUserData(){
        newarraylist.clear()
        for (i in defectList.indices){
            val point = Points(defectList.get(i), defectpointList.get(i),pointList.get(i))
            newarraylist.add(point)
        }
        recyclerview.adapter = PointsAdapter(newarraylist)

    }

    private fun findtotalpoint(){
        var ts:Int = 0
        for (i in pointList){
            ts = ts + Integer.parseInt(i)
        }
//        val ts: Double =  ts

        if(comMtr.text!=null && width.text!=null){
            val comtext: String = comMtr.text.toString()
            val comvalue: Double? = comtext.toDoubleOrNull()

            val text: String = width.text.toString()
            val widthvalue: Double? = text.toDoubleOrNull()

            if (comvalue != null && widthvalue!=null) {
                val resultval = (ts / ((comvalue * widthvalue) / 32.92)).roundToInt() * 100 / 100.0
                result.text=resultval.toString()
            }
            else {

            }
        }

//        println("Result: $result")
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

        var defectSpinner: Spinner = dialog.findViewById<Spinner>(R.id.defectspinner) as Spinner
        var pointSpinner: Spinner = dialog.findViewById<Spinner>(R.id.pointSpinner) as Spinner
        var pointText: TextView = dialog.findViewById<Spinner>(R.id.pointText) as TextView
        var addButton: Button = dialog.findViewById<Button>(R.id.addButton) as Button


        var defectArrayAdapter: ArrayAdapter<String> =
                ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.DefectsList))

        defectSpinner!!.adapter = defectArrayAdapter


        var pointArrayAdapter: ArrayAdapter<String> =
                ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.PointList))
        pointSpinner!!.adapter = pointArrayAdapter


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

        addButton.setOnClickListener{
            defectList.add(defectSpinner.selectedItem as String)
            defectpointList.add(pointSpinner.selectedItem as String)
            if(pointSpinner.selectedItem == "Upto 3 Inch"){
                pointList.add("1")
            }else if(pointSpinner.selectedItem == "3 to 6 Inch"){
                pointList.add("2")
            }else if(pointSpinner.selectedItem == "6 to 9 Inch"){
                pointList.add("3")
            }else{
                pointList.add("4")
            }
            getUserData()
            findtotalpoint()
            dialog.dismiss()

        }
        dialog.show()

    }
}