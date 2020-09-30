package com.scheduler.incodetask.camera

import android.content.Context
import android.hardware.Camera
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import java.io.IOException

class CameraPreview(context: Context, private val camera: Camera) : SurfaceView(context), SurfaceHolder.Callback {

    private val TAG = CameraPreview::class.java.simpleName

    private val surfaceHolder = holder.apply {
        addCallback(this@CameraPreview)
    }

    override fun surfaceCreated(p0: SurfaceHolder) {
        camera.apply {
            try {
                setPreviewDisplay(surfaceHolder)
                startPreview()
            } catch (e: IOException) {
                Log.d(TAG, "Error setting camera preview: ${e.message}")
            }
        }
    }

    override fun surfaceChanged(surfaceHolder: SurfaceHolder, format: Int, width: Int, height: Int) {
        if (surfaceHolder.surface == null) {
            // preview surface does not exist
            return
        }

        try {
            camera.stopPreview()
        } catch (e: Exception) {
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        camera.apply {
            try {
                val params = parameters
                params.setRotation(90)
                setDisplayOrientation(90)
                parameters = params
                setPreviewDisplay(surfaceHolder)
                startPreview()
            } catch (e: Exception) {
                Log.d(TAG, "Error starting camera preview: ${e.message}")
            }
        }
    }

    override fun surfaceDestroyed(p0: SurfaceHolder) {

    }





}