package com.xt.mobile.terminal.log;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 类名        ：LogService
 * 
 * 描述        ：日志服务，登陆成功进入MainActivity后才会启动日志服务
 * 
 * 创建人    ：WH1408008 tianxianjun
 * 
 * 日期        ：2014-12-9
 *
 */
public class LogServiceManager {
    //是否记录日志的tag信息
    private static final boolean recoredTagInfor = true;    //true:记录,    false:不记录
    
    private static LogServiceManager instance = null;
    //调试开关
    public static final boolean OPEN_LOGSERVICE = true;
    //LogService的打印显示
    public static final boolean OPEN_LOGSERVICE_PRINTSHOW = true;
    //标志logservice是否已经退出
    private static boolean logserviceHasDestroy = false;
    //处理日志message的handler
    private LogServiceHandler logServiceHandler;
    //handler配套的单独运行线程
    private HandlerThread logServiceHandlerThread;
    //检测文件大小的timer
    private Timer logServiceCheckFileSizeTimer;
    
    //日志打印标签
    private static final String LOGSERVICE_TAG = "LogService";
    //日志handler处理的消息type ： 初始化日志系统
    private static final int LOG_INIT_TYPE = 0;
    //日志handler处理的消息type ： 检测日志大小
    private static final int LOG_CHECKFILESIZE_TYPE = 1;
    //日志handler处理的消息type ： 聊天消息日志
    private static final int LOG_MSG_TYPE = 2;
    //日志handler处理的消息type ： 退出日志服务
    private static final int LOG_QUITSERVICE_TYPE = 3;
    
    //日志handler处理的消息type ： 日期转换，需要重新生成日志文件
    private static final int LOG_DATECHANGE_TYPE = 4;
    
    
    //日志信息bundle key
    private static final String LOG_MSG_BUNDLE_LOGINFORKEY = "LogMsg_BundleKey_LogInfor";
    private String LogsDir;
    
    //日志目录总容量大小50M
    private static final int DIR_MAX_SIZE = 1024*1024*50;    //日志目录总容量限制50M
    //日志文件限制的大小
    private static final int FILE_MAX_SIZE = 1024*1024*10;    //单个文件10M
    private static final int TIMER_TIME = 60000;    //文件检测时间60s
    //日志一条最大的字符数
    private static final int MAX_MSG_LENGTH = 3000;
    //一天记录的最大日志个数
    private static final int MAX_ONEDAY_LOGS = 100;
    
    //日志流
    private PrintWriter printWriter = null;
    //当前日志
    private File currentLogFile = null;
    
    //日志创建失败次数
    private int logCreateFailCount = 0;
    private static final int MaxCreateFailCount = 5;
    //logServerBinder,对外接口
    //private LogServerBinder logServerBinder = new LogServerBinder(); 
    
    //压缩文件路径
    String dirLogZipPath = null;
    
    //日期改变监听器
    DateChangeBroadcastReceiver dateChangeBroadcastReceiver = null;
    public static Context context;

    //日志文件名时间
    public static String patternTimeByLong = "yyyyMMdd HH:mm:ss";
    public static String patternDate = "yyyyMMdd";

    public static String patternLogSystemTime = "yyyy-MM-dd HH:mm:ss.SSS";

    //日志路径
    public static final String SDCARD = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static final String appName = "terminal";

    //异常记录
    public static final String intelligentDir="/"+appName+"/"+"/IntelligentAcquisition/CoreDump/";
    public static final String intelligentName="KCountJ.txt";

    // 程序运行模式，true:调试模式 false:发布模式
    public static final boolean APPMODEL_DEBUG = true;

    public static final String TAG = "LogServiceManager";

    //构造函数
    private LogServiceManager(Context context) {
        //构造函数
        this.context=context;
        logServiceHandlerThread = new HandlerThread("LogService_Thread");
        logServiceHandlerThread.start();
        logServiceHandler  = new LogServiceHandler(logServiceHandlerThread.getLooper());
        
        //logservice没有销毁
        logserviceHasDestroy = false;
        //创建日期改变监听器
        dateChangeBroadcastReceiver = new DateChangeBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_DATE_CHANGED);
        context.registerReceiver(dateChangeBroadcastReceiver, intentFilter);
        
