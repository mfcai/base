package cn.net.yzl.base.thirdsdk.jsweb;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import android.provider.SyncStateContract;
import android.util.Log;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import androidx.fragment.app.Fragment;

import cn.net.yzl.base.app.LibApplication;
import cn.net.yzl.base.constant.Constant;
import cn.net.yzl.base.utils.PermissionManager;
import cn.net.yzl.base.utils.ToastUtils;


/**
 * Created by XIAO RONG on 2018/11/7.
 * WebChromeClient 可以辅助 WebView 处理 Javascript 的对话框,网站图标,网站标题等等。
 */

public class CustomWebChromeClient extends WebChromeClient {
    private final String TAG = "CustomWebChromeClient";


    private ValueCallback<Uri> mUploadMessage;
    private ValueCallback<Uri[]> mUploadMessage5;
    public static final int FILECHOOSER_RESULTCODE = 5173;
    public static final int REQUEST_UPLOAD_FILE_CODE = 12343;
    public static final int FILECHOOSER_RESULTCODE_FOR_ANDROID_5 = 5174;
    private Activity activity;
    private final static int DEF = 95;
    private NumberProgressBar mProgressBar;
    private Fragment fragment;


    public ValueCallback<Uri> getUploadMessage() {
        return mUploadMessage;
    }

    public void setUploadMessage(ValueCallback<Uri> mUploadMessage) {
        this.mUploadMessage = mUploadMessage;
    }

    public ValueCallback<Uri[]> getUploadMessage5() {
        return mUploadMessage5;
    }

    public void setUploadMessage5(ValueCallback<Uri[]> mUploadMessage5) {
        this.mUploadMessage5 = mUploadMessage5;
    }

    public CustomWebChromeClient(NumberProgressBar progressBar) {
        this.mProgressBar = progressBar;
    }

    public CustomWebChromeClient(Activity activity, NumberProgressBar progressBar) {
        this.activity = activity;
        this.mProgressBar = progressBar;
    }

    public CustomWebChromeClient(Fragment fragment, NumberProgressBar progressBar) {
        this.fragment = fragment;
        this.mProgressBar = progressBar;
    }

    //获取网页的加载进度并显示
    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
        if (newProgress >= DEF) {
            mProgressBar.setVisibility(View.GONE);
        } else {
            if (mProgressBar.getVisibility() == View.GONE) {
                mProgressBar.setVisibility(View.VISIBLE);
            }

            mProgressBar.setProgress(newProgress);
        }
        Log.i(TAG, "onProgressChanged——" + newProgress);
    }

    //获取Web网页中的标题
    @Override
    public void onReceivedTitle(WebView view, String title) {
        Log.i(TAG, "onReceivedTitle——" + title);
    }

    // For Android < 3.0
    public void openFileChooser(ValueCallback<Uri> uploadMsg) {
        Log.i(TAG, "openFileChooser");
        this.openFileChooser(uploadMsg, "*/*");
    }

    // For Android >= 3.0
    public void openFileChooser(ValueCallback<Uri> uploadMsg,
                                String acceptType) {
        this.openFileChooser(uploadMsg, acceptType, null);
    }

    // For Android >= 4.1
    public void openFileChooser(ValueCallback<Uri> uploadMsg,
                                String acceptType, String capture) {
        Log.i(TAG, "openFileChooser");
        mUploadMessage = uploadMsg;
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("*/*");
        if (activity != null) {
            activity.startActivityForResult(Intent.createChooser(i, "File Browser"),
                    FILECHOOSER_RESULTCODE);
        }
        if (fragment != null) {
            fragment.startActivityForResult(Intent.createChooser(i, "File Browser"),
                    FILECHOOSER_RESULTCODE);
        }
    }

    // For Lollipop 5.0+ Devices
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public boolean onShowFileChooser(WebView mWebView,
                                     ValueCallback<Uri[]> filePathCallback,
                                     FileChooserParams fileChooserParams) {
//        判断camera是否可用但是没有判断权限 没有权限的地方应该去申请权限，不能简单提示
//        if (!CameraUtils.cameraIsCanUse()) {
        if (!PermissionManager.checkPermission(LibApplication.getInstance(), Constant.Permission.PERMS_CAMERA)) {
            if (null != activity) {
                PermissionManager.requestPermission(activity, Constant.Permission.CAMERA_PERMISSION_TIP, Constant.Permission.CAMERA_PERMISSION_CODE, Constant.Permission.PERMS_CAMERA);
            } else {
                ToastUtils.showToast(activity,"您没有相机权限，请到设置里打开相机权限~");
            }
            return false;
        }
        String[] types = fileChooserParams.getAcceptTypes();
        String accept = types[0];

//        if ("gsh/*".equals(accept)) {
//            new PickPhotoUtil(activity).promptDialog(true);
//        } else {
//            new PickPhotoUtil(activity).promptDialog(false);
//        }
//
//        PickPhotoUtil.mFilePathCallback = filePathCallback;
        return true;
    }

    @Override
    public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
        callback.invoke(origin, true, false);
        super.onGeolocationPermissionsShowPrompt(origin, callback);
    }

    @Override
    public void onReceivedIcon(WebView view, Bitmap icon) {
        super.onReceivedIcon(view, icon);
    }

    private Intent createCameraIntent() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//拍照
        //=======================================================
        Intent imageIntent = new Intent(Intent.ACTION_GET_CONTENT);//选择图片文件
        imageIntent.setType("image/*");
        //=======================================================
        return cameraIntent;
    }
}

