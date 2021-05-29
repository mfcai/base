package cn.net.yzl.base.thirdsdk.jsweb;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.GeolocationPermissions;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.widget.LinearLayout;


import java.io.File;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * 带有进度条的 WebView
 *
 * @author YEZHENNAN220
 * @date 2016-07-07 17:08
 * 2019-05-23
 * 在LoanWebview中已设置所需要的属性，不需要重复设置
 *
 * 2019-10-10
 * 但是由于webView.canGoBack()一直返回为true，则无法返回到上一Activity(订单管理页面).
 * 保证原来逻辑不变，可以自己维护一个webview的历史栈,根据自己的需求进行过滤跳转或者重新加载页面:
 * 思路：
 * 1）判断当前按键是否为返回按键，
 * 2）判断当前链接，是否有历史链接，
 *    不使用webview.goback(),获取到历史链接后自己进行loadUrl()操作.拦截按键，返回true
 *    不是，不对按键进行拦截，返回false
 *  一共涉及3个类：
 *  CustomWebViewClient,拿到要跳转的url,并调用LoanWebView提供的接口，保存url
 *  LoanWebView,url保存
 *  ProgressBarWebView：用于拦截返回键
 *
 *  2019-10-11，不在webView中处理返回键
 */
public class ProgressBarWebView extends LinearLayout {

    static final String TAG = ProgressBarWebView.class.getSimpleName();
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);
    private NumberProgressBar mProgressBar;
    private LoanWebView mWebView;
    private Activity mContext;
    public ProgressBarWebView(Context context) {
        super(context);
        mContext = (Activity)context;
        init(context, null);
    }



    public ProgressBarWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = (Activity)context;
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public ProgressBarWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = (Activity)context;
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ProgressBarWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = (Activity)context;
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setOrientation(LinearLayout.VERTICAL);

        // 初始化进度条
        if (mProgressBar == null) {
            mProgressBar = new NumberProgressBar(context, attrs);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                mProgressBar.setId(View.generateViewId());
            } else {
                mProgressBar.setId(generateViewId());
            }
        }
        addView(mProgressBar);

        // 初始化webview
        if (mWebView == null) {
            mWebView = new LoanWebView(context.getApplicationContext());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                mWebView.setId(View.generateViewId());
            } else {
                mWebView.setId(generateViewId());
            }
        }

        mWebView.setWebChromeClient(new CustomWebChromeClient(mContext,mProgressBar));
        //在LoanWebview中已设置所需要的属性，不需要重复设置
//        WebSettings webviewSettings = mWebView.getSettings();
//        if(Build.VERSION.SDK_INT >= 19) {
//            mWebView.getSettings().setLoadsImagesAutomatically(true);
//        } else {
//            mWebView.getSettings().setLoadsImagesAutomatically(false);
//        }
//        // 判断系统版本是不是5.0或之上
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            //让系统不屏蔽混合内容和第三方Cookie
//            CookieManager.getInstance().setAcceptThirdPartyCookies(mWebView, true);
//            webviewSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
//        }
//        // 不支持缩放
//        webviewSettings.setSupportZoom(false);
//        // 自适应屏幕大小
//        webviewSettings.setUseWideViewPort(true);
//        webviewSettings.setLoadWithOverviewMode(true);
//        //支持localStorage
//        webviewSettings.setDomStorageEnabled(true);
//        //支持JS
//        webviewSettings.setJavaScriptEnabled(true);
//        webviewSettings.setDefaultTextEncodingName("UTF-8");
//        webviewSettings.setAllowContentAccess(true); // 是否可访问Content Provider的资源，默认值 true
//        webviewSettings.setAllowFileAccess(true);    // 是否可访问本地文件，默认值 true
//        // 是否允许通过file url加载的Javascript读取本地文件，默认值 false
//        webviewSettings.setAllowFileAccessFromFileURLs(false);
//        // 是否允许通过file url加载的Javascript读取全部资源(包括文件,http,https)，默认值 false
//        webviewSettings.setAllowUniversalAccessFromFileURLs(false);
//        //支持定位服务
//        webviewSettings.setDatabaseEnabled(true);
//        String dir = context.getDir("database", Context.MODE_PRIVATE).getPath();
//        webviewSettings.setGeolocationDatabasePath(dir);





        mWebView.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                //webview长按时将会调用系统的复制控件,如果想要屏蔽只需要返回ture 即可
                return false;
