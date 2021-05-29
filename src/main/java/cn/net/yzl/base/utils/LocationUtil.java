package cn.net.yzl.base.utils;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
import android.util.Log;


import androidx.lifecycle.LifecycleObserver;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;

import cn.net.yzl.base.app.LibApplication;
import cn.net.yzl.base.constant.Constant;
import cn.net.yzl.base.utils.DateUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by dell on 2019-2-21.
 */

public class LocationUtil implements LifecycleObserver {
    private final static boolean DEBUG = true;
    private final static String TAG = "LocationUtil";
    private static LocationUtil mInstance;
    private MLocation  mBaseLocation;
    private Object objLock = new Object();
    public final static String TIMER_5 ="5";//每5分钟定位1次
    public final static String TIMEER_45="45";//45分钟定位1次
    public final static String TIMEER_LONG="LONG";//定位1次即可

    public static int locType=1;
    public final static int LOC_BAIDU=0;
    public final static int LOC_GAODE = 1;

    MyLocationListener myLocationListener = new MyLocationListener();
    private AMapLocationClient mLocationClient = null;
    private String timer_type=TIMEER_LONG;
    private List<LocationListener> listenrList = new ArrayList<>();

    public static LocationUtil getInstance(Application application) {
        if (null == mInstance) {
            synchronized (LocationUtil.class) {
                if (null == mInstance) {
                    mInstance = new LocationUtil(application);
                }
            }
        }
        return mInstance;
    }

    private LocationUtil(Application application) {
        initLoc(application);
    }

    public void addListener(LocationListener listener){
        if( !listenrList.contains(listener)){
            listenrList.add(listener);
        }
    }

    public void removeListener(LocationListener listener){
        if(listenrList.size() >0 && listenrList.contains(listener)){
            listenrList.remove(listener);
        }
    }

    public void initLoc(Context context){
        mLocationClient = new AMapLocationClient(context);
        mLocationClient.setLocationListener(myLocationListener);
        mLocationClient.setLocationOption(getDefaultOption());
    }
    public void startMonitor() {
        synchronized (objLock){
            if (mLocationClient != null) {
                mLocationClient.stopLocation();
                mLocationClient.startLocation();
            } else {
                Log.d("LocSDK3", "locClient is null or started");
                initLoc(LibApplication.getInstance());
                mLocationClient.startLocation();
            }
        }


    }

    public void stopMonitor() {
        synchronized (objLock) {
            Log.d(TAG, "stop monitor location");
            if (mLocationClient != null) {
                mLocationClient.stopLocation();
            }
        }
    }

    public void getLastLocation(){
        AMapLocation location = mLocationClient.getLastKnownLocation();
        mBaseLocation= new MLocation();
        int locationType =location.getLocationType();
        mBaseLocation.latitude = location.getLatitude()+"";
        mBaseLocation.longitude = location.getLongitude()+"";
        mBaseLocation.city = location.getCity()+"";
        mBaseLocation.address = location.getAddress();
        if(listenrList.size() >0){
            for(int i=0;i<listenrList.size();i++){
                listenrList.get(i).onSuccess(mBaseLocation);
            }
        }
    }

    /**
     * 判断定位是否可用
     *
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isLocationEnabled(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER) || lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * 打开Gps设置界面
     */
    public static void openGpsSettings(Context context) {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public void onDestroy(){
        if (mLocationClient != null){
            mLocationClient.onDestroy();
            mLocationClient = null;
        }
    }


    /**
     * 默认的定位参数
     * @since 2.8.0
     * @author hongming.wang
     *
     */
    private AMapLocationClientOption getDefaultOption(){
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.SignIn);
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(4500);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        return mOption;
    }



    //高德定位
    public class MyLocationListener implements AMapLocationListener {
        private int count;
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {
                    //可在其中解析amapLocation获取相应内容。
                    if(Math.abs(aMapLocation.getLatitude())<0.01 && Math.abs(aMapLocation.getLongitude())<0.1){
                        return;
                    }
                    mBaseLocation= new MLocation();
                    int locationType =aMapLocation.getLocationType();
                    mBaseLocation.latitude = aMapLocation.getLatitude()+"";
                    mBaseLocation.longitude = aMapLocation.getLongitude()+"";
                    mBaseLocation.city = aMapLocation.getCity()+"";
                    mBaseLocation.address = aMapLocation.getAddress();

                    if(listenrList.size() >0){
                        for(int i=0;i<listenrList.size();i++){
                            listenrList.get(i).onSuccess(mBaseLocation);
                        }
                    }
                    StringBuffer sb = new StringBuffer();
                    sb.append("定位成功" + "\n");
                    sb.append("定位类型: " + aMapLocation.getLocationType() + "\n");
                    sb.append("经    度    : " + aMapLocation.getLongitude() + "\n");
                    sb.append("纬    度    : " + aMapLocation.getLatitude() + "\n");
                    sb.append("精    度    : " + aMapLocation.getAccuracy() + "米" + "\n");
                    sb.append("提供者    : " + aMapLocation.getProvider() + "\n");

                    sb.append("速    度    : " + aMapLocation.getSpeed() + "米/秒" + "\n");
                    sb.append("角    度    : " + aMapLocation.getBearing() + "\n");
                    // 获取当前提供定位服务的卫星个数
                    sb.append("星    数    : " + aMapLocation.getSatellites() + "\n");
                    sb.append("国    家    : " + aMapLocation.getCountry() + "\n");
                    sb.append("省            : " + aMapLocation.getProvince() + "\n");
                    sb.append("市            : " + aMapLocation.getCity() + "\n");
                    sb.append("城市编码 : " + aMapLocation.getCityCode() + "\n");
                    sb.append("区            : " + aMapLocation.getDistrict() + "\n");
                    sb.append("区域 码   : " + aMapLocation.getAdCode() + "\n");
                    sb.append("地    址    : " + aMapLocation.getAddress() + "\n");
                    sb.append("兴趣点    : " + aMapLocation.getPoiName() + "\n");
                    //定位完成的时间
                    sb.append("定位时间: " + DateUtil.formatUTC(aMapLocation.getTime(), "yyyy-MM-dd HH:mm:ss") + "\n");

                } else {
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    Log.e(TAG, "location Error, ErrCode:" + aMapLocation.getErrorCode() + ", errInfo:" + aMapLocation.getErrorInfo());
                    if(listenrList.size() >0){
                        for(int i=0;i<listenrList.size();i++){
                            listenrList.get(i).onFail(aMapLocation.getErrorCode() ,aMapLocation.getErrorInfo());
                        }
                    }
                    //XLog.e(MSG, "AmapError", "location Error, ErrCode:" + aMapLocation.getErrorCode() + ", errInfo:" + aMapLocation.getErrorInfo());
                }
            }
            if(timer_type.equals(TIMER_5)){
                //获取到定位信息后，即停止定位
                stopMonitor();
            }
        }

    }







    public class MLocation {
        public String latitude;
        public String longitude;
        public String city;
        public String address;
    }


    public interface LocationListener{
        public void onSuccess(MLocation mLocation);
        public void onFail(int code,String err);
    }
}
