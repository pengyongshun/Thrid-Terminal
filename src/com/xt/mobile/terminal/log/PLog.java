package com.xt.mobile.terminal.log;

import android.util.Log;


/**
 * @作者： [付欢欢 email：HuanHuanFu@wistronits.com]<br>
 * @版本： [V1.0.0, 2013-1-23]<br>
 * @描述： 日志调试输出<br>
 */
public class PLog extends PLogBase{
    //按照标准格式记录日志
    public static void crash(LogInfor logInfor) {
        handlerLogInfor(LOG_LEVEL_Crash,logInfor);
    }
    
    //按照标准格式记录日志
    public static void e(LogInfor logInfor) {
        handlerLogInfor(LOG_LEVEL_e,logInfor);
    }
    
    //按照标准格式记录日志    
    public static void d(LogInfor logInfor) {
        handlerLogInfor(LOG_LEVEL_d,logInfor);
    }
    
    //按照标准格式记录日志
    public static void v(LogInfor logInfor) {
        handlerLogInfor(LOG_LEVEL_v,logInfor);
    }

    //按照标准格式记录日志
    public static void i(LogInfor logInfor) {
        handlerLogInfor(LOG_LEVEL_i,logInfor);
    }
    
    //按照标准格式记录日志
    public static void crash(String tag, String errorNumber, String className, String methodName, String msgInfo) {
        handlerLogInfor(LOG_LEVEL_Crash,tag,errorNumber,className,methodName,msgInfo);
    }
    
    //按照标准格式记录日志    
    public static void e(String tag, String errorNumber, String className, String methodName, String msgInfo) {
        handlerLogInfor(LOG_LEVEL_e,tag,errorNumber,className,methodName,msgInfo);
    }
    
    //按照标准格式记录日志
    public static void d(String tag, String errorNumber, String className, String methodName, String msgInfo) {
        handlerLogInfor(LOG_LEVEL_d,tag,errorNumber,className,methodName,msgInfo);
    }
    
    //按照标准格式记录日志
    public static void v(String tag, String errorNumber, String className, String methodName, String msgInfo) {
        handlerLogInfor(LOG_LEVEL_v,tag,errorNumber,className,methodName,msgInfo);
    }
    
    //按照标准格式记录日志
    public static void i(String tag, String errorNumber, String className, String methodName, String msgInfo) {
        handlerLogInfor(LOG_LEVEL_i,tag,errorNumber,className,methodName,msgInfo);
    }
    
    
    private static void handlerLogInfor(int logLevel, LogInfor logInfor) {
        if(logLevel>=LOG_LEVEL_d && logLevel<= LOG_LEVEL_i) {
            //低级别日志
            if(!LogServiceManager.APPMODEL_DEBUG) {
                //程序是release模式 不再记录和打印日志
                return;
            }
        }
    
        if(!LogServiceManager.OPEN_LOGSERVICE) {
            //log服务关闭，直接打印输出
            Log.d(logInfor.getClassName() + "_"+ logInfor.getMethodName(), logInfor.getErrorNumber()+ "_" + logInfor.getMsgInfo());
            return;
        }
        
        //log服务开启 根据级别处理日志
        switch(logLevel) {
            case LOG_LEVEL_Crash:
                LogServiceManager.getInstance(mAppContext).handleLogInfor_crash(logInfor);
                break;
            case LOG_LEVEL_e:
                LogServiceManager.getInstance(mAppContext).handleLogInfor_e(logInfor);
                break;
            case LOG_LEVEL_d:
                LogServiceManager.getInstance(mAppContext).handleLogInfor_d(logInfor);
                break;
            case LOG_LEVEL_v:
                LogServiceManager.getInstance(mAppContext).handleLogInfor_v(logInfor);
                break;
            case LOG_LEVEL_i:
                LogServiceManager.getInstance(mAppContext).handleLogInfor_i(logInfor);
                break;
                
            default:
                //默认处理
                Log.d(logInfor.getClassName() + "_"+ logInfor.getMethodName(), logInfor.getErrorNumber()+ "_" + logInfor.getMsgInfo());
                break;
        }
    }
    
    private static void handlerLogInfor(int logLevel, String tag, String errorNumber, String className, String methodName, String msgInfo) {
        if(logLevel>=LOG_LEVEL_d && logLevel<= LOG_LEVEL_i) {
            //低级别日志
            if(!LogServiceManager.APPMODEL_DEBUG) {
                //程序是release模式 不再记录和打印日志
                return;
            }
        }
    
        if(!LogServiceManager.OPEN_LOGSERVICE) {
            //log服务关闭，直接打印输出
            Log.d(className + "_"+ methodName, errorNumber+ "_" + msgInfo);
            return;
        }
        
        //log服务开启 根据级别处理日志
        switch(logLevel) {
            case LOG_LEVEL_Crash:
                LogServiceManager.getInstance(mAppContext).handleLogInfor_crash(tag,errorNumber,className,methodName,msgInfo);
                break;
            case LOG_LEVEL_e:
                LogServiceManager.getInstance(mAppContext).handleLogInfor_e(tag,errorNumber,className,methodName,msgInfo);
                break;
            case LOG_LEVEL_d:
                LogServiceManager.getInstance(mAppContext).handleLogInfor_d(tag,errorNumber,className,methodName,msgInfo);
                break;
            case LOG_LEVEL_v:
                LogServiceManager.getInstance(mAppContext).handleLogInfor_v(tag,errorNumber,className,methodName,msgInfo);
                break;
            case LOG_LEVEL_i:
                LogServiceManager.getInstance(mAppContext).handleLogInfor_i(tag,errorNumber,className,methodName,msgInfo);
                break;
                
            default:
                //默认处理
                Log.d(tag,className + "_" + methodName + "_" + errorNumber+ "_" + msgInfo);
                break;
        }
    }
    
    
}