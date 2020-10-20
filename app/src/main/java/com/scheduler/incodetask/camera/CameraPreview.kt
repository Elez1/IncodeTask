package com.scheduler.incodetask.camera

import android.content.Context
import android.content.res.Configuration
import android.hardware.Camera
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.FrameLayout
import com.scheduler.incodetask.extensions.getString
import java.io.IOException


class CameraPreview(context: Context, private val camera: Camera) : SurfaceView(context), SurfaceHolder.Callback {

    private val TAG = CameraPreview::class.java.simpleName

    private var mPreviewSize: Camera.Size? = null
    private val surfaceHolder = holder.apply {
        addCallback(this@CameraPreview)
    }

    override fun surfaceCreated(surfHolder: SurfaceHolder) {
        camera.apply {
            try {
                surfHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
                val params = parameters
                params.setRotation(90)
                setDisplayOrientation(90)
                parameters = params
                setPreviewDisplay(surfaceHolder)
                startPreview()
            } catch (e: IOException) {
                Log.e(TAG, "Error setting camera preview: ${e.message}")
            }
        }
    }

    override fun surfaceChanged(surfaceHolder: SurfaceHolder, format: Int, width: Int, height: Int) {
        camera.stopPreview()
        val params = camera.parameters
        try {
            requestLayout()
            params.setPreviewSize(mPreviewSize!!.width, mPreviewSize!!.height)
            camera.parameters = params
        } catch (e: IOException) {
            Log.e(TAG, "Error setting preview size: ${e.message}")
        }
        camera.setPreviewDisplay(surfaceHolder)
        camera.startPreview()
    }

    override fun surfaceDestroyed(p0: SurfaceHolder) {
        if(camera!=null){
            camera.stopPreview()
            camera.setPreviewCallback(null)

            camera.release()
        }
    }

    private fun getOptimalSize(params: Camera.Parameters, w: Int, h: Int): Camera.Size? {
        val ASPECT_TH = .2 // Threshold between camera supported ratio and screen ratio.
        var minDiff = Double.MAX_VALUE //  Threshold of difference between screen height and camera supported height.
        var targetRatio = 0.0
        var ratio: Double
        var optimalSize: Camera.Size? = null

        // check if the orientation is portrait or landscape to change aspect ratio.
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            targetRatio = h.toDouble() / w.toDouble()
        } else if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            targetRatio = w.toDouble() / h.toDouble()
        }

        // loop through all supported preview sizes to set best display ratio.
        for (s in params.supportedPreviewSizes) {
            ratio = s.width.toDouble() / s.height.toDouble()
            if (Math.abs(ratio - targetRatio) <= ASPECT_TH) {
                if (Math.abs(h - s.height) < minDiff) {
                    optimalSize = s
                    minDiff = Math.abs(h - s.height).toDouble()
                }
            }
        }

        return optimalSize
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = View.resolveSize(suggestedMinimumWidth, widthMeasureSpec)
        val height = View.resolveSize(suggestedMinimumHeight, heightMeasureSpec)
        setMeasuredDimension(width, height)
        mPreviewSize = getOptimalSize(camera.parameters, width, height)
    }
}