package com.scheduler.incodetask.application

import android.app.Application
import com.scheduler.incodetask.handler.PhotoHandler
import com.scheduler.incodetask.retrofit.RetrofitInstance
import com.scheduler.incodetask.service.BitmapService
import com.scheduler.incodetask.service.FileService
import com.scheduler.incodetask.view.activity.CameraActivity
import com.scheduler.incodetask.view.activity.MainActivity
import com.scheduler.incodetask.view.activity.PhotoActivity
import dagger.Component


class IncodeApplication : Application() {

    lateinit var appComponent: IncodeApplication.ApplicationComponent

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerIncodeApplication_ApplicationComponent.create()
    }

    @Component(modules = [RetrofitInstance::class])
    interface ApplicationComponent {
        fun getFileService(): FileService
        fun getBitmapService(): BitmapService
        fun getPhotoHandler(): PhotoHandler

        //        fun getPhotoService(): PhotoService
        fun injectPhotoActivity(activity: PhotoActivity)
        fun injectMainActivity(activity: MainActivity)
        fun injectCameraActivity(activity: CameraActivity)
    }

}