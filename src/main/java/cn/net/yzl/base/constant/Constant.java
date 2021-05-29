package cn.net.yzl.base.constant;

import android.Manifest;

public class Constant {

    public final static String WX_APP_ID = "wx96a9acdd4af5a705";
    public static final String SP_NAME = "yc";
    public static final String SP_LOGIN_INFO = "login_info";
    public static final String SP_DEVICE_INFO = "device_info";

    public static final String CHOICE_LOCALDATE ="choice_localdate";
    public static final String WEEK_CALANDAR ="week_calandar";//周日历
    public static final String TODAY_DRAW_POINT_WITH_CALENDAR = "today_draw_point_with_calendar";//在日历上的当天绘制小原点
    /**-------------------------------------键-------------------------------------------------**/
    //Sp键
    public static final String KEY_FIRST_SPLASH = "first_splash";                 //是否第一次启动
    public static final String KEY_IS_LOGIN = "is_login";                         //登录

    public static final String APP_ID_KEY = "appid";
    public static final String APP_ID_VALUE = "YZL_APP_Android";

    public static final int SWITCH_4G = 1;
    public static final int SWITCH_WIFI = 0;
    /**
     * 服务器类型
     * SERVER_DEVELOP       开发环境
     * SERVER_PRODUCTION    生产环境
     */
    public static int SERVER_TYPE = ServerType.SERVER_DEVELOP;
    /**
     * 新版本对应的服务器类型
     */
    public class ServerType {
        public static final int SERVER_DEVELOP = 0;//开发环境
        public static final int SERVER_PRODUCTION = 1;//生产环境
    }
    public class  ClockinNoticeCode{
        public static final int NOT_OPEN_WIFI = 101;//没有打开wifi（请打开wifi进行打卡）
        public static final int NOT_CONNECT_SPECIFIED_WIFI = 102;//没有连上指定wifi（请连接指定wifi进行打卡）
        public static final int CLOCKIN_READY =0;//可以打卡
    }

    public class NetStatusCode{
        public static final int success=200;
        public static final int error=-1;

        public static final int INVALID_TOKEN = 10006; //TOKEN失效
        public static final int EXPIRED_LOGIN = 10003;//登陆失效
    }

    public class MOShare{
        public final static int SHARE_WECHAT =0;
        public final static int SHARE_WECHAT_CIRCLE =1;
    }


    public class LoginState {
        public final static int LOGINED= 0;//已登录
        public final static int LOGOUT = 1;//已登出
        public final static int TOKEN_INVALID =2;//token失效
        public final static int COOKIE_INVALID = 3;//cookie失效
        public final static int TOKEN_GETTING =4;//重新获取token
        public final static int COOKIE_GETTING =5;//重新获取cookie
        public final static int LOGING =6;//登录中
        public final static int LOGOUTING =7;//登出中
        public final static int NOT_LOGIN =9;
        public final static int TOKEN_FAILED = 10;//TOKEN获取失败
        //    public final static int TOKEN_SUCCESS = 11;//TOKEN获取成功和LOGINED是同一个意思，暂时不需要
        public final static int LOGIN_FAIL = 12;
    }

    public static final int WORK_TIME_HOUR_OVER_DEVIDE = 24;
    public static final int WORK_TIME_MIN_OVER_DEVIDE =0;
    //考勤统计模块
    public static class ClockInModule{
        public final static String ATTENCE_TYPE = "ATTENCE_TYPE";//考勤展开类型
        public final static String RILI_TYPE = "RILI_TYPE";//日历类型
        public final static int AVERAGE_WORK_HOUR =0;//平均工时
        public final static int ATTENDENCE_DAYS = 1;//出勤天数
//        public final static int ATTENDENCE_CLASSES =2;//出勤班次
        public final static int REST_DAYS = 2;//休息天数
        public final static int WORK_LATER = 3;//迟到
        public final static int WORK_EARILIER = 4;//早退
        public final static int MISSING_CARD = 5;//缺卡
        public final static int ABSENT_WORK =6;//旷工
        public final static int MEND_CARD =7;
        public final static int CLOCKIN_SUM =8;//查看考勤汇总
    }

