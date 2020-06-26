package com.xt.mobile.terminal.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;


import com.xt.mobile.terminal.XTApplication;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * 类名        ：SystemUtil
 *
 * 描述        ：尺寸转换工具
 *
 * 创建人    ：pengyongshun
 *
 * 日期        ：2017-07-28
 *
 * */
public class SystemUtil {
    public static int messageSerial = 1;
    private static String sIMEI = null;
    private String oSInforRandom = null;
    public static final String SCREEN_540_960 = "540*960";// 屏幕尺寸
    public static final String SCREEN_1080_1920 = "1080*1920";// 屏幕尺寸
    public static final String SCREEN_480_854 = "480*854";// 屏幕尺寸
    /*    private OSInfo osInfor = null;
        private DevInfo devInfo = null;
        private LoginInfo loginInfo = null;
        private SimpleOSInfor simpleOSInfor = null;*/
    private static SystemUtil mInstance;
    private static int screenW;
    private static int screenH;

    public static final String CURRENCY_FEN_REGEX = "\\-?[0-9]+";

    //生成随机加密内容
    private static String strRandom = "@#$%^&*!+_-~√≈¨ˆ¨†πø∑œƒß∫";
    private static Random random = new Random();
    private static int length = 0;
    private static int index = 0;


    /**
     * dp转换为px
     * @param context
     * @param dipValue
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * px转换为dp
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    // private static float screenDensity;

    /**
     * 获取屏幕宽度和高度（自定义）
     * @param mActivity
     */
    public static int [] initScreen(Activity mActivity) {
        DisplayMetrics metric = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(metric);
        screenW = metric.widthPixels;
        screenH = metric.heightPixels;
        int [] wh=new int[2];
        wh[0]=screenW;
        wh[1]=screenH;
        return wh;
    }

    private static ActivityManager am = null;

    public SystemUtil() {
        am = (ActivityManager) XTApplication.getmAppContext().getSystemService(Context.ACTIVITY_SERVICE);
    }


    public static SystemUtil getInstance() {
        return mInstance == null ? mInstance = new SystemUtil() : mInstance;
    }

    /**
     * 程序是否在前台运行
     *
     * @return
     */
    public static boolean isAppOnForeground() {
        // Returns a list of application processes that are running on the
        // device

        ActivityManager activityManager = (ActivityManager) XTApplication.getmAppContext().getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = XTApplication.getmAppContext().getApplicationContext().getPackageName();

        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        if (appProcesses == null)
            return false;

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }

