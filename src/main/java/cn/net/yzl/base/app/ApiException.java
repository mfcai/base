package cn.net.yzl.base.app;

/**
 * Created by dell on 2019-3-5.
 */

public class ApiException extends Exception {
    public final static int EX_LOGIN =10016;//用户名或密码错误
    public final static int EX_OLD_LOGOUT = 1002;
    public final static int EX_PWD_DUETO = 5004;//密码过期
    public final static int EX_UNKNOW =90000;
    public final static int EX_CLOCKIN = 10000;
    public final static int EX_TOKEN_INVALID = 10006;
    public final static int EX_GET_STUFF_CLOCKIN_DATA = 10001;//获取指定日期打卡记录异常
    public int code;
    public String message;

    public ApiException( int code,String msg) {
        this.code = code;
        this.message = msg;

    }
}

