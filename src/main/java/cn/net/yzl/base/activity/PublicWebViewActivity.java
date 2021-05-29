package cn.net.yzl.base.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.huantansheng.easyphotos.EasyPhotos;
import com.huantansheng.easyphotos.callback.SelectCallback;
import com.huantansheng.easyphotos.models.album.entity.Photo;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cn.net.yzl.base.R;
import cn.net.yzl.base.arouter.ARouterConstant;
import cn.net.yzl.base.common.BaseActivity;
import cn.net.yzl.base.common.MessageEvent;
import cn.net.yzl.base.constant.Constant;
import cn.net.yzl.base.statusbar.StatusBarBlackOnWhiteUtil;
import cn.net.yzl.base.thirdsdk.jsweb.CustomWebChromeClient;
import cn.net.yzl.base.thirdsdk.jsweb.CustomWebViewClient;
import cn.net.yzl.base.thirdsdk.jsweb.ProgressBarWebView;
import cn.net.yzl.base.utils.GlideEngine;
import cn.net.yzl.base.utils.NetworkUtils;
import cn.net.yzl.base.utils.PermissionManager;
import cn.net.yzl.base.utils.ScreenUtils;

@Route(path = ARouterConstant.ACTIVITY_PUBLIC_WEBVIEW_ACTIVITY)
public class PublicWebViewActivity extends BaseActivity {
    private final static String TAG = PublicWebViewActivity.class.getName();
    ProgressBarWebView webView;
    LinearLayout noWifi;
    TextView tvWebpageReload;
    TextView tvWebpageReturn;
    LinearLayout webvieHeader;
    ImageView left_img;
    TextView title_text;
    RelativeLayout rlPagerLayout;
    private String mUrl;
    private boolean needToken;
    private String orderId;
    private String paramJson;//参数多的情况下以jsion方式传输
    public final static Map<String, String> cacheMap = new ConcurrentHashMap<>();

    private final static int MSG_NOTICE_REFRESH = 4;
    public static String FLAG_REFRESH = "needRereshData";   //H5传过来需要通知下个H5页面刷新数据的标识

    private String tempUrl;        //临时路径
    private String callTel;
    String fromType;
    CustomWebChromeClient chromeClient;
    public static final int MSG_PARAM_TO_H5 = 1;
    public static final int MSG_LOCALINFO_TO_H5 = 2;
    public static final int MSG_TOKEN_INVALID = 3;
    public static final int MSG_TOKEN_INVALID2 = 1231;
    public static final int MSG_WEBVIEWURL_CHANGE = 4;  //页面内加载的路径有变动
    private static final int MSG_NO_NETWORK = 5;
    private static final int MSG_RELOAD = 6;
    private static final int MSG_LOCATION_SUCCESS = 7;//定位成功
    private static final int MSG_LOCATION_FAIL = 8;//定位成功
    private static final int MSG_AUTHOR_LOC_SUC = 9;//定位权限获取成功
    private static final int MSG_AUTHOR_LOC_FAIL = 10;//定位权限获取失败
    private static final int MSG_LOCATION_TIMEOUT = 11;//定位超时
    private static final int MSG_NETWORK_STATE = 12;
    private static final int MSG_MULTI_FILE_CHOICE = 13;//图片多选
    private static final int MSG_CLOSE_WINDOW = 14;
    private final static int PHOTO_REQUEST = 100;
    private final static int VIDEO_REQUEST = 120;
    private final static int IMAGE_REQUEST = 110;
    private final static int REQUEST_CODE_CHOOSE_PICTURES = 10010; //多张图片选择
    private final static int REQUEST_CODE_TAKE_PHOTO_WEB = 10011; //多张图片选择

    private String takePhotoPath = ""; // 选择照片 拍照得到的图片
    private String takePhotoName = ""; // 选择照片 拍照得到的图片名称
    private String actionType = ""; //选择照片

    private final int DELAY_TIME = 500;

    private boolean videoFlag = false;

    private boolean isReload = false;//是否重新加载页面
    private boolean isShowHeader = false;//是否显示头部(true:显示，false:不显示)
    boolean isOrderDetails;

    private ValueCallback<Uri> mUploadMessage;
    private ValueCallback<Uri[]> mUploadCallbackAboveL;

    private String pageFrom = "";

    String userAgent;//webview 的头
    //    int pageContent = 0;
    private File fileUri = new File(Environment.getExternalStorageDirectory().getPath() + "/" + SystemClock.currentThreadTimeMillis() + ".jpg");
    private Uri imageUri;
    ArrayList<String> selectedPhotos = new ArrayList<>();//已选择的图片
    ArrayList<String> mFailUploadPhotos = new ArrayList<>();
    List<Map<String, String>> mH5uploadPhotoList = new ArrayList<>();//已上传成功的图片

    private boolean isPickAndUpload = false;//true:H5正在进行多选图片上传;false:h5没有进行多选图片上传
    private String h5TXToken = "";

