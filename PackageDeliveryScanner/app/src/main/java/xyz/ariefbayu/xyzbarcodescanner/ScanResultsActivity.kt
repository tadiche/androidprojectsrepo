package xyz.ariefbayu.xyzbarcodescanner

import android.Manifest
import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.request.SimpleMultiPartRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.scan_image_fragment.*
import xyz.ariefbayu.xyzbarcodescanner.ui.main.ScanResultsFragment
import xyz.ariefbayu.xyzbarcodescanner.ui.main.ScanViewModel
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class ScanResultsActivity : AppCompatActivity() , Response.ProgressListener{
    //View model variable declared
    private var viewModel: ScanViewModel? = null
    private val MY_CAMERA_PERMISSION_CODE = 100
    private val CAMERA_REQUEST = 1888
    private var pictureFilePath: String? = null
    private var progressBar: ProgressBar? = null
    private var barcodescanText:String ? = null
    private var barcodeFormat:String ? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.scan_results_activity)
        progressBar = findViewById(R.id.progressBar) as? ProgressBar

        // Setting up View model
        viewModel = ViewModelProviders.of(this).get<ScanViewModel>(ScanViewModel::class.java)

        if (savedInstanceState == null) {
            val intent = intent
            val value = intent.getStringExtra("scantext") //if it's a string you stored.
            val format = intent.getStringExtra("barcodeFormat")
            viewModel!!.setScanText(value)
            val data = Bundle() //Use bundle to pass data
            barcodescanText = value
            barcodeFormat = format
            data.putString(
                "scantext",
                value
            ) //put string, int, etc in bundle with a key value

            val scanResultFragment: Fragment = ScanResultsFragment.newInstance() //Get Fragment Instance

            scanResultFragment.setArguments(data) //Finally set argument bundle to fragment

            supportFragmentManager.beginTransaction()
                .replace(R.id.container,scanResultFragment )
                .commitNow()
        }

    }
    protected override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            /*val photo = data?.extras?.get("data") as Bitmap
            imageView.setImageBitmap(photo)
            button.visibility =  View.VISIBLE*/

            /////////////////////////////
            val imgFile = File(pictureFilePath)
            if (imgFile.exists()) {
                //Log.i("Sridhar", "File path ->::" + pictureFilePath)
                imageView.setImageURI(Uri.fromFile(imgFile))
            }
            /////////////////////////////

        }
    }

    fun uploadDeliveredImageToServer(view: View) {

        /////////////////////////////
        //upload image to server
        /////////////////////////////
        val smr =
            SimpleMultiPartRequest(
                Request.Method.POST, "http://192.168.2.11:8080/image/upload",
                Response.Listener<String?> { response ->
                    Log.d("Response", response+pictureFilePath)
                    Toast.makeText(applicationContext, response, Toast.LENGTH_LONG).show()
                    if (response?.contains("ok",ignoreCase = true)!!)
                    {
                        //close this activity and back to continous scan activity
                        val myIntent = Intent(this@ScanResultsActivity, ContinuousActivity::class.java)
                        this@ScanResultsActivity.startActivity(myIntent)
                    }
                }, Response.ErrorListener { error ->
                    Toast.makeText(
                        applicationContext,
                        error.toString(),
                        Toast.LENGTH_LONG
                    ).show()
                })
        smr.addStringParam("barcode",barcodescanText)
        smr.addStringParam("barcodeType",barcodeFormat)
        smr.addStringParam("imageFileName",pictureFilePath)

        //need to make sure file size is less than 1048576 bytes
        //add logic to handle the requirement
        smr.addFile("imageFile", pictureFilePath)
        smr.setFixedStreamingMode(true)
        smr.setOnProgressListener(this)

        val mRequestQueue: RequestQueue = Volley.newRequestQueue(this)
        mRequestQueue.add(smr)
        mRequestQueue.start()
    }

    fun takeMailDeliveryPicture(view: View) {
        if (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
            } else {
                TODO("VERSION.SDK_INT < M")
            }
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                    arrayOf(Manifest.permission.CAMERA),
                    MY_CAMERA_PERMISSION_CODE
                )
            }
        } else {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            cameraIntent.putExtra( MediaStore.EXTRA_FINISH_ON_COMPLETION, true);
            if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                //startActivityForResult(cameraIntent, CAMERA_REQUEST)
                var pictureFile: File? = null
                try {
                    pictureFile = getPictureFile()
                } catch (ex: IOException) {
                    Toast.makeText(
                        this,
                        "Photo file can't be created, please try again",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }
                if (pictureFile != null) {
                    val photoURI = FileProvider.getUriForFile(
                        this,
                        "com.tadiche.android.fileprovider",
                        pictureFile
                    )
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(cameraIntent, CAMERA_REQUEST)
                    uploadButton.visibility =  View.VISIBLE
                }
            }
        }
    }

    private fun getPictureFile(): File? {
        val timeStamp: String = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
        val pictureFile = "tadiche_$timeStamp"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(pictureFile, ".jpg", storageDir)
        Log.i("Sridhar","File path::"+image.absolutePath)
        pictureFilePath = image.absolutePath
        return image
    }

    override fun onProgress(transferredBytes: Long, totalSize: Long) {
        //TODO("Not yet implemented")
        val percentage = (transferredBytes / totalSize.toFloat() * 100).toInt()
        // updating progress bar value
        progressBar?.setProgress(percentage)
    }
}