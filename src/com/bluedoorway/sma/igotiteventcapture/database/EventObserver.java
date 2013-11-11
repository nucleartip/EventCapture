package com.bluedoorway.sma.igotiteventcapture.database;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.bluedoorway.sma.igotiteventcapture.IGotItApplication;
import com.bluedoorway.sma.igotiteventcapture.model.AssetServerDetail;
import com.bluedoorway.sma.igotiteventcapture.model.EventAsset;
import com.bluedoorway.sma.igotiteventcapture.model.EventCapture;
import com.bluedoorway.sma.igotiteventcapture.model.Events;
import com.bluedoorway.sma.igotiteventcapture.model.request.EventCaptureRequest;
import com.bluedoorway.sma.igotiteventcapture.model.response.EventCaptureResponse;
import com.bluedoorway.sma.igotiteventcapture.task.EventCaptureTask;
import com.bluedoorway.sma.igotiteventcapture.task.interfaces.EventCaptureTaskDelegate;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;

public class EventObserver extends ContentObserver implements EventCaptureTaskDelegate
{
	Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);

			switch (msg.what)
			{
				case 0:
					getEventDetails(msg.arg1);
					break;
			}
		}
	};

	public EventObserver(Handler handler)
	{
		super(handler);
	}

	static SimpleDateFormat DateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

	@Override
	public void onChange(boolean selfChange, Uri uri)
	{
		super.onChange(selfChange);

		Message msg = new Message();
		msg.what = 0;
		msg.arg1 = Integer.parseInt(uri.getLastPathSegment());
		mHandler.sendMessageDelayed(msg, 2000);

	}

	void getEventDetails(int eventId)
	{
		ContentResolver resolver = IGotItApplication.getAppContext().getApplicationContext().getContentResolver();
		Events event;
		Cursor cursor = resolver.query(EventsDB.EVENTS_CONTENT_URI, null, EventsDB.COLUMN_ID + "='" + eventId + "'", null, null);
		if (cursor != null && cursor.getCount() > 0 && IGotItApplication.checkNetworkStatus() >= 0)
		{
			if (cursor.moveToNext())
			{
				event = new Events();
				event.setEventName(cursor.getString(cursor.getColumnIndex(EventsDB.EVENT_TITLE)));
				event.setEventCategory(cursor.getString(cursor.getColumnIndex(EventsDB.EVENT_CATEGORY)));
				event.setEventComments(cursor.getString(cursor.getColumnIndex(EventsDB.EVENT_COMMENT)));
				event.setEventID(cursor.getInt(cursor.getColumnIndex(EventsDB.COLUMN_ID)));
				event.setEventGUID(cursor.getString(cursor.getColumnIndex(EventsDB.EVENT_ID)));
				event.setEventCategoryID(cursor.getString(cursor.getColumnIndex(EventsDB.EVENT_CATEGORY_ID)));
				event.setServerID(cursor.getString(cursor.getColumnIndex(EventsDB.SERVER_ID)));
				event.setTimeStamp(cursor.getLong(cursor.getColumnIndex(EventsDB.EVENT_TIME_STAMP)));
				event.setUpdated(cursor.getLong(cursor.getColumnIndex(EventsDB.EVENT_UPDATED_TIME_STAMP)));
				performUpdateTask(event);
				cursor.close();
			}
		}

		if (cursor != null)
			cursor.close();
	}

	void performUpdateTask(Events event)
	{
		EventCaptureRequest request = new EventCaptureRequest();
		request.user.username = IGotItApplication.getUserName();
		request.user.password = IGotItApplication.getUserPswd();

		request.capture = new EventCapture();
		request.capture.eventId = event.getEventGUID();
		request.capture.serverId = event.getServerID();
		request.capture.title = event.getEventName();
		request.capture.type = event.getEventCategory();
		request.capture.typeId = event.getEventCategoryID();
		request.capture.comment = event.getEventComments();
		request.capture.creation = new Date(event.getTimeStamp());
		request.capture.updated = new Date(event.getUpdated());

		request.assets = populateAssets(event.getEventID());
		EventCaptureTask task = new EventCaptureTask(event.getEventID());
		task.setDelegate(this);
		task.execute(request);

	}

	private ArrayList<EventAsset> populateAssets(int eventId)
	{
		ArrayList<EventAsset> assets = new ArrayList<EventAsset>();
		ContentResolver resolver = IGotItApplication.getAppContext().getApplicationContext().getContentResolver();

		Cursor cursor = resolver.query(EventsDB.FILES_CONTENT_URI, null, EventsDB.EVENT_ID + " = '" + eventId + "'", null, null);
		if (cursor != null)
		{
			while (cursor.moveToNext())
			{
				EventAsset asset = new EventAsset();
				asset.assetId = cursor.getString(cursor.getColumnIndex(EventsDB.FILE_GUID));
				asset.creation = new Date(cursor.getLong(cursor.getColumnIndex(EventsDB.FILE_TIME_STAMP)));
				asset.updated = new Date(cursor.getLong(cursor.getColumnIndex(EventsDB.FILE_UPDATED_IME_STAMP)));
				asset.longitude = cursor.getDouble(cursor.getColumnIndex(EventsDB.FILE_LONG));
				asset.latitude = cursor.getDouble(cursor.getColumnIndex(EventsDB.FILE_LAT));
				asset.contentType = cursor.getString(cursor.getColumnIndex(EventsDB.FILE_TYPE));

				assets.add(asset);
			}
			cursor.close();
		}
		return assets;
	}

	@Override
	public void Response(EventCaptureResponse response)
	{
		if (response.errorCode == 0)
		{
			ContentResolver resolver = IGotItApplication.getAppContext().getApplicationContext().getContentResolver();
			Events event = populateEventDetails(response.clientEventId);
			ContentValues values = IGotItApplication.getUpdateValues(event.getEventGUID(), event.getEventName(),
					event.getEventCategory(), event.getEventCategoryID(), event.getEventComments(), event.getTimeStamp(),
					event.getUpdated(), true, response.serverId);
			Uri uri = Uri.withAppendedPath(EventsDB.EVENTS_CONTENT_URI, String.valueOf(event.getEventID()));
			resolver.update(uri, values, null, null);

			// Modify each file, with returned server id
			ArrayList<AssetServerDetail> fileId = response.assetIds;
			int changeCount = 0;
			for (AssetServerDetail detail : fileId)
			{
				String where = EventsDB.FILE_GUID + "='" + detail.assetId + "' and " + EventsDB.UPLOAD_STATUS + "!='true'";
				Cursor cursor = resolver.query(EventsDB.FILES_CONTENT_URI, null, where, null, null);
				if (cursor != null & cursor.getCount() > 0)
				{
					cursor.moveToFirst();
					ContentValues mValue = IGotItApplication.getContentValues(cursor, detail.serverId);
					uri = Uri.withAppendedPath(EventsDB.FILES_CONTENT_URI,
							cursor.getString(cursor.getColumnIndex(EventsDB.COLUMN_ID)));
					resolver.update(uri, mValue, null, null);
					changeCount++;
					cursor.close();
				}
			}
			if (changeCount > 0)
			{
				Uri toReturn = Uri.withAppendedPath(EventsDB.FILES_CONTENT_URI, String.valueOf(response.clientEventId));
				IGotItApplication.getAppContext().getContentResolver().notifyChange(toReturn, null);
			}
		}
	}

	private Events populateEventDetails(int clientEventId)
	{
		ContentResolver resolver = IGotItApplication.getAppContext().getApplicationContext().getContentResolver();
		Events event = null;
		Cursor cursor = resolver.query(EventsDB.EVENTS_CONTENT_URI, null, EventsDB.COLUMN_ID + "='" + clientEventId + "'", null,
				null);
		if (cursor != null)
		{
			while (cursor.moveToNext())
			{
				event = new Events();
				event.setEventName(cursor.getString(cursor.getColumnIndex(EventsDB.EVENT_TITLE)));
				event.setEventCategory(cursor.getString(cursor.getColumnIndex(EventsDB.EVENT_CATEGORY)));
				event.setEventComments(cursor.getString(cursor.getColumnIndex(EventsDB.EVENT_COMMENT)));
				event.setEventID(cursor.getInt(cursor.getColumnIndex(EventsDB.COLUMN_ID)));
				event.setEventGUID(cursor.getString(cursor.getColumnIndex(EventsDB.EVENT_ID)));
				event.setEventCategoryID(cursor.getString(cursor.getColumnIndex(EventsDB.EVENT_CATEGORY_ID)));
				event.setTimeStamp(cursor.getLong(cursor.getColumnIndex(EventsDB.EVENT_TIME_STAMP)));
				event.setUpdated(cursor.getLong(cursor.getColumnIndex(EventsDB.EVENT_UPDATED_TIME_STAMP)));
				event.setServerID(cursor.getString(cursor.getColumnIndex(EventsDB.SERVER_ID)));

			}
			cursor.close();
		}
		return event;
	}
}
