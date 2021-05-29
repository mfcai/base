package cn.net.yzl.base.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;


import androidx.annotation.NonNull;
import androidx.annotation.Size;

import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;

/**
 * Created by ${xinGen} on 2017/11/1.
 * blog: http://blog.csdn.net/hexingen
 *
 * 权限工具类
 *
 */

public class PermissionManager {

    /**
     *
     * @param context
     * return true:已经获取权限
     * return false: 未获取权限，主动请求权限
     */
   // @AfterPermissionGranted 是可选的
    public static boolean checkPermission(Activity context,String[] perms) {
        return EasyPermissions.hasPermissions(context, perms);
    }
    /**
     * 请求权限
     * @param context
     */
    public static void requestPermission(Activity context,String tip,int requestCode,String[] perms) {
        EasyPermissions.requestPermissions(context, tip,requestCode,perms);
    }
    @SuppressLint("RestrictedApi")
    public static void requestPermissions(@NonNull Activity host, String rationale,
                                          int requestCode, @Size(min = 1) @NonNull String... perms) {
        // 需要原因说明弹窗的依然交给EasyPermission处理
        if (!TextUtils.isEmpty(rationale)) {
            EasyPermissions.requestPermissions(host, rationale, requestCode, perms);
        } else {
            // rational的值为空时，直接调用权限申请，绕过EasyPermission的判断
            PermissionRequest request = new PermissionRequest.Builder(host, requestCode, perms).build();
            request.getHelper().directRequestPermissions(requestCode, perms);
        }
    }

    /**
     *
     * @param context
     * return true:已经获取权限
     * return false: 未获取权限，主动请求权限
     */
    // @AfterPermissionGranted 是可选的
    public static boolean checkPermission(Context context, String[] perms) {
        return EasyPermissions.hasPermissions(context, perms);
    }


}
