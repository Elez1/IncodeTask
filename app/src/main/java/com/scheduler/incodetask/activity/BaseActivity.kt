package com.scheduler.incodetask.activity

import androidx.fragment.app.FragmentActivity
import com.scheduler.incodetask.fragment.SpinnerFragment

open class BaseActivity : FragmentActivity() {

    protected fun showSpinner() {
        val fragment = SpinnerFragment.getInstance()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(android.R.id.content, fragment, SpinnerFragment.TAG)
        fragmentTransaction.addToBackStack(SpinnerFragment.TAG)
        fragmentTransaction.commit()
    }

    protected fun hideSpinner() {
        supportFragmentManager.popBackStack()
    }
}