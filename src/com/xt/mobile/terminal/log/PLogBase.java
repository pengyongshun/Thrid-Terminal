package com.xt.mobile.terminal.log;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class PLogBase {

    //app context
    protected static Context mAppContext = LogServiceManager.context;


    //日志级别
    protected static final int LOG_LEVEL_Crash = 1;
    protected static final int LOG_LEVEL_e = 2;
    protected static final int LOG_LEVEL_d = 3;
    protected static final int LOG_LEVEL_v = 4;
    protected static final int LOG_LEVEL_i = 5;



    /**
     * 锁对象
     */
    private static final Object lockObj = new Object();

    //////////////////////////////////////Log.d ///////////////////////////////

    /**
     * @描述：DEBUG级日志 <br>
     * @param msg
     */
    public static void d(String msg) {
        d(LogServiceManager.TAG, msg);
    }

    public static void d(String tag, String msg) {
        if(!LogServiceManager.APPMODEL_DEBUG) {
            //程序是release模式 不再记录和打印日志
            return;
        }
        
        //判断日志记录方式
        if(LogServiceManager.OPEN_LOGSERVICE) {
            //log服务开启 处理日志
            LogServiceManager.getInstance(mAppContext).handleLogInfor_d(tag,msg);
        } else {
            //log服务关闭
            Log.d(tag, msg);
        }

    }

    public static void d(String tag, int msg) {
        d(tag, String.valueOf(msg));
    }

    public static void print(String msg) {
        if(!LogServiceManager.APPMODEL_DEBUG) {
            //程序是release模式 不再记录和打印日志
            return;
        }
        //判断日志记录方式
        if (LogServiceManager.OPEN_LOGSERVICE) {
            //处理日志
            LogServiceManager.getInstance(mAppContext).handleLogInfor_print(msg);
            
        } else {
            Log.d(LogServiceManager.TAG,msg);
        }
    }
    //////////////////////////////////////Log.e ///////////////////////////////
    /**
     * @描述：ERROR级日志 <br>
     * @param msg
     */
    public static void e(String msg) {
        e(LogServiceManager.TAG, msg);
    }

    public static void e(String tag, String msg) {
        if(!LogServiceManager.APPMODEL_DEBUG) {
            //程序是release模式 不再记录和打印日志
            return;
        }
        
        //判断日志记录方式
        if (LogServiceManager.OPEN_LOGSERVICE) {
            //处理日志
            LogServiceManager.getInstance(mAppContext).handleLogInfor_e(tag, msg);

        } else {
            Log.e(tag, msg);

        }
    }
    //////////////////////////////////////Log.i ///////////////////////////////
    /**
     * @描述： INFO级日志<br>
     * @param msg
     */
    public static void i(String msg) {
        i(LogServiceManager.TAG, msg);
    }

    public static void i(String tag, String msg) {
        if(!LogServiceManager.APPMODEL_DEBUG) {
            //程序是release模式 不再记录和打印日志
            return;
        }
        
        //判断日志记录方式
        if (LogServiceManager.OPEN_LOGSERVICE) {      
            //处理日志
            LogServiceManager.getInstance(mAppContext).handleLogInfor_i(tag, msg);

        } else {
            Log.i(tag, msg);
        }
    }
    //////////////////////////////////////Log.v ///////////////////////////////
    /**
     * @描述： <br>
     * @param msg
     */
    /*public static void v(String msg) {
        v(WLHCZRYFLAG, msg);
    }*/

    public static void v(String tag, String msg) {
        if(!LogServiceManager.APPMODEL_DEBUG) {
            //程序是release模式 不再记录和打印日志
            return;
        }
        
        //判断日志记录方式
        if (LogServiceManager.OPEN_LOGSERVICE) {     
            //处理日志
            LogServiceManager.getInstance(mAppContext).handleLogInfor_v(tag, msg);

        } else {
            Log.v(tag, msg);

        }
    }
    
    public static void setContext(Context ctx) {
        mAppContext = ctx.getApplicationContext();
    }

    
    /**
     * 打印到Sdcard，存入崩溃信息 added  by chenwei 
     * 函数重构，原函数内容放到了toDumpFile中，用于区分崩溃日志和捕获异常记 录日志
     * 
     * @param msg
     */
    public static void toCrashFile(final String msg) {
        toDumpFile(msg, 2);
    }



    /**
     * 将原来的toCrashFile内容抽取出来，构成该函数，增加一个类型参数用于支持
     * 区分崩溃日志和捕获异常记录日志。
     * 
     * @param msg
     * @param flag
     */
    private static void toDumpFile(String msg, int flag) {
        String logDir = getLogsRootPath();

        String dataStr = msg == null ? "" : msg;
        byte[] data = null;

        try {
            data = dataStr.getBytes("gb2312");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
            return ;
        }

        if (data == null) {
            return;
        }

        String basePath = logDir + "CoreDump/";
        File dir = new File(basePath);
        if(!dir.exists()){
            dir.mkdirs(); 
        }
        String fileName = GetCrashFileName(flag);
        
        String filePath = basePath +fileName;
        
        File file = new File(dir,fileName+".log");
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return ;
            }
        }

        // 写.log文件
        writeLogFile(data, filePath + ".log");
        
        file = new File(dir,fileName+".log.gz");
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return ;
            }
        }

        // 写.zip文件
        writeZipFile(data, filePath + ".log.gz", fileName + ".log");

        File newfile = new File(filePath + ".log");
        newfile.delete();

        if (flag != 3) {
            increaseCrashCount();
        }
        
        //将崩溃信息记录到log日志中
        if(LogServiceManager.OPEN_LOGSERVICE) {
            //处理日志
            LogServiceManager.getInstance(mAppContext).handleLogInfor_crash(msg);
        }
    }
    
    
    public static String GetCrashFileName(int flag) {
        StringBuilder sb = new StringBuilder(128);
        sb.append("CommunityCollection");
        sb.append('_');
        sb.append(getAppVersion(mAppContext));// 版本号
        sb.append('_');
        sb.append(replaceUnderline(Build.MODEL));// 机型
        sb.append('_');
        sb.append(replaceUnderline(Build.VERSION.RELEASE));// 固件版本
        sb.append('_');
        if (flag == 2) {
            sb.append('_');
            // 年月日时分秒:yyyyMMddHHmmss
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            sb.append(dateFormat.format(new Date(System.currentTimeMillis())));// 日期
            sb.append('_');
            sb.append("java");
            return sb.toString();
        }
        else if (flag == 3) {
            sb.append('_');
            // 年月日时分秒:yyyyMMddHHmmss
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            sb.append(dateFormat.format(new Date(System.currentTimeMillis())));// 日期
            sb.append('_');
            sb.append("java_es");
            return sb.toString();
        }
        return sb.toString();
    }



    /**
     * 获取日期字符串
     *
     * @param formatFalg 1:带时分秒 2:不带时分秒
     * @return
     */
    public static String getNow(int formatFalg) {
        if (formatFalg == 1) {
            return getSdf(LogServiceManager.patternTimeByLong).format(new Date(System.currentTimeMillis()));
        } else {
            return getSdf(LogServiceManager.patternDate).format(new Date(System.currentTimeMillis()));
        }
    }

       /**
     * 存放不同的日期模板格式的sdf的Map
     */
    private static Map<String, ThreadLocal<SimpleDateFormat>> sdfMap = new HashMap<String, ThreadLocal<SimpleDateFormat>>();

    /**
     * 返回一个ThreadLocal的sdf,每个线程只会new一次sdf
     *
     * @param pattern
     * @return
     */
    public static SimpleDateFormat getSdf(final String pattern) {
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


    // 获取日志目录
    public static String getLogsRootPath() {
        File sdCardFile = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            sdCardFile = new File(LogServiceManager.SDCARD);

        } else {
            sdCardFile = Environment.getDataDirectory();
        }


        String path = sdCardFile.getPath() + "/" + LogServiceManager.appName + "/logs";

        return  createFileDir(path);
    }

