package com.bluedoorway.sma.igotiteventcapture;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.bluedoorway.sma.igotiteventcapture.adapters.EventsAdapter;
import com.bluedoorway.sma.igotiteventcapture.database.EventsDB;
import com.bluedoorway.sma.igotiteventcapture.model.Events;

public class IGotItSavedEventsActivity extends Activity
{
	private Context context;
	private ListView eventList;
	private EventsAdapter adapter;
	private ArrayList<Events> list;
	private ContentResolver resolver;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_saved_events);

		context = this;
		eventList = (ListView) findViewById(R.id.activity_saved_events_list);
		list = new ArrayList<Events>();
		resolver = getContentResolver();

		adapter = new EventsAdapter(list);
		eventList.setAdapter(adapter);

		eventList.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position, long id)
			{

				// Fetch selected event
				Events selected = (Events) adapter.getItemAtPosition(position);

				Bundle bundle = new Bundle();
				bundle.putInt(IGotItApplication.EVENT_ID_KEY, selected.getEventID());
				bundle.putString(IGotItApplication.EVENT_GUID_KEY, selected.getEventGUID());
				bundle.putString(IGotItApplication.EVENT_TITLE_KEY, selected.getEventName());
				bundle.putString(IGotItApplication.EVENT_CATEGORY_KEY, selected.getEventCategory());
				bundle.putString(IGotItApplication.EVENT_CATEGORY_ID_KEY, selected.getEventCategoryID());
				bundle.putString(IGotItApplication.EVENT_COMMENT_KEY, selected.getEventComments());
				bundle.putLong(IGotItApplication.EVENT_TIMESTAMP_KEY, selected.getTimeStamp());
				bundle.putBoolean(IGotItApplication.SYNC_STATUS_KEY, selected.isSyncStatus());
				bundle.putString(IGotItApplication.SERVER_ID, selected.getServerID());

				Intent intent = new Intent(context, IGotItNewEventsActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		
		list.clear();
		Cursor cursor = resolver.query(EventsDB.EVENTS_CONTENT_URI, null, null, null, null);
		if (cursor != null)
		{
			while (cursor.moveToNext())
			{
					Events event = new Events();
					event.setEventName(cursor.getString(cursor.getColumnIndex(EventsDB.EVENT_TITLE)));
					event.setEventCategory(cursor.getString(cursor.getColumnIndex(EventsDB.EVENT_CATEGORY)));
					event.setEventComments(cursor.getString(cursor.getColumnIndex(EventsDB.EVENT_COMMENT)));
					event.setEventID(cursor.getInt(cursor.getColumnIndex(EventsDB.COLUMN_ID)));
					event.setEventGUID(cursor.getString(cursor.getColumnIndex(EventsDB.EVENT_ID)));
					event.setEventCategoryID(cursor.getString(cursor.getColumnIndex(EventsDB.EVENT_CATEGORY_ID)));
					event.setCount(getEventCount(cursor.getInt(cursor.getColumnIndex(EventsDB.COLUMN_ID))) + "");
					event.setTimeStamp(cursor.getLong(cursor.getColumnIndex(EventsDB.EVENT_TIME_STAMP)));
					event.setSyncStatus(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(EventsDB.UPLOAD_STATUS))));
					event.setServerID(cursor.getString(cursor.getColumnIndex(EventsDB.SERVER_ID)));
					list.add(event);
			}
			cursor.close();
		}
		
		Collections.sort(list, new Comparator<Events>()
		{
			@Override
			public int compare(Events lhs, Events rhs)
			{
				return (int) rhs.getTimeStamp() - (int) lhs.getTimeStamp();
			}

		});

		adapter.notifyDataSetChanged();
		eventList.invalidate();
	}

	int getEventCount(int eventId)
	{
		Cursor cursor = resolver.query(EventsDB.FILES_CONTENT_URI, null, EventsDB.EVENT_ID + " = '" + eventId + "'", null, null);
		int ret = 0;
		if (cursor != null)
		{
			ret = cursor.getCount();
			cursor.close();
		}

		return ret;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.menu_new, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		String title = item.getTitle().toString();
		if (title.equals("New Event"))
		{
			Intent intent = new Intent(context, IGotItNewEventsActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}

		return super.onOptionsItemSelected(item);
	}
}
