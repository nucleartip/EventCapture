package com.bluedoorway.sma.igotiteventcapture;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.bluedoorway.sma.igotiteventcapture.database.EventsDB;
import com.bluedoorway.sma.igotiteventcapture.model.CategoryDetail;

public class IGotItNewEventsActivity extends Activity
{
	private Button viewCapture;
	private Button addCapture;
	private Context context;
	private boolean isEditMode = false;
	private EditText eventTitle;
	private Spinner eventCategory;
	private EditText eventComment;
	private int eventId = -1;
	private String title;
	private String comment;
	private CategoryDetail category;
	private String categoryName;
	private ViewGroup eventsOptions;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_events);

		context = this;
		viewCapture = (Button) findViewById(R.id.activity_new_event_view_capture);
		addCapture = (Button) findViewById(R.id.activity_new_event_add_capture);
		eventTitle = (EditText) findViewById(R.id.activity_new_event_title);
		eventCategory = (Spinner) findViewById(R.id.activity_new_event_category);
		eventComment = (EditText) findViewById(R.id.activity_new_event_comment);
		eventsOptions = (ViewGroup) findViewById(R.id.activity_new_events_events_options);

		// todo: move to Application
		ArrayList<CategoryDetail> list = MainActivity.getCategories();
		ArrayAdapter<CategoryDetail> spinnerArrayAdapter = new ArrayAdapter<CategoryDetail>(this,
				android.R.layout.simple_spinner_dropdown_item, list);
		eventCategory.setAdapter(spinnerArrayAdapter);

		// Checking for supplied data
		if (getIntent() != null)
		{
			if (getIntent().getExtras() != null)
			{
				Bundle bundle = getIntent().getExtras();

				eventId = bundle.getInt(IGotItApplication.EVENT_ID_KEY, -1);
				title = bundle.getString(IGotItApplication.EVENT_TITLE_KEY, "");
				categoryName = bundle.getString(IGotItApplication.EVENT_CATEGORY_KEY, "");
				comment = bundle.getString(IGotItApplication.EVENT_COMMENT_KEY, "");

				if (eventId != -1)
				{
					isEditMode = true;
					eventTitle.setText("" + title);
					eventComment.setText("" + comment);

					ArrayAdapter<CategoryDetail> adapter = (ArrayAdapter<CategoryDetail>) eventCategory.getAdapter();
					for (int position = 0; position < adapter.getCount(); position++)
					{
						CategoryDetail detail = adapter.getItem(position);
						if (detail.name.equals(categoryName))
						{
							eventCategory.setSelection(position);
							break;
						}
					}
				}
			}
		}

		viewCapture.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(context, IGotItEventCapturesActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				if (eventId == -1)
				{
					// Save event details in DB
					isEditMode = true;
					String name = eventTitle.getText().toString();
					String comment = eventComment.getText().toString();
					CategoryDetail category = (CategoryDetail) eventCategory.getSelectedItem();
					if (category.categoryId.equals("-1"))
					{
						category.name = "";
						category.categoryId = "";
					}
					ContentResolver resolver = getContentResolver();
					ContentValues value = IGotItApplication.getContentValues(name, category.name, category.categoryId, comment);

					Uri uri = resolver.insert(EventsDB.EVENTS_CONTENT_URI, value);
					if (uri != null)
					{
						eventId = Integer.parseInt(uri.getLastPathSegment());
						invalidateOptionsMenu();
						Toast.makeText(context, "Event saved", Toast.LENGTH_SHORT).show();
					}
				}
				else if (eventId > -1)
				{
					title = eventTitle.getText().toString();
					comment = eventComment.getText().toString();
					category = (CategoryDetail) eventCategory.getSelectedItem();
					if (category.categoryId.equals("-1"))
					{
						category.name = "";
						category.categoryId = "";
					}

					ContentResolver resolver = IGotItApplication.getAppContext().getContentResolver();
					Cursor cursor = resolver.query(EventsDB.EVENTS_CONTENT_URI, null, EventsDB.COLUMN_ID + "=?",
							new String[] { String.valueOf(eventId) }, null);
					if (cursor != null)
					{
						if (cursor.moveToFirst())
						{
							// Checking if update is required or not
							if (!title.equals(cursor.getString(cursor.getColumnIndex(EventsDB.EVENT_TITLE)))
									&& !comment.equals(cursor.getString(cursor.getColumnIndex(EventsDB.EVENT_COMMENT)))
									&& !category.name.equals(cursor.getString(cursor.getColumnIndex(EventsDB.EVENT_CATEGORY))))
							{
								// Values are changed, update required
								ContentValues values = IGotItApplication.getUpdateValues(cursor, title, comment, category.name,
										category.categoryId);
								if (values != null)
								{
									Uri uri = Uri.withAppendedPath(EventsDB.EVENTS_CONTENT_URI, String.valueOf(eventId));
									resolver.update(uri, values, null, null);

									Toast.makeText(context, "Event saved", Toast.LENGTH_SHORT).show();
									resolver.notifyChange(uri, null);
								}
							}
						}
						cursor.close();
					}
				}

				Bundle b = new Bundle();
				b.putInt("eventid", eventId);
				intent.putExtras(b);
				startActivity(intent);
			}
		});

		addCapture.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(context, IGotItQuickCaptureCameraActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				MainActivity.FLOW = IGotItApplication.FLOW_NEW_EVENT;
				if (eventId == -1)
				{
					// Save event details in DB
					isEditMode = true;
					String name = eventTitle.getText().toString();
					String comment = eventComment.getText().toString();
					CategoryDetail category = (CategoryDetail) eventCategory.getSelectedItem();
					if (category.categoryId.equals("-1"))
					{
						category.name = "";
						category.categoryId = "";
					}
					ContentResolver resolver = getContentResolver();
					ContentValues value = IGotItApplication.getContentValues(name, category.name, category.categoryId, comment);
					Uri uri = resolver.insert(EventsDB.EVENTS_CONTENT_URI, value);
					if (uri != null)
					{
						eventId = Integer.parseInt(uri.getLastPathSegment());
						invalidateOptionsMenu();
						Toast.makeText(context, "Event saved", Toast.LENGTH_SHORT).show();
					}
				}
				else if (eventId > -1)
				{
					Toast.makeText(context, "Event id: " + eventId, Toast.LENGTH_SHORT).show();
					title = eventTitle.getText().toString();
					comment = eventComment.getText().toString();
					category = (CategoryDetail) eventCategory.getSelectedItem();
					if (category.categoryId.equals("-1"))
					{
						category.name = "";
						category.categoryId = "";
					}
					// Getting data
					ContentResolver resolver = IGotItApplication.getAppContext().getContentResolver();
					Cursor cursor = resolver.query(EventsDB.EVENTS_CONTENT_URI, null, EventsDB.COLUMN_ID + "=?",
							new String[] { String.valueOf(eventId) }, null);
					if (cursor != null)
					{
						if (cursor.moveToFirst())
						{
							// Checking if update is required or not
							if (!title.equals(cursor.getString(cursor.getColumnIndex(EventsDB.EVENT_TITLE)))
									&& !comment.equals(cursor.getString(cursor.getColumnIndex(EventsDB.EVENT_COMMENT)))
									&& !category.name.equals(cursor.getString(cursor.getColumnIndex(EventsDB.EVENT_CATEGORY))))
							{
								// Values are changed, update required
								ContentValues values = IGotItApplication.getUpdateValues(cursor, title, comment, category.name,
										category.categoryId);
								if (values != null)
								{
									Uri uri = Uri.withAppendedPath(EventsDB.EVENTS_CONTENT_URI, String.valueOf(eventId));
									resolver.update(uri, values, null, null);

									Toast.makeText(context, "Event saved", Toast.LENGTH_SHORT).show();
									resolver.notifyChange(uri, null);
								}
							}
						}
						cursor.close();
					}
				}

				Bundle b = new Bundle();
				b.putInt("eventid", eventId);
				intent.putExtras(b);
				startActivity(intent);
			}
		});

	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}

	@Override
	protected void onPause()
	{
		super.onPause();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
	}

	private void setOptions(boolean flag)
	{
		eventTitle.setEnabled(flag);
		eventComment.setEnabled(flag);
		eventCategory.setEnabled(flag);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		menu.clear();

		if (!isEditMode)
		{
			getMenuInflater().inflate(R.menu.menu_new_events, menu);
			setOptions(true);
		}
		else
		{
			getMenuInflater().inflate(R.menu.menu_edit_event, menu);
			setOptions(false);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		String title = item.getTitle().toString();

		if (title.equals("Save"))
		{
			// Save event details in DB
			isEditMode = true;
			if (eventId == -1)
			{
				String name = eventTitle.getText().toString();
				String comment = eventComment.getText().toString();
				CategoryDetail category = (CategoryDetail) eventCategory.getSelectedItem();
				if (category.categoryId.equals("-1"))
				{
					category.name = "";
					category.categoryId = "";
				}
				ContentResolver resolver = getContentResolver();
				ContentValues value = IGotItApplication.getContentValues(name, category.name, category.categoryId, comment);
				Uri uri = resolver.insert(EventsDB.EVENTS_CONTENT_URI, value);
				if (uri != null)
				{
					eventId = Integer.parseInt(uri.getLastPathSegment());
					invalidateOptionsMenu();
					Toast.makeText(context, "Event saved", Toast.LENGTH_SHORT).show();
				}
			}
			else
			{
				title = eventTitle.getText().toString();
				comment = eventComment.getText().toString();
				category = (CategoryDetail) eventCategory.getSelectedItem();
				if (category.categoryId.equals("-1"))
				{
					category.name = "";
					category.categoryId = "";
				}
				// Getting data
				ContentResolver resolver = IGotItApplication.getAppContext().getContentResolver();
				Cursor cursor = resolver.query(EventsDB.EVENTS_CONTENT_URI, null, EventsDB.COLUMN_ID + "=?",
						new String[] { String.valueOf(eventId) }, null);
				if (cursor != null)
				{
					if (cursor.moveToFirst())
					{
						// Checking if update is required or not
						if (!title.equals(cursor.getString(cursor.getColumnIndex(EventsDB.EVENT_TITLE)))
								&& !comment.equals(cursor.getString(cursor.getColumnIndex(EventsDB.EVENT_COMMENT)))
								&& !category.name.equals(cursor.getString(cursor.getColumnIndex(EventsDB.EVENT_CATEGORY))))
						{
							// Values are changed, update required
							ContentValues values = IGotItApplication.getUpdateValues(cursor, title, comment, category.name,
									category.categoryId);
							if (values != null)
							{
								Uri uri = Uri.withAppendedPath(EventsDB.EVENTS_CONTENT_URI, String.valueOf(eventId));
								resolver.update(uri, values, null, null);

								Toast.makeText(context, "Event saved", Toast.LENGTH_SHORT).show();
								resolver.notifyChange(uri, null);
							}
						}
					}
					cursor.close();
				}
			}
		}
		else if (title.equals("Edit"))
		{
			eventsOptions.setEnabled(true);
			isEditMode = false;
		}
		invalidateOptionsMenu();
		
		return super.onOptionsItemSelected(item);
	}
}