        return false;
    }


    /**
     * 获取屏幕像素
     * @param activity
     * @return
     */
    public static int getDpi(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        int height = 0;
        @SuppressWarnings("rawtypes")
        Class c;
        try {
            c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, dm);
            height = dm.heightPixels;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return height;
    }

    /**
     * 获取屏幕宽度和高度
     * @param poCotext
     * @return
     */
    public static int[] getScreenWH(Context poCotext) {
        WindowManager wm = (WindowManager) poCotext
                .getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        return new int[]{width, height};
    }

    public static int getVrtualBtnHeight(Context poCotext) {
        int location[] = getScreenWH(poCotext);
        int realHeiht = getDpi((Activity) poCotext);
        int virvalHeight = realHeiht - location[1];
        return virvalHeight;
    }

    /**
            *将dip转换为px
    *
            *
    @param
    dipValue
    *@return
            *//*

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    *//**
            *将px转换为dip
    *
            *
    @paramdipValue
    *@return
            *//*

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    *//**
            *获取是否在当前Activity
    *
            *
    @param
    context
    *
    @param
    *@return
            *//*

    public static boolean checkTopActivity(Context context, String compName) {
        if (am == null) {
            am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        }

        List<RunningTaskInfo> appTask = am.getRunningTasks(1);
        if (appTask != null) {
            String simName = appTask.get(0).topActivity.getShortClassName().replace(".", "");
            if (compName.equals(simName)) {
                return true;
            }
        }
        return false;
    }

    *//***
            *检查是否该尺寸屏幕
    *
            *
    @method :checkScreen
    *
    @Description :TODO
    *
    @param
    s
    *@return
            *@throws
            *//*

    public static boolean checkScreen(String s) {
        String ojectScreen = (Integer) MemoryCache.getInstance().getValue(MemoryCache.DeviceWidth)
                + "*" +
                (Integer) MemoryCache.getInstance().getValue(MemoryCache.DeviceHeight);
        if (ojectScreen.equals(s)) {
            return true;
        }
        return false;
    }

    *//**
            *
    Fetch phone
    's imei. added by chenwei try-catch to deal with permission
            *
    deny exception
    .
            *
            *
    @param
    context
    *@return
            *//*

    public static String getImei(Context context) {
        if (!TextUtils.isEmpty(sIMEI))
            return sIMEI;

        if (context != null) {
            try {
                TelephonyManager telephonyManager = (TelephonyManager) context
                        .getSystemService(Context.TELEPHONY_SERVICE);
                sIMEI = telephonyManager.getDeviceId();
            } catch (SecurityException e) {
                sIMEI = "";
            }
        }

        if (sIMEI == null) {
            sIMEI = "";
        }
        return sIMEI;
        // return "252339447";
    }

    public void resetOSInforRandom() {
        osInfor = null;
        oSInforRandom = null;
        simpleOSInfor = null;
    }

    *//**
            *
    getSystemInfor get
    system infor
    added by
    tianxianjun
    *
            *
    @param
    *@return
            *//*

    public OSInfo getSystemInfor() {
        if (osInfor != null) {
            return osInfor;
        }

        // 生成新的osInfor对象
        osInfor = new OSInfo();
        // 设置随即种子
        // Random random = new Random(System.currentTimeMillis());
        osInfor.setOperatePlatform("Android");
        // 系统版本
        osInfor.setoS(android.os.Build.VERSION.RELEASE);
        // 手机型号
        osInfor.setMachineType(android.os.Build.MODEL);
        // 获取IMIE
        osInfor.setIMEI(getImei(AppAdapter.getInstance().getContext()));
        osInfor.setlVersion(getAppVersion(AppAdapter.getInstance().getContext()));

        return osInfor;
    }

    public DevInfo getSystemDevInfo() {
        if (devInfo != null) {
            return devInfo;
        }

        // 生成新的osInfor对象
        devInfo = new DevInfo();

        devInfo.setOsType("Android");
        // 系统版本
        devInfo.setDevType("Phone");
        // 手机型号
        devInfo.setDevModel(android.os.Build.MODEL);
        //设置分辨率
        devInfo.setResolution(screenW + "*" + screenH);
        //设置CPU
        devInfo.setCpu(android.os.Build.CPU_ABI);
        return devInfo;
    }

    public LoginInfo getLoginInfo() {
        Context ctx = AppAdapter.getInstance().getContext();
        if (TBaseApp.getUploadInfoMgr() != null) {
            long loginBegin = TBaseApp.getUploadInfoMgr().getLoginBeginTime();
            long loginEnd = TBaseApp.getUploadInfoMgr().getLoginEndTime();
            loginInfo = new LoginInfo();
            loginInfo.setAppVer(SystemUtil.getAppVersion(ctx));
            loginInfo.setOsVersion("Android" + android.os.Build.VERSION.RELEASE);
            loginInfo.setNetWork(NetStatusUtil.GetNetworkType(ctx));
            loginInfo.setLoginTime(loginEnd);
            loginInfo.setLoginCost((int) (loginEnd - loginBegin));
            loginInfo.setLocation(getLocation(ctx));
        } else {
            PLog.d("SystemUtil", "getLoginInfo not install uploadinfo module");
        }

        return loginInfo;
    }

//    /**
//     * @param context
//     * @return
//     */
//    //获取经纬度
//    public WiLocation getLocation(Context context) {
//        //获取地理位置管理器
//        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
//        //获取所有可用的位置提供器
//        String locationProvider = null;
//        double latitude = 0;//纬度
//        double longitude = 0;//经度
//        List<String> providers = locationManager.getProviders(true);
//        if (providers.contains(LocationManager.GPS_PROVIDER)) {
//            //如果是GPS
//            locationProvider = LocationManager.GPS_PROVIDER;
//        } else if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
//            //如果是Network
//            locationProvider = LocationManager.NETWORK_PROVIDER;
//        } else {
//            PLog.e("SystemUtil" + "getLocation" + "没有可用的位置提供器");
//        }
//        if (locationProvider != null) {
//            PLog.e("SystemUtil" + "locationProvider" + "没有可用的locationProvider");
//            //获取Location
//            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//                return TODO;
//            }
//            Location location = locationManager.getLastKnownLocation(locationProvider);
//            if(location!=null){
//                latitude =location.getLatitude();
//            	longitude =location.getLongitude();
//            	PLog.e("SystemUtil" +"latitude:"+latitude +"longitude:" + longitude);
//            }
//		}
//
//		WiLocation wiLocation = new WiLocation();
//		wiLocation.setLat(Double.toString(latitude));
//		wiLocation.setLog(Double.toString(longitude));
//		return wiLocation;
//
//    }