//                return true;
            }
        });
        //2019-10-11,在这里拦截返回键不太合适，放到Activity中去处理
//        mWebView.setOnKeyListener(new OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if (event.getAction() == KeyEvent.ACTION_DOWN) {
//                    final String url = mWebView.popLastPageUrl();
//                    if (keyCode == KeyEvent.KEYCODE_BACK && !TextUtils.isEmpty(url)) {
//                        mWebView.loadUrl(url);
//                        return true;
//                    }
//                    if(keyCode == KeyEvent.KEYCODE_BACK){
//                        if(mWebView.canGoBack()) {
//                            //获取webView的浏览记录
//                            WebBackForwardList mWebBackForwardList = mWebView.copyBackForwardList();
//                            //这里的判断是为了让页面在有上一个页面的情况下，跳转到上一个html页面，而不是退出当前activity
//                            if (mWebBackForwardList.getCurrentIndex() > 0) {
//                                String historyUrl = mWebBackForwardList.getItemAtIndex(mWebBackForwardList.getCurrentIndex() - 1).getUrl();
//                                final String url = mWebView.popLastPageUrl();
//                                if (!historyUrl.equals(url)) {
//                                    mWebView.goBack();
//                                    return true;
//                                }
//                            }
//                    }
//                }
//                return false;
//            }
//        });

        addView(mWebView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }


    public NumberProgressBar getProgressBar() {
        return mProgressBar;
    }


    public LoanWebView getWebView() {
        return mWebView;
    }

    /**
     * Loads the given URL.
     *
     * @param url the URL of the resource to load
     */
    public void loadUrl(String url) {
        if(!TextUtils.isEmpty(url) && mWebView != null) {
            mWebView.loadUrl(url);
        }
    }

    public void reload() {
        if(mWebView != null) {
            mWebView.reload();
        }
    }

    public void clearCache(boolean includeDiskFiles) {
        if(mWebView != null) {
            mWebView.clearCache(includeDiskFiles);
        }
    }

    @SuppressLint("JavascriptInterface")
    public void addJavascriptInterface(Object object, String name){
        if(mWebView != null) {
            mWebView.addJavascriptInterface(object, name);
        }
    }



    public void setWebViewClient(CustomWebViewClient client) {
        if(mWebView != null) {
            mWebView.setWebViewClient(client);
        }
    }

    public void setWebChromeClient(WebChromeClient chromeClient) {
        if(mWebView != null) {
            mWebView.setWebChromeClient(chromeClient);
        }
    }

    public void setCustomWebChromeClient(CustomWebChromeClient chromeClient){
        if(mWebView != null) {
            mWebView.setWebChromeClient(chromeClient);
        }
    }

    public void onResume(){
        if(mWebView != null) {
            mWebView.onResume();
        }
    }

    public void resumeTimers(){
        if(mWebView != null) {
            mWebView.resumeTimers();
        }
    }

    public void onPause(){
        if(mWebView != null) {
            mWebView.onPause();
        }
    }

    public void pauseTimers(){
        if(mWebView != null) {
            mWebView.pauseTimers();
        }
    }

    public void destroy(){
        if(mWebView != null){
            mWebView.setWebViewClient(null);
            mWebView.setWebChromeClient(null);
            mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            //deleted by caimingfu .2019-07-01. repeat.fixedStillAttached()有这个功能
            //add by caimingfu  2019-07-03 极光统计错误：View=android.widget.ZoomButtonsController$Container{c071fc9 V.E...... ........ 0,0-1080,146} not attached to window manager
            //原因：防止Activity destroy时webView缩放引起崩溃，先把webView从窗体中删除
            ((ViewGroup) mWebView.getParent()).removeView(mWebView);
            mWebView.destroy();
            mWebView = null;
        }
    }


    /**
     * Generate a value suitable for use in View.setId(int).
     * This value will not collide with ID values generated at build time by aapt for R.id.
     *
     * @return a generated ID value
     */
    public static int generateViewId() {
        for (;;) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }


}
