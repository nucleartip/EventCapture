package com.bluedoorway.sma.igotiteventcapture.database;

import java.io.File;

import com.bluedoorway.sma.igotiteventcapture.IGotItApplication;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class EventProvider extends ContentProvider
{
	private SQLiteDatabase eventdb;
	private EventsDB event;

	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

	static
	{
		sURIMatcher.addURI(EventsDB.AUTHORITY, EventsDB.EVENT_TABLE_NAME, 1);
		sURIMatcher.addURI(EventsDB.AUTHORITY, EventsDB.EVENT_TABLE_NAME + "/#", 2);
		sURIMatcher.addURI(EventsDB.AUTHORITY, EventsDB.FILES_TABLE_NAME, 3);
		sURIMatcher.addURI(EventsDB.AUTHORITY, EventsDB.FILES_TABLE_NAME + "/#", 4);
	}
	
	public void closeDatabases()
	{
		if (eventdb != null)
			eventdb.close();
		if (event != null)
			event.close();
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs)
	{
		int toReturn = -1;

		switch (sURIMatcher.match(uri))
		{
			case 2:
				int eventId = Integer.parseInt(uri.getLastPathSegment());
				selection = EventsDB.COLUMN_ID + "= ?";
				selectionArgs = new String[] { "" + eventId };
				event = new EventsDB(getContext());
				eventdb = event.getWritableDatabase();
				toReturn = eventdb.delete(EventsDB.EVENT_TABLE_NAME, selection, selectionArgs);
				break;
			case 1:
				event = new EventsDB(getContext());
				eventdb = event.getWritableDatabase();
				toReturn = eventdb.delete(EventsDB.EVENT_TABLE_NAME, selection, selectionArgs);
				break;
			case 3:
				event = new EventsDB(getContext());
				eventdb = event.getWritableDatabase();

				// First delete all the saved files from disc, then delete rows
				Cursor cursor = eventdb.query(EventsDB.FILES_TABLE_NAME, null, selection, null, null, null, null);
				if (cursor != null && cursor.getCount() > 0)
				{
					while (cursor.moveToNext())
					{
						String path = cursor.getString(cursor.getColumnIndex(EventsDB.FILE_PATH));
						try
						{
							File file = new File(path);
							if (file.exists())
								file.delete();
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
					}
					cursor.close();
				}
				toReturn = eventdb.delete(EventsDB.FILES_TABLE_NAME, selection, selectionArgs);
				
				break;
			default:
				throw new IllegalArgumentException("Unkonw URI: " + uri.toString());

		}
		closeDatabases();
		return toReturn;
	}

	@Override
	public String getType(Uri uri)
	{
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values)
	{
		Uri toReturn = null;

		switch (sURIMatcher.match(uri))
		{
			case 1:
				event = new EventsDB(getContext());
				eventdb = event.getWritableDatabase();
				long id = eventdb.insert(EventsDB.EVENT_TABLE_NAME, null, values);
				if (id != -1)
				{
					toReturn = Uri.withAppendedPath(EventsDB.EVENTS_CONTENT_URI, String.valueOf(id));
					IGotItApplication.getAppContext().getContentResolver().notifyChange(toReturn, null);
				}
				break;
			case 3:
				event = new EventsDB(getContext());
				eventdb = event.getWritableDatabase();
				long fId = eventdb.insert(EventsDB.FILES_TABLE_NAME, null, values);
				if (fId != -1)
				{
					toReturn = Uri.withAppendedPath(EventsDB.EVENTS_CONTENT_URI, String.valueOf(values.get(EventsDB.EVENT_ID)));
					IGotItApplication.getAppContext().getContentResolver().notifyChange(toReturn, null);
				}
				break;
			default:
				throw new IllegalArgumentException("Unkonw URI: " + uri.toString());
		}
		closeDatabases();
		return toReturn;
	}

	@Override
	public boolean onCreate()
	{
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
	{
		Cursor toReturn = null;

		// cleanup prior connections
		closeDatabases();
		
		switch (sURIMatcher.match(uri))
		{
			case 1:
				event = new EventsDB(getContext());
				eventdb = event.getWritableDatabase();
				toReturn = eventdb.query(EventsDB.EVENT_TABLE_NAME, projection, selection, selectionArgs, null, null, null);
				break;
			case 2:
				event = new EventsDB(getContext());
				eventdb = event.getWritableDatabase();
				String where = EventsDB.COLUMN_ID + " = ?";
				String[] args = new String[] { uri.getLastPathSegment() };

				toReturn = eventdb.query(EventsDB.EVENT_TABLE_NAME, projection, where, args, null, null, null);
				break;
			case 3:
				event = new EventsDB(getContext());
				eventdb = event.getWritableDatabase();

				toReturn = eventdb.query(EventsDB.FILES_TABLE_NAME, projection, selection, selectionArgs, null, null, null);
				break;

			default:
				break;
		}

		return toReturn;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs)
	{
		int id = -1;
		switch (sURIMatcher.match(uri))
		{
			case 1:
				event = new EventsDB(getContext());
				eventdb = event.getWritableDatabase();
				id = eventdb.update(EventsDB.EVENT_TABLE_NAME, values, selection, selectionArgs);
				break;
			case 2:
				event = new EventsDB(getContext());
				eventdb = event.getWritableDatabase();
				String where = EventsDB.COLUMN_ID + "='" + uri.getLastPathSegment() + "'";
				id = eventdb.update(EventsDB.EVENT_TABLE_NAME, values, where, null);
				break;
			case 4:
				event = new EventsDB(getContext());
				eventdb = event.getWritableDatabase();
				selection = EventsDB.COLUMN_ID + "='" + uri.getLastPathSegment() + "'";
				id = eventdb.update(EventsDB.FILES_TABLE_NAME, values, selection, null);
				break;

			default:
				throw new IllegalArgumentException("Unkonw URI: " + uri.toString());

		}
		closeDatabases();
		return id;
	}
}