    int success_count = 0;
    int fail_count = 0;
    long uploadCurTime;
    private String tongtianxiaoOldNew = "";//新版从bi调用通天晓和老版调用不同


    long lasttime;
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what) {
                case MSG_PARAM_TO_H5:
                    break;
                case MSG_CLOSE_WINDOW:
                    finish();
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    protected int getLayoutId() {
        return R.layout.pub_activity_webview;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        ARouter.getInstance().inject(this);
        StatusBarBlackOnWhiteUtil.setStatusBarColorAndFontColor(this, 0);
        lasttime = System.currentTimeMillis();
        webView = findViewById(R.id.web_view);
        noWifi = findViewById(R.id.no_wifi);
        tvWebpageReload = findViewById(R.id.tv_webpage_reload);
        tvWebpageReturn = findViewById(R.id.tv_webpage_return);
        webvieHeader = findViewById(R.id.webvie_header);
        left_img = findViewById(R.id.left_img);
        title_text = findViewById(R.id.title_text);
        rlPagerLayout = findViewById(R.id.rl_pager_layout);
        if (!NetworkUtils.hasNetWork(PublicWebViewActivity.this)) {
            showNoNetwork();
        }
        rlPagerLayout.setPadding(0, ScreenUtils.getStatusBarHeight(this), 0, 0);
    }


    @Override
    protected void onDestroy() {

        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        webView.destroy();
        super.onDestroy();

    }

    @Override
    protected void onResume() {
        super.onResume();
        webView.resumeTimers();
        webView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        webView.onPause();
        webView.pauseTimers();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void initData() {

        mUrl ="file:///android_asset/dist/index.html";
        Log.i(TAG, "paramrl===" + mUrl);
        if ("true".equals(cacheMap.get("hastitle"))) {
            webvieHeader.setVisibility(View.VISIBLE);
            String title_name = cacheMap.get("titlename");
            if (!TextUtils.isEmpty(title_name)) {
                title_text.setText(title_name);
            }
            cacheMap.put("hastitle", "");
        }
        initWebView(mUrl);
    }

    private void showNoNetwork() {
        webView.setVisibility(View.GONE);
        noWifi.setVisibility(View.VISIBLE);
        if (isReload) {
            isReload = false;
            hideProgressDialog();
        }
    }

    @Override
    protected void initEvents() {
        tvWebpageReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noWifi.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
                webView.loadUrl(mUrl);
            }
        });
        tvWebpageReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                StatusBarCompat.cancelLightStatusBar(getWindow());
                finish();
            }
        });

        left_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //update by caimingfu 2019-01-10,原因：点击按钮：webView.getWebView().canGoBack()返回true,无法退回上一页
                finish();
