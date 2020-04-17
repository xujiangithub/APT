package com.example.xj.aptdemo;

import android.app.Application;
import android.content.Context;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        XRouterUtils.init(this);
    }

    @Override
    public Context getApplicationContext() {
        return super.getApplicationContext();
    }
}
