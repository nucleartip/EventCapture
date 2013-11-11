package com.bluedoorway.sma.igotiteventcapture.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.util.Log;

@SuppressLint("SimpleDateFormat")
public class BaseObject
{
	static SimpleDateFormat DateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

	public BaseObject()
	{
	}

	public BaseObject(JSONObject object) throws JSONException
	{
	}
	
	public JSONObject getJSON() throws JSONException
	{
		return new JSONObject();
	}

	public Date dateFromString(String str)
	{
		try
		{
			Date date = DateFormatter.parse(str);
			return date;
		}
		catch (ParseException e)
		{
			Log.v("Date Exception", "Could't Parse", e);
		}
		return null;
	}
	
	public String dateToString(Date date)
	{
		String str = DateFormatter.format(date);
		return str;
	}
}
