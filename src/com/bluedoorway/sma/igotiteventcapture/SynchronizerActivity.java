package com.bluedoorway.sma.igotiteventcapture;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bluedoorway.sma.igotiteventcapture.database.EventUpdateService;
import com.bluedoorway.sma.igotiteventcapture.database.EventsDB;
import com.bluedoorway.sma.igotiteventcapture.model.EventAsset;
import com.bluedoorway.sma.igotiteventcapture.model.EventCapture;
import com.bluedoorway.sma.igotiteventcapture.model.UserRecord;
import com.bluedoorway.sma.igotiteventcapture.model.request.UserAccountSyncRequest;
import com.bluedoorway.sma.igotiteventcapture.model.response.UserAccountSyncResponse;
import com.bluedoorway.sma.igotiteventcapture.task.UserAccountSyncTask;
import com.bluedoorway.sma.igotiteventcapture.task.interfaces.UserAccountSyncTaskDelegate;

public class SynchronizerActivity extends Activity implements UserAccountSyncTaskDelegate
{
	private Context context;
	private TextView syncMsg;
	private ProgressBar syncBar;
	private boolean isSyncComplete = false;
	private int startPosition = 0;
	private AlertDialog.Builder confirmExitDialog;
	// Get all the events and save then into Hash
	HashMap<String, UserRecord> eventMap;
	HashMap<String, Integer> savedEvents;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_sync);

		context = this;
		syncMsg = (TextView) findViewById(R.id.sync_msg);
		syncBar = (ProgressBar) findViewById(R.id.progressBar1);
		eventMap = new HashMap<String, UserRecord>();
		savedEvents = new HashMap<String, Integer>();

		syncMsg.setText("Synchronizing please wait...");

		confirmExitDialog = new AlertDialog.Builder(context);
		confirmExitDialog.setTitle("Exit Sync !!");
		confirmExitDialog.setMessage("Cancel Synchronization?");
		confirmExitDialog.setPositiveButton("Yes", new OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				finish();
				IGotItEventsActivity.registerObserver();
			}
		});
		confirmExitDialog.setNegativeButton("Continue", null);
		confirmExitDialog.setCancelable(false);

		syncRequest(0);

		IGotItEventsActivity.unRegisterObserver();
	}

	@Override
	public void onBackPressed()
	{
		if (!isSyncComplete)
			confirmExitDialog.create().show();
		else
			finish();
	}

	public void syncRequest(int offset)
	{
		UserAccountSyncRequest request = new UserAccountSyncRequest();
		request.user.username = IGotItApplication.getUserName();
		request.user.password = IGotItApplication.getUserPswd();
		request.startingPosition = offset;
		request.recordCount = 20; // page size

		UserAccountSyncTask task = new UserAccountSyncTask();
		task.setDelegate(this);
		task.execute(request);
	}

	@Override
	public void Response(UserAccountSyncResponse response)
	{
		if (response.errorCode == 0)
		{
			syncMsg.setText(String.format("Syncing %d of %d", startPosition, response.totalRecordCount));
			syncBar.setVisibility(View.VISIBLE);
			new SynchronizerTask(response).execute();
		}
	}

	private class SynchronizerTask extends AsyncTask<Void, Void, Void>
	{
		private UserAccountSyncResponse response;

		public SynchronizerTask(UserAccountSyncResponse response)
		{
			this.response = response;
		}

		// todo: breakout into methods, to allow for -ilitites
		@Override
		protected Void doInBackground(Void... params)
		{
			ArrayList<UserRecord> records = response.records;

			for (UserRecord record : records)
			{
				EventCapture capture = record.eventCapture;
				eventMap.put(capture.serverId, record);
			}

			// Get all the local events, with upload status as True
			String where = EventsDB.UPLOAD_STATUS + "='true'";
			Cursor cursor = getContentResolver().query(EventsDB.EVENTS_CONTENT_URI,
					new String[] { EventsDB.COLUMN_ID, EventsDB.SERVER_ID }, where, null, null);

			if (cursor != null)
			{
				while (cursor.moveToNext())
				{
					savedEvents.put(cursor.getString(1), cursor.getInt(0));
					if (eventMap.containsKey(cursor.getString(1)))
					{
						// Updating Synced events which are present in DB
						UserRecord record = eventMap.get(cursor.getString(1));
						EventCapture capture = record.eventCapture;
						String title = "";
						if (capture.title != null && !capture.title.equals("null"))
							title = capture.title;

						String comment = "";
						if (capture.comment != null && !capture.comment.equals("null"))
							comment = capture.comment;

						ContentValues value = IGotItApplication.getUpdateValues(capture.eventId, title, capture.type,
								capture.typeId, comment, capture.creation.getTime(), capture.updated.getTime(), true,
								capture.serverId);
						String selection = EventsDB.SERVER_ID + "='" + cursor.getString(1) + "'";
						int rows = getContentResolver().update(EventsDB.EVENTS_CONTENT_URI, value, selection, null);
						if (rows > 0)
						{
							// Event updated, now publish the files list
							HashMap<String, Integer> filesMap = new HashMap<String, Integer>();
							selection = EventsDB.EVENT_ID + "=" + cursor.getInt(0);
							Cursor fc = getContentResolver().query(EventsDB.FILES_CONTENT_URI,
									new String[] { EventsDB.SERVER_ID, EventsDB.COLUMN_ID }, selection, null, null);
							if (fc != null)
							{
								while (fc.moveToNext())
									filesMap.put(fc.getString(0), fc.getInt(1));
								fc.close();

								record = eventMap.get(cursor.getString(1));
								ArrayList<EventAsset> assets = record.assets;
								for (EventAsset asset : assets)
								{
									if (filesMap.containsKey(asset.serverId) == false)
									{
										ContentValues insert = IGotItApplication.getFilesContentValues(cursor.getInt(0),
												getPath(asset.contentType, asset.creation.getTime()), asset.contentType,
												asset.assetId, asset.latitude, asset.longitude, asset.creation.getTime(),
												asset.updated.getTime(), asset.serverId, true);
										getContentResolver().insert(EventsDB.FILES_CONTENT_URI, insert);
									}
								}
							}
							else
							{
								record = eventMap.get(cursor.getString(1));
								ArrayList<EventAsset> assets = record.assets;
								if (assets != null)
								{
									for (EventAsset asset : assets)
									{
										ContentValues insert = IGotItApplication.getFilesContentValues(cursor.getInt(0),
												getPath(asset.contentType, asset.creation.getTime()), asset.contentType,
												asset.assetId, asset.latitude, asset.longitude, asset.creation.getTime(),
												asset.updated.getTime(), asset.serverId, true);
										getContentResolver().insert(EventsDB.FILES_CONTENT_URI, insert);
									}
								}
							}
						}
					}
					else
					{
						// This Synced is not part of server, deleting this
						// event
						String delete = EventsDB.COLUMN_ID + "=" + cursor.getInt(0);
						int rows = getContentResolver().delete(EventsDB.EVENTS_CONTENT_URI, delete, null);
						if (rows > 0)
						{
							// Deleting all the related files
							delete = EventsDB.EVENT_ID + "=" + cursor.getInt(0);
							rows = getContentResolver().delete(EventsDB.FILES_CONTENT_URI, delete, null);
						}

					}
				}

				// Creating synced events which were not present in db
				Set<String> keyset = eventMap.keySet();
				Iterator<String> iterator = keyset.iterator();
				while (iterator.hasNext())
				{
					String server_id = iterator.next();
					if (!savedEvents.containsKey(server_id))
					{
						// Creating this event in local db
						UserRecord record = eventMap.get(server_id);
						EventCapture capture = record.eventCapture;

						String title = "";
						if (capture.title != null && !capture.title.equals("null"))
							title = capture.title;

						String comment = "";
						if (capture.comment != null && !capture.comment.equals("null"))
							comment = capture.comment;

						ContentValues value = IGotItApplication.getUpdateValues(capture.eventId, title, capture.type,
								capture.typeId, comment, capture.creation.getTime(), capture.updated.getTime(), true,
								capture.serverId);
						Uri uri = getContentResolver().insert(EventsDB.EVENTS_CONTENT_URI, value);
						int id = Integer.valueOf(uri.getLastPathSegment());

						if (id > 0)
						{
							// Creating Files
							record = eventMap.get(server_id);
							ArrayList<EventAsset> assets = record.assets;
							if (assets != null)
							{
								for (EventAsset asset : assets)
								{
									ContentValues insert = IGotItApplication.getFilesContentValues(id,
											getPath(asset.contentType, asset.creation.getTime()), asset.contentType, asset.assetId,
											asset.latitude, asset.longitude, asset.creation.getTime(), asset.updated.getTime(),
											asset.serverId, true);
									getContentResolver().insert(EventsDB.FILES_CONTENT_URI, insert);
								}
							}
						}
					}
				}
				cursor.close();
			}
			else
			{
				// No Event records found, which were synced. creating all
				Set<String> keyset = eventMap.keySet();
				Iterator<String> iterator = keyset.iterator();
				while (iterator.hasNext())
				{
					String key = iterator.next();
					if (!savedEvents.containsKey(key))
					{
						// Create this events and subsequent files

						UserRecord record = eventMap.get(key);
						EventCapture capture = record.eventCapture;
						String title = "";
						if (capture.title != null && !capture.title.equals("null"))
							title = capture.title;

						String comment = "";
						if (capture.comment != null && !capture.comment.equals("null"))
							comment = capture.comment;

						ContentValues value = IGotItApplication.getUpdateValues(capture.eventId, title, capture.type,
								capture.typeId, comment, capture.creation.getTime(), capture.updated.getTime(), true,
								capture.serverId);
						Uri uri = getContentResolver().insert(EventsDB.EVENTS_CONTENT_URI, value);
						int id = Integer.valueOf(uri.getLastPathSegment());
						if (id > 0)
						{
							ArrayList<EventAsset> assets = record.assets;
							if (assets != null)
							{
								for (EventAsset asset : assets)
								{
									ContentValues insert = IGotItApplication.getFilesContentValues(id,
											getPath(asset.contentType, asset.creation.getTime()), asset.contentType, asset.assetId,
											asset.latitude, asset.longitude, asset.creation.getTime(), asset.updated.getTime(),
											asset.serverId, true);
									getContentResolver().insert(EventsDB.FILES_CONTENT_URI, insert);
								}
							}
						}
					}
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result)
		{
			super.onPostExecute(result);

			if (response.totalRecordCount > 20 && response.recordCount >= 20)
			{
				startPosition = startPosition + 20;

				if (startPosition < response.totalRecordCount)
					syncRequest(startPosition);
				else
					syncComplete();
			}
			else
				syncComplete();
		}
	}

	protected void syncComplete()
	{
		IGotItEventsActivity.registerObserver();
		isSyncComplete = true;
		syncMsg.setText("Sync Complete");
		syncBar.setVisibility(View.INVISIBLE);
		startService(new Intent(IGotItApplication.getAppContext(), EventUpdateService.class));

		Handler handler = new Handler();
		handler.postDelayed(new Runnable()
		{
			@Override
			public void run()
			{
				finish();
			}
		}, 2000);
	}

	private String getPath(String type, long timestamp)
	{
		String path = "";
		if (type.contains("image"))
		{
			File mediaStorageDir = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/IGotIt/Pics");
			path = mediaStorageDir.getPath() + File.separator + "IMG_" + timestamp + ".jpg";
		}
		else if (type.contains("video"))
			path = Environment.getExternalStorageDirectory().getAbsoluteFile() + "/IGotIt/Video/IGotIt_" + timestamp + ".mp4";
		else if (type.contains("audio"))
		{
			File directory = new File(Environment.getExternalStorageDirectory() + "/IGotIt/Audio");
			path = directory.getAbsolutePath() + "/IGotIt_" + timestamp + "_.mp3";
		}

		return path;
	}
}
