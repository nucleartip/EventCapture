package com.bluedoorway.sma.igotiteventcapture.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

public class EventsDB extends SQLiteOpenHelper
{
	public static String EVENT_TABLE_NAME = "events_table";
	public static String FILES_TABLE_NAME = "files_table";
	public static int DATABASE_VERSION = 31;
	public static String DATABASE_NAME = "event.db";

	// Column Names
	public static final String COLUMN_ID = "id";
	public static final String EVENT_ID = "event_id";
	public static final String EVENT_TITLE = "title";
	public static final String EVENT_CATEGORY = "category";
	public static final String EVENT_CATEGORY_ID = "category_id";
	public static final String EVENT_COMMENT = "comment";
	public static final String EVENT_TIME_STAMP = "date_time";
	public static final String EVENT_UPDATED_TIME_STAMP = "updated";

	public static final String FILE_PATH = "file_path";
	public static final String FILE_TYPE = "file_type";
	public static final String FILE_LAT = "latitude";
	public static final String FILE_LONG = "longitude";
	public static final String UPLOAD_STATUS = "status";
	public static final String SERVER_ID = "server_id";
	public static final String FILE_GUID = "file_guid";
	public static final String FILE_TIME_STAMP = "file_time_stamp";
	public static final String FILE_UPDATED_IME_STAMP = "file_updated_time_stamp";
	// Contents
	public static String AUTHORITY = "com.bluedoorway.sma.igotiteventcapture.provider";
	public static Uri EVENTS_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + EVENT_TABLE_NAME);
	public static Uri FILES_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + FILES_TABLE_NAME);

	// Database creation sql statement
	private static final String DATABASE_CREATE = "create table " + EVENT_TABLE_NAME + "(" + COLUMN_ID
			+ " integer primary key autoincrement, " + EVENT_ID + " TEXT, " + EVENT_TITLE + " TEXT, " + EVENT_CATEGORY + " TEXT, "
			+ EVENT_CATEGORY_ID + " TEXT, " + EVENT_COMMENT + " TEXT, " + EVENT_TIME_STAMP + " TEXT, " + EVENT_UPDATED_TIME_STAMP
			+ " TEXT, " + UPLOAD_STATUS + " TEXT, " + SERVER_ID + " TEXT" + ");";
	// Database creation sql statement
	private static final String FILES_DATABASE_CREATE = "create table " + FILES_TABLE_NAME + "(" + COLUMN_ID
			+ " integer primary key autoincrement, " + EVENT_ID + " INTEGER, " + FILE_GUID + " TEXT , " + FILE_PATH + " TEXT, "
			+ FILE_TYPE + " TEXT, " + FILE_LAT + " TEXT, " + FILE_LONG + " TEXT, " + UPLOAD_STATUS + " TEXT, " + SERVER_ID
			+ " TEXT, " + FILE_TIME_STAMP + " TEXT, " + FILE_UPDATED_IME_STAMP + " TEXT" + ");";

	public EventsDB(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database)
	{
		database.execSQL(DATABASE_CREATE);
		database.execSQL(FILES_DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		// TODO Auto-generated method stub
		Log.w(EventsDB.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + EVENT_TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + FILES_TABLE_NAME);
		onCreate(db);

	}

}