    public static class ClockinRule{
        public final static int REGULAR_CLOCKIN = 0;//正常打卡
        public final static int FLEXIBLE_CLOCKIN =1;//弹性打卡
    }
    public static class WorkOnStatus{
        public final static int STATUS_FLAG_SHANGBAN = 1;//上班打卡
        public final static int STATUS_FLAG_XIABAN =2;//下班打卡
    }

    public static class BaseModule{
        public static final String BIGIMG_URL = "bigimg_url";
        public static final String BIGIMG_POS = "bigimg_pos";
    }
    //每天考勤状态
    public static class DayAttenceStatus{
        public final static int WORK_REGULAR =611;
        public final static int WOKR_LATER =612;  //迟到
        public final static int WORK_EARLIER =616;//早退
        public final static int SHANGBAN_MISSING_CARD = 614;//上午缺卡
        public final static int XIABAN_MISSING_CARD = 615;//下午缺卡
        public final static int SHANGBAN_ABSENT = 618;//上午旷工
        public final static int XIABAN_ABSENT = 619;//下午旷工

        public final static int LEAVE_AM =621;//上午请假
        public final static int LEAVE_PM =622;//下午请假
        public final static int WORK_EXTRA_AM = 642;//上午加班
        public final static int WORK_EXTRA_PM = 643;//上午加班
    }


    public class MapKey {
        /**
         * 记录定位信息
         */
        public static final String LOCATION_INFO = "locationInfo";

        /**
         * 记录搜索信息
         */
        public static final String SEARCH_INFO = "searchInfo";
    }

    public static class Permission {
        /**
         * 写入权限的请求code,提示语，和权限码
         */
        public final static int WRITE_PERMISSION_CODE = 110;
        public final static String WRITE_PERMISSION_TIP = "为了正常使用，请允许读写权限!";
        public final static String[] PERMS_WRITE_READ = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

        public static final int PHONE_STATE_REQUESTCODE = 111;
        public final static String PHONE_STATE_PERMISSION_TIP = "为了正常使用，请允许获取IMEI权限!";
        public final static String[] PERMS_PHONE_STATE = {Manifest.permission.READ_PHONE_STATE};

        public static final int CAMERA_PERMISSION_CODE = 112;
        public final static String CAMERA_PERMISSION_TIP = "为了正常使用，请允许获取拍照权限!";
        public final static String[] PERMS_CAMERA = {Manifest.permission.CAMERA};

        public static final int LOCACTION_PERMISSION_CODE = 113;
        public final static String LOCACTION_PERMISSION_TIP = "为了正常使用，请允许获取定位权限!";
        public final static String[] PERMS_LOCACTION = {Manifest.permission.ACCESS_FINE_LOCATION};

        public static final int CALLPHONE_PERMISSION_CODE = 114;
        public final static String CALLPHONE_PERMISSION_TIP = "为了正常使用，请允许获取拨打电话权限!";
        public final static String[] PERMS_CALLPHONE = {Manifest.permission.CALL_PHONE};
        public final static String[] PERMS_LOCACTION_FINE = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE};
        public final static String PERMISSION_TIP = "为了您能正常使用，请开启相关权限！";

        public static final int ALERT_WINDOW_PERMISSION_CODE = 115;
        public final static String ALERT_WINDOW_PERMISSION_TIP = "为了正常使用，请允许获取悬浮窗权限!";
        public final static String[] PERMS_ALERT_WINDOW = {Manifest.permission.SYSTEM_ALERT_WINDOW};
        /**
         * 相机，图库的请求code
         */
        public final static int PICTURE_CODE = 10;
        public final static int GALLERY_CODE = 11;

        public final static int RECORD_AUDIO_CODE = 115;
        public final static String RECORD_AUDIO_MESSAGE = "为了正常使用，请允许录音权限!";
        public final static String[] RECORD_AUDIO_PERMISSION = {Manifest.permission.RECORD_AUDIO};

    }
}
