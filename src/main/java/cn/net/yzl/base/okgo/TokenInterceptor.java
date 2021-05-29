package cn.net.yzl.base.okgo;

import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;


import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import cn.net.yzl.base.app.LibApplication;
import cn.net.yzl.base.bean.LoginInfo;
import cn.net.yzl.base.common.MessageEvent;
import cn.net.yzl.base.config.HttpUrlBody;
import cn.net.yzl.base.config.NetConfig;
import cn.net.yzl.base.constant.Constant;
import okhttp3.Cookie;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

/**
 * Created by dell on 2019-1-19.
 * desc:新系统token失效拦截和老系统cookie失效拦截
 * 腾讯token拦截4.2.0暂时不加
 */

public class TokenInterceptor  implements Interceptor {
    private final static String TAG = TokenInterceptor.class.getSimpleName();
    private static final Charset UTF_8 = Charset.forName("UTF-8");
    volatile Boolean tokenValid =false;
    private static  final Object lockObj = new Object();
    private static final Object lockOldObj = new Object();
    final  static ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    //deleted by caimingfu 2019-07-04,没有用到
//    private final Map<String, Long> reqMap = Collections.synchronizedMap(new HashMap<String, Long>());
//    private final Map<String,Long> oldReqMap =Collections.synchronizedMap(new HashMap<String, Long>());

    private long thresholdTime = 2*1000;//token 有效间隔暂定10分钟
    private long flushOldTokenTime;//老版本token失效，记录开始刷新时间
    private Boolean isGetToken = false;
    private Boolean isGetOldToken = false;

    @Override
    public  Response  intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        HttpUrl originalHttpUrl = originalRequest.url();
        String originalUrl = originalHttpUrl.url().toString();
        String method = originalRequest.method();

        final Response originalResponse = chain.proceed(originalRequest);

        int responseCode = originalResponse.code();

