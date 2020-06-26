package com.xt.mobile.terminal.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * @Author:pengyongshun
 * @Desc:
 * @Time:2017/3/6
 */
public class NetworkUtil {

    private static final String TAG = "NetworkUtil";
    public static final int NETWORK_TYPE_EHRPD=14; // Level 11
    public static final int NETWORK_TYPE_EVDO_B=12; // Level 9
    public static final int NETWORK_TYPE_HSPAP=15; // Level 13
    public static final int NETWORK_TYPE_IDEN=11; // Level 8
    public static final int NETWORK_TYPE_LTE=13; // Level 11

    //没有网络连接
    public static final int NETWORN_NONE = 0;
    //wifi连接
    public static final int NETWORN_WIFI = 1;
    //手机网络数据连接类型
    public static final int NETWORN_2G = 2;
    public static final int NETWORN_3G = 3;
    public static final int NETWORN_4G = 4;
    public static final int NETWORN_MOBILE = 5;



    /**
     * 获取网络的ip地址
     * @return
     */
    public static String getLocalIpAddress() {
        try{
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        //IPV4的地址
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        }catch (SocketException e) {
            // TODO: handle exception
            Log.e("IpAddress","WifiPreference IpAddress---error-" + e.toString());
        }

        return null;

    }


    /**
     * 获取mac地址
     * @param context
     * @return
     */
    public static String getLocalMacAddress(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }


