package com.xt.mobile.terminal.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 彭永顺 on 2019/3/19.
 */
public class TimeUitls {

    /**
     * 定义常量
     * 格式类型
     * 注意HH大写就是24小时，小写就是12小时
     * */
    public static final String DATE_YEAR_MOTH="yyyyMM";
    public static final String DATE_FULL_SYMBOL= "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_YEAR_MOTH_DAY = "yyyy-MM-dd";
    public static final String DATE_FULL_UNSYMBOL = "yyyyMMddHHmmss";
    /**
     * 将毫秒数转换为date型
     * @return date
     */
    public static Date parseLongToDate(){
        long value = getLongSysnTime();
        Date date=new Date(value);
        return date;
    }
    /**
     * 将毫秒数转换为string型
     * @param type
     * @return
     */
    public static String parseLongToString(String type){
        long value = getLongSysnTime();
        SimpleDateFormat df = new SimpleDateFormat(type);
        Date date=new Date(value);
        String time = df.format(date);
        return time;
    }

    /**
     * 获取系统时间
     * @return
     */
    public static long getLongSysnTime()
    {
        long time=System.currentTimeMillis();
        return time;

    }
}
