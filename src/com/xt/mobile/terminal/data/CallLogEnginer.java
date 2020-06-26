package com.xt.mobile.terminal.data;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xt.mobile.terminal.domain.CallLog;
import com.xt.mobile.terminal.sip.SipManager;

public class CallLogEnginer {
	public static final String CALL_LOGS_TABLE_NAME = "call_logs";

	Context context;
	private static SQLiteDatabase db;
	private static SQLiteHelper helper;
	private static CallLogEnginer enginer;

	private CallLogEnginer(Context context) {
		super();
		this.context = context;
	}

	public static CallLogEnginer getInstance(Context context) {
		if (enginer == null) {
			enginer = new CallLogEnginer(context);
			helper = SQLiteHelper.getInstance(context, SQLiteHelper.DATABASE_NAME, null, SQLiteHelper.DATABASE_VERSION);
			db = helper.getWritableDatabase();
		}
		return enginer;
	}

	/**
	 * 查询所有的通话记录
	 * 
	 * @return
	 */
	public ArrayList<CallLog> queryAll() {
		ArrayList<CallLog> result = new ArrayList<CallLog>();
		Cursor cursor = db.query(CALL_LOGS_TABLE_NAME, null, "myId=?", new String[] { SipManager.me.getId() }, null,
				null, "time desc");
		while (cursor.moveToNext()) {
			CallLog log = new CallLog();
			int media_type = cursor.getInt(cursor.getColumnIndex("media_type"));
			String name = cursor.getString(cursor.getColumnIndex("name"));
			int dir_type = cursor.getInt(cursor.getColumnIndex("dir_type"));
			long time = cursor.getLong(cursor.getColumnIndex("time"));
			log.setMediaType(media_type);
			log.setName(name);
			log.setDirType(dir_type);
			log.setTime(time);
			result.add(log);
		}
		cursor.close();
		return result;
	}

	public Cursor getQueryCursor() {
		return db.query(CALL_LOGS_TABLE_NAME, null, null, null, null, null, " time desc ");
	}

	/**
	 * 插入一条新的通话记录
	 * 
	 * @param log
	 * @return
	 */
	public long insert(CallLog log) {
		if (log == null) {
			return -1;
		}
		ContentValues values = new ContentValues();
		values.put("media_type", log.getMediaType());
		values.put("name", log.getName());
		values.put("dir_type", log.getDirType());
		values.put("time", log.getTime());
		values.put("myId", SipManager.me.getId());
		return db.insert(CALL_LOGS_TABLE_NAME, null, values);
	}

	public int delete(CallLog log) {
		return db.delete(CALL_LOGS_TABLE_NAME, " time=? ", new String[] { "" + log.getTime() });
	}

	public int deleteAll() {
		return db.delete(CALL_LOGS_TABLE_NAME, null, null);
	}
}