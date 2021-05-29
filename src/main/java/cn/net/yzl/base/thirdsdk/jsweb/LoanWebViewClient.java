package cn.net.yzl.base.thirdsdk.jsweb;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.util.Log;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import cn.net.yzl.base.BuildConfig;


/**
 * Created by dell on 2018-5-24.
 */

public  class LoanWebViewClient extends WebViewClient {
    private static final String TAG =LoanWebViewClient.class.getSimpleName();
    private LoanWebView webView;

    public LoanWebViewClient(LoanWebView webView) {
        this.webView = webView;
    }
    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        if (webView.mJsCallJavas != null) {
            webView.injectJavaScript();
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "injectJavaScript, onPageStarted.url = " + view.getUrl());
            }
        }
        if (webView.mInjectJavaScripts != null) {
            webView.injectExtraJavaScript();
        }
        webView.fixedAccessibilityInjectorExceptionForOnPageFinished(url);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onPageFinished.url = " + view.getUrl());
        }
//        getCoockie();
    }
    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error){
        handler.proceed();  // 接受所有网站的证书
        super.onReceivedSslError(view, handler, error);
    }
    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
    }


    @Override
    public void onScaleChanged(WebView view, float oldScale, float newScale) {
        super.onScaleChanged(view, oldScale, newScale);
        webView.requestFocus();
        webView.requestFocusFromTouch();
    }


}
