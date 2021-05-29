package cn.net.yzl.base.okgo;

import android.util.Log;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.convert.StringConvert;
import com.lzy.okrx2.adapter.ObservableResponse;

import org.json.JSONObject;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import cn.net.yzl.base.app.LibApplication;
import cn.net.yzl.base.bean.LoginInfo;
import cn.net.yzl.base.config.HttpUrlBody;
import cn.net.yzl.base.config.NetConfig;
import cn.net.yzl.base.constant.Constant;
import cn.net.yzl.base.utils.SPUtils;
import okhttp3.Headers;
import okhttp3.Response;

/**
 * Created by dell on 2019-5-26.
 */

public class NewTokenReqHandler {
    private final static String TAG = NewTokenReqHandler.class.getSimpleName();
    //同时进行的最大请求数
    private int maxRequests = 1;
    //新版易享通请求等待执行队列
    private Deque<String> newReadyReq = new ArrayDeque<>();
    //新版易享通请求正在执行队列
    private Deque<String> newRunningReq = new ArrayDeque<>(maxRequests);
    private static NewTokenReqHandler instance;
    public static  synchronized NewTokenReqHandler getInstance(){
        if(instance == null){
            instance = new NewTokenReqHandler();
        }
        return instance;
    }
    /*
         * desc：获取token时，要考虑并发
         * 1）先去判断token是否失效
         * 2）如果失效则调用接口获取token
         * 3）成功获取token之后，将token和获取token的时间一起保存起来，并将tolekn放到请求头中
         * 4）如果token没有失效，则不做处理
         */
    public  synchronized  boolean getNewToken(){
        LoginInfo loginInfo = LibApplication.getInstance().getLoginInfo();
        LibApplication.getInstance().getLoginInfo().setLoginState(Constant.LoginState.TOKEN_GETTING);
        try{
            Map<String,String> map = new HashMap();
            String jsonstr="";
            map.put("userNo",loginInfo.getStaffNo());
            map.put("password",loginInfo.getPassword());
            String json =new Gson().toJson(map);
            Response response = OkGo.<String>post(NetConfig.getLoginHost()+ HttpUrlBody.USER_LOGIN)
                    .headers("Content-Type","application/json")
                    .headers("Connection", "close").upJson(json)
                    .execute();
            if(response.code() == 200){
                Headers headers = response.headers();
                String token = headers.get("token");
                LibApplication.getInstance().getLoginInfo().setToken(token);
                SPUtils.getInstance().saveLoginInfo( LibApplication.getInstance().getLoginInfo());//将token信息重新保存到缓存
                /**
                 * OkGo设置公用headers参数
                 */
                Map<String ,String> map2 = new HashMap<>();
                map2.put("token", token);
                OkGoUtils.setCommonHeaders(map2);
                LibApplication.getInstance().getLoginInfo().setLoginState(Constant.LoginState.LOGINED);
                return true;
            }
        }catch (Exception ex){
            LibApplication.getInstance().getLoginInfo().setLoginState(Constant.LoginState.TOKEN_FAILED);
            Log.e(TAG,ex.toString());
        }finally {
            if(newRunningReq.size() > 0)
                newRunningReq.remove();
        }

        return false;
    }



    public synchronized boolean isRunning(){
        if(newRunningReq.size() > 0 || newReadyReq.size() >0){
            return true;
        }
        return false;
    }

    /**
     * 使用调度器进行任务调度
     */
    public synchronized void enqueue(String req) {
        //不能超过最大请求数与相同host的请求数
        //满足条件意味着可以马上开始任务
        if (newRunningReq.size() < maxRequests) {
            newRunningReq.add(req);
        } else {
            if(!newReadyReq.contains(req)) {
                newReadyReq.add(req);
            }
        }
    }

    public synchronized void clearAll(){
        newReadyReq.clear();
        newRunningReq.clear();
    }

    public synchronized void clearReq(String req){
        if(newRunningReq.contains(req)){
            newRunningReq.remove(req);
        }
        if(newReadyReq.contains(req)) {
            newReadyReq.remove(req);
        }
    }

    public synchronized boolean checkNewReady() {
        //没有执行的任务
        if (newReadyReq.isEmpty()) {
            return false;
        }
        if (newReadyReq.size() >0) {

            newRunningReq.add(newReadyReq.remove());
            return true;
        }
        return false;
    }
}
