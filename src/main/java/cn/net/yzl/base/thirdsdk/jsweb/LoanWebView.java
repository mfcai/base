package cn.net.yzl.base.thirdsdk.jsweb;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityManager;
import android.webkit.GeolocationPermissions;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;



import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import cn.net.yzl.base.BuildConfig;
import cn.net.yzl.base.utils.ToastUtils;


/**
 * Created by dell on 2018-5-24.
 * 2019-05-23
 * 在webview设置中，禁用H5缓存
 *
 *  2019-10-10
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
 *  2019-10-11，不在webview中处理返回键
 */

public class LoanWebView extends WebView {
    private static final String TAG =LoanWebView.class.getSimpleName();
    protected Map<String, JsCallJava> mJsCallJavas;
    protected Map<String, String> mInjectJavaScripts;
    private boolean mIsInited;
    private Boolean mIsAccessibilityEnabledOriginal;
    /**
     * 记录URL的栈
     */
//    private final Stack<String> mUrls = new Stack<>();
    public LoanWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        mIsInited = true;
    }

//    public Stack<String> getUrlStack(){
//        return mUrls;
//    }
//    /**
//     * 记录非重定向链接, 避免刷新页面造成的重复入栈
//     *
//     * @param url 链接
//     */
//    public void recordUrl(String url) {
//        mUrls.push(url);
//    }
//    /**
//     * 获取上一页的链接
//     **/
//    public synchronized String getLastPageUrl() {
//        return mUrls.size() > 0 ? mUrls.peek() : null;
//    }
//
//    /**
//     * 推出上一页链接
//     */
//    public String popLastPageUrl() {
//        if (mUrls.size() >= 2) {
//            mUrls.pop(); //当前url
//            return mUrls.pop();
//        }
//        return null;
//    }
    public LoanWebView(Context context) {
        super(context,null);
        init();
    }

    @Override
    public void destroy() {

        //每次destroy webview清理缓存
        clearCache(true);
        clearHistory();
        clearFormData();
//        mUrls.clear();
        setVisibility(View.GONE);
        if (mJsCallJavas != null) {
            mJsCallJavas.clear();
        }
        if (mInjectJavaScripts != null) {
            mInjectJavaScripts.clear();
        }
        //deleted by caimingfu .2019-07-01. repeat.fixedStillAttached()有这个功能
        //add by caimingfu  2019-07-03 极光统计错误：View=android.widget.ZoomButtonsController$Container{c071fc9 V.E...... ........ 0,0-1080,146} not attached to window manager
        //原因：将WebView中的控件删除
        removeAllViewsInLayout();
        //fixedStillAttached功能重复，暂时先放在这里
        fixedStillAttached();
        releaseConfigCallback();
        if (mIsInited) {
            resetAccessibilityEnabled();
            //LogUtils.i(TAG, "destroy web");

        }
        super.destroy();
    }
    @Override
    public void setOverScrollMode(int mode) {
        try {
            super.setOverScrollMode(mode);
        } catch (Throwable e) {
            Pair<Boolean, String> pair = isWebViewPackageException(e);
            if (pair.first) {
                destroy();
            } else {
                throw e;
            }
        }
    }

    public static Pair<Boolean, String> isWebViewPackageException(Throwable e) {
        String messageCause = e.getCause() == null ? e.toString() : e.getCause().toString();
        String trace = Log.getStackTraceString(e);
        if (trace.contains("android.content.pm.PackageManager$NameNotFoundException")
                || trace.contains("java.lang.RuntimeException: Cannot load WebView")
                || trace.contains("android.webkit.WebViewFactory$MissingWebViewPackageException: Failed to load WebView provider: No WebView installed")) {
            if(TextUtils.isEmpty(messageCause)){
                messageCause ="未知错误";
            }
            return new Pair<Boolean, String>(true, "WebView load failed, " + messageCause);
        }
        //极光统计错误：java.lang.NullPointerException: Attempt to read from field 'java.lang.Object android.util.Pair.second' on a null object reference
        if(!TextUtils.isEmpty(messageCause)) {
            return new Pair<Boolean, String>(false, messageCause);
        }else{
            return new Pair<Boolean, String>(false, "未知错误");
        }
    }

    @TargetApi(11)
    protected boolean removeSearchBoxJavaBridge() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB
                    && Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                Method method = this.getClass().getMethod("removeJavascriptInterface", String.class);
                method.invoke(this, "searchBoxJavaBridge_");
                return true;
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
        }
        return false;
    }
    // Activity在onDestory时调用webView的destroy，可以停止播放页面中的音频
    private void fixedStillAttached() {
        // java.lang.Throwable: Error: destroy() called while still attached!
        // at android.webkit.WebViewClassic.destroy(WebViewClassic.java:4142)
        // at android.webkit.destroy(java:707)
        ViewParent parent = getParent();
        if (parent instanceof ViewGroup) { // 由于自定义webView构建时传入了该Activity的context对象，因此需要先从父容器中移除webView，然后再销毁webView；
            ViewGroup mWebViewContainer = (ViewGroup) getParent();
            mWebViewContainer.removeAllViewsInLayout();
        }
    }

    // 解决WebView内存泄漏问题；
    private void releaseConfigCallback() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) { // JELLY_BEAN
            try {
                Field field = WebView.class.getDeclaredField("mWebViewCore");
                field = field.getType().getDeclaredField("mBrowserFrame");
                field = field.getType().getDeclaredField("sConfigCallback");
                field.setAccessible(true);
                field.set(null, null);
            } catch (NoSuchFieldException e) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace();
                }
            } catch (IllegalAccessException e) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace();
                }
            }
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) { // KITKAT
            try {
                Field sConfigCallback = Class.forName("android.webkit.BrowserFrame").getDeclaredField("sConfigCallback");
                if (sConfigCallback != null) {
                    sConfigCallback.setAccessible(true);
                    sConfigCallback.set(null, null);
                }
            } catch (NoSuchFieldException e) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace();
                }
            } catch (ClassNotFoundException e) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace();
                }
            } catch (IllegalAccessException e) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void resetAccessibilityEnabled() {
        if (mIsAccessibilityEnabledOriginal != null) {
            setAccessibilityEnabled(mIsAccessibilityEnabledOriginal);
        }
    }

    private void setAccessibilityEnabled(boolean enabled) {
        AccessibilityManager am = (AccessibilityManager) getContext().getSystemService(Context.ACCESSIBILITY_SERVICE);
        try {
            Method setAccessibilityState = am.getClass().getDeclaredMethod("setAccessibilityState", boolean.class);
            setAccessibilityState.setAccessible(true);
            setAccessibilityState.invoke(am, enabled);
            setAccessibilityState.setAccessible(false);
        } catch (Throwable e) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "setAccessibilityEnabled", e);
            }
        }
    }

    @Override
    public boolean isPrivateBrowsingEnabled() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1
                && getSettings() == null) {

            return false; // getSettings().isPrivateBrowsingEnabled()
        } else {
            return super.isPrivateBrowsingEnabled();
        }
    }
    /**
     * 经过大量的测试，按照以下方式才能保证JS脚本100%注入成功：
     * 1、在第一次loadUrl之前注入JS（在addJavascriptInterface里面注入即可，setWebViewClient和setWebChromeClient要在addJavascriptInterface之前执行）；
     * 2、在webViewClient.onPageStarted中都注入JS；
     * 3、在webChromeClient.onProgressChanged中都注入JS，并且不能通过自检查（onJsPrompt里面判断）JS是否注入成功来减少注入JS的次数，因为网页中的JS可以同时打开多个url导致无法控制检查的准确性；
     *
     * @deprecated Android 4.2.2及以上版本的 addJavascriptInterface 方法已经解决了安全问题，如果不使用“网页能将JS函数传到Java层”功能，不建议使用该类，毕竟系统的JS注入效率才是最高的；
     */
    @SuppressLint("JavascriptInterface")
    @Override
    @Deprecated
    public final void addJavascriptInterface(Object interfaceObj, String interfaceName) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            super.addJavascriptInterface(interfaceObj, interfaceName);
            return;
        } else {
            if(BuildConfig.DEBUG) {
                Log.i(TAG, "use mJsCallJavas:" + interfaceName);
            }
        }

        Log.d(TAG, "addJavascriptInterface:" + interfaceObj + "   interfaceName:" + interfaceName);
        if (mJsCallJavas == null) {
            mJsCallJavas = new HashMap<String, JsCallJava>();
        }
        mJsCallJavas.put(interfaceName, new JsCallJava(interfaceObj, interfaceName));
        injectJavaScript();
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "injectJavaScript, addJavascriptInterface.interfaceObj = " + interfaceObj + ", interfaceName = " + interfaceName);
        }
        addJavascriptInterfaceSupport(interfaceObj, interfaceName);
    }

    protected void addJavascriptInterfaceSupport(Object interfaceObj, String interfaceName) {
    }

    protected void injectJavaScript() {
        for (Map.Entry<String, JsCallJava> entry : mJsCallJavas.entrySet()) {
            this.loadUrl(buildNotRepeatInjectJS(entry.getKey(), entry.getValue().getPreloadInterfaceJs()));
        }
    }

    /**
     * 构建一个“不会重复注入”的js脚本；
     *
     * @param key
     * @param js
     * @return
     */
    public String buildNotRepeatInjectJS(String key, String js) {
        String obj = String.format("__injectFlag_%1$s__", key);
        StringBuilder sb = new StringBuilder();
        sb.append("javascript:try{(function(){if(window.");
        sb.append(obj);
        sb.append("){console.log('");
        sb.append(obj);
        sb.append(" has been injected');return;}window.");
        sb.append(obj);
        sb.append("=true;");
        sb.append(js);
        sb.append("}())}catch(e){console.warn(e)}");
        return sb.toString();
    }
    private boolean isAccessibilityEnabled() {
        AccessibilityManager am = (AccessibilityManager) getContext().getSystemService(Context.ACCESSIBILITY_SERVICE);
        return am.isEnabled();
    }
    protected void fixedAccessibilityInjectorExceptionForOnPageFinished(String url) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN
                && getSettings().getJavaScriptEnabled()
                && mIsAccessibilityEnabledOriginal == null
                && isAccessibilityEnabled()) {
            try {
                try {
                    URLEncoder.encode(String.valueOf(new URI(url)), "utf-8");
//                    URLEncodedUtils.parse(new URI(url), null); // AccessibilityInjector.getAxsUrlParameterValue
                } catch (IllegalArgumentException e) {
                    if ("bad parameter".equals(e.getMessage())) {
                        mIsAccessibilityEnabledOriginal = true;
                        setAccessibilityEnabled(false);
                        Log.e(TAG, "fixedAccessibilityInjectorExceptionForOnPageFinished.url = " + url, e);
                    }
                }
            } catch (Throwable e) {
                if (BuildConfig.DEBUG) {
                    Log.e(TAG, "fixedAccessibilityInjectorExceptionForOnPageFinished", e);
                }
            }
        }
    }
    protected void injectExtraJavaScript() {
        for (Map.Entry<String, String> entry : mInjectJavaScripts.entrySet()) {
            this.loadUrl(buildNotRepeatInjectJS(entry.getKey(), entry.getValue()));
        }
    }


    private void init(){
        //每次打开webview清理缓存
        clearHistory();
        clearCache(true);
        getSettings().setJavaScriptEnabled(true);
        getSettings().setBuiltInZoomControls(true);
        getSettings().setSupportZoom(true);
        getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);        //不使用缓存：
        getSettings().setBlockNetworkImage(false);//解决图片不显示, 是否阻塞加载网络图片  协议http or https
        // 开启DOM缓存，开启LocalStorage存储（html5的本地存储方式）
        getSettings().setDomStorageEnabled(true);


        // 允许加载本地文件html  file协议
        getSettings().setAllowFileAccess(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            // 通过 file url 加载的 Javascript 读取其他的本地文件 .建议关闭
            getSettings().setAllowFileAccessFromFileURLs(false);
            // 允许通过 file url 加载的 Javascript 可以访问其他的源，包括其他的文件和 http，https 等其他的源
            getSettings().setAllowUniversalAccessFromFileURLs(false);
        }
        getSettings().setAppCacheEnabled(false);//设置H5的缓存是否打开，默认关闭
        getSettings().setPluginState(WebSettings.PluginState.ON);//Disable Support for Plugins
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
        } else {
            getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        }
        /**
         * 原因：webview 从Lollipop(5.0)开始 webview默认不允许混合模式，https当中不能加载http资源，如果要加载，需单独设置开启。
         * @auth liupan  文件服务器返回的文件路径是http开头的，为防止广告侵入，我们的页面路径是https开头的
         */
        if (Build.VERSION.SDK_INT >= 21) {
            getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        getSettings().setSavePassword(false);//关闭密码保存提醒功能

        getSettings().setSaveFormData(false);
        getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        getSettings().setUseWideViewPort(false);
        getSettings().setLoadWithOverviewMode(true);
        //为解决webView高德地图无法定位问题
        //启用数据库
        getSettings().setDatabaseEnabled(true);
        getSettings().setLoadsImagesAutomatically(true);
        getSettings().setSupportMultipleWindows(false);
        //启用地理定位
        getSettings().setGeolocationEnabled(true);
        getSettings().setDefaultTextEncodingName("utf-8");//设置编码格式
        getSettings().setDefaultFontSize(16);
        getSettings().setMinimumFontSize(12);//设置 WebView 支持的最小字体大小，默认为 8
        getSettings().setTextZoom(100);
        getSettings().setNeedInitialFocus(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //适配5.0不允许http和https混合使用情况
            getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        String dir = LoanWebConfig.getCachePath(getContext());
        //设置数据库路径  api19 已经废弃,这里只针对 webkit 起作用
        getSettings().setGeolocationDatabasePath(dir);
        getSettings().setDatabasePath(dir);
        getSettings().setAppCachePath(dir);
        //缓存文件最大值
        getSettings().setAppCacheMaxSize(Long.MAX_VALUE);


        removeSearchBoxJavaBridge();
    }

}
