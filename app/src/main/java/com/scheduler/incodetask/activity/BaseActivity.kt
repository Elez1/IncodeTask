package com.scheduler.incodetask.activity

import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.scheduler.incodetask.fragment.SpinnerFragment

open class BaseActivity : FragmentActivity() {

    protected fun showSpinner() {
        Log.e("sdfsdfsdf", "showing fragment")
        val fragment = SpinnerFragment.getInstance()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(android.R.id.content, fragment, SpinnerFragment.TAG)
        fragmentTransaction.addToBackStack(SpinnerFragment.TAG)
        fragmentTransaction.commit()
    }

    protected fun hideSpinner() {
        Log.e("sdfsdfsdf", "hiding fragment")
        supportFragmentManager.popBackStack()
    }
}