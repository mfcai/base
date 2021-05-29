package cn.net.yzl.base.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;


import com.alexvasilkov.gestures.animation.ViewPosition;


import java.math.RoundingMode;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import cn.net.yzl.base.activity.BigPhotoActivity;

import cn.net.yzl.base.arouter.ARouterConstant;
import cn.net.yzl.base.arouter.ARouterUtils;
import cn.net.yzl.base.constant.Constant;


/**
 * Created by wei on 2016/6/2.
 */
public class BigPhotoUtils {

    private static final String TAG = "Utils";

    private static int screenW;
    private static int screenH;
    //设置超长图的最大尺寸
    private static int mPicSizeLong=6000;
    //设置是否支持gif，true支持
    private static boolean mIsGif=true;
    //设置是否可以弹窗下载
    private static boolean mCanDown=true;
    //设置最大放大倍数
    private static float mMaxZoom=6.0f;
    //设置普通图下是否可以旋转
    private static boolean mRotationEnabled=true;




    public static String numberFormat(Number price) {
        if (price == null) {
            return "0";
        }
        if (price.doubleValue() >= 10000) {
            DecimalFormat df = new DecimalFormat("0.00");

            double nomalPrice = price.doubleValue() / 10000;
            return df.format(nomalPrice) + "万";
        } else {
            return price + "";
        }
    }

    public static String numberFormat2(Number number) {
        if (number == null) {
            return "0";
        }
        if (number.doubleValue() >= 1000) {
            DecimalFormat df = new DecimalFormat("0.00");

            double nomalPrice = number.doubleValue() / 1000;
            return df.format(nomalPrice) + "k";
        } else {
            return number + "";
        }
    }

    //只检测 是否是11位的数字，具体格式是否正确，在服务端检测
    public static boolean checkMobileNum(String phoneNumber) {

        Pattern p = Pattern.compile("^1\\d{10}$");
        Matcher m = p.matcher(phoneNumber);
        return m.matches();
    }

