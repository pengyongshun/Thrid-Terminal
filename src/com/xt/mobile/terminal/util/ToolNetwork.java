package com.xt.mobile.terminal.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/**
 * 网络方法
 * 
 */
public class ToolNetwork {

	/**
	 * 判断是否联网
	 */
	public static boolean isNetworkConnected(Context context) {

		if (context == null) {
			return false;
		}
		ConnectivityManager mConnectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (mConnectivityManager == null) {
			return false;
		}
		NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
		if (mNetworkInfo == null) {
			return false;
		}
		return mNetworkInfo.isAvailable();
	}

	/**
	 * 判断是否WIFI联网
	 */
	public static boolean isWifiConnected(Context context) {

		if (context == null) {
			return false;
		}
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager == null) {
			return false;
		}
		NetworkInfo wifiNetworkInfo = connectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (wifiNetworkInfo.isConnected()) {
			return true;
		}
		return false;
	}

	/**
	 * 判断是否手机信号联网：3G,4G
	 * 
	 * @return
	 */
	public static boolean isMobile(Context context) {
		if (context == null) {
			return false;
		}
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (manager == null) {
			return false;
		}
		NetworkInfo networkInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (networkInfo == null) {
			return false;
		}
		return networkInfo.isConnected();
	}

	/**
	 * 获取手机ip
	 */
	public static String getIP() {
		String IP = null;
		StringBuilder IPStringBuilder = new StringBuilder();
		try {
			Enumeration<NetworkInterface> networkInterfaceEnumeration = NetworkInterface
					.getNetworkInterfaces();
			while (networkInterfaceEnumeration.hasMoreElements()) {
				NetworkInterface networkInterface = networkInterfaceEnumeration.nextElement();
				Enumeration<InetAddress> inetAddressEnumeration = networkInterface
						.getInetAddresses();
				while (inetAddressEnumeration.hasMoreElements()) {
					InetAddress inetAddress = inetAddressEnumeration.nextElement();
					if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()
							&& inetAddress.isSiteLocalAddress()) {
						IPStringBuilder.append(inetAddress.getHostAddress().toString() + "\n");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		IP = IPStringBuilder.toString();
		return IP;
	}

	/**
	 * 获取手机Mac地址
	 */
	public static String getMac(Context context) {
		WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		return info.getMacAddress();
	}

	/**
	 * 打开网络配置
	 */
	public static void openSetNetWork(Context context) {
		// 判断手机系统的版本 即API大于10 就是3.0或以上版本
		if (android.os.Build.VERSION.SDK_INT > 10) {
			// 3.0以上打开设置界面，也可以直接用ACTION_WIRELESS_SETTINGS打开到wifi界面
			context.startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
		} else {
			context.startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
		}
	}
}