    /**
     * 判断网速的快慢
     * @param context
     * @return
     */
    public static boolean isConnectedFast(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info==null){
            return false;
        }else if (info.isConnected()){
            int type = info.getType();
            int subtype = info.getSubtype();
            if(type==ConnectivityManager.TYPE_WIFI){
                System.out.println("CONNECTED VIA WIFI");
                return true;
            }else if(type==ConnectivityManager.TYPE_MOBILE){
                switch(subtype){
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                        return false; // ~ 50-100 kbps
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                        return false; // ~ 14-64 kbps
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                        return false; // ~ 50-100 kbps
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        return true; // ~ 400-1000 kbps
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                        return true; // ~ 600-1400 kbps
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                        return false; // ~ 100 kbps
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                        return true; // ~ 2-14 Mbps
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                        return true; // ~ 700-1700 kbps
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                        return true; // ~ 1-23 Mbps
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                        return true; // ~ 400-7000 kbps
                    // NOT AVAILABLE YET IN API LEVEL 7
                    case NETWORK_TYPE_EHRPD:
                        return true; // ~ 1-2 Mbps
                    case NETWORK_TYPE_EVDO_B:
                        return true; // ~ 5 Mbps
                    case NETWORK_TYPE_HSPAP:
                        return true; // ~ 10-20 Mbps
                    case NETWORK_TYPE_IDEN:
                        return false; // ~25 kbps
                    case NETWORK_TYPE_LTE:
                        return true; // ~ 10+ Mbps
                    // Unknown
                    case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                        return false;
                    default:
                        return false;
                }
            }else {
                return false;
            }
        }else {
            return false;
        }

    }



    /**
     * 将string型的ip转换为long型
     * @param ip
     * @return
     */
    public static long ip2int(String ip) {
        String[] items = ip.split("\\.");
        return Long.valueOf(items[0]) << 24
                | Long.valueOf(items[1]) << 16
                | Long.valueOf(items[2]) << 8 | Long.valueOf(items[3]);
    }


    /**
     * 将long型的ip转换为string型
     * @param ipInt
     * @return
     */
    public static String int2ip(long ipInt) {
        StringBuilder sb = new StringBuilder();
        sb.append(ipInt & 0xFF).append(".");
        sb.append((ipInt >> 8) & 0xFF).append(".");
        sb.append((ipInt >> 16) & 0xFF).append(".");
        sb.append((ipInt >> 24) & 0xFF);
        return sb.toString();
    }

    /**
     * 获取运营商名字
     * @param context
     * @return
     */
    public static String getOperatorName(Context context) {
        TelephonyManager telephonyManager =
                (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        String operator = telephonyManager.getSimOperator();
        String operatorName="";
        if (operator != null) {
            if (operator.equals("46000") || operator.equals("46002")) {
                 operatorName="中国移动";

            } else if (operator.equals("46001")) {
                 operatorName="中国联通";

            } else if (operator.equals("46003")) {
                 operatorName="中国电信";

            }
        }
        return operatorName;
    }

    /**
     * 获取当前网络连接类型
     * @param context
     * @return
     */
    public static int getNetworkState(Context context) {
         //获取系统的网络服务
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
         //如果当前没有网络
        if (null == connManager) {
            return NETWORN_NONE;
        }
         //获取当前网络类型，如果为空，返回无网络
        NetworkInfo activeNetInfo = connManager.getActiveNetworkInfo();
        if (activeNetInfo == null || !activeNetInfo.isAvailable()) {
            return NETWORN_NONE;
        }
         // 判断是不是连接的是不是wifi
        NetworkInfo wifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (null != wifiInfo) {
            NetworkInfo.State state = wifiInfo.getState();
            if (null != state)
                if (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING) {
                    return NETWORN_WIFI;
                }
        }
        // 如果不是wifi，则判断当前连接的是运营商的哪种网络2g、3g、4g等
        NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (null != networkInfo) {
            NetworkInfo.State state = networkInfo.getState();
            String strSubTypeName = networkInfo.getSubtypeName();
            if (null != state)
                if (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING) {
                    switch (activeNetInfo.getSubtype()) {
                        //如果是2g类型
                        case TelephonyManager.NETWORK_TYPE_GPRS: // 联通2g
                        case TelephonyManager.NETWORK_TYPE_CDMA: // 电信2g
                        case TelephonyManager.NETWORK_TYPE_EDGE: // 移动2g
                        case TelephonyManager.NETWORK_TYPE_1xRTT:
                        case TelephonyManager.NETWORK_TYPE_IDEN:
                            return NETWORN_2G;
                        //如果是3g类型
                        case TelephonyManager.NETWORK_TYPE_EVDO_A: // 电信3g
                        case TelephonyManager.NETWORK_TYPE_UMTS:
                        case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        case TelephonyManager.NETWORK_TYPE_HSDPA:
                        case TelephonyManager.NETWORK_TYPE_HSUPA:
                        case TelephonyManager.NETWORK_TYPE_HSPA:
                        case TelephonyManager.NETWORK_TYPE_EVDO_B:
                        case TelephonyManager.NETWORK_TYPE_EHRPD:
                        case TelephonyManager.NETWORK_TYPE_HSPAP:
                            return NETWORN_3G;
                       //如果是4g类型
                        case TelephonyManager.NETWORK_TYPE_LTE:
                            return NETWORN_4G;
                        default:
                       //中国移动 联通 电信 三种3G制式
                            if (strSubTypeName.equalsIgnoreCase("TD-SCDMA") || strSubTypeName.equalsIgnoreCase("WCDMA") || strSubTypeName.equalsIgnoreCase("CDMA2000")) {
                                return NETWORN_3G;
                            } else {
                                return NETWORN_MOBILE;
                            }
                    }
                }
        }
        return NETWORN_NONE;
    }


//
//    /**
//     * 获取wifi状态的ip地址
//     * @param context
//     * @return
//     */
//    public static String getWifiIp(Context context) {
//        //获取wifi服务
//        WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
////        //判断wifi是否开启
////        if (!wifiManager.isWifiEnabled()) {
////            wifiManager.setWifiEnabled(true);
////        }
//        if (wifiManager.isWifiEnabled()){
//            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
//            int ipAddress = wifiInfo.getIpAddress();
//            String ip = (ipAddress & 0xFF ) + "." +
//                    ((ipAddress >> 8 ) & 0xFF) + "." +
//                    ((ipAddress >> 16 ) & 0xFF) + "." +
//                    ( ipAddress >> 24 & 0xFF) ;
//            return ip;
//        }else {
//            return "0.0.0.0";
//        }
//
//    }
//


}
