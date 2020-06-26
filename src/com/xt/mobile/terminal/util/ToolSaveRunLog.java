package com.xt.mobile.terminal.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

import com.xt.mobile.terminal.contants.ConstantsValues;
import com.xt.mobile.terminal.service.VideoService;

public class ToolSaveRunLog {

	private String LOG_PATH_SDCARD_DIR; // 日志文件在sdcard中的路径
	private Process process;
	private WakeLock wakeLock;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HHmmss", Locale.getDefault());// 日志名称格式
	private String logServiceLogName = "Log.log";// 本服务输出的日志文件名称

	private String mPackageName = null;
	private Context mContext = null;

	public ToolSaveRunLog(Context context, String packageName) {
		
		this.mContext = context;
		this.mPackageName = packageName;

		PowerManager pm = (PowerManager) context.getApplicationContext().getSystemService(
				Context.POWER_SERVICE);
		wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getSimpleName());

		createLogDir();
		deployLogTask();
	}

	public void startSaveLog() {
		new LogCollectorThread().start();
	}

	/**
	 * 日志收集 1.清除日志缓存 2.杀死应用程序已开启的Logcat进程防止多个进程写入一个日志文件 3.开启日志收集进程 4.处理日志文件 移动
	 * OR 删除
	 */
	class LogCollectorThread extends Thread {

		public LogCollectorThread() {
			super("LogCollectorThread");
		}

		@SuppressLint("Wakelock")
		@Override
		public void run() {
			try {
				wakeLock.acquire(); // 唤醒手机
				clearLogCache();
				List<String> orgProcessList = getAllProcess();
				List<ProcessInfo> processInfoList = getProcessInfoList(orgProcessList);
				killLogcatProc(processInfoList);// 关闭与我的应用名同一个进程的logcat程序
				createLogCollector();
				Thread.sleep(1000);// 休眠，创建文件，然后处理文件，不然该文件还没创建，会影响文件删除
				deleteSDcardExpiredLog();
				wakeLock.release(); // 释放
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 去除文件的扩展类型（.log）
	 * 
	 * @param fileName
	 * @return
	 */
	private String getFileNameWithoutExtension(String fileName) {
		return fileName.substring(0, fileName.indexOf("."));
	}

	/**
	 * 判断sdcard上的日志文件是否可以删除
	 * 
	 * @param createDateStr
	 * @return
	 */
	public boolean canDeleteSDLog(String createDateStr) {
		boolean canDel = false;
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, -1 * ConstantsValues.SDCARD_LOG_FILE_SAVE_DAYS);// 删除7天之前日志
		Date expiredDate = calendar.getTime();
		try {
			Date createDate = sdf.parse(createDateStr);
			canDel = createDate.before(expiredDate);
		} catch (ParseException e) {
			canDel = false;
		}
		return canDel;
	}

	/**
	 * 删除SD卡下过期的日志
	 */
	private void deleteSDcardExpiredLog() {
		File file = new File(LOG_PATH_SDCARD_DIR);
		if (file.isDirectory()) {
			File[] allFiles = file.listFiles();
			if (allFiles != null) {
				for (File logFile : allFiles) {
					String fileName = logFile.getName();
					if (logServiceLogName.equals(fileName)) {
						continue;
					}
					String createDateInfo = getFileNameWithoutExtension(fileName);
					if (canDeleteSDLog(createDateInfo)) {
						logFile.delete();
					}
				}
			}
		}
	}

	/**
	 * 根据当前的存储位置得到日志的绝对存储路径
	 * 
	 * @return
	 */
	public String getLogPath() {
		createLogDir();
		String logFileName = sdf.format(new Date()) + ".log";// 日志文件名称
		return LOG_PATH_SDCARD_DIR + File.separator + logFileName;
	}

	/**
	 * 开始收集日志信息
	 */
	public void createLogCollector() {
		List<String> commandList = new ArrayList<String>();
		commandList.add("logcat");
		commandList.add("-f");
		commandList.add(getLogPath());
		commandList.add("-v");
		commandList.add("time");
		commandList.add("*:I");

		// commandList.add("*:E");// 过滤所有的错误信息
		// 过滤指定TAG的信息
		// commandList.add("MyAPP:V");
		// commandList.add("*:S");
		try {
			process = Runtime.getRuntime()
					.exec(commandList.toArray(new String[commandList.size()]));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取本程序的用户名称
	 * 
	 * @param packName
	 * @param allProcList
	 * @return
	 */
	private String getAppUser(String packName, List<ProcessInfo> allProcList) {
		for (ProcessInfo processInfo : allProcList) {
			if (processInfo.name.equals(packName)) {
				return processInfo.user;
			}
		}
		return null;
	}

	/**
	 * 关闭由本程序开启的logcat进程： 根据用户名称杀死进程(如果是本程序进程开启的Logcat收集进程那么两者的USER一致)
	 * 如果不关闭会有多个进程读取logcat日志缓存信息写入日志文件
	 * 
	 * @param allProcList
	 * @return
	 */
	private void killLogcatProc(List<ProcessInfo> allProcList) {
		if (process != null) {
			process.destroy();
		}
		String myUser = getAppUser(mPackageName, allProcList);
		for (ProcessInfo processInfo : allProcList) {
			if (processInfo.name.toLowerCase(Locale.getDefault()).equals("logcat")
					&& processInfo.user.equals(myUser)) {
				android.os.Process.killProcess(Integer.parseInt(processInfo.pid));
			}
		}
	}

	class ProcessInfo {
		public String user;
		public String pid;
		public String ppid;
		public String name;

		@Override
		public String toString() {
			String str = "user=" + user + " pid=" + pid + " ppid=" + ppid + " name=" + name;
			return str;
		}
	}

	/**
	 * 根据ps命令得到的内容获取PID，User，name等信息
	 * 
	 * @param orgProcessList
	 * @return
	 */
	private List<ProcessInfo> getProcessInfoList(List<String> orgProcessList) {
		List<ProcessInfo> procInfoList = new ArrayList<ProcessInfo>();
		for (int i = 1; i < orgProcessList.size(); i++) {
			String processInfo = orgProcessList.get(i);
			String[] proStr = processInfo.split(" ");
			// USER PID PPID VSIZE RSS WCHAN PC NAME
			// root 1 0 416 300 c00d4b28 0000cd5c S /init
			List<String> orgInfo = new ArrayList<String>();
			for (String str : proStr) {
				if (!"".equals(str)) {
					orgInfo.add(str);
				}
			}
			if (orgInfo.size() == 9) {
				ProcessInfo pInfo = new ProcessInfo();
				pInfo.user = orgInfo.get(0);
				pInfo.pid = orgInfo.get(1);
				pInfo.ppid = orgInfo.get(2);
				pInfo.name = orgInfo.get(8);
				procInfoList.add(pInfo);
			}
		}
		return procInfoList;
	}

	/**
	 * 运行PS命令得到进程信息
	 * 
	 * @return USER PID PPID VSIZE RSS WCHAN PC NAME root 1 0 416 300 c00d4b28
	 *         0000cd5c S /init
	 */
	private List<String> getAllProcess() {
		List<String> orgProcList = new ArrayList<String>();
		Process proc = null;
		try {
			proc = Runtime.getRuntime().exec("ps");
			StreamConsumer outputConsumer = new StreamConsumer(proc.getInputStream(), orgProcList);
			outputConsumer.start();
			proc.waitFor();
		} catch (Exception e) {
		} finally {
			try {
				proc.destroy();
			} catch (Exception e) {
			}
		}
		return orgProcList;
	}

	class StreamConsumer extends Thread {
		InputStream is;
		List<String> list;

		StreamConsumer(InputStream is, List<String> list) {
			this.is = is;
			this.list = list;
		}

		public void run() {
			try {
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String line = null;
				while ((line = br.readLine()) != null) {
					if (list != null) {
						list.add(line);
					}
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

	/**
	 * 每次记录日志之前先清除日志的缓存, 不然会在两个日志文件中记录重复的日志
	 */
	private void clearLogCache() {
		Process proc = null;
		List<String> commandList = new ArrayList<String>();
		commandList.add("logcat");
		commandList.add("-c");
		try {
			proc = Runtime.getRuntime().exec(commandList.toArray(new String[commandList.size()]));
			proc.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				proc.destroy();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 部署日志任务，每天凌晨开启收集
	 */
	public void deployLogTask() {
		Intent intent = new Intent(VideoService.ACTION_START_NEW_DAY_LOG);
		PendingIntent sender = PendingIntent.getBroadcast(mContext, 0, intent, 0);
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		// 部署定时任务
		AlarmManager am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
		am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
				AlarmManager.INTERVAL_DAY, sender);
	}

	/**
	 * 创建日志输出文件
	 */
	private void createLogDir() {
		LOG_PATH_SDCARD_DIR = Environment.getExternalStorageDirectory().getAbsolutePath()
				+ File.separator + "XTXK" + File.separator + "log";
		File file = new File(LOG_PATH_SDCARD_DIR);
		if (!file.isDirectory()) {
			file.mkdirs();
		}
	}
}
