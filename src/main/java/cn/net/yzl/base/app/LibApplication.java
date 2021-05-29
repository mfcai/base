package cn.net.yzl.base.app;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.util.Log;

import androidx.multidex.MultiDex;

import com.bumptech.glide.Glide;

import cn.net.yzl.base.arouter.ARouterUtils;
import cn.net.yzl.base.bean.LoginInfo;
import cn.net.yzl.base.config.AppConfig;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;


import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.cookie.CookieJarImpl;
import com.lzy.okgo.cookie.store.MemoryCookieStore;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.HttpParams;


import cn.net.yzl.base.constant.Constant;
import cn.net.yzl.base.okgo.TokenInterceptor;
import okhttp3.OkHttpClient;
/**
 * <pre>
 *     @author 杨充
 *     blog  : https://github.com/yangchong211
 *     time  : 2017/05/23
 *     desc  : 可以做一些公共处理逻辑
 *     revise:
 * </pre>
 */
public class LibApplication extends Application {

    private static LibApplication instance;
    private LoginInfo loginInfo;//登陆信息
    public static LibApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        AppConfig.INSTANCE.initConfig(this);
        getAppEnv();
        initOkGo();
        //在子线程中初始化
        InitializeService.start(this);
        // 设置权限申请拦截器
//        XXPermissions.setPermissionInterceptor(new PermissionInterceptor());
//
//        if(Constant.SERVER_TYPE == Constant.ServerType.SERVER_PRODUCTION){
//            Bugly.init(getApplicationContext(), "bfedff8cc3", false);
//        }else{
//            Bugly.init(getApplicationContext(), "e2c35b71c4", false);
//        }

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    /**
     * 程序终止的时候执行
     */
    @Override
    public void onTerminate() {
        Log.d("Application", "onTerminate");
        super.onTerminate();
        AppConfig.INSTANCE.closeExecutor();
        ARouterUtils.destroy();
    }

    private void getAppEnv() {
        String app_env = "uat";
        try {
            ApplicationInfo activityInfo = this.getPackageManager().getApplicationInfo(this.getPackageName(), PackageManager.GET_META_DATA);
            app_env = activityInfo.metaData.getString("APP_ENV");
        } catch (Exception e) {
            Log.e("ContextApp", e.toString());
        }
        if ("pro".equals(app_env)) {
            Constant.SERVER_TYPE = Constant.ServerType.SERVER_PRODUCTION;
        } else {
            Constant.SERVER_TYPE= Constant.ServerType.SERVER_DEVELOP;
        }

    }

    private  void initOkGo(){
        /**
         * 公用header
         */
        HttpHeaders headers = new HttpHeaders();
        HttpParams params = new HttpParams();
//        String ts = getTimestamps();
//        headers.put("ts", ts);
        headers.put("X-Requested-With","JSONHttpRequest");
        headers.put("Accept","application/json");
        headers.put("Content-Type","application/json; charset=UTF-8");

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        //log相关
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("OkGo");
        loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);        //log打印级别，决定了log显示的详细程度
        loggingInterceptor.setColorLevel(Level.INFO);                               //log颜色级别，决定了log在控制台显示的颜色
        builder.addInterceptor(loggingInterceptor);                                 //添加OkGo默认debug日志
        //第三方的开源库，使用通知显示当前请求的log，不过在做文件下载的时候，这个库好像有问题，对文件判断不准确
        builder.addInterceptor(new TokenInterceptor());

        //超时时间设置，默认60秒
        builder.readTimeout(30000, TimeUnit.MILLISECONDS);      //全局的读取超时时间
        builder.writeTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);     //全局的写入超时时间
        builder.connectTimeout(20000, TimeUnit.MILLISECONDS);   //全局的连接超时时间

        //自动管理cookie（或者叫session的保持），以下几种任选其一就行
        //builder.cookieJar(new CookieJarImpl(new SPCookieStore(this)));            //使用sp保持cookie，如果cookie不过期，则一直有效
//        builder.cookieJar(new CookieJarImpl(new DBCookieStore(this)));              //使用数据库保持cookie，如果cookie不过期，则一直有效
        builder.cookieJar(new CookieJarImpl(new MemoryCookieStore()));            //使用内存保持cookie，app退出后，cookie消失

        //以下设置的所有参数是全局参数,同样的参数可以在请求的时候再设置一遍,那么对于该请求来讲,请求中的参数会覆盖全局参数
        //好处是全局参数统一,特定请求可以特别定制参数
        try {
            //以下都不是必须的，根据需要自行选择,一般来说只需要 debug,缓存相关,cookie相关的 就可以了
            OkGo.getInstance().init(this)                           //必须调用初始化
                    .setOkHttpClient(builder.build())               //建议设置OkHttpClient，不设置会使用默认的
                    .setCacheMode(CacheMode.NO_CACHE)               //全局统一缓存模式，默认不使用缓存，可以不传
                    .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)   //全局统一缓存时间，默认永不过期，可以不传
                    .setRetryCount(0)                         //全局统一超时重连次数，默认为三次，那么最差的情况会请求4次(一次原始请求，三次重连请求)，不需要可以设置为0
//                .addCommonHeaders(headers);                      //全局公共头
//                .addCommonParams(params);                       //全局公共参数
                    .addCommonHeaders(headers)                                        //设置全局公共头
                    .addCommonParams(params);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * 低内存的时候执行
     */
    @Override
    public void onLowMemory() {
        Log.d("Application", "onLowMemory");
        super.onLowMemory();
        Glide.get(this).clearMemory();
    }


    /**
     * HOME键退出应用程序
     * 程序在内存清理的时候执行
     */
    @Override
    public void onTrimMemory(int level) {
        Log.d("Application", "onTrimMemory");
        super.onTrimMemory(level);
        if (level == TRIM_MEMORY_UI_HIDDEN){
            Glide.get(this).clearMemory();
        }
        Glide.get(this).trimMemory(level);
    }


    /**
     * onConfigurationChanged
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.d("Application", "onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
    }


    /*
     *   *desc:获取老版本的登陆信息
     */
    public LoginInfo getLoginInfo() {
        return loginInfo;
    }

    /*
     *   *desc:设置老版本的登陆信息
     */
    public void setLoginInfo(LoginInfo loginInfo) {
        this.loginInfo = loginInfo;
    }


}
