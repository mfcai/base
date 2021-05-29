package cn.net.yzl.base.okgo;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.webkit.WebSettings;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.HttpParams;


import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

public class OkGoUtils {
    /**
     * 设置OKGo公用body参数
     * @param map
     *
     * 2019-07-17
     * 为防止闪退，加入非空处理
     */
    public synchronized  static void   setCommonParams(Map<String ,String> map){
        HttpParams commonhttpParams = OkGo.getInstance().getCommonParams();
        if (commonhttpParams  == null){
            commonhttpParams = new HttpParams();
            OkGo.getInstance().addCommonParams(commonhttpParams);
        }
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            OkGo.getInstance().getCommonParams().put(key,value);
        }
    }
    /**
     * 设置OKGo公用header参数
     * @param map
     */
    public synchronized static void   setCommonHeaders(Map<String ,String> map){
        HttpHeaders commonHttpHeaders = OkGo.getInstance().getCommonHeaders();
        if (commonHttpHeaders  == null){
            commonHttpHeaders = new HttpHeaders();
            OkGo.getInstance().addCommonHeaders(commonHttpHeaders);
        }
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            OkGo.getInstance().getCommonHeaders().put(key,value);
        }
    }

    public synchronized static  void removeAllCache(){
        OkGo.getInstance().cancelAll();
        removeAllCookies();
        removeAllHeaders();
        removeAllParams();
    }

    public synchronized static void removeAllHeaders() {
        if(OkGo.getInstance().getCommonHeaders() != null) {
            OkGo.getInstance().getCommonHeaders().clear();
        }
    }

    public synchronized static void  removeAllParams() {
        if(OkGo.getInstance().getCommonParams() != null) {
            OkGo.getInstance().getCommonParams().clear();
        }
    }

    public synchronized static void removeAllCookies(){
        OkGo.getInstance().getCookieJar().getCookieStore().removeAllCookie();
    }
    /**
     * 获取OKGo公用header参数
     * @param
     */
    public static HttpHeaders   getCommonHeaders(){
        HttpHeaders commonHttpHeaders = OkGo.getInstance().getCommonHeaders();
        return commonHttpHeaders;
    }

    /**
     * 获取OKGo公用body参数
     * @param
     */
    public static HttpParams   getCommonParams(){
        HttpParams httpParams = OkGo.getInstance().getCommonParams();
        return httpParams;
    }


    private static String cookieHeader(List<Cookie> cookies) {
        StringBuilder cookieHeader = new StringBuilder();
        for (int i = 0, size = cookies.size(); i < size; i++) {
            if (i > 0) {
                cookieHeader.append("; ");
            }
            Cookie cookie = cookies.get(i);
            if("JSESSIONID".equalsIgnoreCase(cookie.name())){
                return cookie.value();
            }
        }
        return "";
    }


    public static String getUserAgent(Context context) {
        String userAgent = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            try {
                userAgent = WebSettings.getDefaultUserAgent(context);
            } catch (Exception e) {
                userAgent = System.getProperty("http.agent");
            }
        } else {
            userAgent = System.getProperty("http.agent");
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0, length = userAgent.length(); i < length; i++) {
            char c = userAgent.charAt(i);
            if (c <= '\u001f' || c >= '\u007f') {
                sb.append(String.format("\\u%04x", (int) c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }


}