        LogServiceManager.d(LOGSERVICE_TAG, "LogService-->saveLogMsg-->onCreate");
    }

    //启动日志服务
    public void startLogService() {
        //发送初始化消息，进行日志初始化
        initLogSystem();
    }
    
    //单实例
    public static LogServiceManager getInstance(Context context) {
        if(instance == null) {
            instance = new LogServiceManager(context);
        }
        
        return instance;
    }

    private String getTag(LogInfor logInfor) {
        return "[" + logInfor.getTag() + "]";
    }

    /*private String getCurrentTimeMs() {
        return "[" + DateUtil.returnLogSystemTime() + "]";
    }*/

    private String getErrorNmber(LogInfor logInfor) {
        return "[" + logInfor.getErrorNumber() + "]";
    }

    private String getClassMethodName(LogInfor logInfor) {
        return "[" + logInfor.getClassName() + "/" + logInfor.getMethodName() + "]";
    }

    private String getLogInfor(LogInfor logInfor) {
        return "[" + logInfor.getMsgInfo() + "]";
    }

    private String getFormatLogInfor(LogInfor logInfor) {
        if(recoredTagInfor) {
            //记录tag信息
            return getTag(logInfor) +  getErrorNmber(logInfor) + getClassMethodName(logInfor) + getLogInfor(logInfor);
        } else {
            //不记录tag信息
            return getErrorNmber(logInfor) + getClassMethodName(logInfor) + getLogInfor(logInfor);
        }
    }
    
    //按照标准格式记录崩溃日志
    public void handleLogInfor_crash(LogInfor logInfor) {
        //格式化消息内容
        String strLogInfor = getFormatLogInfor(logInfor);
        //标准输出显示
        System.out.println(strLogInfor);
        //拼装日志消息格式 ，并处理
        handleCarshInfor(strLogInfor);
    }
    
    //按照标准格式记录错误日志
    public void handleLogInfor_e(LogInfor logInfor) {
        //格式化消息内容
        String strLogInfor = getFormatLogInfor(logInfor);
        
        //以类名为标签，标准输出显示
        Log.e(logInfor.getTag(), strLogInfor);
        
        //存储消息
        handleLogInfor(strLogInfor);
    }
    
    //按照标准格式记录调试日志
    public void handleLogInfor_d(LogInfor logInfor) {
        //格式化消息内容
        String strLogInfor = getFormatLogInfor(logInfor);
        
        //以类名为标签，标准输出显示
        Log.d(logInfor.getTag(),strLogInfor);
        
        //存储消息
        handleLogInfor(strLogInfor);
    }
    
    //按照标准格式记录警告日志
    public void handleLogInfor_v(LogInfor logInfor) {
        //格式化消息内容
        String strLogInfor = getFormatLogInfor(logInfor);
        
        //以类名为标签，标准输出显示
        Log.v(logInfor.getTag(),strLogInfor);
        
        //存储消息
        handleLogInfor(strLogInfor);
    }
    
    //按照标准格式记录infor日志
    public void handleLogInfor_i(LogInfor logInfor) {
        //格式化消息内容
        String strLogInfor = getFormatLogInfor(logInfor);
        
        //以类名为标签，标准输出显示
        Log.i(logInfor.getTag(),strLogInfor);
        
        //存储消息
        handleLogInfor(strLogInfor);
    }
    
    private String getTag(String tag) {
        return "[" + tag + "]";
    }
    
    private String getErrorNmber(String errorNumber) {
        return "[" + errorNumber + "]";
    }
    
    private String getClassMethodName(String className, String methodName) {
        return "[" + className + "/" + methodName + "]";
    }
    
    private String getLogInfor(String msgInfo) {
        return "[" + msgInfo + "]";
    }
    
    private String getFormatLogInfor(String tag, String errorNumber, String className, String methodName, String msgInfo) {
        if(recoredTagInfor) {
            //记录tag
            return getTag(tag) +  getErrorNmber(errorNumber) + getClassMethodName(className,methodName) + getLogInfor(msgInfo);
        } else {
            //不记录tag
            return getErrorNmber(errorNumber) + getClassMethodName(className,methodName) + getLogInfor(msgInfo);
        }
    }
    
    //按照标准格式记录崩溃日志
    public void handleLogInfor_crash(String tag, String errorNumber, String className, String methodName, String msgInfo) {
        //格式化消息内容
        String strLogInfor = getFormatLogInfor(tag,errorNumber, className, methodName, msgInfo);
        //标准输出显示
        System.out.println(strLogInfor);
        //拼装日志消息格式 ，并处理
        handleCarshInfor(strLogInfor);
    }
    
    //按照标准格式记录错误日志
    public void handleLogInfor_e(String tag, String errorNumber, String className, String methodName, String msgInfo) {
        //格式化消息内容
        String strLogInfor = getFormatLogInfor(tag, errorNumber, className, methodName, msgInfo);
        
        //以类名为标签，标准输出显示
        Log.e(tag,strLogInfor);
        
        //存储消息
        handleLogInfor(strLogInfor);
    }
    
    //按照标准格式记录调试日志
    public void handleLogInfor_d(String tag, String errorNumber, String className, String methodName, String msgInfo) {
        //格式化消息内容
        String strLogInfor = getFormatLogInfor(tag, errorNumber, className, methodName, msgInfo);
        
        //以类名为标签，标准输出显示
        Log.d(tag,strLogInfor);
        
        //存储消息
        handleLogInfor(strLogInfor);
    }

    
    //按照标准格式记录infor日志
    public void handleLogInfor_i(String tag, String errorNumber, String className, String methodName, String msgInfo) {
        //格式化消息内容
        String strLogInfor = getFormatLogInfor(tag, errorNumber, className, methodName, msgInfo);
        
        //以类名为标签，标准输出显示
        Log.i(tag,strLogInfor);
        
        //存储消息
        handleLogInfor(strLogInfor);
    }
    
    
    //按照标准格式记录警告日志
    public void handleLogInfor_v(String tag, String errorNumber, String className, String methodName, String msgInfo) {
        //格式化消息内容
        String strLogInfor = getFormatLogInfor(tag, errorNumber, className, methodName, msgInfo);
        
        //以类名为标签，标准输出显示
        Log.v(tag,strLogInfor);
        
        //存储消息
        handleLogInfor(strLogInfor);
    }
    
    
    //处理日志消息
    public void handleLogInfor_crash(String msg) {
        //标准输出显示
        //System.out.println(msg);
        Log.e("Crash",msg);
        //存储消息
        handleCarshInfor(msg);
    }
    //处理日志消息
    public void handleLogInfor_e(String tag, String msg) {
        //标准输出显示
        Log.e(tag, msg);
        
        //存储消息
        handleLogInfor(tag +" ："+ msg);
    }
    
    //处理日志消息
    public void handleLogInfor_d(String tag, String msg) {
        //标准输出显示
        Log.d(tag, msg);
        
        //存储消息
        handleLogInfor(tag +" ："+ msg);

    }
    
    //处理日志消息
    public void handleLogInfor_i(String tag, String msg) {
        //标准输出显示
        Log.i(tag, msg);
        
        //存储消息
        handleLogInfor(tag +" ："+ msg);
    }
    
    //处理日志消息
    public void handleLogInfor_v(String tag, String msg) {
        //标准输出显示
        Log.v(tag, msg);
        //存储消息
        handleLogInfor(tag +" ："+ msg);
    }
    
    //处理日志消息
    public void handleLogInfor_print(String msg) {
        //标准输出显示
        System.out.println(msg);
        //存储消息
        handleLogInfor(msg);
    }
    
    
    
    /**
     * 类名        ：LogServerBinder
     * 
     * 描述        ：log服务的Binder,对外操作的唯一接口
     * 
     * 创建人    ：WH1408008 tianxianjun
     * 
     * 日期        ：2014-12-9
     *
     */
    /*public class LogServerBinder extends Binder {
        //处理日志消息
        public void handleLogInfor_crash(String msg) {
            //标准输出显示
            System.out.println(msg);
            //存储消息
            handleCarshInfor(msg);
        }
        //处理日志消息
        public void handleLogInfor_e(String tag, String msg) {
            //标准输出显示
            Log.e(tag, msg);
            
            //存储消息
            handleLogInfor(tag +" ："+ msg);
        }
        
        //处理日志消息
        public void handleLogInfor_d(String tag, String msg) {
            //标准输出显示
            Log.d(tag, msg);
            
            //存储消息
            handleLogInfor(tag +" ："+ msg);

        }
        
        //处理日志消息
        public void handleLogInfor_i(String tag, String msg) {
            //标准输出显示
            Log.i(tag, msg);
            
            //存储消息
            handleLogInfor(tag +" ："+ msg);
        }
        
        //处理日志消息
        public void handleLogInfor_v(String tag, String msg) {
            //标准输出显示
            Log.v(tag, msg);
            //存储消息
            handleLogInfor(tag +" ："+ msg);
        }
        
        //处理日志消息
        public void handleLogInfor_print(String msg) {
            //标准输出显示
            System.out.println(msg);
            //存储消息
            handleLogInfor(msg);
        }
    }*/
    
    /**
     * 类名        ：LogServiceHandler
     * 
     * 描述        ：日志服务处理handler
     * 
     * 创建人    ：WH1408008 tianxianjun
     * 
     * 日期        ：2014-12-9
     *
     */
    class LogServiceHandler extends Handler {
        public LogServiceHandler(Looper looper) {
            super(looper);

        }

        /**
         * 函数名     ：handleMessage
         * 
         * 功能描述 ：handler消息处理
         *  
         * 输入参数 ： 
         *        msg:需要处理的消息
         * 输出参数 ：无
         * 
         * 返回值     ：无
         * 
         * 异 常        ：无
         * 
         * 创建人     ：WH1408008 tianxianjun
         * 
         * 日 期        ：2014-12-09
         */
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            
            int msgWhat = msg.what;
            switch(msgWhat) {
            case LOG_INIT_TYPE:
                //初始化日志系统
                init();
                break;
                
            case LOG_MSG_TYPE:
                //日志消息，进行存储处理
                saveLogMsg(msg);
                break;
                
            case LOG_CHECKFILESIZE_TYPE:
                // 检测文件大小
                checkLogFileSize();
                break;
                
            case LOG_DATECHANGE_TYPE:
                //日期转变了
                handleDateChange();
                break;
                
            case LOG_QUITSERVICE_TYPE:
                //线程退出
                logServiceHandlerThread.getLooper().quit();
                LogServiceManager.d(LOGSERVICE_TAG, "LogService-->LogServiceHandler-->handleMessage-->quit thread!");
                break;
            }
            
        }
        
    }

    //处理日期转变事件
    private void handleDateChange() {
        LogServiceManager.d(LOGSERVICE_TAG,"handleDateChange-->will create new log file...");
        //1 关闭旧日志 释放文件资源
        try {
            if(printWriter != null) {
                printWriter.flush();
                printWriter.close();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            LogServiceManager.e(LOGSERVICE_TAG, "LogService-->handleDateChange-->error");
        }
        
        //达到了5M,则重新开启新日志
        if(!createLogFile()) {
            LogServiceManager.e(LOGSERVICE_TAG, "LogService-->handleDateChange-->createLogFile fail");
            //创建新日志文件,停止服务，退出
            //stopSelf();
            stopLogService();
        }
    }
    
    public void stopLogService() {
        release();
    }
    
    public void logServiceFail() {
        LogServiceManager.d(LOGSERVICE_TAG, "LogService-->logServiceFail");
        //定时器
        if (logServiceCheckFileSizeTimer != null) {
            logServiceCheckFileSizeTimer.cancel();
            logServiceCheckFileSizeTimer = null;
        }
        
        //释放文件资源
        try {
            if(printWriter != null) {
                printWriter.flush();
                printWriter.close();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            LogServiceManager.d(LOGSERVICE_TAG, "LogService-->onDestroy-->error");
        }
        
    }
    
    private void release() {
        LogServiceManager.d(LOGSERVICE_TAG, "LogService-->onDestroy");
        //logservice已销毁
        logserviceHasDestroy = true;
        
        //定时器
        if (logServiceCheckFileSizeTimer != null) {
            logServiceCheckFileSizeTimer.cancel();
            logServiceCheckFileSizeTimer = null;
        }
        
        //发送handler线程退出消息
        if(logServiceHandler != null && logServiceHandlerThread != null) {
            Message msg = logServiceHandler.obtainMessage(LOG_QUITSERVICE_TYPE);
            msg.sendToTarget();
        }
        
        
        //释放文件资源
        try {
            if(printWriter != null) {
                printWriter.flush();
                printWriter.close();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            LogServiceManager.d(LOGSERVICE_TAG, "LogService-->onDestroy-->error");
        }
        
        //释放对象
        instance = null;
    }
    
    /**
     * 函数名     ：initLogSystem
     * 
     * 功能描述 ：初始化日志系统，发送初始化消息，初始化日志系统
     *  
     * 输入参数 ：无 
     *        
     * 输出参数 ：无
     * 
     * 返回值     ：无
     * 
     * 异 常        ：无
     * 
     * 创建人     ：WH1408008 tianxianjun
     * 
     * 日 期        ：2014-12-13
     */
    private void initLogSystem() {
//        Message msg = logServiceHandler.obtainMessage(LOG_INIT_TYPE);
//        msg.sendToTarget();
        if(!init()) {
            //初始化失败，则稍后再次初始化日志系统
            logCreateFailCount++;
            if(logCreateFailCount >= MaxCreateFailCount) {
                //已超过最大次数
                stopLogService();    //则停止日志服务
                return;
            }
            
            //间隔2s再次尝试初始化日志系统
            logServiceHandler.postDelayed(new InitLogSystemRunable(),2000);
        }
    }
    
    class InitLogSystemRunable implements Runnable {
        @Override
        public void run() {
            //再次初始化日志系统
            initLogSystem();
        }
    }
    
    //检测文件大小，达到固定大小，则重新创建新的文件
    private static int checkCount = 0;
    private void checkLogFileSize() {
        checkCount++;
        if(checkCount >= 10) {
            // 每检查10次显示一次
            checkCount = 0;
            float currentLogFileSize = (float)(currentLogFile.length())/(float)(1024*1024);
            LogServiceManager.d(LOGSERVICE_TAG,"checkLogFileSize , maxFileSize = " + FILE_MAX_SIZE/(1024*1024) + 
                    "M, current size = " + currentLogFileSize+ "M");    
        }
        
        if(currentLogFile.length() >= FILE_MAX_SIZE) {
            LogServiceManager.d(LOGSERVICE_TAG,"checkLogFileSize-->file > FILE_MAX_SIZE"); 
            //1 关闭旧日志 释放文件资源
            try {
                if(printWriter != null) {
                    printWriter.flush();
                    printWriter.close();
                }
                
            } catch (Exception e) {
                e.printStackTrace();
                LogServiceManager.e(LOGSERVICE_TAG, "LogService-->checkLogFileSize-->error");
            }
            
            //达到了5M,则重新开启新日志
            if(!createLogFile()) {
                LogServiceManager.e(LOGSERVICE_TAG, "LogService-->checkLogFileSize-->createLogFile fail");
                //创建新日志文件,停止服务，退出
                //stopSelf();
                stopLogService();
            }
            
        }
    }
    
    /**
     * 函数名     ：getDirSize
     * 
     * 功能描述 ：获取目录大小
     *  
     * 输入参数 ：file:需要检查大小的文件目录
     *        
     * 输出参数 ：无
     * 
     * 返回值     ：目录大小
     * 
     * 异 常        ：无
     * 
     * 创建人     ：WH1408008 tianxianjun
     * 
     * 日 期        ：2014-12-13
     */
    public static double getDirSize(File file) {
        // 判断文件是否存在
        if (file.exists()) {
            // 如果是目录则递归计算其内容的总大小
            if (file.isDirectory()) {
                File[] children = file.listFiles();
                if (children == null || children.length <= 0) {
                    // 无有效数据
                    return 0;
                }
                double size = 0;
                for (File f : children) {
                    size += getDirSize(f);
                }
                return size;
            } else {// 如果是文件则直接返回其大小
                double size = (double) file.length();
                return size;
            }
        } else {
            LogServiceManager.e(LOGSERVICE_TAG,
                    "please check the log path,path error!");
            return 0.0;
        }
    } 
   
    /**
     * 函数名     ：checkLogDirSize
     * 
     * 功能描述 ：校验日志目录总容量
     *  
     * 输入参数 ：无
     *        
     * 输出参数 ：无
     * 
     * 返回值     ：true：目录ok，没有超出容量，false：检查目录失败，超出了目录大小
     * 
     * 异 常        ：无
     * 
     * 创建人     ：WH1408008 tianxianjun
     * 
     * 日 期        ：2014-12-13
     */
    private boolean checkLogDirSize() {
        //容量超过限制则删除多余的日志
        String dirLogsPath = LogsDir;
        File dirFile = new File(dirLogsPath);
        //获取总大小
        double dirSize = getDirSize(dirFile);
        if(dirSize > DIR_MAX_SIZE) {
            //删除除今天之外的所有日志
            if(!DeleteFolder(dirLogsPath)) {
                //删除失败
                return false;
            }
        }
        
        return true;
    }
    

    /**
     * 函数名     ：getLogFileName
     * 
     * 功能描述 ：获取日志文件名称
     *  
     * 输入参数 ：无
     *        
     * 输出参数 ：dirLogsPath:日志目路径
     * 
     * 返回值     ：String 日志文件名称
     * 
     * 异 常        ：无
     * 
     * 创建人     ：WH1408008 tianxianjun
     * 
     * 日 期        ：2014-12-13
     */
    private String getLogFileName(String dirLogsPath) {
        //当前电话号码
        //String currentUserPhoneNumber = LoginHelper.getInstance().getCurrentUserPhone();
        //当天日期
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date curDate = new Date(System.currentTimeMillis());
        //文件名字
        String strFileName = null;
        File file = null;
        
        for(int todayLogFileIndex=1; todayLogFileIndex <= MAX_ONEDAY_LOGS; todayLogFileIndex++) {
            //遍历文件名字
            /*strFileName = currentUserPhoneNumber + "_"  + formatter.format(curDate)
                    + "_" + todayLogFileIndex
                    + ".log";*/
            //V2:TChat2.0
            strFileName = "V2_" + formatter.format(curDate) + "_" + todayLogFileIndex + ".log";
            
            Log.d(LOGSERVICE_TAG,"todayLogFileIndex = " + todayLogFileIndex);
            file = new File(dirLogsPath,strFileName);
            if(!file.exists()) {
                //文件名称可用
                return strFileName;
            } else {
                //日志文件已存在，则判断是否重新创建
                if(file.length() < FILE_MAX_SIZE) {
                    //日志文件已超过容量限制,则索引加1，重新创建
                    return strFileName;
                }
            }
        }
        
        return null;
    }
    
    //获取机器类型
    private String getMachineType() {
        return Build.BRAND + " " + Build.MODEL;
    }
    
    //获取Android版本号
    private String getAndroidVersion() {
        return "AndroidVer" + Build.VERSION.RELEASE;
    }
    
    //获取应用程序版本号
    private String getAppVersion() {
        String appVersion = "appVersion:" + PLogBase.getAppVersion(context);
        return appVersion;
    }
    
    
    //获取日志开始记录信息
    private String getLogheard() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd HH:mm:ss", Locale.getDefault());
        Date curDate = new Date(System.currentTimeMillis());
        
        String machineType = "[" + getMachineType()+ "],";
        String androidVersion = "[" + getAndroidVersion()+ "],";
        String appVersion = "[" + getAppVersion()+ "]";
        
        String logHeard = ".........................................New log begin,time:"
                + formatter.format(curDate) + "........." 
                + machineType +  androidVersion + appVersion +
                ".........................................";
        return logHeard;
    }
    
    private static boolean checkDirs(final String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }

        File file = new File(path);

        if (!file.exists()) {
            //路径不存在
            return file.mkdirs();
        } else {
            //路径存在
            return true;
        }
    }
    
    /**
     * 函数名     ：createLogFile
     * 
     * 功能描述 ：创建日志文件,日志文件名称规则：用户电话号码 _yyyy-MM-dd_HH:mm + .log
     *  
     * 输入参数 ：无
     *        
     * 输出参数 ：无
     * 
     * 返回值     ：true 创建成功，false 创建失败
     * 
     * 异 常        ：无
     * 
     * 创建人     ：WH1408008 tianxianjun
     * 
     * 日 期        ：2014-12-13
     */
    private boolean createLogFile() {
        //日志路径
        String dirLogsPath = LogsDir;
        
        //判断日志目录是否存在
        if(!checkDirs(dirLogsPath)) {
            LogServiceManager.d(LOGSERVICE_TAG, "createLogFile-->checkDirs fail, path = " + dirLogsPath);
            return false;
        }
        
        //获取新日志文件名称
        String strFileName = getLogFileName(dirLogsPath);
        if(strFileName == null) {
            //日志名称获取失败
            return false;
        }
        
        LogServiceManager.d(LOGSERVICE_TAG, "createLogFile-->dirLogFile = " + dirLogsPath + "/" + strFileName);
        
        File file = new File(dirLogsPath,strFileName);
        if(!file.exists()){
            //日志文件不存在，当天第一个日志文件
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                LogServiceManager.e(LOGSERVICE_TAG, "LogService-->createLogFile-->createNewFile fail!");
                return false;
            }
        }
        
        //记录当前的日志file
        currentLogFile = file;
        
        //日志开始记录标记
        String logHeard = getLogheard();
        
        try {
            //追加模式记录日志
            printWriter = new PrintWriter(new BufferedOutputStream(new FileOutputStream(file,true)));
            //记录开始标记
            printWriter.write(logHeard + "\r\n");
            //获取服务器环境
      /*      String serviceConfig = AppAdapter.getInstance().getServiceConfig();
            //记录本机版本和服务器信息
            String hostIp =  (String)MemoryCache.getInstance().getValue(MemoryCache.MstpHost);
            String hostPort = (String)MemoryCache.getInstance().getValue(MemoryCache.MstpPort);*/
            //String appVersion = SystemUtil.getAppVersion(App.getContext());
            
            String deviceID = PLogBase.getDeviceID();
/*            printWriter.write("serviceConfig = " + serviceConfig + "\r\n");
            printWriter.write("HostIP = " + hostIp + "\r\n");
            printWriter.write("hostPort = " + hostPort + "\r\n");*/
            //printWriter.write("AppVersion = " + appVersion + "\r\n");
            printWriter.write("DeviceID = " + deviceID + "\r\n");
            //刷新数据
            printWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
            LogServiceManager.e(LOGSERVICE_TAG, "LogService-->createLogFile-->create printWriter fail!");
            return false;
        }
        
        return true;
    }
    
    /**
     * 函数名     ：init
     * 
     * 功能描述 ：初始化
     *  
     * 输入参数 ：无
     *        
     * 输出参数 ：无
     * 
     * 返回值     ：无
     * 
     * 异 常        ：无
     * 
     * 创建人     ：WH1408008 tianxianjun
     * 
     * 日 期        ：2014-12-13
     */
    private boolean init() {
        //获取日志根目录
        LogsDir = PLogBase.getLogsRootPath();
        LogServiceManager.d(LOGSERVICE_TAG, "LogService-->init-->LogsDir = " + LogsDir);
        
        if(LogsDir == null) {
            //日志目录创建失败
            LogServiceManager.e(LOGSERVICE_TAG, "LogService-->init-->getLogsRootPath fail!");
            //结束日志服务
            logServiceFail();
            return false;
        }
        
        //创建日志文件夹 创建日志文件
        if(!createLogFile()) {
            //创建目录和日志文件失败
            LogServiceManager.e(LOGSERVICE_TAG, "LogService-->init-->createLogFile!");
            //结束日志服务
            logServiceFail();
            return false;
        }
        
        //检查当前目录下日志总量，如果日志总量超过30M则删除非当天的日志
        if(!checkLogDirSize()) {
            LogServiceManager.e(LOGSERVICE_TAG, "LogService-->init-->checkLogDirSize fail!");
            //结束日志服务
            logServiceFail();
            return false;
        }
        
        //创建检测文件大小的定时器
        logServiceCheckFileSizeTimer = new Timer();
        logServiceCheckFileSizeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                //发送检测文件大小的消息
                Message msg = logServiceHandler.obtainMessage(LOG_CHECKFILESIZE_TYPE);
                msg.sendToTarget();
            }
        }, 0,TIMER_TIME); //5s一次
        
        return true;
    }
    
    //记录崩溃消息
    public void handleCarshInfor(String crashMsgInfor) {
        //记录崩溃消息
        if(printWriter != null) {
            printWriter.print(crashMsgInfor + "\r\n");
            printWriter.flush();
        }
    }
    
    /**
     * 函数名     ：handleLogInfor
     * 
     * 功能描述 ：处理日志消息
     *  
     * 输入参数 ：logInfor 日志信息
     *        
     * 输出参数 ：无
     * 
     * 返回值     ：无
     * 
     * 异 常        ：无
     * 
     * 创建人     ：WH1408008 tianxianjun
     * 
     * 日 期        ：2014-12-13
     */
    private void handleLogInfor(String logInfor) {
        if(logserviceHasDestroy || logInfor == null) {
            //已退出
            return;
        }
        
        //判断数据是否可用
        if(printWriter == null || currentLogFile == null) {
            LogServiceManager.e(LOGSERVICE_TAG, "LogService-->handleLogInfor-->printWriter or currentLogFile is null,msg = " + logInfor);
            return;
        }
        
        String infor;
        if(logInfor.length() >= MAX_MSG_LENGTH) {
            //超过了限制，截断处理
            infor =  "[" + PLogBase.returnLogSystemTime() + "]" + " " + logInfor.substring(0, MAX_MSG_LENGTH-1);
        } else {
            infor = "[" + PLogBase.returnLogSystemTime() + "]" + " "  + logInfor;
        }
        //组装消息
        if(logServiceHandler != null && logServiceHandlerThread != null) {
            if(!logServiceHandlerThread.isAlive()) {
                //线程死亡 重新启动
				LogServiceManager.e(LOGSERVICE_TAG, "LogService-->handleLogInfor-->handler thread dead!");
                logServiceHandlerThread = new HandlerThread("LogService_Thread");
                logServiceHandlerThread.start();
                logServiceHandler  = new LogServiceHandler(logServiceHandlerThread.getLooper());
            }
            Message msg = logServiceHandler.obtainMessage(LOG_MSG_TYPE);
            Bundle bundle = new Bundle();
            bundle.putString(LOG_MSG_BUNDLE_LOGINFORKEY, infor);
            msg.setData(bundle);
            //发送到处理队列
            msg.sendToTarget();
        } else {
            //handler异常了
            LogServiceManager.e(LOGSERVICE_TAG, "LogService-->handleLogInfor-->handler is null!");
            //重启handler和线程
            logServiceHandlerThread = new HandlerThread("LogService_Thread");
            logServiceHandlerThread.start();
            logServiceHandler  = new LogServiceHandler(logServiceHandlerThread.getLooper());
        }

    }
    
    /**
     * 函数名     ：saveLogMsg
     * 
     * 功能描述 ：保存日志到日志文件中
     *  
     * 输入参数 ：msg 日志msg
     *        
     * 输出参数 ：无
     * 
     * 返回值     ：无
     * 
     * 异 常        ：无
     * 
     * 创建人     ：WH1408008 tianxianjun
     * 
     * 日 期        ：2014-12-13
     */
    private void saveLogMsg(Message msg) {
        Bundle bundle = msg.getData();
        if(bundle == null) {
            return;
        }
        
        //获取
        String logInfor = bundle.getString(LOG_MSG_BUNDLE_LOGINFORKEY);
        if(logInfor == null) {
            return;
        }
        
        //存储
        if(logInfor != null) {
        	
            printWriter.print(logInfor + "\r\n");
            //1 关闭旧日志 释放文件资源
            try {
                if(printWriter != null) {
                    printWriter.flush();
                }

            } catch (Exception e) {
                e.printStackTrace();
                LogServiceManager.e(LOGSERVICE_TAG, "LogService-->saveLogMsg-->error");
            }
        }
    }
    
    
    /**
     * 函数名     ：DeleteFolder
     * 
     * 功能描述 ：根据路径删除指定的目录或文件，无论存在与否 
     *  
     * 输入参数 ：sPath  要删除的目录或文件 
     *        
     * 输出参数 ：无
     * 
     * 返回值     ：删除成功返回 true，否则返回 false。 
     * 
     * 异 常        ：无
     * 
     * 创建人     ：WH1408008 tianxianjun
     * 
     * 日 期        ：2014-12-13
     */
    public boolean DeleteFolder(String sPath) {
        boolean flag = false;
        File file = new File(sPath);
        // 判断目录或文件是否存在  
        if (!file.exists()) {  // 不存在返回 false  
            return flag;
        } else {
            // 判断是否为文件  
            if (file.isFile()) {  // 为文件时调用删除文件方法  
                return deleteFile(sPath);  
            } else {  // 为目录时调用删除目录方法  
                return deleteDirectory(sPath,true);  
            }  
        }
    }  
    
    /**
     * 函数名     ：deleteFile
     * 
     * 功能描述 ：删除单个文件 
     *  
     * 输入参数 ：sPath    被删除文件的文件名 
     *        
     * 输出参数 ：无
     * 
     * 返回值     ：单个文件删除成功返回true，否则返回false 
     * 
     * 异 常        ：无
     * 
     * 创建人     ：WH1408008 tianxianjun
     * 
     * 日 期        ：2014-12-13
     */
    public boolean deleteFile(String sPath) {
        boolean flag = true;  
        File file = new File(sPath);
        
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date curDate = new Date(System.currentTimeMillis());
        String today = formatter.format(curDate);
        // 路径为文件且不为空则进行删除  
        if (file.isFile() && file.exists()) {
            if(!sPath.contains(today)) {
                //删除非当天的日志
                if(!file.delete()) {
                    //删除失败
                    flag = false;
                }
            }
            
        } else {
            flag = false;
        }
        
        return flag;  
    }
    
    /**
     * 函数名     ：deleteDirectory
     * 
     * 功能描述 ：删除目录（文件夹）以及目录下的文件 
     *  
     * 输入参数 ：sPath 被删除目录的文件路径 
     *        
     * 输出参数 ：无
     * 
     * 返回值     ：目录删除成功返回true，否则返回false 
     * 
     * 异 常        ：无
     * 
     * 创建人     ：WH1408008 tianxianjun
     * 
     * 日 期        ：2014-12-13
     */
    public boolean deleteDirectory(String sPath, boolean isRootDir) {
        boolean flag = true;
        
        //如果sPath不以文件分隔符结尾，自动添加文件分隔符  
        if (!sPath.endsWith(File.separator)) {
            sPath = sPath + File.separator;
        }
        
        File dirFile = new File(sPath);
        //如果dir对应的文件不存在，或者不是一个目录，则退出  
        if (!dirFile.exists() || !dirFile.isDirectory()) {  
            return false;  
        }  
        
        //删除文件夹下的所有文件(包括子目录)  
        File[] files = dirFile.listFiles();
        if(files == null || files.length <= 0) {
            //无有效文件，无需删除
            return true;
        }
        
        for (int i = 0; i < files.length; i++) {  
            //删除子文件  
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());  
                if (!flag) {
                    break;  
                }
            } 
            //删除子目录,目录暂时不删除  
            else {
                flag = deleteDirectory(files[i].getAbsolutePath(),false);  
                if (!flag) break;  
            }
        }  
        if (!flag) {
            return false;
        }
            
        //删除当前目录 
        if(!isRootDir) {
            //不是顶层目录，则删除
            if (dirFile.delete()) {
                return true;  
            } else {  
                return false;  
            }
        }
        
        return flag;
        
    }  
    
    /**
     * 函数名     ：d
     * 
     * 功能描述 ：调试打印函数
     *  
     * 输入参数 ：tag 标签 ， msg 打印内容 
     *        
     * 输出参数 ：无
     * 
     * 返回值     ：无
     * 
     * 异 常        ：无
     * 
     * 创建人     ：WH1408008 tianxianjun
     * 
     * 日 期        ：2014-12-13
     */
    public static void d(String tag, String msg) {
        if(OPEN_LOGSERVICE_PRINTSHOW) {
            //打印开
            Log.d(tag, msg);
        }
    }
    
    /**
     * 函数名     ：e
     * 
     * 功能描述 ：错误打印函数
     *  
     * 输入参数 ：tag 标签 ， msg 打印内容 
     *        
     * 输出参数 ：无
     * 
     * 返回值     ：无
     * 
     * 异 常        ：无
     * 
     * 创建人     ：WH1408008 tianxianjun
     * 
     * 日 期        ：2014-12-13
     */
    public static void e(String tag, String msg) {
        if(OPEN_LOGSERVICE_PRINTSHOW) {
            //打印开
            Log.e(tag, msg);
        }
    }
    
    //关闭日志服务
    private void closeLog() {
        try {
            if(printWriter != null) {
                printWriter.flush();
                printWriter.close();
                printWriter = null;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            LogServiceManager.d(LOGSERVICE_TAG, "LogService-->onDestroy-->error");
        }
    }
    
    //开启日志服务
    private void openLog() {
        //达到了5M,则重新开启新日志
        if(!createLogFile()) {
            LogServiceManager.e(LOGSERVICE_TAG, "LogService-->openLog-->createLogFile fail");
            //创建新日志文件,停止服务，退出
            //stopSelf();
            stopLogService();
        }
    }
    

    
    //删除日志临时压缩文件
    public void delLogByData() {
        //删除zip路径下的所有临时文件，防止重复上传
        if(TextUtils.isEmpty(dirLogZipPath)) {
            //不用删除
            return;
        }
        PLogBase.deleteDirFile(dirLogZipPath);
    }

    
    //日志服务中监听日期改变
    class DateChangeBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Message msg = logServiceHandler.obtainMessage(LOG_DATECHANGE_TYPE);
            msg.sendToTarget();
        }
    }




}