    /**
     * 通过正则判断是否是手机号
     *
     * @param mobiles
     * @return
     */
    @Deprecated
    public static boolean isPhoneNumber(String mobiles) {


        Pattern p = Pattern.compile("^((13[0-9])|(15[^4])|(166)|(17[0-8])|(18[0-9])|(19[8-9])|(147,145))\\\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }



    public static String md5(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(string.getBytes());
            String result = "";
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    // 替换、过滤特殊字符
    public static String stringFilter(String str) throws PatternSyntaxException {
        if (str == null) {
            return "";
        }
        str = str.replaceAll("【", "[").replaceAll("】", "]").replaceAll("！", "!");//替换中文标号
        String regEx = "[『』]"; // 清除掉特殊字符
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }


    public static int dpToPx(int dp, Context context) {
        return (int) (context.getResources().getDisplayMetrics().density * dp + 0.5f);

    }

    public static int pxToDp(int px, Context context) {

        return (int) (px / context.getResources().getDisplayMetrics().density + 0.5f);
    }

    /**
     * 保留两位小数，小数后面的只舍不入
     *
     * @param number
     * @return
     */
    public static String moneyFormat(Object number) {
        if (number == null) {
            return "0.00";
        }
        DecimalFormat format = new DecimalFormat("###,###0.00");
        format.setRoundingMode(RoundingMode.FLOOR);
        String amount = format.format(number);
        //如果有小数点，并且结尾是 0的情况下，清除掉一个0
//        if (amount.contains(".") && amount.endsWith("0")) {
//            amount = amount.substring(0, amount.length() - 1);
//        }
        return amount;
    }

    public static double moneyFormatStringToDouble(String moneyString) {

        DecimalFormat format = new DecimalFormat("###,###0.00");
        format.setRoundingMode(RoundingMode.FLOOR);
        try {
            Number parse = format.parse(moneyString);
            if (parse != null) {
                return parse.doubleValue();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * 获取状态栏高度
     *
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 判断字符串是否为URL
     *
     * @param urls 用户头像key
     * @return true:是URL、false:不是URL
     */
    public static boolean isHttpUrl(String urls) {
        String regex = "(((https|http)?://)?([a-z0-9]+[.])|(www.))" + "\\w+[.|\\/]([a-z0-9]{0,})?[[.]([a-z0-9]{0,})]+((/[\\S&&[^,;\u4E00-\u9FA5]]+)+)?([.][a-z0-9]{0,}+|/?)";
        //设置正则表达式
        Pattern pat = Pattern.compile(regex.trim());
        Matcher mat = pat.matcher(urls.trim());
        return mat.matches();
    }

    /**
     * 判断字符串中是否有超链接，若有，则返回超链接。
     * @param str
     * @return
     */
    public static String[] judgeString(String str){
        Matcher m = Pattern.compile("(((https|http)?://)?([a-z0-9]+[.])|(www.))"
                + "\\w+[.|\\/]([a-z0-9]{0,})?[[.]([a-z0-9]{0,})]+((/[\\S&&[^,;\u4E00-\u9FA5]]+)+)?([.][a-z0-9]{0,}+|/?)").matcher(str);
        String[] url = new String[str.length()/5];
        int count = 0;
        while(m.find()){
            count++;
            url[count] = m.group();
        }
        return url;
    }










    public static int getmPicSizeLong() {
        return mPicSizeLong;
    }

    public static void setmPicSizeLong(int picSizeLong) {
        mPicSizeLong = picSizeLong;
    }

    public static int getScreenW(Context context){
        if (screenW == 0){
            initScreen(context);
        }
        return screenW;
    }

    public static int getScreenH(Context context){
        if (screenH == 0){
            initScreen(context);
        }
        return screenH;
    }

    public static boolean ismCanDown() {
        return mCanDown;
    }

    public static void setmCanDown(boolean canDown) {
        mCanDown = canDown;
    }

    public static boolean ismIsGif() {
        return mIsGif;
    }

    public static void setmIsGif(boolean isGif) {
        mIsGif = isGif;
    }

    public static float getmMaxZoom() {
        return mMaxZoom;
    }

    public static void setmMaxZoom(float maxZoom) {
        mMaxZoom = maxZoom;
    }

    public static boolean ismRotationEnabled() {
        return mRotationEnabled;
    }

    public static void setmRotationEnabled(boolean rotationEnabled) {
        mRotationEnabled = rotationEnabled;
    }

    private static void initScreen(Context context){
        DisplayMetrics metric = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metric);
        screenW = metric.widthPixels;
        screenH = metric.heightPixels;
    }


    private BigPhotoUtils()
    {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 获得屏幕高度
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context)
    {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     * 获得屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context)
    {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }



    public static void startImagePage(Activity context, ArrayList<String> urls, List<ImageView> imageViews, int pos) {
        ArrayList<String> viewPositions = new ArrayList<>();
        for (ImageView imageView : imageViews) {
            if (imageView == null)
                break;
            ViewPosition viewPosition = ViewPosition.from(imageView);
            viewPositions.add(viewPosition.pack());
        }
        Intent intent = new Intent(context, BigPhotoActivity.class);
        Bundle bundle = new Bundle();
        // 图片url,从数据库中或网络中获取
        bundle.putStringArrayList("image_urls",  urls);
        bundle.putString("image_index", pos+"");
        bundle.putStringArrayList("positions",viewPositions);
        intent.putExtras(bundle);
        context.startActivity(intent);
        context.overridePendingTransition(0, 0);
    }


    public static void startImagePage(Activity context, String urls, View imageViews) {
        ArrayList<String> viewPositions = new ArrayList<>();
        if (imageViews == null)
            return;
        ViewPosition viewPosition = ViewPosition.from(imageViews);
        viewPositions.add(viewPosition.pack());
        Bundle bundle1 = new Bundle();
        if (!TextUtils.isEmpty(urls)){
            ArrayList<String> urlList=new ArrayList<>();
            urlList.clear();
            urlList.add(urls);
            bundle1.putStringArrayList(Constant.BaseModule.BIGIMG_URL,urlList);
        }else{
            return;
        }

        bundle1.putStringArrayList(Constant.BaseModule.BIGIMG_POS,viewPositions);
        ARouterUtils.navigation(ARouterConstant.ACTIVITY_BIGPHOTO_ACTIVITY,bundle1);
    }


    private static BackListeners mBackListeners;

    public static BackListeners getBackListeners() {
        return mBackListeners;
    }

    public static void setBackListeners(BackListeners backListeners) {
        mBackListeners = backListeners;
    }

    public interface BackListeners{
        void backMethod(int arg0);
    }


}
