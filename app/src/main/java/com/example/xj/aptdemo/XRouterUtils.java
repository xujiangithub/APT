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

    private Context mContext;
    private HashMap<String, String> xRouteMap = new HashMap();


    private  XRouterUtils() {}
    private static XRouterUtils mInstance;
    public static XRouterUtils getInstance() {

        if (null == mInstance) {
            synchronized (XRouterUtils.class) {
                if (null == mInstance) {
                    mInstance = new XRouterUtils();
                }
            }
        }
        return mInstance;
    }

    public static void init(Application appContext) {
        getInstance().mContext = appContext;
        throughApk(appContext, XRouterInterface.class);
    }

    // 遍历整个Apk所有类
    private static void throughApk(Application appContext, Class cls) {

        if (cls.isInterface()) { // 是接口
            DexFile dexFile = null;
            try {
                dexFile = new DexFile(appContext.getPackageCodePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            Enumeration<String> classNames = dexFile.entries(); // 获取到所有类

            Log.i(TAG, "开始遍历所有apk下的类");
            while (classNames.hasMoreElements()) {
                String name = classNames.nextElement();

                if (name.contains("XRouter$$")) { // 是否包含生成文件类名段（筛选一次）
                    Log.i(TAG, name);
                    try {
                        Class cs = Class.forName(name);
                        if (cls.isAssignableFrom(cs)) { // 是否是约束接口的实现类（这里再筛选一次）
                            // 反射调用内部loadInto，将映射关系读入xRouteMap
                            Object obj = cs.newInstance();
                            Method method = cs.getDeclaredMethod("loadInto", Map.class);
                            method.setAccessible(true);
                            method.invoke(obj, mInstance.xRouteMap);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


            }
            Log.i(TAG, mInstance.xRouteMap.toString());

        }
    }

    // 跳转调用方法
    public void navigation(String path) {
        try {
            mContext.startActivity(new Intent(mContext, Class.forName(xRouteMap.get(path))));
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "该路由没有注册");
            e.printStackTrace();
        } catch (Exception e) {
            Log.e(TAG, "路由出现问题");
            e.printStackTrace();

        }
    }
}