//                if (webView.getWebView().canGoBack()) {
//                    if (!Utils.hasNetWork(PublicWebViewActivity.this)) {
//                        showNoNetwork();
//                        return;
//                    }
//                    webView.getWebView().goBack();
//                } else {
////                    StatusBarCompat.cancelLightStatusBar(getWindow());
//
//                }
            }
        });
    }





    private void initWebView(String url) {
        /**
         * 原因：webview 从Lollipop(5.0)开始 webview默认不允许混合模式，https当中不能加载http资源，如果要加载，需单独设置开启。
         * @auth liupan  文件服务器返回的文件路径是http开头的，为防止广告侵入，我们的页面路径是https开头的
         */
        try {
            webView.setWebViewClient(new CustomWebViewClient(webView.getWebView()) {
                @Override
                public void onPageSuccess() {
                    long spend_time = System.currentTimeMillis() - lasttime;
                    Log.e("SPEND_TIME", "IT'S SPEND:" + spend_time);
                    if (isReload) {
                        isReload = false;
                        hideProgressDialog();
                    }
                    if (isShowHeader) {
                        if (TextUtils.isEmpty(title_text.getText().toString())) {
                            title_text.setText(webView.getWebView().getTitle());
                        }
                    }
                }

                @Override
                public void onPageError() {
                    showNoNetwork();
                }


                @NonNull
                @Override
                public Map<String, String> onPageHeaders(String url) {
                    return null;
                }


            });
        } catch (Exception e) {
            Log.d("tag", "onCreate", e);
        }
        chromeClient = new CustomWebChromeClient(this, webView.getProgressBar()) {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (view.getUrl() != null) {
                    Log.e(TAG, "view.getUrl()====" + view.getUrl());
                    if (view.getUrl().contains("details")) {        //易点通内的详情，配合H5把webView往下移动一点距离
                        Message msg = handler.obtainMessage();
                        msg.what = MSG_WEBVIEWURL_CHANGE;
                        handler.removeMessages(MSG_WEBVIEWURL_CHANGE);
                        handler.sendMessageDelayed(msg, DELAY_TIME);
                    }
                }
                super.onProgressChanged(view, newProgress);
            }
        };
        webView.setCustomWebChromeClient(chromeClient);
        webView.addJavascriptInterface(new JavaScriptInterface(), "appInterface");
        userAgent = webView.getWebView().getSettings().getUserAgentString();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        //todo : 极光bug
        // 为啥会存在null的情况
        if (mUrl == null) return;
        webView.loadUrl(url);
        tempUrl = url;              //记录下当前页面的路径

    }

    public void paramtoH5(boolean isLocalInfo, String param, String param2) {
        System.out.println("MSG_CLUEID_OK - > " + param);
        if (isLocalInfo) {
            if (!TextUtils.isEmpty(param2)) {
                webView.loadUrl("javascript:localInfo(" + param + "," + param2 + ")");
            } else {
                webView.loadUrl("javascript:localInfo(" + param + ")");
            }
        } else {
            webView.loadUrl("javascript:paramToH5(" + param + ")");
        }

    }

    @SuppressLint("NewApi")
    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);


    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onActivityResultAboveL(int requestCode, int resultCode, Intent data) {
        if (requestCode != PHOTO_REQUEST || mUploadCallbackAboveL == null) {
            return;
        }
        Uri[] results = null;
        if (resultCode == Activity.RESULT_OK) {
            if (data == null) {
                results = new Uri[]{imageUri};
            } else {
                String dataString = data.getDataString();
                ClipData clipData = data.getClipData();
                if (clipData != null) {
                    results = new Uri[clipData.getItemCount()];
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        results[i] = item.getUri();
                    }
                }

                if (dataString != null)
                    results = new Uri[]{Uri.parse(dataString)};
            }
        }
        mUploadCallbackAboveL.onReceiveValue(results);
        mUploadCallbackAboveL = null;
    }

    private void checkCallPhonePermission(String phoneNum) {
        boolean result = PermissionManager.checkPermission(PublicWebViewActivity.this, Constant.Permission.PERMS_CALLPHONE);
        if (!result) {
            PermissionManager.requestPermission(PublicWebViewActivity.this, Constant.Permission.CALLPHONE_PERMISSION_TIP,
                    Constant.Permission.CALLPHONE_PERMISSION_CODE, Constant.Permission.PERMS_CALLPHONE);
        } else {
            Intent intent = new Intent(Intent.ACTION_CALL);
            Uri data = Uri.parse("tel:" + phoneNum);
            intent.setData(data);
            startActivity(intent);
        }
    }



    class JavaScriptInterface {

        @JavascriptInterface
        public void hideTabBar(String param) {


        }


        /**
         * 拨打电话（直接拨打电话）
         *
         * @param phoneNum 电话号码
         */
        @JavascriptInterface
        public void callPhone() {

            checkCallPhonePermission("13811371197");
            Log.d(TAG,callTel);
        }

        @JavascriptInterface
        public void takephoto() {
            EasyPhotos.createCamera(PublicWebViewActivity.this)
                    .setFileProviderAuthority("cn.net.yzl.main.fileprovider")
                    .start(Constant.Permission.PICTURE_CODE);
        }

        @JavascriptInterface
        public void takepic() {
            EasyPhotos.createAlbum(PublicWebViewActivity.this, false, GlideEngine.getInstance())
                    .setFileProviderAuthority("cn.net.yzl.main.fileprovider")
                    .setOriginalMenu(false, true, "")//显示 原图按键，默认不选择，表示 压缩发送
                    .setCount(1)
                    .start(new SelectCallback() {

                        @Override
                        public void onResult(ArrayList<Photo> photos, boolean isOriginal) {

                        }
                    });
        }


        @JavascriptInterface
        public void copyToClipper(String param) {
            // 得到剪贴板管理器
            ClipboardManager cmb = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            cmb.setText(param.trim());
        }


        @JavascriptInterface
        public void noNetWork(String param) {
            Message msg = handler.obtainMessage();
            msg.what = MSG_NO_NETWORK;
            handler.sendMessage(msg);
        }

    }




    /**
     * 通过文件下载地址 获取到文件的名称
     *
     * @param url 下载地址
     * @return 文件名 包含后缀
     */
    private String getFileName(String url) {
        if (null == url || url.isEmpty()) {
            return "";
        } else if (!url.contains("name")) {
            String[] names = url.split("/");
            return names[names.length - 1];
        } else {
            String[] urls = url.split("\\?");
            if (urls.length == 2) {
                String[] params = urls[1].split("&");
                HashMap<String, String> hashMap = new HashMap(params.length);
                for (String param : params) {
                    String[] keyAndValue = param.split("=");
                    if (keyAndValue.length == 2) {
                        hashMap.put(keyAndValue[0], keyAndValue[1]);
                    }
                }
                return hashMap.get("name");
            } else {
                String[] names = url.split("/");
                return names.length == 2 ? names[1] : url;
            }
        }
    }









}
