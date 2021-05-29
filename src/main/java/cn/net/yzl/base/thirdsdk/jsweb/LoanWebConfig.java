package cn.net.yzl.base.thirdsdk.jsweb;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.ValueCallback;
import android.webkit.WebView;


import androidx.annotation.RequiresApi;

import java.io.File;

import cn.net.yzl.base.BuildConfig;

/**
 * Created by dell on 2019-3-20.
 */

public class LoanWebConfig {
    private final static String TAG = LoanWebConfig.class.getSimpleName();
    public static final String FILE_CACHE_PATH = "ectweb-cache";
    public static final String ECTWEB_VERSION = " ectweb/4.0.2 ";
    /**
     * 缓存路径
     */
    static String AGENTWEB_FILE_PATH;
    /**
     * DEBUG 模式 ， 如果需要查看日志请设置为 true
     */
    public static boolean DEBUG = false;
    /**
     * 当前操作系统是否低于 KITKAT
     */
    static final boolean IS_KITKAT_OR_BELOW_KITKAT = Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT;
    /**
     * 通过JS获取的文件大小， 这里限制最大为5MB ，太大会抛出 OutOfMemoryError
     */
    public static int MAX_FILE_LENGTH = 1024 * 1024 * 5;
    //获取Cookie
    public static String getCookiesByUrl(String url) {
        return CookieManager.getInstance() == null ? null : CookieManager.getInstance().getCookie(url);
    }

    public static void debug() {
        DEBUG = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
    }
    /**
     * 删除所有已经过期的 Cookies
     */
    public static void removeExpiredCookies() {
        CookieManager mCookieManager = null;
        if ((mCookieManager = CookieManager.getInstance()) != null) { //同步清除
            mCookieManager.removeExpiredCookie();
            toSyncCookies();
        }
    }
    /**
     * 删除所有 Cookies
     */

    public static void removeAllCookies() {
        removeAllCookies(null);
    }
    //Android  4.4  NoSuchMethodError: android.webkit.CookieManager.removeAllCookies

    public static void removeAllCookies( ValueCallback<Boolean> callback) {
        if (callback == null) {
            callback = getDefaultIgnoreCallback();
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().removeAllCookie();
            toSyncCookies();
            callback.onReceiveValue(!CookieManager.getInstance().hasCookies());
            return;
        }
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP ) {
            CookieManager.getInstance().removeAllCookies(callback);
        }
        toSyncCookies();
    }
    private static void toSyncCookies() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.getInstance().sync();
            return;
        }
        AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run() {
                CookieManager.getInstance().flush();
            }
        });
    }

    private static ValueCallback<Boolean> getDefaultIgnoreCallback() {
        return new ValueCallback<Boolean>() {
            @Override
            public void onReceiveValue(Boolean ignore) {
                if(BuildConfig.DEBUG) {
                    Log.i(TAG, "removeExpiredCookies:" + ignore);
                }
            }
        };
    }

    /**
     * @param context
     * @return WebView 的缓存路径
     */
    public static String getCachePath(Context context) {
        return context.getCacheDir().getAbsolutePath()+File.separator + FILE_CACHE_PATH;
    }
}
