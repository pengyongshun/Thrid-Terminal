package com.xt.mobile.terminal.util;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 类名        ：DateUtil
 *
 * 描述        ： 时间处理类
 *
 * 创建人    ：pengyongshun
 *
 * 日期        ：2017-07-28
 *
 * */
public class DateUtil {

    public static long timeRecording = 0; // 时间记录
    public static int REFRESHTIME = 500;// 刷新时间
    public static int DEL_REFRESHTIME = 1000;// 删除操作
    public static int REDENVELOPE_REFRESHTIME = 1500;// 红包刷新时间
    public static int REFRESHTIME_RECODING = 1000;// 刷新时间
    public static String[] WEEK = {"周日","周一","周二","周三","周四","周五","周六"};
	public static final int WEEKDAYS = 7;
	
    /** 锁对象 */
    private static final Object lockObj = new Object();

    /** 存放不同的日期模板格式的sdf的Map */
    private static Map<String, ThreadLocal<SimpleDateFormat>> sdfMap = new HashMap<String, ThreadLocal<SimpleDateFormat>>();
    
    /**
     * 返回一个ThreadLocal的sdf,每个线程只会new一次sdf
     * 
     * @param pattern
     * @return
     */
    private static SimpleDateFormat getSdf(final String pattern) {
        ThreadLocal<SimpleDateFormat> tl = sdfMap.get(pattern);

        // 此处的双重判断和同步是为了防止sdfMap这个单例被多次put重复的sdf
        if (tl == null) {
            synchronized (lockObj) {
                tl = sdfMap.get(pattern);
                if (tl == null) {
                    // 只有Map中还没有这个pattern的sdf才会生成新的sdf并放入map
                    // 这里是关键,使用ThreadLocal<SimpleDateFormat>替代原来直接new SimpleDateFormat
                    tl = new ThreadLocal<SimpleDateFormat>() {
                        @Override
                        protected SimpleDateFormat initialValue() {
                            return new SimpleDateFormat(pattern);
                        }
                    };
                    sdfMap.put(pattern, tl);
                }
            }
        }

        return tl.get();
    }

    
    /**
     * 返回年，月，日，小时，分，钟，秒
     * 
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    private static String patternSystemTime = "yyyy-MM-dd HH:mm:ss";
    public static String returnSystemTime() {
        return getSdf(patternSystemTime).format(new Date(System.currentTimeMillis()));
    }

    private static String patternLogSystemTime = "yyyy-MM-dd HH:mm:ss.SSS";
    public static String returnLogSystemTime() {
        String str = getSdf(patternLogSystemTime).format(new Date(System.currentTimeMillis()));
        return str;
    }

    private static String patternTimeByLong = "yyyyMMdd HH:mm:ss";
    public static String returnTimeByLong(long timemillis) {
        return getSdf(patternTimeByLong).format(new Date(timemillis));
    }
    
    public static String returnTimeByLongByFormat(long timemillis, String formatString) {
        return getSdf(formatString).format(new Date(timemillis));
    }

    private static String patternLoginTimeByLong = "MM-dd HH:mm";
    public static String returnLoginTimeByLong(long timemillis){
        return getSdf(patternLoginTimeByLong).format(new Date(timemillis));
    }
    
    private static String patternHourTime= "yyyy-MM-dd HH:mm";
    public static String returnHourTime(long timemillis) {
        return getSdf(patternHourTime).format(new Date(timemillis));
    }

    private static String patternMinuteTime= "yyyy-MM-dd HH:mm:ss";
    public static String returnMinuteTime(long timemillis) {
        return getSdf(patternMinuteTime).format(new Date(timemillis));
    }

    private static String patternDayTime= "yyyy-MM-dd";
    public static String returnDayTime(long timemillis) {
        return getSdf(patternDayTime).format(new Date(timemillis));
    }

    private static String patternSecondTime= "yyyyMMddHHmmss";
    public static String returnSecondTime(long timemillis) {
        return getSdf(patternSecondTime).format(new Date(timemillis));
    }
    
    public static String returnSecondTime(long timemillis, String format) {
        return getSdf(format).format(new Date(timemillis));
    }

    private static String patternDate= "yyyyMMdd";
    public static String returnDate(long timemillis) {
        return getSdf(patternDate).format(new Date(timemillis));
    }
    
    private static String patternYYYYMM= "yyyyMM";
    public static String returnDateYYYYMM(long timemillis) {
        return getSdf(patternYYYYMM).format(new Date(timemillis));
    }

    /**
     * 返回年，月，日，小时，分，钟，秒
     * 
     * @return
     */
    public static String getCurrentTime(String format) {
        return getSdf(format).format(new Date(System.currentTimeMillis()));
    }


