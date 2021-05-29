/*
 * Copyright (C) 2013 Peng fei Pan <sky@xiaopan.me>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.net.yzl.base.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Build;
import android.provider.Settings;

import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;


import androidx.core.app.ActivityCompat;

import org.json.JSONException;
import org.json.JSONObject;


import java.util.UUID;


/**
 * 设备工具箱，提供与设备硬件相关的工具方法
 */
public class DeviceUtils {

    private static final String TAG = DeviceUtils.class.getSimpleName();

    /**
     * 获取屏幕尺寸
     */
    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public static Point getScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR2) {
            return new Point(display.getWidth(), display.getHeight());
        } else {
            Point point = new Point();
            display.getSize(point);
            return point;
        }
    }

    public static String getDeviceModel(){
        return Build.MODEL;
    }

   public static String getDeviceOS(){
       return Build.VERSION.RELEASE;
   }


    public static String getDeviceSN(Context context){
        String serialNumber =Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return serialNumber;
    }

    public static String getDeviceSDK(){
        return Build.VERSION.SDK;
    }

    /**
     * 获取设备信息
     *
     * @return
     */
    public static String getClientInfo() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("client", "Android");
            jsonObject.put("version", Build.VERSION.RELEASE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    /**
     * 返回当前程序版本名
     */
    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            Log.d(TAG, "Exception:" + e);
        }
        return versionName;
    }

    public static String getHandSetInfo() {
        String handSetInfo =
                "model:" + Build.MODEL +
                        ",SDK:" + Build.VERSION.SDK +
                        ",os:" + Build.VERSION.RELEASE;
        return handSetInfo;
    }



}