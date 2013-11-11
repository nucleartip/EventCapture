package com.bluedoorway.sma.igotiteventcapture.database;

import com.bluedoorway.sma.igotiteventcapture.IGotItApplication;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;

public class EventUpdateService extends Service
{
	ContentResolver resolver;

	@Override
	public IBinder onBind(Intent arg0)
	{
		return null;
	}

	@Override
	public void onCreate()
	{
		super.onCreate();

		resolver = getContentResolver();
		uploadEventDetails();
		uploadFileDetails();
		stopSelf();
	}

	void uploadEventDetails()
	{
		Cursor cursor = resolver.query(EventsDB.EVENTS_CONTENT_URI, null, EventsDB.UPLOAD_STATUS + "='" + false + "'", null, null);
		if (cursor != null)
		{
			while (cursor.moveToNext())
			{
				Uri uri = Uri.withAppendedPath(EventsDB.EVENTS_CONTENT_URI,
						String.valueOf(cursor.getString(cursor.getColumnIndex(EventsDB.COLUMN_ID))));
				IGotItApplication.getAppContext().getContentResolver().notifyChange(uri, null);
			}
			cursor.close();
		}
	}

	void uploadFileDetails()
	{
		Cursor cursor = resolver.query(EventsDB.FILES_CONTENT_URI, null, EventsDB.UPLOAD_STATUS + "='" + false + "'", null, null);

		if (cursor != null)
		{
			while (cursor.moveToNext())
			{
				Uri uri = Uri.withAppendedPath(EventsDB.EVENTS_CONTENT_URI,
						String.valueOf(cursor.getString(cursor.getColumnIndex(EventsDB.EVENT_ID))));
				IGotItApplication.getAppContext().getContentResolver().notifyChange(uri, null);
			}
			cursor.close();
		}
	}
}