    /**
     * 返回年月日
     * 
     * @return
     */
    public static String returnCurrentDate(String format) {
        return getSdf(format).format(new Date(System.currentTimeMillis()));
    }

    
    public static long parseFormatDate2Milliseconds(String dateStr, String format) {
        long ret = System.currentTimeMillis();
        if (!TextUtils.isEmpty(dateStr)) {
            try {
                SimpleDateFormat formater = (SimpleDateFormat) SimpleDateFormat
                        .getDateTimeInstance();
                formater.applyPattern(format);
                Date date = formater.parse(dateStr);
                ret = date.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return ret;
    }
    

    /*
     * 当前日期加减n天后的日期，返回String (yyyy-mm-dd) 对当前日期减1
     */
    public static String Subtractiontoday(int n, String format) {
        Calendar rightNow = Calendar.getInstance();
        rightNow.add(Calendar.DAY_OF_MONTH, -n);
        return getSdf(format).format(rightNow.getTime());
    }

    public static String getCurrDateWithFormat(String format) {
        
        if (TextUtils.isEmpty(format)) {
            return "";
        }
        
        return getSdf(format).format(new Date(System.currentTimeMillis()));
    }

    /**
     * @param: 将日期2013-03-11 11:12:23
     * @return转换为2013年03月11日
     * @throws ParseException 12"yyyy-MM-dd HH:mm:ss" 2."yyyy-MM-dd"
     */
    public static String getpublicSecondDate(String dateStr)
            throws ParseException {
        String strFormatold = "yyyyMMddHHmmss";
        String strFormatnew = "yyyy年MM月dd日 HH时mm分ss秒";
        SimpleDateFormat formater = (SimpleDateFormat) SimpleDateFormat
                .getDateTimeInstance();
        formater.applyPattern(strFormatold);
        Date date = formater.parse(dateStr);
        SimpleDateFormat formatter = new SimpleDateFormat(strFormatnew, Locale.getDefault());
        return formatter.format(date);
    }


    /**
     * 时间去掉中文
     *
     * @return返回字符串格式 yyyy-MM-dd HH:mm:ss
     */
    public static  String subStrForMath(String dateStr){
        String strFormatnew = "yyyyMMdd";
        String strFormatold = "yyyy年MM月dd日";
        SimpleDateFormat formater = (SimpleDateFormat) SimpleDateFormat
                .getDateTimeInstance();
        formater.applyPattern(strFormatold);
        Date date = null;
        try {
            date = formater.parse(dateStr);
        } catch (ParseException e) {

            e.printStackTrace();
        }
        SimpleDateFormat formatter = new SimpleDateFormat(strFormatnew, Locale.getDefault());
        if ( formatter != null && date!=null){
            return   formatter.format(date);
        }else{
            return dateStr;
        }
    }


    /**
     * @param: 将日期2013-03-11 11:12:23
     * @return转换为2013年03月11日
     * @throws ParseException 12"yyyy-MM-dd HH:mm:ss" 2."yyyy-MM-dd"
     */
    public static String getpublicDate(String dateStr)
            throws ParseException {
        String strFormatold = "yyyyMMdd";
        String strFormatnew = "yyyy年MM月dd日";
        String strFormatold2 = "yyyyMMddHHmmss";
        String strFormatnew2 = "yyyy年MM月dd日 HH时mm分ss秒";
        SimpleDateFormat formater = (SimpleDateFormat) SimpleDateFormat
                .getDateTimeInstance();
        if ( dateStr.length() > 8){
            formater.applyPattern(strFormatold2);
        }else {
            formater.applyPattern(strFormatold);
        }

        if(dateStr.equals("0")){
            return dateStr;
        }
        if (!TextUtils.isEmpty(dateStr)){
            Date date = formater.parse(dateStr);
            if ( dateStr.length() > 8){
                SimpleDateFormat formatter = new SimpleDateFormat(strFormatnew2, Locale.getDefault());
                return formatter.format(date);
            }else {
                SimpleDateFormat formatter = new SimpleDateFormat(strFormatnew, Locale.getDefault());
                return formatter.format(date);
            }

        }else{
            return dateStr;
        }

    }

    /**
     * 获取日期字符串
     * 
     * @param formatFalg 1:带时分秒 2:不带时分秒
     * @return
     */
    public static String getNow(int formatFalg) {
        if (formatFalg == 1) {
            return getSdf(patternTimeByLong).format(new Date(System.currentTimeMillis()));
        } else {
            return getSdf(patternDate).format(new Date(System.currentTimeMillis()));
        }
    }

    public static long returnlongTime(String time) {
        long data = 0;
        try {
            data = getSdf(patternTimeByLong).parse(time).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return data;
    }
    
    public static long returnlongTimeByDate(String time) {
        long data = 0;
        try {
            data = getSdf(patternDayTime).parse(time).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * @method: convertDetailTime 得到2个时间差 返回分钟
     * @Description: SimpleDateFormat df = new
     *               SimpleDateFormat("yyyyMMdd HH:mm:ss"); String dateString =
     *               "20140813 10:30:00"; Date date; try { date =
     *               df.parse(dateString); long data=date.getTime();
     *               System.out.println("----xx--->" +
     *               convertDetailTime(data)+"--->"); } catch (ParseException e)
     *               { e.printStackTrace(); }
     * @param time
     * @return
     * @throws
     */
    public static long convertDetailTime(long time) {
        long current = System.currentTimeMillis();
        int time1 = (int) (current - time);
        int timeSec = time1 / (1000 * 60);
        return timeSec;
    }

    /**
     * @method: convertDetailTime 得到2个时间差 返回秒
     * @Description: SimpleDateFormat df = new
     *               SimpleDateFormat("yyyyMMdd HH:mm:ss"); String dateString =
     *               "20140813 10:30:00"; Date date; try { date =
     *               df.parse(dateString); long data=date.getTime();
     *               System.out.println("----xx--->" +
     *               convertDetailTime(data)+"--->"); } catch (ParseException e)
     *               { e.printStackTrace(); }
     * @param time
     * @return
     * @throws
     */
    public static long returnSubtraction(long time) {
        long current = System.currentTimeMillis();
        int time1 = (int) (current - time);
        int timeSec = time1 / 1000;
        return timeSec;
    }

    
    public static int convetSecondToMillSecond(String second){
        float secondTime = Float.valueOf(second)*1000;
        int millTime = (int)secondTime;
        return millTime;
    }

    public static String convertDate(String dateTime, String dateStr) {
        Date date = null;
        try {
            date = getSdf(patternTimeByLong).parse(dateTime);
        } catch (Exception e) {
            return "";
        }
        
        return getSdf(dateStr).format(date);
    }

    public static String subTools(String value, int begin, int end) {
        if (value.length() >= end) {
            return value.substring(begin, end);
        } else {
            return value;
        }
    }

    /**
     * @method: convertTime
     * @Description:
     * @param time1
     * @param time2
     * @return convertTime("20140923 14:04:42","20140923 14:04:55")
     * @throws
     */
    public static int convertTime(String time1, String time2) {
        int results = 0;
        if (time1 == null || time2 == null) {
            return results;
        }
        
        try {
            long data_1 = getSdf(patternTimeByLong).parse(time1).getTime();
            long data_2 = getSdf(patternTimeByLong).parse(time2).getTime();
            long result = data_2 - data_1;
            return (int) result / (1000 * 60);
        } catch (ParseException e) {
            results = -1;
        }
        return results;
    }

    /**
     * @method: secondToMillisecond
     * @Description: convert second to millisecond
     * @param milliSecond
     * @return
     * @throws
     */
    public static String milliSecondToSecond(String milliSecond) {

        if (TextUtils.isEmpty(milliSecond)) {
            return "";
        }
        try {
            if (milliSecond.length() == 13) {// 判读是不是精确到毫秒级的,说明milliSecond毫秒级别的
                milliSecond = String.valueOf(Long.valueOf(milliSecond) / 1000);// 是毫米级的就要除以1000表位秒级别方便与IOS通讯
            }
        } catch (NumberFormatException e) {
            return "";
            /*PLog.e("ApplyForm", " secondToMillisecond milliSecond = " + milliSecond
                    + " e.getMessage = " + e.getMessage());*/
        }

        return milliSecond;

    }

    /**
     * @method: secondToMillisecond
     * @Description: convert second to millisecond  发送数据时需要把系统当前时间 /1000 和其他平台保持一致，为了解决linux平台中的时差问题
     * @param milliSecond
     * @return
     * @throws
     */
    public static long milliSecondToSecond(long milliSecond) {
        try {

            if (String.valueOf(milliSecond).length() == 13) {// 判读是不是精确到毫秒级的,说明milliSecond毫秒级别的
                milliSecond = (milliSecond) / 1000;// 是毫米级的就要除以1000表位秒级别方便与IOS通讯
            }
        } catch (NumberFormatException e) {
            return 0;
//            PLog.e("ApplyForm", " secondToMillisecond milliSecond = " + milliSecond
//                    + " e.getMessage = " + e.getMessage());
        }

        return milliSecond;
    }

    public static long secondToMillisecond(long second) {
        try {
            if (String.valueOf(second).length() == 10) {// 判读是不是精确到毫秒级的,说明second毫秒级别的
                second = second * 1000;// 是毫米级的就要除以1000表位秒级别方便与IOS通讯
            }
        } catch (NumberFormatException e) {
            return 0;
//            PLog.e("ApplyForm",
//                    " milliSecondToSecond second = " + second + " e.getMessage = " + e.getMessage());
        }

        return second;
    }

    public static String secondToMillisecond(String second) {
        if (TextUtils.isEmpty(second)) {
            return "";
        }
        try {
            // Long nowMilli = System.currentTimeMillis();
            if (second.length() == 10) {// 判读是不是精确到毫秒级的,说明second毫秒级别的
                second = String.valueOf(Long.valueOf(second) * 1000);// 是毫米级的就要除以1000表位秒级别方便与IOS通讯
            }
        } catch (NumberFormatException e) {
            return "";
//            PLog.e("ApplyForm",
//                    " milliSecondToSecond second = " + second + " e.getMessage = " + e.getMessage());
        }

        return second;
    }

    /**
     * @method: equalsTime 比较时间是否执行
     * @Description:
     * @param: currentTime
     * @param: upTime
     * @return false 是执行 true 是不执行
     * @throws
     */
    public static boolean equalsDelTime() {
        long currentTime = System.currentTimeMillis();
        if (Math.abs(currentTime - timeRecording) > DEL_REFRESHTIME) {
            timeRecording = System.currentTimeMillis();
            return false;
        }
        return true;
    }
    
    
    
    /**
     * @method: equalsTime 比较时间是否执行
     * @Description:
     * @param ，currentTime
     * @param 、upTime
     * @return false 是执行 true 是不执行
     * @throws
     */
    public static boolean equalsRedenvelopeTime() {
        long currentTime = System.currentTimeMillis();
        if (Math.abs(currentTime - timeRecording) > REDENVELOPE_REFRESHTIME) {
            timeRecording = System.currentTimeMillis();
            return false;
        }
        return true;
    }
    
    /**
     * @method: equalsTime 比较删除操作时间是否执行
     * @Description:
     * @param: currentTime
     * @param: upTime
     * @return false 是执行 true 是不执行
     * @throws
     */
    public static boolean equalsTime() {
        long currentTime = System.currentTimeMillis();
        if (Math.abs(currentTime - timeRecording) > REFRESHTIME) {
            timeRecording = System.currentTimeMillis();
            return false;
        }
        return true;
    }
    
    /*
     * 录音使用
     */
    public static boolean equalsTime_recoding() {
        long currentTime = System.currentTimeMillis();
        if (Math.abs(currentTime - timeRecording) > REFRESHTIME_RECODING) {
            timeRecording = System.currentTimeMillis();
            return false;
        }
        return true;
    }
    
    public static int REFRESHTIME_NOTE = 3000;
    public static boolean equalsTime_Note() {
        long currentTime = System.currentTimeMillis();
        long result = Math.abs(currentTime - timeRecording);
        if (result > REFRESHTIME_NOTE) {
            timeRecording = System.currentTimeMillis();
            return true;
        }
        return false;
    }
    
	private static long lastClickTime;
	public static boolean isFastDoubleClick() {
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		if (0 < timeD && timeD < 500) {
			return true;
		}
		lastClickTime = time;
		return false;
	}

    
    
    /**
     * 将日期型数据格式化为"yyyy-MM-dd HH:mm:ss"的字符串
     * 
     * @param date 需要被格式化的日期型数据
     * @return 格式化后的"yyyy-MM-dd HH:mm:ss"字符串
     */
    public static String formatLongDate(Date date) {
        return formatDate(date, patternSecondTime);
    }

    /**
     * 将日期型数据格式化为"yyyy-MM-dd HH:mm:ss"的字符串
     * 
     * @param date 需要被格式化的日期型数据
     * @return 格式化后的"yyyy-MM-dd HH:mm:ss"字符串
     */
    public static String formatCNDate(Date date) {
        return formatDate(date, "yyyy年MM月dd日-HH:mm");
    }

    /**
     * 将日期型数据格式化为"yyyy-MM-dd"的字符串
     * 
     * @param date 需要被格式化的日期型数据
     * @return 格式化后的"yyyy-MM-dd"字符串
     */
    public static String formatShortDate(Date date) {
        return formatDate(date, patternDayTime);
    }

    /**
     * 将日期型数据格式化为"yyyy-MM-dd"的字符串
     * 
     * @param date 需要被格式化的日期型数据
     * @return 格式化后的"yyyy-MM-dd"字符串
     */
    private static String formatDate(Date date, String mask) {
        return getSdf(mask).format(date);
    }

    /**
     * 将日期型数据格式化为patternDate的字符串
     * 
     * @param date 需要被格式化的日期型数据
     * @return 格式化后的patternDate字符串
     */
    public static String formatSimpleDate(Date date) {
        return getSdf(patternDate).format(date);
    }

    public static Date parseShortDate(String dateStr) throws ParseException {
        return getSdf(patternDayTime).parse(dateStr);
    }

    /**
     * 20121224转成2012-12-24
     * 
     * @param date
     * @return
     */
    public static String parDate(String date) throws ParseException {
        Date dates = null;
        try {
            if (date != null && !date.equals("")) {
                dates = getSdf(patternDate).parse(date);
            } else {
                return "";
            }
        } catch (Exception e) {
            return "";
        }
        return getSdf(patternDayTime).format(dates);
    }
       // 返回 月/日 2016/12/24
    private static String patternYearMonthDay = "yyyy/MM/dd";

    public static String returnYearMonthDay(long timemillis) {
        if (timemillis < 0) {
            return "";
        }

        return getSdf(patternYearMonthDay).format(new Date(timemillis));
    }
    

    private static String patternLongDate2 = "yyyy年MM月dd日-dd:mm";
    public static Date parseLongDate2(String dateStr) throws ParseException {
        return getSdf(patternLongDate2).parse(dateStr);
    }

    /**
     * 将字符串转成日期
     * 
     * @param dateStr 日期字符串
     * @param formatStr 日期字符串样式
     * @return 日期
     */
    public static Date StringToDate(String dateStr, String formatStr) {
        Date date = null;
        try {
            date = getSdf(formatStr).parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
    
    /*
     * get current time string : "2014-12-01 19:16:45"
     */
    public static String getCurrentTimeString() {
        return getSdf(patternSecondTime).format(new Date(System.currentTimeMillis()));
    }

    /*
     * 生成表单ID: "yyyyMMddHHmmss" applyId-20150505141458
     */
    public static String generateApplyId() {
        return getSdf(patternConvertLong).format(new Date(System.currentTimeMillis()));
    }
    
    /**
      * 函数名     ：compareDate
      * 
      * 功能描述 ：与当前时间比较
      *  
      * 输入参数 ：需要比较的时间。long型的时间戳，精确到秒
      *        
      * 返回值     ： boolean
      *       
      * 异 常        ：无
      * 
      * 创建人     ：WH1407033 吴凯
      * 
      * 日 期        ：2015年6月19日
      */
    public static boolean compareDate(String time){//time传入的是秒级别
       Long currentTime =  System.currentTimeMillis();//毫秒级
       Long timeLong = secondToMillisecond(Long.valueOf(time));
       if(currentTime>timeLong){//
           return false;//token超時了
       }else{
           return true;//token有效
       }
        
    }

    /**
     * 将11位时间戳转换为精确到“日”的时间戳： 2016-03-01 22:56:30 -> 2016-03-01 00:00:00 (都是时间戳)
     * 注意：11位时间戳是精确到s的，标准的unix时间戳为11位, 为了各平台兼容，采用11位
     */
    public static String formatStamp11ToDayStamp11String(long stamp11) {
    	long t = formatStamp11ToDayStamp11Long(stamp11);
    	String str = returnTimeByLong(t * 1000);
    	return String.valueOf(t);
    }
    
    // 服务器有时候传过来的时间是13位 需要转换成10位
    public static long converTo10Time(long stamp) {
    	long time = stamp;
    	String timeString = Long.toString(stamp);
    	if(timeString.length()>10) {
    		time= stamp / 1000;
    	}
    	return time ;
    }

    /**
     * 将11位时间戳转换为精确到“日”的时间戳(11位)： 2016-03-01 22:56:30 -> 2016-03-01 00:00:00 (都是时间戳)
     * 注意：11位时间戳是精确到s的，标准的unix时间戳为 11位, 为了各平台兼容，采用11位
     */
    public static long formatStamp11ToDayStamp11Long(long stamp11) {
    	Date da = new Date(stamp11 * 1000);
    	da.setHours(0);
    	da.setMinutes(0);
    	da.setSeconds(0);

    	return da.getTime() / 1000;
    }

    /**
     * 将11位时间戳转换为精确到“月”的时间戳(11位)： 2016-03-01 22:56:30 -> 2016-03-01 00:00:00 (都是时间戳)
     * 注意：11位时间戳是精确到s的，标准的unix时间戳为 11位, 为了各平台兼容，采用11位
     */
    public static long formatStamp11ToMonthStamp11Long(long stamp11) {
    	Date da = new Date(stamp11 * 1000);
    	da.setDate(1);
    	da.setHours(0);
    	da.setMinutes(0);
    	da.setSeconds(0);

    	return da.getTime() / 1000;
    }
    
    /**
     * 获取今天的时间戳: 2016-03-03 00:00:00 的11位时间戳
     * @return
     */
    public static long getTodayStamp11() {
    	return formatStamp11ToDayStamp11Long(System.currentTimeMillis() / 1000);
    }

    /**
     * 获取年开始时间戳
     * @return
     */
    public static long getYearStartStamp() {
    	Date da = new Date(System.currentTimeMillis());
    	da.setMonth(0);
    	da.setDate(1);
    	da.setHours(0);
    	da.setMinutes(0);
    	da.setSeconds(0);
    	
    	String str = returnTimeByLong(da.getTime());
    	return da.getTime() / 1000;
    }

    /**
     * 获取年结束的时间戳
     * @return
     */
    public static long getYearEndStamp() {
    	Date da = new Date(System.currentTimeMillis());
    	da.setYear(da.getYear()+1);
    	da.setMonth(0);
    	da.setDate(1);
    	da.setHours(0);
    	da.setMinutes(0);
    	da.setSeconds(0);
    	
    	String str = returnTimeByLong(da.getTime());
    	return da.getTime() / 1000;
    }

	 /**
    * 新建重复会议的下次会议时，更新其startTime为下次会议的日期
    * meetingDay : 下次会议的日期 (10位时间戳)， 如 2016-03-04 00:00:00
    * @param meetingDay
    */
	public static long updateNextMeetingStartTime(long meetingDay, long startTime) {
		// 下次会议开始的日期： 2016-03-04 00:00:00
		Calendar nextDay = Calendar.getInstance();
		nextDay.setTimeInMillis(meetingDay * 1000);
		// 本次会议的开始时间： 2016-03-03 09:30:00, 这里需要将startTime更新为下次会议的日期： 2016-03-04
		// 09:30:00
		Calendar start = Calendar.getInstance();
		start.setTimeInMillis(startTime * 1000);
		nextDay.set(Calendar.HOUR_OF_DAY, start.get(Calendar.HOUR_OF_DAY));
		nextDay.set(Calendar.MINUTE, start.get(Calendar.MINUTE));
		nextDay.set(Calendar.SECOND, start.get(Calendar.SECOND));

		// 将下次会议的开始时间保存下来
		return nextDay.getTimeInMillis() / 1000;
	}
	
	/**
	 * 返回带周
	 * 
	 * @param praseTime
	 * @return
	 */
	public static String parseToWeek(long praseTime) {
		String strDate = returnYearMonthDay(praseTime);

		String today =  getCurrentTime("yyyy/MM/dd");
		if (!TextUtils.isEmpty(strDate) && strDate.equals(today)) {
			return "今天";
		}

		long time = parseFormatDate2Milliseconds(strDate, "yyyy/MM/dd");
		Date date = new Date(time);
		return DateToWeek(date);
	}

	/**
	 * 日期变量转成对应的星期字符串
	 * 
	 * @param date
	 * @return
	 */
	public static String DateToWeek(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int dayIndex = calendar.get(Calendar.DAY_OF_WEEK);
		if (dayIndex < 1 || dayIndex > WEEKDAYS) {
			return null;
		}

		return WEEK[dayIndex - 1];
	}
	
	// 返回小时，分钟  
	public static String returnHourMinDate(long timemillis) {
		if (timemillis < 0) {
			return "";
		}
		Date curDate = new Date(timemillis);
		String strRet = "";
		SimpleDateFormat formatterMD = new SimpleDateFormat("MM/dd ",
				Locale.getDefault());
		SimpleDateFormat formatterHM = new SimpleDateFormat(" HH:mm",
				Locale.getDefault());
		strRet = formatterMD.format(curDate) + formatterHM.format(curDate);
		return strRet;
	}

      /** 
      * @method: formatDuartionTime 
      * @Description:将时间转
      * @param ms
      * @return
      * @throws 
      */
    public static String formatDuartionTime(Long ms) {
        Integer ss = 1000;
        Integer mi = ss * 60;
        Integer hh = mi * 60;
        Integer dd = hh * 24;

        Long day = ms / dd;
        Long hour = (ms - day * dd) / hh;
        Long minute = (ms - day * dd - hour * hh) / mi;
        Long second = (ms - day * dd - hour * hh - minute * mi) / ss;
        
        StringBuffer sb = new StringBuffer();
        if(day > 0) {
            sb.append(day+"天");
        }
        if(hour > 0) {
            sb.append(hour+":");
        }
        if(minute >= 0) {
            if(minute<=9){
                sb.append("0"+minute+":"); 
            }else{
                sb.append(minute+":"); 
            }
        }
        if(second >= 0) {
            if(second<=9){
                sb.append("0"+second); 
            }else{
                sb.append(second);  
            }
        }
        return sb.toString();
    }
	/*
	 * 返回转换时间
	 */
	public static String returnMeetingDuration(int time) {
		if (time > 0) {
			int ret = time % 60;
			int result = time / 60;
			if(result > 0){
				if (ret == 0) {
					return result + "小时";
				}else {
					return result + "小时" + ret + "分钟";
				}
				
			}else{
				return  ret + "分钟";
			}
			
		}
		return "";
	}
	
	// 时间转化后为毫秒 2016033115490 
	private static String patternConvertLong = "yyyyMMddHHmmss";
	public static long convertLong(String time) {
		//"2016033115490"
		if(TextUtils.isEmpty(time)){
			return 0;
		}
		Calendar c = Calendar.getInstance();
		try {
			c.setTime(getSdf(patternConvertLong).parse(time));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return c.getTimeInMillis();
	}
	
	/*
	 * 格式化为时间戳（ms）
	 */
	public static long formatTimeStamp11(int year, int month, int day, int hour, int min) {
		try {
			DateFormat df = getSdf(patternHourTime);
			Date date = null;
			String str = String.format("%d-%02d-%02d %02d:%02d", year, month,
					day, hour, min);
			date = df.parse(str);
			long retS = date.getTime(); // 返回S
			return retS;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/*
	 * 返回当年数字
	 */
	public static int returnCurrentYear(){
		Date date = new Date(System.currentTimeMillis());
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int iYear = calendar.get(Calendar.YEAR);
		return iYear;
	}



    /**
     * 时间格式：yyyy-MM-dd HH:mm:ss
     * 时间比较
     * @param kssj 开始时间
     * @param jssj 结束时间
     * @return 0-开始时间 大于 结束时间
     * 1-开始时间 等于 结束时间
     * 2- 开始时间 小于 结束时间
     * -1 - 代表格式不正确
     */
    public static int compareTime(String kssj, String jssj) {
        if (kssj !=null && kssj.length()>0
                && jssj !=null && jssj.length()>0
                && kssj.contains("-") && jssj.contains("-")
                && kssj.contains(":") && jssj.contains(":")
                && kssj.length()==jssj.length()){
            if (kssj.length()==19){
                //包含年月日时分秒
                int year1=Integer.parseInt(kssj.substring(0,4));
                int moth1=Integer.parseInt(kssj.substring(5,7));
                int day1=Integer.parseInt(kssj.substring(8,10));
                int hour1=Integer.parseInt(kssj.substring(11,13));
                int min1=Integer.parseInt(kssj.substring(14,16));
                int second1=Integer.parseInt(kssj.substring(17,19));

                int year2=Integer.parseInt(jssj.substring(0,4));
                int moth2=Integer.parseInt(jssj.substring(5,7));
                int day2=Integer.parseInt(jssj.substring(8,10));
                int hour2=Integer.parseInt(jssj.substring(11,13));
                int min2=Integer.parseInt(jssj.substring(14,16));
                int second2=Integer.parseInt(jssj.substring(17,19));
                if (year1>year2){
                    return 0;
                }else if (year1==year2){
                    if (moth1>moth2){
                        return 0;
                    }else if (moth1==moth2){
                        if (day1>day2){
                            return 0;
                        }else if (day1==day2){
                            if (hour1>hour2){
                                return 0;
                            }else if (hour1==hour2){
                                if (min1>min2){
                                    return 0;
                                }else if (min1==min2){
                                    if (second1>second2){
                                        return 0;
                                    }else if (second1==second2){
                                        return 1;
                                    }else if (second1<second2){
                                        return 2;
                                    }

                                }else if (min1<min2){
                                    return 2;
                                }

                            }else if (hour1<hour2){
                                return 2;
                            }

                        }else if (day1<day2){
                            return 2;
                        }
                    }else if (moth1<moth2){
                        return 2;
                    }

                }else if (year1<year2){
                    return 2;
                }
            }else if (kssj.length()==16){
                //包含年月日时分
                int year1=Integer.parseInt(kssj.substring(0,4));
                int moth1=Integer.parseInt(kssj.substring(5,7));
                int day1=Integer.parseInt(kssj.substring(8,10));
                int hour1=Integer.parseInt(kssj.substring(11,13));
                int min1=Integer.parseInt(kssj.substring(14,16));


                int year2=Integer.parseInt(jssj.substring(0,4));
                int moth2=Integer.parseInt(jssj.substring(5,7));
                int day2=Integer.parseInt(jssj.substring(8,10));
                int hour2=Integer.parseInt(jssj.substring(11,13));
                int min2=Integer.parseInt(jssj.substring(14,16));
                if (year1>year2){
                    return 0;
                }else if (year1==year2){
                    if (moth1>moth2){
                        return 0;
                    }else if (moth1==moth2){
                        if (day1>day2){
                            return 0;
                        }else if (day1==day2){
                            if (hour1>hour2){
                                return 0;
                            }else if (hour1==hour2){
                                if (min1>min2){
                                    return 0;
                                }else if (min1==min2){
                                    return 1;
                                }else if (min1<min2){
                                    return 2;
                                }

                            }else if (hour1<hour2){
                                return 2;
                            }

                        }else if (day1<day2){
                            return 2;
                        }
                    }else if (moth1<moth2){
                        return 2;
                    }

                }else if (year1<year2){
                    return 2;
                }
            }else if (kssj.length()==13){
                //包含年月日时
                int year1=Integer.parseInt(kssj.substring(0,4));
                int moth1=Integer.parseInt(kssj.substring(5,7));
                int day1=Integer.parseInt(kssj.substring(8,10));
                int hour1=Integer.parseInt(kssj.substring(11,13));


                int year2=Integer.parseInt(jssj.substring(0,4));
                int moth2=Integer.parseInt(jssj.substring(5,7));
                int day2=Integer.parseInt(jssj.substring(8,10));
                int hour2=Integer.parseInt(jssj.substring(11,13));
                if (year1>year2){
                    return 0;
                }else if (year1==year2){
                    if (moth1>moth2){
                        return 0;
                    }else if (moth1==moth2){
                        if (day1>day2){
                            return 0;
                        }else if (day1==day2){
                            if (hour1>hour2){
                                return 0;
                            }else if (hour1==hour2){
                                return 1;

                            }else if (hour1<hour2){
                                return 2;
                            }

                        }else if (day1<day2){
                            return 2;
                        }
                    }else if (moth1<moth2){
                        return 2;
                    }

                }else if (year1<year2){
                    return 2;
                }
            }else if (kssj.length()==10){
                //包含年月日
                int year1=Integer.parseInt(kssj.substring(0,4));
                int moth1=Integer.parseInt(kssj.substring(5,7));
                int day1=Integer.parseInt(kssj.substring(8,10));


                int year2=Integer.parseInt(jssj.substring(0,4));
                int moth2=Integer.parseInt(jssj.substring(5,7));
                int day2=Integer.parseInt(jssj.substring(8,10));
                if (year1>year2){
                    return 0;
                }else if (year1==year2){
                    if (moth1>moth2){
                        return 0;
                    }else if (moth1==moth2){
                        if (day1>day2){
                            return 0;
                        }else if (day1==day2){
                            return 1;

                        }else if (day1<day2){
                            return 2;
                        }
                    }else if (moth1<moth2){
                        return 2;
                    }

                }else if (year1<year2){
                    return 2;
                }
            }
        }else {
            return -1;
        }
        return -1;

    }



}
