package cn.net.yzl.base.utils;

import android.text.TextUtils;
import android.text.format.Time;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.net.yzl.base.config.AppConfig;

/*
 * 消息页面的日期格式是yyyy/mm/dd
 * 其他页面的日期格式是yyyy-mm-dd
*/
public class DateUtil {

    private static final SimpleDateFormat mdyFormat = new SimpleDateFormat("MM-dd-yyyy");
    private static final SimpleDateFormat hmsFormat = new SimpleDateFormat("HH:mm:ss");
    private static final SimpleDateFormat hmFormat = new SimpleDateFormat("HH:mm");
    private static final SimpleDateFormat datetimeFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
    private static final SimpleDateFormat formatCheckin = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static final SimpleDateFormat sdfDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat sdfDate2 = new SimpleDateFormat("MM/yyyy");
    private static final SimpleDateFormat sdfDate3 = new SimpleDateFormat("MM月dd日");

    private final static long minute = 60 * 1000;// 1分钟
    private final static long hour = 60 * minute;// 1小时
    private final static long day = 24 * hour;// 1天
    private final static long month = 31 * day;// 月
    private final static long year = 12 * month;// 年

    /**
     * 获得当前时间的<code>java.util.Date</code>对象
     *
     * @return
     */
    public static Date now() {
        return new Date();
    }

    /**
     * 获得当前日期
     * <p>
     * 日期格式MM-dd-yyyy
     *
     * @return
     */
    public static String currentDateMdy() {
        return mdyFormat.format(now());
    }

    /**
     * 获得当前时间
     * <p>
     * 时间格式HH:mm:ss
     *
     * @return
     */
    public static String currentTime() {
        return hmsFormat.format(now());
    }

    public static String currentTimeNoSecond(){
        return hmFormat.format(now());
    }

    public static String getTimeNoSecond(Date date){
        return hmFormat.format(date);
    }

    public static String currentDateTime() {
        return datetimeFormat.format(now());
    }

    public static String curYYYYMM(){
        return sdfDate2.format(now());
    }

    public static String curMMdd(){
        return sdfDate3.format(now());
    }

    /**
     * 日期 按规定格式输出
     *
     * @param date
     * @param fmt
     * @return String
     */
    public static String datetoString(Date date, String fmt) {
        if (date == null) {
            return "";
        }
        DateFormat dateFormat = new SimpleDateFormat(fmt);
        return dateFormat.format(date);
    }

    //把字符串转为日期
    public static String getCurTime() {
        try {
            Date date = new Date();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return df.format(date);
        } catch (Exception e) {

        }
        return "";
    }

    //把字符串转为日期
    public static String getToday() {
        try {
            Date date = new Date();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            return df.format(date);
        } catch (Exception e) {

        }
        return "";
    }

