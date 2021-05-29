package cn.net.yzl.base.common;

import android.os.Bundle;

public class MessageEvent {
    Bundle mBundle;
    int    tag;
    public Bundle getBundle() {
        return mBundle;
    }

    public void setBundle(Bundle bundle) {
        mBundle = bundle;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public static class EventApp {
        public final static int TAG_NOTICE_LATLON_CHANGE = 21;//经纬度变更
        public final static int TAG_NOTICE_CALENDAR_CHANGE = 31;//日期变换
        public final static int TAG_NOTICE_CALENDAR_SEL_TODAY = 32;//打卡统计，检查今天是否被选中,如被选中，则调用接口获取今天的打卡记录
        public final static int TAG_NOTICE_CALENDAR_TODAY_DRAW_POINT = 33;//给当天绘制小圆点

        public final static int TAG_NOTICE_LOCATION_NO_DETECTED = 41;//没有检测到定位
        public final static int TAG_NOTICE_LOCATION_DETECTED = 42;//检测到定位

        public final static int TAG_NOTICE_LOGOUT = 101;
        public final static int TAG_NOTICE_EXPIRED_LOGIN = 102;
        public final static int TAG_NOTICE_ACCOUNT_REJECTION = 103;//单点登录被踢出
    }
}
