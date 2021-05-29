package cn.net.yzl.base.config;

import android.app.Application;

import com.alibaba.android.arouter.launcher.ARouter;

import com.amap.api.maps.model.LatLng;


import cn.net.yzl.base.BuildConfig;
import cn.net.yzl.base.callback.BaseLifecycleCallback;
import cn.net.yzl.base.callback.LogCallback;
import cn.net.yzl.base.constant.Constant;
import cn.net.yzl.base.utils.SPUtils;
import cn.net.yzl.base.utils.ToastUtils;
import cn.ycbjie.ycthreadpoollib.PoolThread;


public class AppConfig {

    //对象
    public static final AppConfig INSTANCE= new AppConfig();

    private boolean isLogin;
    private boolean isShowListImg;
    private boolean isShowGirlImg;
    private boolean isProbabilityShowImg;
    private int thumbnailQuality;
    private String bannerUrl;
    private boolean isNight;
    private PoolThread executor;
    private static Application sApplication;

    private LatLng centerLatLng;
    public  int fenceRadius = 150;//围栏范围半径


    //下班时间有效范围设定
    public static  int WORK_END_HOUR_1 = 15;
    public static  int WORK_END_HOUR_2 = 22;
    public static  int WORK_END_MIN_1 = 0;
    public static  int WORK_END_MIN_2 = 30;

    public static  int WORK_LATER_HOUR = 9;
    public static  int WORK_LATER_MIN = 10;//迟到打卡时间点

    public static  int WORK_EARLY_HOUR=18;
    public static  int WORK_EARLY_MIN=30;//早退打卡时间点

    public static  int WORK_TIME_HOUR_DEVIDE = 12;
    public static  int WORK_TIME_MIN_DEVIDE =0;//上下班时间区分：12点钟之前，上午上班时间；12点钟之后下班上班时间
    //上班自动打卡时间有效范围设定
    public static  int WORK_START_HOUR_1 = 7;
    public static  int WORK_START_HOUR_2 = 9;
    public static  int WORK_START_MIN_1 = 0;
    public static  int WORK_START_MIN_2 = 0;

    public void initConfig(Application application){
        this.sApplication = application;
        initThreadPool();
        BaseLifecycleCallback.getInstance().init(application);
        initARouter();
        //1.是否是登录状态
        isLogin = SPUtils.getInstance(Constant.SP_NAME).getBoolean(Constant.KEY_IS_LOGIN, false);

    }

    /**
     * 获取 Application
     *
     * @return Application
     */
    public static Application getApp() {
        if (sApplication != null) return sApplication;
        throw new NullPointerException("u should init first");
    }
    /**
     * 初始化线程池管理器
     */
    private void initThreadPool() {
        // 创建一个独立的实例进行使用
        if (executor==null){
            executor = PoolThread.ThreadBuilder
                    .createFixed(6)
                    .setPriority(Thread.MAX_PRIORITY)
                    .setCallback(new LogCallback())
                    .build();
        }
    }

    /**
     * 获取线程池管理器对象，统一的管理器维护所有的线程池
     * @return                      executor对象
     */
    public PoolThread getExecutor(){
        initThreadPool();
        return executor;
    }

    public void closeExecutor(){
        if(executor!=null){
            executor.close();
            executor = null;
        }
    }


    private void initARouter(){
        if (BuildConfig.IS_DEBUG) {
            //打印日志
            ARouter.openLog();
            //开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
            ARouter.openDebug();
        }
        //推荐在Application中初始化
        ARouter.init(getApp());
    }


    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean login) {
        SPUtils.getInstance(Constant.SP_NAME).put(Constant.KEY_IS_LOGIN,login);
        this.isLogin = login;
    }

    public LatLng getCenterLatLng() {
        return centerLatLng;
    }

    public void setCenterLatLng(LatLng centerLatLng) {
        this.centerLatLng = centerLatLng;
    }

    public void clearCenterLoc(){
        this.centerLatLng = null;

    }

    public int getFenceRadius() {
        return fenceRadius;
    }

    public void setFenceRadius(int fenceRadius) {
        this.fenceRadius = fenceRadius;
    }


}