//    //获取用户信息
//    public UserInformation getUserInformation(long UAId) {
//        //用户信息数据对象
//        UserInformation userInformation= new UserInformation();
//
//        long validUAId = 0 ;
//        if(UAId > 0) {
//            //查询其他用户的id
//            validUAId = UAId;
//        } else {
//            //查询当前用户自身信息
//            validUAId = getUAID();
//
//        }
//        int companyId = TBaseApp.getUserState().getCurrentCompanyId();
//        //查询通讯信息
//        ContactBaseInfor contactBaseInfor = null;
//		if (TBaseApp.getContactsMgr() != null) {
//			contactBaseInfor = TBaseApp.getContactsMgr().getContactBaseInfo(companyId, validUAId);
//		}
//        if(contactBaseInfor == null) {
//            //查询人员不存在
//            return null;
//        }
//        //设置用户UAId
//        userInformation.setUAId(validUAId);
//        //设置用户名称
//        userInformation.setUserName(contactBaseInfor.getuName());
//        //设置公司ID
//        userInformation.setCpyId(companyId);
//        //设置公司名称
//        userInformation.setCpyName(TBaseApp.getUserState().getCurrentCompanyName());
//        //设置电话号码
//        userInformation.setuPhone(contactBaseInfor.getuPhone());
//        //返回查询的用户信息
//        return userInformation;
//    }
//

    /**
     * getRandom get 随即数字符串(1000以内) added by tianxianjun
     * 
     * @param
     * @return
     */
    public static String getRandom() {
        Random random = new Random(System.currentTimeMillis());
        int rand = random.nextInt();
        if (rand != Integer.MIN_VALUE) {
            return Math.abs(rand) % 1000 + "";
        } else {
            return Math.abs(Integer.MIN_VALUE) % 1000 + "";
        }

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

    /**
     * getServerSessionKeyRandom get 16位的随机key added by tianxianjun
     * 
     * @param
     * @return
     */
    public static String getChatSessionKeyRandom() {
        return getServerSessionKeyRandom();
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
        String strRandom1 = getFixLenthString(6,currentTime);
        String strRandom2 = getFixLenthString(6,currentTime+10);
        String strRandom3 = getFixLenthString(4,currentTime+20);

        return strRandom1 + strRandom2 + strRandom3;
     // modified by ChenWei end 2015-10-29  去掉随机数之间休眠的时间  加快速度
    }

    public static String getAppVersion(Context ctx) {
        PackageInfo pkg;
        String version = "";
        try {
            pkg = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
            version = pkg.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        return version;
    }

    public static String getAppName(Context ctx) {
        PackageInfo pkg;
        String appName = "";

        try {
            pkg = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
            appName = pkg.applicationInfo.loadLabel(ctx.getPackageManager()).toString();

        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        return appName;
    }

    /*
     * sort string arrays
     */
    public static String[] sortArrays(String[] array) {
        Comparator<? super String> comp = new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                String str1 = (lhs == null) ? "" : lhs;
                String str2 = (rhs == null) ? "" : rhs;

                return str1.compareTo(str2);
            }
        };

        Arrays.sort(array, comp);

        return array;
    }

    /*
     * 获取android设备ID, 如果不足16位，自动在后面补位
     */
    public static String getDeviceID() {
        // 调用系统接口 不能保证这个值一定不为空
        String androidID = Settings.Secure.getString(XTApplication.getmAppContext().getContentResolver(),
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
      * 函数名     ：changeF2Y
      * 
      * 功能描述 ：分转换为元
      *  
      * 输入参数 ：
      *        
      * 返回值     ：
      *        String
      * 异 常        ：无
      * 
      * 创建人     ：WH1407033 吴凯
      * 
      * 日 期        ：2015年9月14日
      */

    public static String changeF2Y(int amount) {
        try{
            String amountString = String.valueOf(amount);
            if(!amountString.matches(CURRENCY_FEN_REGEX)) {  
                return "";
            }    
            return BigDecimal.valueOf(Long.valueOf(amount)).divide(new BigDecimal(100),2, BigDecimal.ROUND_HALF_EVEN).toString();
        }catch(Exception e){
            return "";
        }
    } 
    //解决 int数据长度溢出的问题
    public static String changeF2Y(long  amount) {
        try{
            String amountString = String.valueOf(amount);
            if(!amountString.matches(CURRENCY_FEN_REGEX)) {  
                return "";
            }    
            return BigDecimal.valueOf(Long.valueOf(amount)).divide(new BigDecimal(100),2, BigDecimal.ROUND_HALF_EVEN).toString();
        }catch(Exception e){
            return "";
        }
    }


    
    private static int getRandonIndex() {
        return random.nextInt(25); //[0-24]
    }
    
    //生成随机加密内容
    public static String createEncryptRandomContent(String msgContent) {
        if(TextUtils.isEmpty(msgContent)) {
            //非文本
            return "";
        }
        
        String resultString = "";
        random.setSeed(System.currentTimeMillis());
        
        //length =  random.nextInt(100) + 10;   //随机内容长度
        try {
            length =  (int)((msgContent.getBytes("GBK").length)*1.5); //内容长度
        } catch (Exception e) {
            length =  (int)((msgContent.getBytes().length)*1.5); //内容长度
        }
        
        index = 0;
        
        for(int i=0; i<length; i++) {
            index = getRandonIndex();
            resultString += strRandom.substring(index, index+1);
        }
        
        return resultString;
    }


}
