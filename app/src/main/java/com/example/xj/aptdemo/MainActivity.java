package com.example.xj.aptdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.annolib.anno.XRouter;



@XRouter( path = "mainpage")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        startActivity(new Intent(this, SecondActivity.class));

        System.out.println("tagtag" + getApplication().getPackageCodePath());
        System.out.println("\ntagtag" + getApplication().getPackageResourcePath());

    }
}