    //把字符串转为日期
    public static Date str2DateTime(String str_date) {
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return df.parse(str_date);
        } catch (Exception e) {

        }
        return null;
    }

    //把字符串转为日期
    public static Date str2Date(String strDate) {
        try {
            DateFormat df = new SimpleDateFormat("MM-dd-yyyy");
            return df.parse(strDate);
        } catch (Exception e) {

        }
        return null;
    }

    //把字符串转为日期
    public static Date str2Date(String strDate, String pattern) {
        try {
            DateFormat df = new SimpleDateFormat(pattern);
            return df.parse(strDate);
        } catch (Exception e) {

        }
        return null;
    }

    /**
     *将字符串格式yyyyMMdd的字符串转为日期，格式"yyyy-MM-dd"
     *
     * @param strDate 日期字符串
     * @return 返回格式化的日期
     * @throws Exception 分析时意外地出现了错误异常
     */
    public static String strToDateFormat(String strDate) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
            formatter.setLenient(false);
            Date newDate= formatter.parse(strDate);
            formatter = new SimpleDateFormat("yyyy-MM-dd");
            return formatter.format(newDate);
        } catch (Exception e) {
        }
        return strDate;
    }

    /**
     *将字符串格式yyyyMMdd的字符串转为日期，格式"yyyy-MM-dd"
     *
     * @param strDate 日期字符串
     * @return 返回格式化的日期
     * @throws Exception 分析时意外地出现了错误异常
     */
    public static String strToTimeFormat(String strDate) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            formatter.setLenient(false);
            Date newDate= formatter.parse(strDate);
            formatter = new SimpleDateFormat("HH:mm");
            return formatter.format(newDate);
        } catch (Exception e) {
        }
        return strDate;
    }

    /**
     * 调用此方法输入所要转换的时间戳输入例如（1441595166277）输出（"2014年06月14日16时09分00秒"）
     *
     * @param timestamp
     * @return
     */
    public static String times(long timestamp) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd");
        String times = sdr.format(new Date(timestamp));
        return times;
    }

    /**
     * 调用此方法输入所要转换的时间戳输入例如（1441595166277）输出（"2014年06月14日16时09分00秒"）
     *
     * @param timestamp
     * @return
     */
    public static String parseDate(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateTime = sdf.format(timestamp);
        return dateTime;
    }
    public static String parseDateNoS(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String dateTime = sdf.format(timestamp);
        return dateTime;
    }
    public static String parseDateNoS2(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        String dateTime = sdf.format(timestamp);
        return dateTime;
    }
    public static String getWeekOfDate(Date dt) {
        if(null == dt) {
            return "";
        }
        String[] weekDays = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }

    public static String getWeekOfDate2(Date dt) {
        if(null == dt) {
            return "";
        }
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }

    public static String toStringCheckin(Date date) {
        if (date == null) {
            return "";
        }
        return formatCheckin.format(date);
    }

    /**
     * 几周前
     * @param date
     * @param week  0:本周,1:1周前
     * @return
     */
    public static boolean whichWeek(Date date, int week) {
        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        int dayOfWeek = calendar1.get(Calendar.DAY_OF_WEEK) - 1;
        int offset1 = 1 - dayOfWeek;
        int offset2 = 7 - dayOfWeek;
        calendar1.add(Calendar.DATE, offset1 - week*7);
        calendar2.add(Calendar.DATE, offset2 - week*7);

        String beginDateStr = sdfDate.format(calendar1.getTime());
        String endDateStr = sdfDate.format(calendar2.getTime());

        try {
            Date beginDate = sdfDateTime.parse(beginDateStr + " 00:00:00");
            Date endDate = sdfDateTime.parse(endDateStr + " 23:59:59");

            if(date.before(endDate) && date.after(beginDate)) {
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getCurDate(){
        int y,m,d,h,mi,s;
        Calendar cal=Calendar.getInstance();
        y=cal.get(Calendar.YEAR);
        m=cal.get(Calendar.MONTH);
        d=cal.get(Calendar.DATE);
        h=cal.get(Calendar.HOUR_OF_DAY);
        mi=cal.get(Calendar.MINUTE);
        s=cal.get(Calendar.SECOND);
        if(d < 10){
            return "0"+d+"";
        }
        return d+"";
    }
    public  static String formatUTC(long l, String strPattern) {
        if (TextUtils.isEmpty(strPattern)) {
            strPattern = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf= new SimpleDateFormat(strPattern, Locale.CHINA);
        return sdf == null ? "NULL" : sdf.format(l);
    }
    /**
     * 几周前
     * @param date
     * @return
     */
    public static String getWeekStr(Date date) {
        if(whichWeek(date, 0)) {
            return "本周";
        } else if(whichWeek(date, 1)){
            return "1周前";
        } else if(whichWeek(date, 2)){
            return "2周前";
        } else if(whichWeek(date, 3)){
            return "3周前";
        }
        return "3周前";
    }


    public static boolean isDateFormat(String str) {
        if(str == null||"".equals(str)){
            return false;
        }
        String regex = "[0-9]{4}-[0-9]{2}-[0-9]{2}";
        Pattern pattern = Pattern.compile(regex);
        Matcher m = pattern.matcher(str);
        boolean dateFlag = m.matches();
        if (!dateFlag) {
            System.out.println("格式错误");
            return false;
        }
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        formatter.setLenient(false);
        try {
            Date date = formatter.parse(str);
            System.out.println(date);
            System.out.print("格式正确！");
            return true;
        } catch (Exception e) {
            System.out.println("格式错误！");
            return false;
        }
    }

    public static boolean compareDate(String date1, String date2) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date3 = format.parse(date1);
            Date date4 = format.parse(date2);
            return date3.before(date4);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Date addDate(String strdate,int period){
        Date date =str2DateTime(strdate);
        return addDate(date,period);
    }


    public static Date addDate(Date date,int period){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);//设置起时间
        cal.add(Calendar.DATE,period);//增加一天
        return cal.getTime();
    }

    /**
     * 比较两个日期的大小，日期格式为yyyy-MM-dd
     *
     * @param dt1 the first date
     * @param dt2 the second date
     * @return true <br/>false
     */
    public static boolean isDate2Bigger(Date dt1, Date dt2) {
        boolean isBigger = false;
        if (dt1.getTime() >= dt2.getTime()) {
            isBigger = false;
        } else if (dt1.getTime() < dt2.getTime()) {
            isBigger = true;
        }
        return isBigger;
    }



    /* //日期转换为时间戳 */
    public long timeToStamp(String timers) {
        Date d = new Date();
        long timeStemp = 0;
        try {
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
            d = sf.parse(timers);// 日期转换为时间戳
        } catch (ParseException e) {
            e.printStackTrace();
        }
        timeStemp = d.getTime();
        return timeStemp;
    }



    /****
     * 传入具体日期 ，返回具体日期增加指定的月数。
     * @param date 日期(2017-04-13)
     * @return 2017-05-13
     * @throws ParseException
     */
    private  String addMonth(String date,int n) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date dt = sdf.parse(date);
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(dt);
        rightNow.add(Calendar.MONTH, n);
        Date dt1 = rightNow.getTime();
        String reStr = sdf.format(dt1);
        return reStr;
    }

    public static Date addMonth(Date date,int period){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);//设置起时间
        cal.add(Calendar.MONTH,period);//增加指定的月数
        return cal.getTime();
    }







    /**
     获取当前时间的时间戳，精确到毫秒
     * @param
     * @return
     **/
    public static Long getCurTimestamp(){
        Date date = new Date();
        return date.getTime();
    }

    /**
     * 判断当前系统时间是否在指定时间的范围内
     *
     * @param beginHour
     * 开始小时，例如22
     * @param beginMin
     * 开始小时的分钟数，例如30
     * @param endHour
     * 结束小时，例如 8
     * @param endMin
     * 结束小时的分钟数，例如0
     * @return true表示在范围内，否则false
     */
    public static boolean isCurrentInTimeScope(int beginHour, int beginMin, int endHour, int endMin) {
        boolean result = false;
        final long aDayInMillis = 1000 * 60 * 60 * 24;
        final long currentTimeMillis = System.currentTimeMillis();

        Time now = new Time();
        now.set(currentTimeMillis);

        Time startTime = new Time();
        startTime.set(currentTimeMillis);
        startTime.hour = beginHour;
        startTime.minute = beginMin;

        Time endTime = new Time();
        endTime.set(currentTimeMillis);
        endTime.hour = endHour;
        endTime.minute = endMin;

        if (!startTime.before(endTime)) {
            // 跨天的特殊情况（比如22:00-8:00）
            startTime.set(startTime.toMillis(true) - aDayInMillis);
            result = !now.before(startTime) && !now.after(endTime); // startTime <= now <= endTime
            Time startTimeInThisDay = new Time();
            startTimeInThisDay.set(startTime.toMillis(true) + aDayInMillis);
            if (!now.before(startTimeInThisDay)) {
                result = true;
            }
        } else {
        // 普通情况(比如 8:00 - 14:00)
            result = !now.before(startTime) && !now.after(endTime); // startTime <= now <= endTime
        }
        return result;
    }

    /**
     * 判断当前系统时间是否小于指定时间
     *
     * @param specialHour
     * 开始小时，例如22
     * @param specialMin
     * 开始小时的分钟数，例如30

     * @return true表示在范围内，否则false
     */
    public static boolean isCurrentSmallerWithSpecialTime(int specialHour, int specialMin) {
        boolean result = false;
        final long aDayInMillis = 1000 * 60 * 60 * 24;
        final long currentTimeMillis = System.currentTimeMillis();

        Time now = new Time();
        now.set(currentTimeMillis);

        Time startTime = new Time();
        startTime.set(currentTimeMillis);
        startTime.hour = specialHour;
        startTime.minute = specialMin;

        result = now.before(startTime);

        return result;
    }

    /**
     * 判断当前系统时间是否小于指定时间
     *
     * @param specialHour
     * 开始小时，例如22
     * @param specialMin
     * 开始小时的分钟数，例如30

     * @return true表示在范围内，否则false
     */
    public static boolean compareTime(String date_time,int specialHour, int specialMin) {
        boolean result = false;
        final long aDayInMillis = 1000 * 60 * 60 * 24;
        final long currentTimeMillis = System.currentTimeMillis();

        Date max_date = str2DateTime(date_time);
        String str_time =getTimeNoSecond(max_date);

        String[] temp = str_time.split(":");

        Time curTime = new Time();
        curTime.set(currentTimeMillis);
        curTime.hour =  Integer.parseInt(temp[0]);
        curTime.minute = Integer.parseInt(temp[1]);

        Time specialTime = new Time();
        specialTime.set(currentTimeMillis);
        specialTime.hour = specialHour;
        specialTime.minute = specialMin;

        result = curTime.before(specialTime);

        return result;
    }

    /**
     * 判断当前系统时间是否大于指定时间
     *
     * @param specialHour
     * 开始小时，例如22
     * @param specialMin
     * 开始小时的分钟数，例如30

     * @return true表示在范围内，否则false
     */
    public static boolean isCurrentBiggerWithSpecialTime(int specialHour, int specialMin) {
        boolean result = false;
        final long aDayInMillis = 1000 * 60 * 60 * 24;
        final long currentTimeMillis = System.currentTimeMillis();

        Time now = new Time();
        now.set(currentTimeMillis);

        Time startTime = new Time();
        startTime.set(currentTimeMillis);
        startTime.hour = specialHour;
        startTime.minute = specialMin;

        result = now.after(startTime);

        return result;
    }

    /**
     * 本次打卡时间和上次打卡时间是否超出1分钟限制
     *
     * @param date1,date2
     * @return
     */
    public static boolean  overLimit(Date date1,Date date2) {
        if (date2 == null || date1 == null) {
            return false;
        }
        long diff = date2.getTime() - date1.getTime();
        if (diff > minute) {
            return true;
        }
        return false;
    }

    /**
     * 校验时间格式HH:MM（精确）
     */
    public static boolean checkTime(String time) {
        if (checkHHMM(time)) {
            String[] temp = time.split(":");
            if ((temp[0].length() == 2 || temp[0].length() == 1) && temp[1].length() == 2) {
                int h,m;
                try {
                    h = Integer.parseInt(temp[0]);
                    m = Integer.parseInt(temp[1]);
                } catch (NumberFormatException e) {
                    return false;
                }
                if (h >= 0 && h <= 24 && m <= 60 && m >= 0) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 校验时间格式（仅格式）
     */
    public static boolean checkHHMM(String time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm");
        try {
            @SuppressWarnings("unused")
            Date t = dateFormat.parse(time);
        }
        catch (Exception ex) {
            return false;
        }
        return true;
    }

    /**
     * //计算时间差
     * int daynumber = dateDiffDay(startDate, endDate, "yyyy-MM-dd");
     */
    public static long dateDiffDay(String startTime, String endTime, String format) {
        // 按照传入的格式生成一个simpledateformate对象
        SimpleDateFormat sd = new SimpleDateFormat(format);
        long nd = 1000 * 24 * 60 * 60;// 一天的毫秒数
        long nh = 1000 * 60 * 60;// 一小时的毫秒数
        long nm = 1000 * 60;// 一分钟的毫秒数
        long ns = 1000;// 一秒钟的毫秒数
        long diff;
        long day = 0;
        try {
            // 获得两个时间的毫秒时间差异
            diff = Math.abs(sd.parse(endTime).getTime()
                    - sd.parse(startTime).getTime());

            long hour = diff /nh;// 计算差多少小时

            return hour;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

}
