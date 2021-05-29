package cn.net.yzl.base.thirdsdk.jsweb;


import android.graphics.Bitmap;
import android.os.Build;
import android.text.TextUtils;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;

import androidx.annotation.NonNull;

import java.util.Map;
import java.util.Stack;

/**
 * Class description
 *
 * @author YEZHENNAN220
 * @date 2016-07-08 13:54
 * 问题：
 * onPageFinished()被调用两次，onPageStarted()调用1次
 * 所以定义1个标志变量。避免onPageFinished重复调用
 *
 * webView.canGoBack()一直返回为true,
 * 检查是否有可以返回的历史记录（H5之间的跳转），如果在webview中存在H5页面跳转，则先返回上一个H5页面；否则关闭窗口；
 * 代码如下：
 * if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
 * if (webView.canGoBack()) {
 *    webView.goBack();
 *    return true;
 * ｝
 * return false;
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
 */
public abstract class CustomWebViewClient extends LoanWebViewClient {
    private boolean isLoad;

    public CustomWebViewClient(LoanWebView webView) {
        super(webView);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {

        if (onPageHeaders(url) != null) {
            view.loadUrl(url, onPageHeaders(url));
        }
        return super.shouldOverrideUrlLoading(view, url);
    }

    @Override
    public synchronized void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        if (this.isLoad) {
            this.isLoad = false;
            if (!view.getSettings().getLoadsImagesAutomatically()) {
                view.getSettings().setLoadsImagesAutomatically(true);
            }
            onPageSuccess();
        }


    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon){
        super.onPageStarted(view,url,favicon);
//        if (isLoad && loanWebView.getUrlStack().size() > 0) {
//            mUrlBeforeRedirect = loanWebView.getUrlStack().pop();
//        }
//        recordUrl(url);
        isLoad = true;
    }

    /**
     * 记录非重定向链接, 避免刷新页面造成的重复入栈
     */
//    private void recordUrl(String url) {
//        //这里还可以根据自身业务来屏蔽一些链接被放入URL栈
//        if (!TextUtils.isEmpty(url) && !url.equalsIgnoreCase(loanWebView.getLastPageUrl())) {
//            loanWebView.recordUrl(url);
//        } else if (!TextUtils.isEmpty(mUrlBeforeRedirect)) {
//            loanWebView.recordUrl(mUrlBeforeRedirect);
//            mUrlBeforeRedirect = null;
//        }
//    }


    @Override
    public void onReceivedError(WebView view, int errorCode,
                                String description, String failingUrl) {
        onPageError();
    }
    public abstract void onPageSuccess();
    /**
     * return errorUrl
     * @return
     */
    public abstract void onPageError();

    /**
     * HttpHeaders
     * return
     * @return
     */
    @NonNull
    public abstract Map<String, String> onPageHeaders(String url);

}