//
//    /**
//     * @method: createDir
//     * @Description: create folder by specify path
//     * @param path
//     * @return
//     * @throws
//     */
//    public static boolean createDir(final String path) {
//        boolean result = false;
//
//        if (TextUtils.isEmpty(path)) {
//            return result;
//        }
//
//        File cache = new File(path);
//        if (cache.isFile()) {
//            Logi("WiCacheTools -- createDir -- error : " + path
//                    + " is a file not cache folder, delete it and reCreate dir ... ");
//            cache.delete();
//        }
//
//        if (!cache.exists()) {
//            result = cache.mkdirs();
//        } else {
//            //存在返回true
//            return true;
//        }
//
//        return result;
//    }


    /**
     * 创建文件目录
     */
    public static String createFileDir(String fileDir) {
        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED))
        { // 检测sd是否可用
            Log.i("TestFile", "SD card is not avaiable/writeable right now.");
            return "";
        }
        // 新建自己存放图片的目录

        File file = new File(fileDir);
        if( !file.exists() )
            file.mkdirs();
        else if( !file.isDirectory() && file.canWrite() ){
            file.delete();
            file.mkdirs();
        }
        return file.getAbsolutePath() ;
    }


    private static void Logi(String msg) {
        PLog.print(msg);
    }


    public final static void writeLogFile(byte[] data, String filePath) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filePath, false);
            fos.write(data);
            fos.flush();
            fos.close();
            fos = null;
        } catch (Exception e) {

        } finally {
            if (fos != null) {
                try {
                    fos.close();
                    fos = null;
                } catch (Exception e1) {

                }
            }
        }
    }


    public final static void writeZipFile(byte[] data, String filePath, String entryName) {
        ZipOutputStream fos = null;
        try {
            fos = new ZipOutputStream(new FileOutputStream(filePath, false));
            fos.setMethod(ZipOutputStream.DEFLATED);
            ZipEntry zipEntry = new ZipEntry(entryName);
            fos.putNextEntry(zipEntry);
            fos.write(data);
            fos.flush();
            fos.closeEntry();
            fos.finish();
            fos.close();
            fos = null;
        } catch (Exception e) {

        } finally {
            if (fos != null) {
                try {
                    fos.close();
                    fos = null;
                } catch (Exception e1) {

                }
            }
        }
    }


    public final static void increaseCrashCount() {
        // KCountJ.txt 如果不存在则创建，并填写1，如果存在则取出内容+1
        File file = new File(LogServiceManager.SDCARD +  LogServiceManager.intelligentDir + LogServiceManager.intelligentName);

        if (!file.exists()) {
            createFileDir(file.getPath());
            // 第一次文件写1
            writeCountStr(1);
        } else {

            int count = 0;
            count = readCount();

            // 数量增1
            count = count <= 0 ? 1 : count + 1;

            writeCountStr(count);
        }

        return;
    }

    public final static void writeCountStr(int count) {
        File file = new File(LogServiceManager.SDCARD + LogServiceManager.intelligentDir + LogServiceManager.intelligentName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(count);
            fos.close();
            fos = null;
        } catch (Exception e) {

        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public final static int readCount() {
        File file = new File(LogServiceManager.SDCARD +  LogServiceManager.intelligentDir + LogServiceManager.intelligentName);

        FileInputStream fis = null;
        int Count = 0;

        try {
            fis = new FileInputStream(file);
            Count = fis.read();
            fis.close();
            fis = null;

        } catch (Exception e) {
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                    fis = null;
                    file = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return Count;
    }


    public static String getAppVersion(Context ctx) {
        PackageInfo pkg;
        String version = "";
        try {
            pkg = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
            version = pkg.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return version;
    }

    public static String replaceUnderline(String src) {
        if (TextUtils.isEmpty(src)) {
            return "-";
        }

        return src.replaceAll("\\_", "-");
    }


    /**
     * 递归删除目录下的所有文件
     * @paramdir将要删除的文件目录
     * @return boolean Returns "true" if all deletions were successful.
     *                 If a deletion fails, the method stops attempting to
     *                 delete and returns "false".
     */
    public static  boolean deleteDirFile(String filePath) {
        deleteDirectory(filePath);
        return true;
    }

    /**
     * 删除文件夹以及目录下的文件
     * @param   filePath 被删除目录的文件路径
     * @return  目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String filePath) {
        boolean flag = false;
        // 如果filePath不以文件分隔符结尾，自动添加文件分隔符
        if (!filePath.endsWith(File.separator)) {
            filePath = filePath + File.separator;
        }
        File dirFile = new File(filePath);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        flag = true;
        File[] files = dirFile.listFiles();
        if (files == null || files.length <= 0) {
            // 无有效数据，不需要复制
            return true;
        }
        if (files != null && files.length > 0) {
            // 遍历删除文件夹下的所有文件(包括子目录)
            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile()) {
                    // 删除子文件
                    String path = (files[i].getAbsolutePath());
                    File file = new File(path);
                    if (file.isFile() && file.exists()) {
                        flag = file.delete();
                    } else {
                        flag = false;
                    }
                    if (!flag) {
                        break;
                    }
                } else {
                    // 删除子目录
                    flag = deleteDirectory(files[i].getAbsolutePath());
                    if (!flag) {
                        break;
                    }
                }
            }
        }
        if (!flag) {
            return false;
        }
        return true;
    }


    public static String returnLogSystemTime() {
        String str = PLogBase.getSdf(LogServiceManager.patternLogSystemTime).format(new Date(System.currentTimeMillis()));
        return str;
    }



    /*
 * 获取android设备ID, 如果不足16位，自动在后面补位
 */
    public static String getDeviceID() {
        // 调用系统接口 不能保证这个值一定不为空
        String androidID = Settings.Secure.getString(mAppContext.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        // PLog.d(WiControllerConstants.ADDORUPDATEFLAG, "getDeviceID-->androidID = " +
        // androidID);

        // TODO
        if (TextUtils.isEmpty(androidID)) {
            // 生成随机AndroidID
            androidID = getServerSessionKeyRandom();
        }

        // 保证不为 null
        androidID = (androidID == null) ? "" : androidID;

        // 判断一下，如果少于16位，在后面补位
        int srcLen = androidID.length();
        if (srcLen < 16) {
            for (int num = 16 - srcLen; num > 0; num--) {
                androidID += (num % 10);
            }
        }
        return androidID;
    }


    /**
     * getServerSessionKeyRandom get 16位的随机key added by tianxianjun
     *
     * @param
     * @return
     */
    private static String getServerSessionKeyRandom() {
        // modified by ChenWei begin 2015-10-29  去掉随机数之间休眠的时间  加快速度
        long currentTime = System.currentTimeMillis();
        // 拼装长度为16的密码
        String strRandom1 = getFixLenthString(6, currentTime);
        String strRandom2 = getFixLenthString(6, currentTime + 10);
        String strRandom3 = getFixLenthString(4, currentTime + 20);

        return strRandom1 + strRandom2 + strRandom3;
        // modified by ChenWei end 2015-10-29  去掉随机数之间休眠的时间  加快速度
    }
    /*
     * 返回长度为【strLength <= 7】的随机数，在前面补0
     */
    private static String getFixLenthString(int strLength, long time) {

        Random rm = new Random(time);

        // 获得随机数
        double pross = (1 + rm.nextDouble()) * Math.pow(10, strLength);

        // 将获得的获得随机数转化为字符串
        String fixLenthString = String.valueOf(pross);

        // 返回固定的长度的随机数
        return fixLenthString.substring(1, strLength + 1);
    }

}
