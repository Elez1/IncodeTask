package com.scheduler.incodetask.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.scheduler.incodetask.R

class SpinnerFragment private constructor() : Fragment() {

    companion object {
        val TAG = SpinnerFragment::class.java.simpleName

        fun getInstance() = SpinnerFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_spinner, container, false)
    }
}