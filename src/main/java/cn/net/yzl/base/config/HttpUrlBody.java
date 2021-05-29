package cn.net.yzl.base.config;

public class HttpUrlBody {
    public final static String GET_DETAIL_BY_NO="/appStaffClockLog/getDetailsByNo";
    public final static String USER_LOGIN ="/oauth/app/user/login";
    public final static String USER_LOGON = "/oauth/app/user/logout";
    //修改密码
    public final static String UPDATE_PASSWORD ="/oauth/app/user/updatePassword";
    //强制修改密码
    public final static String FORCE_UPDATE_PASSWORD ="/oauth/app/user/updatePassword/old";
    //文件上传
    public final static String UPLOAD_FILE ="/appStaffClockLog/upload";
    //初始化打卡页面
    public final static String GET_STAFF_CLOCKIN_LOG = "/appStaffClockLog/initStaffClock";

    public final static String STAFF_CLOCKIN = "/appStaffClockLog/staffClock";//打卡

    public final static String GET_STAFF_CLOCKIN_DATA = "/appStaffClockLog/staffClockLog";//指定日期内的打卡情况
    public final static String GET_STAFF_CLOCKIN_DATA_WITH_DAY ="/appStaffClockLog/staffClockLogDay";//获取指定日期打卡记录

    public final static String GET_STAFF_CLOCKIN_STATISTICS ="/appStaffClockLog/staffClockStatistics";//考勤统计

    public final static String GET_CLOCKIN_STATUS = "/appStaffClockLog/getClockStatus";//查询当前考勤状态接口

    public final static String DEVICE_RECORD_COLLECT = "/appStaffClockLog/insert";//设备信息采集

    public final static String GET_NEW_VERSION = "/appVersion/getNewestVersion";
}
