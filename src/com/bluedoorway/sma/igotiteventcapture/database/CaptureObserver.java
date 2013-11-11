package com.bluedoorway.sma.igotiteventcapture.database;

import java.io.File;

import com.bluedoorway.sma.igotiteventcapture.IGotItApplication;
import com.bluedoorway.sma.igotiteventcapture.model.request.EventAssetUploadRequest;
import com.bluedoorway.sma.igotiteventcapture.model.response.EventAssetUploadResponse;
import com.bluedoorway.sma.igotiteventcapture.task.AssetUploadTask;
import com.bluedoorway.sma.igotiteventcapture.task.interfaces.EventAssetUploadTaskDelegate;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;

public class CaptureObserver extends ContentObserver implements EventAssetUploadTaskDelegate
{
	private ContentResolver resolver;
	Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);

			switch (msg.what)
			{
				case 0:
					uploadAsset(msg.arg1);
					break;
			}
		}
	};

	public CaptureObserver(Handler handler)
	{
		super(handler);
		resolver = IGotItApplication.getAppContext().getContentResolver();
	}

	@Override
	public void onChange(boolean selfChange, Uri uri)
	{
		super.onChange(selfChange);
		Message msg = new Message();
		msg.what = 0;
		msg.arg1 = Integer.parseInt(uri.getLastPathSegment());
		mHandler.sendMessageDelayed(msg, 2000);
	}

	public void uploadAsset(int id)
	{
		// Getting file detail
		String where = EventsDB.EVENT_ID + " = ?";
		Cursor cursor = resolver.query(EventsDB.FILES_CONTENT_URI, null, where, new String[] { String.valueOf(id) }, null);
		if (cursor != null && cursor.getCount() > 0 && IGotItApplication.checkNetworkStatus() >= 0)
		{
			while (cursor.moveToNext())
			{
				boolean status = Boolean.valueOf(cursor.getString(cursor.getColumnIndex(EventsDB.UPLOAD_STATUS)));
				if (!status)
				{
					EventAssetUploadRequest request = new EventAssetUploadRequest();
					request.user.username = IGotItApplication.getUserName();
					request.user.password = IGotItApplication.getUserPswd();

					File f = new File(cursor.getString(cursor.getColumnIndex(EventsDB.FILE_PATH)));
					// Getting the server id
					String event_id = cursor.getString(cursor.getColumnIndex(EventsDB.EVENT_ID));
					Cursor eventCursor = resolver.query(EventsDB.EVENTS_CONTENT_URI, null, EventsDB.COLUMN_ID + "=?",
							new String[] { event_id }, null);

					if (eventCursor != null && eventCursor.getCount() > 0)
					{

						eventCursor.moveToFirst();
						boolean uploadStatus = Boolean.valueOf(eventCursor.getString(eventCursor
								.getColumnIndex(EventsDB.UPLOAD_STATUS)));
						if (uploadStatus)
						{
							// see model for comments about the fields
							request.assetId = cursor.getString(cursor.getColumnIndex(EventsDB.FILE_GUID));
							request.serverId = cursor.getString(cursor.getColumnIndex(EventsDB.SERVER_ID));
							request.contentType = cursor.getString(cursor.getColumnIndex(EventsDB.FILE_TYPE));
							request.filePath = f.getAbsolutePath();
							request.dataLength = f.length();

							AssetUploadTask task = new AssetUploadTask(cursor.getString(cursor.getColumnIndex(EventsDB.COLUMN_ID)));
							task.setDelegate(this);
							task.execute(request);
						}
						else
						{
							// Event itself was not uploaded, Notifying for
							// uploading event
							Uri uri = Uri.withAppendedPath(EventsDB.EVENTS_CONTENT_URI,
									eventCursor.getString(eventCursor.getColumnIndex(EventsDB.COLUMN_ID)));
							resolver.notifyChange(uri, null);
						}
					}

				}
			}
			if (cursor != null)
				cursor.close();
		}
	}

	@Override
	public void Response(EventAssetUploadResponse response)
	{
		if (response.errorCode == 0)
		{
			String file_id = response.file_id;
			Cursor cursor = resolver.query(EventsDB.FILES_CONTENT_URI, null, EventsDB.COLUMN_ID + "=?", new String[] { file_id },
					null);
			if (cursor != null && cursor.getCount() > 0)
			{
				cursor.moveToFirst();
				ContentValues value = IGotItApplication.getContentValues(cursor, true);
				Uri uri = Uri.withAppendedPath(EventsDB.FILES_CONTENT_URI, String.valueOf(file_id));
				resolver.update(uri, value, null, null);
			}
		}
	}
}
