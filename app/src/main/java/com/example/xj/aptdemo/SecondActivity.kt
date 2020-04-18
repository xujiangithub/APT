package com.example.xj.aptdemo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.annolib.anno.XRouter
import kotlinx.android.synthetic.main.second_activity.*

class SecondActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.second_activity)

        txt.setOnClickListener {
            XRouterUtils.getInstance().navigation("mainpage")
        }
    }

}