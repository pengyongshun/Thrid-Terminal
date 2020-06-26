package com.xt.mobile.terminal.data;

import com.xt.mobile.terminal.util.ToolLog;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {
	private static SQLiteHelper sqLiteHelper;

	private SQLiteHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	public static SQLiteHelper getInstance(Context context, String name, CursorFactory factory, int version) {
		if (sqLiteHelper == null) {
			sqLiteHelper = new SQLiteHelper(context, name, factory, version);
		}
		return sqLiteHelper;
	}

	public static final String USER_TABLE_NAME = "user";
	public static final String DATABASE_NAME = "xtxk.db";
	public static final int DATABASE_VERSION = 1;

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table if not exists "
				+ CallLogEnginer.CALL_LOGS_TABLE_NAME
				+ " (_id integer primary key autoincrement, media_type integer, name text not null, dir_type integer, time long, myId text not null);");
		}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Logs that the database is being upgraded
		ToolLog.i("Upgrading database from version " + oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		// Kills the table and existing data
		db.execSQL("DROP TABLE IF EXISTS " + CallLogEnginer.CALL_LOGS_TABLE_NAME);
		onCreate(db);
	}
}
