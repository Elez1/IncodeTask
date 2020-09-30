package com.scheduler.incodetask.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Camera
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.scheduler.incodetask.R
import com.scheduler.incodetask.camera.CameraPreview
import kotlinx.android.synthetic.main.activity_camera.*

class CameraActivity : AppCompatActivity() {

    private var camera: Camera? = null
    private var cameraPreview: CameraPreview? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
    }

    override fun onResume() {
        super.onResume()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), 1)
            }else{
                startCamera()
            }
        } else {
            startCamera()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCamera()
        }
    }

    private fun startCamera() {
        camera = getCameraInstance()
        Log.e("sdfsdfsdf", "Starting camera")
        cameraPreview = camera?.let {
            CameraPreview(this, it)
        }

        cameraPreview.also {
            cameraHolderLayout.addView(it)
        }

        captureButton.setOnClickListener {
            camera!!.takePicture(null, null, Camera.PictureCallback { photoBytes, _ ->
                setResult(Activity.RESULT_OK, Intent().apply { putExtra("sdfsdfsdf", photoBytes) })
            })
        }
    }

    private fun getCameraInstance(): Camera? {
        return try {
            Camera.open() // attempt to get a Camera instance
        } catch (e: Exception) {
            // Camera is not available (in use or does not exist)
            throw e
        }
    }
}