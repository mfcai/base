package cn.net.yzl.base.thirdsdk.jsweb;

import android.os.Build;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;


/**
 * Created by dell on 2019-3-20.
 */

public class LoanWebSetting {
    private WebSettings mWebSettings;
    public static final String USERAGENT_UC = " UCBrowser/11.6.4.950 ";
    public static final String USERAGENT_QQ_BROWSER = " MQQBrowser/8.0 ";
    public static final String USERAGENT_AGENTWEB = LoanWebConfig.ECTWEB_VERSION;
    private static final String TAG = LoanWebSetting.class.getSimpleName();
    public static LoanWebSetting getInstance() {
        return new LoanWebSetting();
    }

    public LoanWebSetting() {
    }
    public void settings(WebView webView) {

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setBlockNetworkImage(false);//解决图片不显示, 是否阻塞加载网络图片  协议http or https
        // 开启DOM缓存，开启LocalStorage存储（html5的本地存储方式）
        webView.getSettings().setDomStorageEnabled(true);


        // 允许加载本地文件html  file协议
        webView.getSettings().setAllowFileAccess(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            // 通过 file url 加载的 Javascript 读取其他的本地文件 .建议关闭
            webView.getSettings().setAllowFileAccessFromFileURLs(false);
            // 允许通过 file url 加载的 Javascript 可以访问其他的源，包括其他的文件和 http，https 等其他的源
            webView.getSettings().setAllowUniversalAccessFromFileURLs(false);
        }
        webView.getSettings().setAppCacheEnabled(true);
        String user_agent = webView.getSettings().getUserAgentString()+";yx-app";
        webView.getSettings().setUserAgentString(user_agent);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);//Disable Support for Plugins
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
        } else {
            webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        }
        /**
         * 原因：webview 从Lollipop(5.0)开始 webview默认不允许混合模式，https当中不能加载http资源，如果要加载，需单独设置开启。
         * @auth liupan  文件服务器返回的文件路径是http开头的，为防止广告侵入，我们的页面路径是https开头的
         */
        if (Build.VERSION.SDK_INT >= 21) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        webView.getSettings().setSavePassword(false);//关闭密码保存提醒功能

        webView.getSettings().setSaveFormData(false);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setUseWideViewPort(false);
        webView.getSettings().setLoadWithOverviewMode(true);
        //不使用缓存：
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        //为解决webView高德地图无法定位问题
        //启用数据库
        webView.getSettings().setDatabaseEnabled(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setSupportMultipleWindows(false);
        //启用地理定位
        webView.getSettings().setGeolocationEnabled(true);
        webView.getSettings().setDefaultTextEncodingName("utf-8");//设置编码格式
        webView.getSettings().setDefaultFontSize(16);
        webView.getSettings().setMinimumFontSize(12);//设置 WebView 支持的最小字体大小，默认为 8
        webView.getSettings().setTextZoom(100);
        webView.getSettings().setNeedInitialFocus(true);
        if (LoanWebUtils.checkNetwork(webView.getContext())) {
            //根据cache-control获取数据。
            webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        } else {
            //没网，则从本地获取，即离线加载
            webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //适配5.0不允许http和https混合使用情况
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        String dir = LoanWebConfig.getCachePath(webView.getContext());
        Log.i(TAG, "dir:" + dir + "   appcache:" + LoanWebConfig.getCachePath(webView.getContext()));
        //设置数据库路径  api19 已经废弃,这里只针对 webkit 起作用
        webView.getSettings().setGeolocationDatabasePath(dir);
        webView.getSettings().setDatabasePath(dir);
        webView.getSettings().setAppCachePath(dir);
        //缓存文件最大值
        webView.getSettings().setAppCacheMaxSize(Long.MAX_VALUE);
        webView.getSettings().setUserAgentString(getWebSettings()
                .getUserAgentString()
                .concat(USERAGENT_AGENTWEB)
                .concat(USERAGENT_UC)
        );
        Log.d(TAG, "UserAgentString : " + webView.getSettings().getUserAgentString());
        mWebSettings = webView.getSettings();
    }

    public WebSettings getWebSettings() {
        return mWebSettings;
    }

}