        //排除登录的API
        if (!originalUrl.equals(NetConfig.getLoginHost()+ HttpUrlBody.USER_LOGIN)){
            if(responseCode == Constant.NetStatusCode.EXPIRED_LOGIN ){
                //登录失效
                return originalResponse;
            }else if(responseCode ==401 ){
                synchronized (lockObj) {
                    if (!tokenValid) {
                        tokenValid = true;
                        MessageEvent event_msg = new MessageEvent();
                        event_msg.setTag(MessageEvent.EventApp.TAG_NOTICE_ACCOUNT_REJECTION);
                        EventBus.getDefault().post(event_msg);
                    }
                }
                return originalResponse;
                //如果token失效
//                NewTokenReqHandler.getInstance().enqueue(originalUrl);
//                LoginInfo loginInfo = LibApplication.getInstance().getLoginInfo();
//
//                /*
//                 *1.先判断用户是否已退出登陆
//                 * 2.根据失效时间是否超过token重新获取时间，判断token是否重新获取
//                 * 3.token没有重新获取，则去获取token
//                 * 4.token重新获取后，则重新建立原来的请求
//                 */
//                synchronized (lockObj){
//                    try {
//                        if(loginInfo.getLoginState() != Constant.LoginState.TOKEN_GETTING){
//                            if(loginInfo.getLoginState() != Constant.LoginState.LOGINED) {
//                                isGetToken = NewTokenReqHandler.getInstance().getNewToken();
//                            }else if(loginInfo.getLoginState() == Constant.LoginState.LOGINED){
//                                NewTokenReqHandler.getInstance().clearReq(originalUrl);
//                            }
//                        }else{
//                            while (NewTokenReqHandler.getInstance().isRunning()) {
//                                lockObj.wait();
//                            }
//                        }
//                        if(isGetToken){
//                            Request newRequest =rebuildPostRequest(originalRequest,originalResponse,loginInfo.getToken());
//                            return chain.proceed(newRequest);
//                        }
//                    }catch (Exception ex){
//                        return originalResponse;
//                    }finally {
//                        boolean flg =NewTokenReqHandler.getInstance().checkNewReady();
//                        if(flg){
//                            lockObj.notifyAll();
//                        }
//                    }
//                }





            }
        }else{
            tokenValid = false;
        }
        return originalResponse;

    }


    private static Charset getCharset(MediaType contentType)
    {
        Charset charset = contentType != null ? contentType.charset(UTF_8) : UTF_8;
        if (charset == null) charset = UTF_8;
        return charset;
    }
    private void bodyToString(Request request)
    {
        try
        {
            Request copy = request.newBuilder().build();
            RequestBody body = copy.body();
            if (body == null) return;
            Buffer buffer = new Buffer();
            body.writeTo(buffer);
            Charset charset = getCharset(body.contentType());
            Log.i( "TokenInterceptor","TokenInterceptor拦截后的body参数：" + buffer.readString(charset));
        }
        catch (Exception e)
        {
            Log.i( "TokenInterceptor",e.toString());
        }
    }

    /**
     * 获取常规post请求参数
     */
    private String getParamContent(RequestBody body) throws IOException {
        Buffer buffer = new Buffer();
        body.writeTo(buffer);
        return buffer.readUtf8();
    }













    private  String bodyToString2(final RequestBody request) {
        try {
            final RequestBody copy = request;
            final Buffer buffer = new Buffer();
            if (copy != null)
                copy.writeTo(buffer);
            else
                return "";
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }

    /**
     * 对post请求添加统一参数
     */
    private Request rebuildPostRequest(Request request,Response response,String token) {
        String method = request.method();
        String url = request.url().toString();
        if (method.equalsIgnoreCase("GET"))
        {
            //重新构建Request
            Request.Builder requestBuilder = request.newBuilder();
            //关闭原先的请求响应
            response.body().close();
            return requestBuilder.url(url).build();
        }
        else if (method.equalsIgnoreCase("POST"))
        {
            //初始化新的FormBody
            //重新生成URL
            if(request.body() instanceof FormBody){
                FormBody.Builder builder = new FormBody.Builder();
                FormBody requestBody = (FormBody) request.body();
                int fieldSize = requestBody == null ? 0 : requestBody.size();
                for (int i = 0; i < fieldSize; i++)
                {
                    //在新的FormBody增加旧的FormBody的参数
                    builder.addEncoded(requestBody.encodedName(i), requestBody.encodedValue(i));
                }
                FormBody newRequestBody = builder.build();
                //关闭原先的请求响应
                response.body().close();
                return request.newBuilder().method(request.method(), newRequestBody).build();
            }else  if(request.body() instanceof RequestBody){

                RequestBody requestBody =RequestBody.create(MediaType.parse("application/json;charset=UTF-8"),
                        bodyToString2(request.body()));
                Request.Builder requestBuilder = request.newBuilder();
                Request newrequest;
                if(!TextUtils.isEmpty(token)){
                     newrequest = requestBuilder
                            .post(requestBody)//关键部分，设置requestBody的编码格式为json
                            .url(url).header("token",token)
                            .build();
                }else{
                    newrequest = requestBuilder
                            .post(requestBody)//关键部分，设置requestBody的编码格式为json
                            .url(url)
                            .build();
                }

                return newrequest;
            }else if (request.body() instanceof MultipartBody) { // 文件
                MultipartBody requestBody = (MultipartBody) request.body();
                MultipartBody.Builder multipartBodybuilder = new MultipartBody.Builder();
                for (int i = 0; i < requestBody.size(); i++) {
                    MultipartBody.Part part = requestBody.part(i);
                    multipartBodybuilder.addPart(part);

                }
                MultipartBody newRequestBody = multipartBodybuilder.build();
                return request.newBuilder().method(request.method(), newRequestBody).build();
            }else {
                RequestBody newRequestBody;
                try {
                    JSONObject jsonObject;
                    RequestBody originalRequestBody = request.body();
                    if (originalRequestBody.contentLength() == 0) {
                        jsonObject = new JSONObject();
                    } else {
                        jsonObject = new JSONObject(getParamContent(originalRequestBody));
                    }
                    newRequestBody = RequestBody.create(originalRequestBody.contentType(), jsonObject.toString());

                } catch (Exception e) {
                    newRequestBody = request.body();
                    e.printStackTrace();
                }
                return request.newBuilder().method(request.method(), newRequestBody).build();
            }


        }

        return request.newBuilder().method(request.method(), request.body()).build();
    }






}
