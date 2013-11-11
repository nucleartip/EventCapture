package com.bluedoorway.sma.igotiteventcapture;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ListView;

import com.bluedoorway.sma.igotiteventcapture.adapters.CaptureAdapter;
import com.bluedoorway.sma.igotiteventcapture.database.EventsDB;
import com.bluedoorway.sma.igotiteventcapture.model.SavedCaptures;

public class IGotItEventCapturesActivity extends Activity
{
	private Context context;
	private int eventId = -1;
	private ListView savedCapturesList;
	private CaptureAdapter adapter;
	private ArrayList<SavedCaptures> capturesList;
	private ContentResolver resolver;
	private ViewGroup noCaptures;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// Setting up view
		setContentView(R.layout.activity_captures);

		// Fetching form objects
		context = this;
		savedCapturesList = (ListView) findViewById(R.id.activity_capture_list);
		noCaptures = (ViewGroup) findViewById(R.id.activity_capture_no_capture);
		capturesList = new ArrayList<SavedCaptures>();
		resolver = getContentResolver();

		if (getIntent().getExtras() != null)
			eventId = getIntent().getExtras().getInt("eventid");

		// Setting Up Adapter
		adapter = new CaptureAdapter(context, capturesList, eventId);
		savedCapturesList.setAdapter(adapter);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		capturesList.clear();

		Cursor cursor = resolver.query(EventsDB.FILES_CONTENT_URI, null, EventsDB.EVENT_ID + " = '" + eventId + "'", null, null);
		if (cursor != null)
		{
			while (cursor.moveToNext())
			{
				SavedCaptures savedCaptures = new SavedCaptures();
				savedCaptures.setID(cursor.getInt(cursor.getColumnIndex(EventsDB.COLUMN_ID)));
				savedCaptures.setFileName(cursor.getString(cursor.getColumnIndex(EventsDB.FILE_PATH)));
				savedCaptures.setCaptureType(cursor.getString(cursor.getColumnIndex(EventsDB.FILE_TYPE)));
				savedCaptures.setLatitude(cursor.getDouble(cursor.getColumnIndex(EventsDB.FILE_LAT)));
				savedCaptures.setLongitude(cursor.getDouble(cursor.getColumnIndex(EventsDB.FILE_LONG)));
				savedCaptures.setUploadStatus(Boolean.valueOf(cursor.getString(cursor.getColumnIndex(EventsDB.UPLOAD_STATUS))));
				savedCaptures.setEventServerId(getEventServerID(cursor.getInt(cursor.getColumnIndex(EventsDB.EVENT_ID))));
				savedCaptures.setAssetServerId(cursor.getString(cursor.getColumnIndex(EventsDB.SERVER_ID)));

				capturesList.add(savedCaptures);
			}
			cursor.close();
		}

		adapter.notifyDataSetChanged();
		savedCapturesList.invalidate();

		if (capturesList.size() <= 0)
			noCaptures.setVisibility(ViewGroup.VISIBLE);
		else
			noCaptures.setVisibility(ViewGroup.GONE);
	}

	String getEventServerID(int columnId)
	{
		String ret = "";

		Cursor cursor = resolver.query(EventsDB.EVENTS_CONTENT_URI, null, EventsDB.COLUMN_ID + " = '" + columnId + "'", null, null);

		if (cursor != null)
		{
			while (cursor.moveToNext())
				ret = cursor.getString(cursor.getColumnIndex(EventsDB.SERVER_ID));
			cursor.close();
		}

		return ret;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.menu_capture, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		String title = item.getTitle().toString();
		if (title.equals("Home"))
		{
			Intent intent = new Intent(context, IGotItEventsActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}
		
		if (title.equals("New Capture"))
		{
			Intent intent = new Intent(context, IGotItQuickCaptureCameraActivity.class);
			Bundle b = new Bundle();
			b.putInt("eventid", eventId);
			intent.putExtras(b);
			MainActivity.FLOW = IGotItApplication.FLOW_NEW_CAPTURE;
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}
}
