package com.bluedoorway.sma.igotiteventcapture;

import java.util.UUID;

import com.bluedoorway.sma.igotiteventcapture.database.EventsDB;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class IGotItApplication extends Application
{
	// Constants

	public static final int FINISH_APP = 0;
	public static final int START_EVENT_ACTIVITY = 1;
	public static final String EVENT_ID_KEY = "EVENT_ID";
	public static final String EVENT_GUID_KEY = "event_guid";
	public static final String EVENT_TITLE_KEY = "EVENT_TITLE";
	public static final String EVENT_COMMENT_KEY = "EVENT_COMMENT";
	public static final String EVENT_CATEGORY_KEY = "EVENT_CATEGORY";
	public static final String EVENT_CATEGORY_ID_KEY = "EVENT_CATEGORY_ID";
	public static final String EVENT_TIMESTAMP_KEY = "TIME_STAMP";

	public static final int FLOW_QUICK_CAPTURE = 0;
	public static final int FLOW_NEW_EVENT = 1;
	public static final int FLOW_NEW_CAPTURE = 2;

	public static final String USER_NAME = "user_name";
	public static final String USER_PSWD = "user_password";
	public static final String LOGIN_STATUS = "login_status";
	public static final String USER_PREFS = "saved_prefs";
	public static final String USER_CATEGORIES = "categories";
	public static final String SYNC_STATUS_KEY = "sync";
	public static final String SERVER_ID = "server_id";
	private static Context context;

	@Override
	public void onCreate()
	{
		super.onCreate();
		IGotItApplication.context = getApplicationContext();
	}

	public static ContentValues getContentValues(Cursor capture, String serverId)
	{
		ContentValues value = new ContentValues();
	
		value.put(EventsDB.EVENT_ID, capture.getString(capture.getColumnIndex(EventsDB.EVENT_ID)));
		value.put(EventsDB.FILE_GUID, capture.getString(capture.getColumnIndex(EventsDB.FILE_GUID)));
		value.put(EventsDB.FILE_PATH, capture.getString(capture.getColumnIndex(EventsDB.FILE_PATH)));
		value.put(EventsDB.FILE_TYPE, capture.getString(capture.getColumnIndex(EventsDB.FILE_TYPE)));
		value.put(EventsDB.FILE_LAT, capture.getString(capture.getColumnIndex(EventsDB.FILE_LAT)));
		value.put(EventsDB.FILE_LONG, capture.getString(capture.getColumnIndex(EventsDB.FILE_LONG)));
		value.put(EventsDB.UPLOAD_STATUS, capture.getString(capture.getColumnIndex(EventsDB.UPLOAD_STATUS)));
		value.put(EventsDB.SERVER_ID, serverId);
		value.put(EventsDB.FILE_TIME_STAMP, capture.getLong(capture.getColumnIndex(EventsDB.FILE_TIME_STAMP)));
		value.put(EventsDB.FILE_UPDATED_IME_STAMP, capture.getLong(capture.getColumnIndex(EventsDB.FILE_UPDATED_IME_STAMP)));
	
		return value;
	}

	public static ContentValues getContentValues(Cursor capture, boolean status)
	{
		ContentValues value = new ContentValues();
	
		value.put(EventsDB.EVENT_ID, capture.getString(capture.getColumnIndex(EventsDB.EVENT_ID)));
		value.put(EventsDB.FILE_GUID, capture.getString(capture.getColumnIndex(EventsDB.FILE_GUID)));
		value.put(EventsDB.FILE_PATH, capture.getString(capture.getColumnIndex(EventsDB.FILE_PATH)));
		value.put(EventsDB.FILE_TYPE, capture.getString(capture.getColumnIndex(EventsDB.FILE_TYPE)));
		value.put(EventsDB.FILE_LAT, capture.getString(capture.getColumnIndex(EventsDB.FILE_LAT)));
		value.put(EventsDB.FILE_LONG, capture.getString(capture.getColumnIndex(EventsDB.FILE_LONG)));
		value.put(EventsDB.UPLOAD_STATUS, String.valueOf(status));
		value.put(EventsDB.SERVER_ID, capture.getString(capture.getColumnIndex(EventsDB.SERVER_ID)));
		value.put(EventsDB.FILE_TIME_STAMP, capture.getLong(capture.getColumnIndex(EventsDB.FILE_TIME_STAMP)));
		value.put(EventsDB.FILE_UPDATED_IME_STAMP, capture.getLong(capture.getColumnIndex(EventsDB.FILE_UPDATED_IME_STAMP)));
		return value;
	}

	public static ContentValues getContentValues(Cursor capture, double lat, double longi)
	{
		ContentValues value = new ContentValues();
	
		value.put(EventsDB.EVENT_ID, capture.getString(capture.getColumnIndex(EventsDB.EVENT_ID)));
		value.put(EventsDB.FILE_GUID, capture.getString(capture.getColumnIndex(EventsDB.FILE_GUID)));
		value.put(EventsDB.FILE_PATH, capture.getString(capture.getColumnIndex(EventsDB.FILE_PATH)));
		value.put(EventsDB.FILE_TYPE, capture.getString(capture.getColumnIndex(EventsDB.FILE_TYPE)));
		value.put(EventsDB.FILE_LAT, String.valueOf(lat));
		value.put(EventsDB.FILE_LONG, String.valueOf(longi));
		value.put(EventsDB.UPLOAD_STATUS, capture.getString(capture.getColumnIndex(EventsDB.UPLOAD_STATUS)));
		value.put(EventsDB.FILE_TIME_STAMP, capture.getLong(capture.getColumnIndex(EventsDB.FILE_TIME_STAMP)));
		value.put(EventsDB.FILE_UPDATED_IME_STAMP, System.currentTimeMillis());
		value.put(EventsDB.SERVER_ID, capture.getString(capture.getColumnIndex(EventsDB.SERVER_ID)));
	
		return value;
	}

	public static ContentValues getFilesContentValues(int eventId, String filePath, String fileType, String fileGuid, double lat,
			double longi, long time, long updated, String serverID, boolean status)
	{
		ContentValues value = new ContentValues();
		value.put(EventsDB.EVENT_ID, eventId);
		value.put(EventsDB.FILE_PATH, filePath);
		value.put(EventsDB.FILE_TYPE, fileType);
		value.put(EventsDB.FILE_LAT, String.valueOf(lat));
		value.put(EventsDB.FILE_LONG, String.valueOf(longi));
		value.put(EventsDB.UPLOAD_STATUS, String.valueOf(status));
		value.put(EventsDB.FILE_GUID, fileGuid);
		value.put(EventsDB.FILE_TIME_STAMP, time);
		value.put(EventsDB.FILE_UPDATED_IME_STAMP, updated);
		value.put(EventsDB.SERVER_ID, serverID);
		return value;
	}

	public static ContentValues getFilesContentValues(int eventId, String filePath, String fileType, String fileGuid)
	{
		ContentValues value = new ContentValues();
		value.put(EventsDB.EVENT_ID, eventId);
		value.put(EventsDB.FILE_PATH, filePath);
		value.put(EventsDB.FILE_TYPE, fileType);
		value.put(EventsDB.FILE_LAT, String.valueOf(0));
		value.put(EventsDB.FILE_LONG, String.valueOf(0));
		value.put(EventsDB.UPLOAD_STATUS, String.valueOf(false));
		value.put(EventsDB.FILE_GUID, fileGuid);
		value.put(EventsDB.FILE_TIME_STAMP, System.currentTimeMillis());
		value.put(EventsDB.FILE_UPDATED_IME_STAMP, System.currentTimeMillis());
		return value;
	}

	public static ContentValues getUpdateValues(Cursor cursor, String title, String comment, String category, String categoryId)
	{
		ContentValues value = null;
		value = new ContentValues();
	
		// Updating Values
		value.put(EventsDB.EVENT_ID, cursor.getString(cursor.getColumnIndex(EventsDB.EVENT_ID)));
		value.put(EventsDB.EVENT_TITLE, title);
		value.put(EventsDB.EVENT_CATEGORY, category);
		value.put(EventsDB.EVENT_CATEGORY_ID, categoryId);
		value.put(EventsDB.EVENT_COMMENT, comment);
		value.put(EventsDB.EVENT_TIME_STAMP, cursor.getString(cursor.getColumnIndex(EventsDB.EVENT_TIME_STAMP)));
		value.put(EventsDB.EVENT_UPDATED_TIME_STAMP, System.currentTimeMillis());
		value.put(EventsDB.UPLOAD_STATUS, String.valueOf(false));
		value.put(EventsDB.SERVER_ID, cursor.getString(cursor.getColumnIndex(EventsDB.SERVER_ID)));
	
		return value;
	}

	public static ContentValues getUpdateValues(String eventId, String name, String category, String categoryId, String comment,
			long timeStamp, long updated, boolean updateStatus, String serverID)
	{
		ContentValues value = new ContentValues();
	
		value.put(EventsDB.EVENT_ID, eventId);
		value.put(EventsDB.EVENT_TITLE, name);
		value.put(EventsDB.EVENT_CATEGORY, category);
		value.put(EventsDB.EVENT_CATEGORY_ID, categoryId);
		value.put(EventsDB.EVENT_COMMENT, comment);
		value.put(EventsDB.EVENT_TIME_STAMP, timeStamp);
		value.put(EventsDB.EVENT_UPDATED_TIME_STAMP, updated);
		String status = String.valueOf(updateStatus);
		value.put(EventsDB.UPLOAD_STATUS, status);
		value.put(EventsDB.SERVER_ID, serverID);
		return value;
	}

	public static ContentValues getContentValues(String name, String category, String categoryId, String comment)
	{
		ContentValues value = new ContentValues();
		value.put(EventsDB.EVENT_ID, getGUID());
		value.put(EventsDB.EVENT_TITLE, name);
		value.put(EventsDB.EVENT_CATEGORY, category);
		value.put(EventsDB.EVENT_CATEGORY_ID, categoryId);
		value.put(EventsDB.EVENT_COMMENT, comment);
		value.put(EventsDB.EVENT_TIME_STAMP, System.currentTimeMillis());
		value.put(EventsDB.EVENT_UPDATED_TIME_STAMP, System.currentTimeMillis());
		value.put(EventsDB.UPLOAD_STATUS, String.valueOf(false));
		return value;
	}

	public static boolean hasUserDetails()
	{
		String name = getUserName();
		String pswd = getUserPswd();
		name = name.trim();
		pswd = pswd.trim();
		
		return pswd.length() > 0 && name.length() > 0;
	}

	public static String getUserPswd()
	{
		Context context = getAppContext();
		SharedPreferences prefs = context.getSharedPreferences(USER_PREFS, 0);
		String name = prefs.getString(USER_PSWD, "");
	
		return name;
	}

	public static String getUserName()
	{
		Context context = getAppContext();
		SharedPreferences prefs = context.getSharedPreferences(USER_PREFS, 0);
		String name = prefs.getString(USER_NAME, "");
	
		return name;
	}

	public static Context getAppContext()
	{
		return IGotItApplication.context;
	}

	public static int getApplicationId()
	{
		return Integer.parseInt(getAppContext().getResources().getString(R.string.application_id));
	}

	public static String getGUID()
	{
		return UUID.randomUUID().toString();
	}

	// Checking Network Availibility
	public static int checkNetworkStatus(Context context)
	{

		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectivityManager.getActiveNetworkInfo();
		if (info == null)
			return -1;
		else
		{
			if (info.isConnected() && info.isAvailable())
				return 0;
			else
				return -1;
		}
	}

	// Checking Network Availibility
	public static int checkNetworkStatus()
	{

		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectivityManager.getActiveNetworkInfo();
		if (info == null)
			return -1;
		else
		{
			if (info.isConnected() && info.isAvailable())
				return 0;
			else
				return -1;
		}
	}
}
