package com.example.xj.aptdemo;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.annotationlib.interfaceAnno.XRouterInterface;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dalvik.system.DexFile;

public class XRouterUtils {

    private static String TAG = "XRouterLog";

    private static Context mContext;
    private static HashMap<String, String> xRouteMap = new HashMap();

    public static void init(Application appContext) {
        mContext = appContext;
        throughApk(appContext, XRouterInterface.class);
    }

    private static void throughApk(Application appContext, Class cls) {


//        try {
//            XRouter$$MainActivity.class.newInstance().loadInto(xRouteMap);
//        } catch (InstantiationException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }
        //后面的代码实际上就是实现上面注释的功能

        if (cls.isInterface()) {
            Log.i(XRouterUtils.TAG, "是接口");
            DexFile dexFile = null;
            try {
                dexFile = new DexFile(appContext.getPackageCodePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            Enumeration<String> classNames = dexFile.entries();

            Log.i(TAG, "开始遍历所有apk下的类");
            while (classNames.hasMoreElements()) {
                String name = classNames.nextElement();

                if (name.contains("XRouter$$")) {
                    Log.i(TAG, name);
                    try {
                        Class cs = Class.forName(name);
                        Object obj = cs.newInstance();
                        Method method = cs.getDeclaredMethod("loadInto", Map.class);
                        method.setAccessible(true);
                        method.invoke(obj, xRouteMap);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


            }
            Log.i(TAG, xRouteMap.toString());

        }
    }

    public static void navigation(String path) {
        try {
            mContext.startActivity(new Intent(mContext, Class.forName(xRouteMap.get(path))));
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "该路由没有注册");
            e.printStackTrace();
        }
    }

}
